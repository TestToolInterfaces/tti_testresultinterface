/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.Hashtable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestGroupResult;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestGroupResultXmlWriter extends TestResultXmlWriter
{
    /**
	 * @param aTestGroupName
	 */
	public TestGroupResultXmlWriter(TestGroupResult aTestGroupResult, File aBaseLogDir, int anIndentLevel)
	{
		super( aTestGroupResult, aBaseLogDir, anIndentLevel );
		Trace.println(Trace.CONSTRUCTOR);
	}

	/**
	 * @param aFile
	 * @throws IOException 
	 */
	public void printXml(OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		TestGroupResult result = (TestGroupResult) getResult();
		aStream.write("    <testGroup");
		aStream.write(" id='" + result.getId() + "'");
		aStream.write(" sequence='" + result.getSequenceNr() + "'");
		aStream.write(">\n");			

		// Test Groups
		Hashtable<Integer, TestGroupResult> tgResults = result.getTestGroupResults();
    	for (int key = 0; key < tgResults.size(); key++)
    	{
    		TestGroupResultXmlWriter tgResultWriter = new TestGroupResultXmlWriter( tgResults.get(key), getBaseLogDir(), 0 );
    		tgResultWriter.printXml(aStream);
    	}

		// Test Cases
		Hashtable<Integer, TestCaseResult> tcResults = result.getTestCaseResults();
    	for (int key = 0; key < tcResults.size(); key++)
    	{
    		TestCaseResultXmlWriter tcResultWriter = new TestCaseResultXmlWriter( tcResults.get(key), getBaseLogDir(), 0 );
    		tcResultWriter.printXml(aStream);
    	}

	    printXmlLogFiles(aStream);

	    printXmlSummary( aStream );

	    aStream.write("    </testGroup>\n");
	    aStream.flush();
	}

	/**
	 * Prints the Summary in XML format
	 * 
	 * @param aStream   OutputStreamWriter of the stream to print the xml to
	 * @param aTotal    the total number of test cases
	 * @param aPassed   the total number of passed test cases
	 * @param aFailed   the total number of failed test cases
	 * 
	 * @throws IOException
	 */
	public void printXmlSummary(OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		TestGroupResult result = (TestGroupResult) getResult();
	    aStream.write("      <summary>\n");
	    aStream.write("        <totalTestCases>");
	    aStream.write( ((Integer) result.getNrOfTCs()).toString() );
	    aStream.write("</totalTestCases>\n");
	    aStream.write("        <totalPassed>");
	    aStream.write( ((Integer) result.getNrOfTCsPassed()).toString() );
	    aStream.write("</totalPassed>\n");
	    aStream.write("        <totalFailed>");
	    aStream.write( ((Integer) result.getNrOfTCsFailed()).toString() );
	    aStream.write("</totalFailed>\n");
	    aStream.write("      </summary>\n");
	}
}
