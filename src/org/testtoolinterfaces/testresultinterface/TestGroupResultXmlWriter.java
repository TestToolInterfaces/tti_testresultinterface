/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.Hashtable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.testtoolinterfaces.testresult.ResultSummary;
import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestGroupResultXmlWriter implements TestGroupResultWriter
{
	private File myXslDir;
	private File myResultFile;
	
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
		
		myResultFile = aResultFile;

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

			xmlFile.write("<testgroup\n");
			xmlFile.write("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			xmlFile.write("    xsi:noNamespaceSchemaLocation=\"TestResult_Group.xsd\"\n");
			xmlFile.write("    xsdMain='0'\n");
			xmlFile.write("    xsdSub='2'\n");
			xmlFile.write("    xsdPatch='0'\n");
			xmlFile.write("    id='" + aTestGroupResult.getId() + "'\n");
			xmlFile.write(">\n");			

			printTestGroupLinks(aTestGroupResult, xmlFile, "", logDir);
			printTestCaseLinks(aTestGroupResult, xmlFile, "", logDir);

	    	XmlWriterUtils.printXmlLogFiles( aTestGroupResult.getLogs(), xmlFile, logDir.getAbsolutePath(), "  ");

			printSummary( xmlFile, aTestGroupResult.getSummary(), "  " );

			xmlFile.write("</testgroup>\n");
			xmlFile.flush();
		}
		catch (IOException exception)
		{
			Warning.println("Saving Test Group Result XML failed: " + exception.getMessage());
			Trace.print(Trace.LEVEL.SUITE, exception);
		}
	}

	@Override
	public void update( TestGroupResult aTestGroupResult )
	{
	    Trace.println(Trace.UTIL, "update( " + aTestGroupResult.getId() + " )", true);

	    if (myResultFile == null)
		{
			Warning.println("Cannot update a test group file that is not yet written");
		}
		else
		{
			write( aTestGroupResult, myResultFile );			
		}
	}

	/**
	 * @param aFile
	 * @throws IOException 
	 */
	public void printXml(TestGroupResult aTestGroupResult, OutputStreamWriter aStream, String anIndent, File aLogDir) throws IOException
	{
	    Trace.println(Trace.UTIL, "printXml( " + aTestGroupResult.getId() + " )", true);

	    aStream.write(anIndent + "<testgroup");
		aStream.write(" id='" + aTestGroupResult.getId() + "'");
		aStream.write(">\n");			

		printTestGroupLinks(aTestGroupResult, aStream, anIndent, aLogDir);
		printTestCaseLinks(aTestGroupResult, aStream, anIndent, aLogDir);

    	XmlWriterUtils.printXmlLogFiles( aTestGroupResult.getLogs(), aStream, aLogDir.getAbsolutePath(), anIndent + "  ");

		printSummary( aStream, aTestGroupResult.getSummary(), anIndent + "  " );

	    aStream.write(anIndent + "</testgroup>\n");
	    aStream.flush();
	}

	/**
	 * @param aTestGroupResult
	 * @param aStream
	 * @param anIndent
	 * @param aLogDir 
	 * @throws IOException
	 */
	private void printTestCaseLinks( TestGroupResult aTestGroupResult,
									 OutputStreamWriter aStream,
									 String anIndent,
									 File aLogDir ) throws IOException
	{
	    Trace.println(Trace.UTIL, "printTestCaseLinks( " + aTestGroupResult.getId() + ", " 
	                  									 + "aStream, "
	                  									 + anIndent + ", "
	                  									 + aLogDir.getPath() + " )", true);

	    Hashtable<Integer, TestCaseResultLink> tcResults = aTestGroupResult.getTestCaseResultLinks();
    	for (int key = 0; key < tcResults.size(); key++)
    	{
    	    aStream.write(anIndent + "  <testcaselink");
    		aStream.write(" id='" + tcResults.get(key).getId() + "'");
    		aStream.write(" type='" + tcResults.get(key).getType() + "'");
    		aStream.write(" sequence='" + tcResults.get(key).getSequenceNr() + "'");
    		aStream.write(">\n");

    		aStream.write(anIndent + "    <link>");
    		String tcLink = tcResults.get(key).getLink().getAbsolutePath();
    		String relativeTcLink = XmlWriterUtils.makeFileRelative(tcLink, aLogDir.getAbsolutePath());
    		aStream.write(relativeTcLink);
    		aStream.write("</link>\n");
    		
    		aStream.write(anIndent + "    <verdict>");
    		aStream.write(tcResults.get(key).getResult().toString());
    		aStream.write("</verdict>\n");
    		
    	    aStream.write(anIndent + "  </testcaselink>\n");
    	}
	}

	/**
	 * @param aTestGroupResult
	 * @param aStream
	 * @param anIndent
	 * @param aLogDir 
	 * @throws IOException
	 */
	private void printTestGroupLinks( TestGroupResult aTestGroupResult,
	                                  OutputStreamWriter aStream,
	                                  String anIndent, File aLogDir ) throws IOException
	{
	    Trace.println(Trace.UTIL, "printTestGroupLinks( " + aTestGroupResult.getId() + ", " 
	                  									  + "aStream, "
	                  									  + anIndent + " )", true);
		Hashtable<Integer, TestGroupResultLink> tgResults = aTestGroupResult.getTestGroupResultLinks();
    	for (int key = 0; key < tgResults.size(); key++)
    	{
    	    aStream.write(anIndent + "  <testgrouplink");
    		aStream.write(" id='" + tgResults.get(key).getId() + "'");
    		aStream.write(" type='" + tgResults.get(key).getType() + "'");
    		aStream.write(" sequence='" + tgResults.get(key).getSequenceNr() + "'");
    		aStream.write(">\n");

    		aStream.write(anIndent + "    <link>");
    		String tgLink = tgResults.get(key).getLink().getAbsolutePath();
    		String relativeTgLink = XmlWriterUtils.makeFileRelative(tgLink, aLogDir.getAbsolutePath());
    		aStream.write(relativeTgLink);
    		aStream.write("</link>\n");
    		
    		ResultSummary summary = tgResults.get(key).getSummary();
    		printSummary( aStream, summary, anIndent + "    " );

    	    aStream.write(anIndent + "  </testgrouplink>\n");
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
