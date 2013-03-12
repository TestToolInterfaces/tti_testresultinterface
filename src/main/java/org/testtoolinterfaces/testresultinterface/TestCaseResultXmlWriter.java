/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResultBase;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestCaseResultXmlWriter implements TestCaseResultWriter
{
	private File myXslDir;
	private File myResultFile;
	
	TestStepResultXmlWriter myTsResultWriter;
	
	/**
	 * @param aTestCaseName
	 */
	public TestCaseResultXmlWriter(Configuration aConfiguration)
	{
		Trace.println(Trace.CONSTRUCTOR, "TestCaseResultXmlWriter( aConfiguration )", true);
		myXslDir = aConfiguration.getCaseXslDir();
		if (myXslDir == null)
		{
		throw new Error( "No directory specified." );
		}
		
		if (! myXslDir.isDirectory())
		{
			throw new Error( "Not a directory: " + myXslDir.getPath() );
		}

		myTsResultWriter = new TestStepResultXmlWriter( );
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultWriter#write(org.testtoolinterfaces.testresultinterface.TestRunResult)
	 */
	public void write( TestCaseResult aTestCaseResult, File aResultFile )
	{
	    Trace.println( Trace.UTIL,
	                   "write( " + aResultFile.getPath() + " )",
	                   true );
		if ( aTestCaseResult == null )
		{
			return;
		}

		myResultFile = aResultFile;

		writeToFile(aTestCaseResult, aResultFile);

		aTestCaseResult.register(this);
	}

	public void notify( TestCaseResult aTestCaseResult )
	{
	    Trace.println( Trace.UTIL,
	                   "notify( " + aTestCaseResult.getId() + " )",
	                   true );
		writeToFile(aTestCaseResult, myResultFile);
	}

	/**
	 * @param aTestCaseResult
	 * @param aResultFile
	 */
	private void writeToFile(TestCaseResult aTestCaseResult, File aResultFile)
	{
	    Trace.println( Trace.UTIL,
	                   "writeToFile( " + aTestCaseResult.getId() + ", "
	                   				   + aResultFile.getPath() + " )",
	                   true );

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

			XmlWriterUtils.printXmlDeclaration(xmlFile, "testcase.xsl");

			printTestCase(xmlFile, "", aTestCaseResult, logDir);
			xmlFile.flush();
		}
		catch (IOException exception)
		{
			Warning.println("Saving Test Case Result XML failed: " + exception.getMessage());
			Trace.print(Trace.SUITE, exception);
		}
	}

	/**
	 * @param aStream
	 * @param anIndent
	 * @param aTestCaseResult
	 * @param logDir
	 * @throws IOException
	 */
	public void printTestCase(OutputStreamWriter aStream, String anIndent,
			TestCaseResult aTestCaseResult, File logDir) throws IOException {
		aStream.write(anIndent + "<testcase\n");
		aStream.write(anIndent + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		aStream.write(anIndent + "    xsi:noNamespaceSchemaLocation=\"TestResult_Case.xsd\"\n");
		aStream.write(anIndent + "    xsdMain='0'\n");
		aStream.write(anIndent + "    xsdSub='2'\n");
		aStream.write(anIndent + "    xsdPatch='0'\n");
		aStream.write(anIndent + "    id='" + aTestCaseResult.getId() + "'\n");
		aStream.write(anIndent + "    sequence='" + aTestCaseResult.getSequenceNr() + "'\n");
		aStream.write(anIndent + ">\n");
		
		printDescription(aStream, aTestCaseResult.getDescription(), "");
		printRequirements(aStream, aTestCaseResult.getRequirements(), "");

		aStream.write(anIndent + "  <prepare>\n");
		printStepResults(aStream, aTestCaseResult.getPrepareResults(), logDir);
		aStream.write(anIndent + "  </prepare>\n");

		aStream.write(anIndent + "  <execute>\n");
		printStepResults(aStream, aTestCaseResult.getExecutionResults(), logDir);
		aStream.write(anIndent + "  </execute>\n");

		aStream.write(anIndent + "  <restore>\n");
		printStepResults(aStream, aTestCaseResult.getRestoreResults(), logDir);
		aStream.write(anIndent + "  </restore>\n");

		aStream.write(anIndent + "  <result>" + aTestCaseResult.getResult().toString() + "</result>\n");

		XmlWriterUtils.printXmlLogFiles(aTestCaseResult.getLogs(), aStream, logDir.getAbsolutePath(), "  ");
		XmlWriterUtils.printXmlComment(aTestCaseResult, aStream, "  ");

		aStream.write(anIndent + "</testcase>\n");
	}

	/**
	 * @param aStream
	 * @param aDescription
	 * @param anIndent
	 * @throws IOException
	 */
	private void printDescription( OutputStreamWriter aStream,
	                               String aDescription,
	                               String anIndent ) throws IOException
	{
		aStream.write(anIndent + "  <description>");
    	aStream.write(aDescription);
    	aStream.write("</description>\n");
	}

	/**
	 * @param aStream
	 * @param aRequirementList
	 * @param anIndent
	 * @throws IOException
	 */
	private void printRequirements(OutputStreamWriter aStream,
									ArrayList<String> aRequirementList,
									String anIndent) throws IOException
	{
		for (int key = 0; key < aRequirementList.size(); key++)
    		XmlWriterUtils.printXmlRequirement(aStream, aRequirementList.get(key), anIndent + "  ");
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
}
