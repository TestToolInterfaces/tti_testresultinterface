/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResult;

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

	@Override
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

			xmlFile.write("<testcase\n");
			xmlFile.write("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			xmlFile.write("    xsi:noNamespaceSchemaLocation=\"TestResult_Case.xsd\"\n");
			xmlFile.write("    xsdMain='0'\n");
			xmlFile.write("    xsdSub='2'\n");
			xmlFile.write("    xsdPatch='0'\n");
			xmlFile.write("    id='" + aTestCaseResult.getId() + "'\n");
			xmlFile.write("    sequence='" + aTestCaseResult.getSequenceNr() + "'\n");
			xmlFile.write(">\n");
			
	    	printDescription(xmlFile, aTestCaseResult.getDescription(), "");
	    	printRequirements(xmlFile, aTestCaseResult.getRequirements(), "");

	    	printStepResults(xmlFile, aTestCaseResult.getInitializationResults(), logDir);

	    	xmlFile.write("  <execution>\n");
	    	printStepResults(xmlFile, aTestCaseResult.getExecutionResults(), logDir);
	    	xmlFile.write("  </execution>\n");

	    	printStepResults(xmlFile, aTestCaseResult.getRestoreResults(), logDir);

	    	xmlFile.write("  <result>" + aTestCaseResult.getResult().toString() + "</result>\n");

	    	XmlWriterUtils.printXmlLogFiles(aTestCaseResult.getLogs(), xmlFile, logDir.getAbsolutePath(), "  ");
	    	XmlWriterUtils.printXmlComment(aTestCaseResult, xmlFile, "  ");

	    	xmlFile.write("</testcase>\n");
			xmlFile.flush();
		}
		catch (IOException exception)
		{
			Warning.println("Saving Test Case Result XML failed: " + exception.getMessage());
			Trace.print(Trace.LEVEL.SUITE, exception);
		}
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
									Hashtable<Integer, TestStepResult> aStepResults,
									File aLogDir ) throws IOException
	{
		for (int key = 0; key < aStepResults.size(); key++)
    	{
    		myTsResultWriter.printXml(aStepResults.get(key), aStream, aLogDir);
    	}
	}
}
