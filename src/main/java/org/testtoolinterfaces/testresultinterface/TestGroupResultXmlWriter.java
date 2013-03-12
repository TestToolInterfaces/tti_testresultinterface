/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.testtoolinterfaces.testresult.ResultSummary;
import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.TestExecItemIterationResult;
import org.testtoolinterfaces.testresult.TestExecItemResult;
import org.testtoolinterfaces.testresult.TestExecItemResultLink;
import org.testtoolinterfaces.testresult.TestExecItemSelectionResult;
import org.testtoolinterfaces.testresult.TestGroupEntryResult;
import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testresult.TestStepResultBase;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestGroupResultXmlWriter implements TestGroupResultWriter
{
	private File myXslDir;
	private Hashtable<String, File> myResultFiles;
	
	private TestCaseResultXmlWriter myTcResultWriter;
	private TestStepResultXmlWriter myTsResultWriter;
	
	public TestGroupResultXmlWriter( Configuration aConfiguration )
	{
		Trace.println(Trace.CONSTRUCTOR, "TestGroupResultXmlWriter( aConfiguration )", true);
		myXslDir = aConfiguration.getGroupXslDir();
		if (myXslDir == null)
		{
			throw new Error( "No directory specified." );
		}
		
		if (! myXslDir.isDirectory())
		{
			throw new Error( "Not a directory: " + myXslDir.getPath() );
		}
		
		myResultFiles = new Hashtable<String, File>();
		myTcResultWriter = new TestCaseResultXmlWriter( aConfiguration );
		myTsResultWriter = new TestStepResultXmlWriter( );
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultWriter#write(org.testtoolinterfaces.testresultinterface.TestRunResult)
	 */
	public void write( TestGroupResult aTestGroupResult, File aResultFile )
	{
	    Trace.println(Trace.UTIL, "write( " + aResultFile.getPath() + " )", true);
		if ( aTestGroupResult == null )
		{
			return;
		}
		myResultFiles.put(aTestGroupResult.getId(), aResultFile);

		writeToFile(aTestGroupResult, aResultFile);

		aTestGroupResult.register(this);
	}

	public void notify( TestGroupResult aTestGroupResult )
	{
	    Trace.println(Trace.UTIL, "notify( " + aTestGroupResult.getId() + " )", true);

	    File resultFile = myResultFiles.get( aTestGroupResult.getId() );
	    if (resultFile == null)
		{
			Warning.println("Cannot update a test group file that is not yet written");
		}
		else
		{
			writeToFile(aTestGroupResult, resultFile);
		}
	}

	/**
	 * @param aTestGroupResult
	 * @param aResultFile
	 */
	private void writeToFile(TestGroupResult aTestGroupResult, File aResultFile)
	{
		File logDir = aResultFile.getParentFile();
        if (!logDir.exists())
        {
        	logDir.mkdir();
        }

		XmlWriterUtils.copyXsl( myXslDir, logDir );

		FileWriter xmlFile;
		try
		{
			xmlFile = new FileWriter( aResultFile );

			XmlWriterUtils.printXmlDeclaration(xmlFile, "testgroup.xsl");

			this.printXml(aTestGroupResult, xmlFile, "", logDir);
			xmlFile.flush();
		}
		catch (IOException exception)
		{
			Warning.println("Saving Test Group Result XML failed: " + exception.getMessage());
			Trace.print(Trace.SUITE, exception);
		}
	}

	/**
	 * @param aTestGroupResult
	 * @param aStream
	 * @param anIndent
	 * @param aLogDir
	 * @throws IOException
	 */
	public void printXml(TestGroupResult aTestGroupResult, OutputStreamWriter aStream,
			String anIndent, File aLogDir) throws IOException
	{
	    Trace.println(Trace.UTIL, "printXml( " + aTestGroupResult.getId() + " )", true);

	    printOpeningTag(aStream, anIndent, aTestGroupResult);
	    
	    String indent = anIndent + "  ";
		printPrepareSteps(aStream, indent, aTestGroupResult, aLogDir);

		Collection<TestGroupEntryResult> tgEntryResults
			= aTestGroupResult.getTestGroupEntryResults();

		printTgEntryResults(aStream, indent, tgEntryResults, aLogDir);

		printRestoreSteps(aStream, indent, aTestGroupResult, aLogDir);
		printSummary( aStream, aTestGroupResult.getSummary(), indent );

		XmlWriterUtils.printXmlLogFiles(aTestGroupResult.getLogs(), aStream, aLogDir.getAbsolutePath(), indent);
		XmlWriterUtils.printXmlComment(aTestGroupResult, aStream, "  ");

		aStream.write(anIndent + "</testgroup>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param tgEntryResultList
	 * @param aLogDir
	 * @throws IOException
	 */
	public void printTgEntryResults(OutputStreamWriter aStream, String indent,
			Collection<TestGroupEntryResult> tgEntryResultList, File aLogDir)
					throws IOException {
		Iterator<TestGroupEntryResult> tgEntryResultListItr	= tgEntryResultList.iterator();
		while ( tgEntryResultListItr.hasNext() ) {
			TestGroupEntryResult tgEntryResult = tgEntryResultListItr.next();
		    if ( tgEntryResult instanceof TestExecItemResultLink ) {
		    	if ( tgEntryResult instanceof TestGroupResultLink ) {
		    	    printTgResultLink(aStream, indent,
		    	    		(TestGroupResultLink) tgEntryResult, aLogDir);
		    	}
		    	else if ( tgEntryResult instanceof TestCaseResultLink ) {
		    	    printTcResultLink(aStream, indent,
		    	    		(TestCaseResultLink) tgEntryResult, aLogDir);
		    	}
			    else {
			    	System.out.println( "ERROR: Don't know how to save link " + tgEntryResult.getId() );
			    }
		    }
		    else if ( tgEntryResult instanceof TestExecItemResult ) {
		    	if ( tgEntryResult instanceof TestExecItemSelectionResult ) {
					printSelection(aStream, indent,
							(TestExecItemSelectionResult) tgEntryResult, aLogDir);
		    	}
		    	else if ( tgEntryResult instanceof TestGroupResult ) {
		    		this.printXml( (TestGroupResult) tgEntryResult,
		    				aStream, indent, aLogDir );
		    	}
		    	else if ( tgEntryResult instanceof TestCaseResult ) {
		    		myTcResultWriter.printTestCase(aStream, indent,
		    				(TestCaseResult) tgEntryResult, aLogDir);
		    	}
			    else {
			    	System.out.println( "ERROR: Don't know how to save entry " + tgEntryResult.getId() );
			    }
		    }
		    else if ( tgEntryResult instanceof TestExecItemIterationResult ) {
		    	this.printEntryIteration( aStream, indent,
		    			(TestExecItemIterationResult) tgEntryResult,aLogDir);
		    }
		    else {
		    	System.out.println( "ERROR: Don't know how to save " + tgEntryResult.getId() );
		    }
		}
	}

	/**
	 * @param aStream
	 * @param aLogDir
	 * @param aTestGroupEntryResult
	 * @throws IOException
	 */
	private void printRestoreSteps(OutputStreamWriter aStream, String anIndent,
			TestGroupResult aTestGroupResult, File aLogDir) throws IOException {
		aStream.write(anIndent + "<restore>\n");
		printStepResults(aStream, aTestGroupResult.getRestoreResults(), aLogDir);
		aStream.write(anIndent + "</restore>\n");
	}

	/**
	 * @param aStream
	 * @param aTestGroupResult
	 * @param aLogDir
	 * @throws IOException
	 */
	private void printPrepareSteps(OutputStreamWriter aStream, String anIndent,
			TestGroupResult aTestGroupResult, File aLogDir)
			throws IOException {
		aStream.write( anIndent + "<prepare>\n");
		printStepResults(aStream, aTestGroupResult.getPrepareResults(), aLogDir);
		aStream.write(anIndent + "</prepare>\n");
	}

	/**
	 * @param aStream
	 * @param anIndent
	 * @param aTestGroupEntryResult
	 * @throws IOException
	 */
	private void printOpeningTag(OutputStreamWriter aStream, String anIndent,
			TestGroupEntryResult aTestGroupEntryResult) throws IOException {
		aStream.write(anIndent + "<testgroup");
		aStream.write(" id='" + aTestGroupEntryResult.getId() + "'");
		aStream.write(">\n");
	}

	private void printSelection(OutputStreamWriter aStream,
			String anIndent, TestExecItemSelectionResult teiSelectionResult, File aLogDir)
					throws IOException {

		aStream.write(anIndent + "<selection");
		aStream.write(" id='" + teiSelectionResult.getId() + "'");
		aStream.write(">\n");
	    
		String indent = anIndent + "  ";
		aStream.write( indent + "  <ifstep>\n");
		myTsResultWriter.printXml(teiSelectionResult.getIfResult(), aStream, aLogDir);
		aStream.write( indent + "  </ifstep>\n");

		printTgEntryResults(aStream, indent, 
				teiSelectionResult.getTestGroupEntryResults(), aLogDir);

//		printSummary( aStream, teiSelectionResult.getSummary(), indent );
		aStream.write(anIndent + "</selection>\n");
	}

	private void printEntryIteration( OutputStreamWriter aStream, String indent,
			TestExecItemIterationResult aResult, File aLogDir) throws IOException {
		aStream.write( indent + "  <foreach>\n");
		aStream.write( indent + "    <item>" + aResult.getItemName() + "</item>\n");
		aStream.write( indent + "    <list>" + aResult.getListName() + "</list>\n");

		Hashtable<Integer, List<TestGroupEntryResult>> tgEntryResultListTable = aResult.getTestResultSequenceTable();
		Hashtable<Integer, Object> testValueTable = aResult.getIterationValues();
		Hashtable<Integer, TestStepResult> untilTestStepTable = aResult.getUntilResults();

		int i = 0;
		while ( i < aResult.getSize() ) {
			List<TestGroupEntryResult> tgEntryResultList = tgEntryResultListTable.get(i);
			Object testValue = testValueTable.get(i);
			TestStepResult untilTestStepResult = untilTestStepTable.get(i);

			aStream.write( indent + "    <do itemValue=\"" + testValue.toString() + "\">\n");

			printTgEntryResults(aStream, indent, tgEntryResultList, aLogDir);

			if( untilTestStepResult != null ) {
		    	aStream.write( indent + "      <until>\n");
	    		myTsResultWriter.printXml(untilTestStepResult, aStream, aLogDir);
				aStream.write( indent + "      </until>\n");
			}

			aStream.write( indent + "    </do>\n");
			i++;
		}

		aStream.write( indent + "  </foreach>\n");		
	}

	/**
	 * @param aStream
	 * @param anIndent
	 * @param tcResultLink
	 * @param aLogDir
	 * @throws IOException
	 */
	private void printTcResultLink(OutputStreamWriter aStream, String anIndent,
			TestCaseResultLink tcResultLink, File aLogDir) throws IOException {
		aStream.write(anIndent + "  <testcaselink");
		aStream.write(" id='" + tcResultLink.getId() + "'");
		aStream.write(" type='" + tcResultLink.getType() + "'");
		aStream.write(" sequence='" + tcResultLink.getSequenceNr() + "'");
		aStream.write(">\n");

		aStream.write(anIndent + "    <link>");
		String tcLink = tcResultLink.getLink().getAbsolutePath();
		String relativeTcLink = XmlWriterUtils.makeFileRelative(tcLink, aLogDir.getAbsolutePath());
		aStream.write(relativeTcLink);
		aStream.write("</link>\n");
		
		aStream.write(anIndent + "    <verdict>");
		aStream.write(tcResultLink.getResult().toString());
		aStream.write("</verdict>\n");
		
		aStream.write(anIndent + "    <comment>");
		aStream.write(tcResultLink.getComment());
		aStream.write("</comment>\n");

		aStream.write(anIndent + "  </testcaselink>\n");
	}

	/**
	 * @param aStream
	 * @param anIndent
	 * @param tgResult
	 * @param aLogDir
	 * @throws IOException
	 */
	private void printTgResultLink(OutputStreamWriter aStream, String anIndent,
			TestGroupResultLink tgResult, File aLogDir)
					throws IOException {
		aStream.write(anIndent + "  <testgrouplink");
		aStream.write(" id='" + tgResult.getId() + "'");
		aStream.write(" type='" + tgResult.getType() + "'");
		aStream.write(" sequence='" + tgResult.getSequenceNr() + "'");
		aStream.write(">\n");

		File tgLink = tgResult.getLink();
		if ( tgLink != null )
		{
			aStream.write(anIndent + "    <link>");
			
			String tgLinkString = tgLink.getAbsolutePath();
			String relativeTgLink = XmlWriterUtils.makeFileRelative(tgLinkString, aLogDir.getAbsolutePath());
			aStream.write(relativeTgLink);

			aStream.write("</link>\n");
		}
		
		ResultSummary summary = tgResult.getSummary();
		printSummary( aStream, summary, anIndent + "    " );

		aStream.write(anIndent + "  </testgrouplink>\n");
	}

	/**
	 * @param aStream
	 * @param aStepResults
	 * @throws IOException
	 */
	private void printStepResults(	OutputStreamWriter aStream,
									Hashtable<Integer, TestStepResultBase> aStepResults,
									File aLogDir ) throws IOException
	{
		for (int key = 0; key < aStepResults.size(); key++)
    	{
    		myTsResultWriter.printXml(aStepResults.get(key), aStream, aLogDir);
    	}
	}

	/**
	 * @param aStream
	 * @param aSummary
	 * @param anIndent
	 * @throws IOException
	 */
	private void printSummary( OutputStreamWriter aStream,
								ResultSummary aSummary,
								String anIndent) throws IOException
	{
	    Trace.println(Trace.UTIL, "printSummary( aStream, aSummary, " + anIndent + " )", true);
		aStream.write(anIndent + "<summary>\n");
		aStream.write(anIndent + "  <totaltestcases>");
		aStream.write( ((Integer) aSummary.getNrOfTCs()).toString() );
		aStream.write("</totaltestcases>\n");
		aStream.write(anIndent + "  <totalpassed>");
		aStream.write( ((Integer) aSummary.getNrOfTCsPassed()).toString() );
		aStream.write("</totalpassed>\n");
		aStream.write(anIndent + "  <totalfailed>");
		aStream.write( ((Integer) aSummary.getNrOfTCsFailed()).toString() );
		aStream.write("</totalfailed>\n");
		aStream.write(anIndent + "  <totalunknown>");
		aStream.write( ((Integer) aSummary.getNrOfTCsUnknown()).toString() );
		aStream.write("</totalunknown>\n");
		aStream.write(anIndent + "  <totalerror>");
		aStream.write( ((Integer) aSummary.getNrOfTCsError()).toString() );
		aStream.write("</totalerror>\n");
		aStream.write(anIndent + "</summary>\n");
	}
}
