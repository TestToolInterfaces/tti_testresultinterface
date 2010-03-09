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
	private TestGroupResultXmlWriter myTgResultWriter = null;
	private TestCaseResultXmlWriter myTcResultWriter;

	public TestGroupResultXmlWriter(File aBaseLogDir, int anIndentLevel)
	{
		super( aBaseLogDir, anIndentLevel );
		Trace.println(Trace.CONSTRUCTOR);

		myTcResultWriter = new TestCaseResultXmlWriter( aBaseLogDir, anIndentLevel+1 );
	}

	/**
	 * @param aFile
	 * @throws IOException 
	 */
	public void printXml(TestGroupResult aTestGroupResult, OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL, "printXml( " + aTestGroupResult.getId() + " )", true);
		aStream.write("    <testGroup");
		aStream.write(" id='" + aTestGroupResult.getId() + "'");
		aStream.write(" sequence='" + aTestGroupResult.getSequenceNr() + "'");
		aStream.write(">\n");			

		// Test Groups
		Hashtable<Integer, TestGroupResult> tgResults = aTestGroupResult.getTestGroupResults();
    	for (int key = 0; key < tgResults.size(); key++)
    	{
    		if ( myTgResultWriter == null )
    		{
        		myTgResultWriter = new TestGroupResultXmlWriter( this.getBaseLogDir(), this.getIndentLevel()+1 );
    		}

    		myTgResultWriter.printXml(tgResults.get(key), aStream);
    	}

		// Test Cases
		Hashtable<Integer, TestCaseResult> tcResults = aTestGroupResult.getTestCaseResults();
    	for (int key = 0; key < tcResults.size(); key++)
    	{
    		myTcResultWriter.printXml(tcResults.get(key), aStream);
    	}

	    printXmlLogFiles( aTestGroupResult, aStream);

	    printXmlSummary( aTestGroupResult, aStream );

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
	public void printXmlSummary(TestGroupResult aTestGroupResult, OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
	    aStream.write("      <summary>\n");
	    aStream.write("        <totalTestCases>");
	    aStream.write( ((Integer) aTestGroupResult.getNrOfTCs()).toString() );
	    aStream.write("</totalTestCases>\n");
	    aStream.write("        <totalPassed>");
	    aStream.write( ((Integer) aTestGroupResult.getNrOfTCsPassed()).toString() );
	    aStream.write("</totalPassed>\n");
	    aStream.write("        <totalFailed>");
	    aStream.write( ((Integer) aTestGroupResult.getNrOfTCsFailed()).toString() );
	    aStream.write("</totalFailed>\n");
	    aStream.write("      </summary>\n");
	}
}
