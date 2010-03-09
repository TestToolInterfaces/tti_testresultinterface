/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResult;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestCaseResultXmlWriter extends TestResultXmlWriter
{
	TestStepResultXmlWriter myTsResultWriter;
	
	/**
	 * @param aTestCaseName
	 */
	public TestCaseResultXmlWriter(File aBaseLogDir, int anIndentLevel)
	{
		super( aBaseLogDir, anIndentLevel );
		Trace.println(Trace.CONSTRUCTOR);
		
		myTsResultWriter = new TestStepResultXmlWriter( aBaseLogDir, anIndentLevel+1 );
	}

	/**
	 * @param aResult	the Test Case Result
	 * @param aStream	the Stream to write the result in xml-format to
	 * 
	 * @throws IOException 
	 */
	public void printXml(TestCaseResult aResult, OutputStreamWriter aStream) throws IOException
	{
		Trace.println(Trace.UTIL);

		aStream.write("      <testCase");
		aStream.write(" id='" + aResult.getId() + "'");
		aStream.write(" sequence='" + aResult.getSequenceNr() + "'");
		aStream.write(">\n");
		
		String description = aResult.getDescription();
    	aStream.write("        <description>");
    	aStream.write(description);
    	aStream.write("</description>\n");
		
	    ArrayList<String> requirements = aResult.getRequirements();
    	for (int key = 0; key < requirements.size(); key++)
    		TestResultXmlWriter.printXmlRequirement(aStream, requirements.get(key));

		Hashtable<Integer, TestStepResult> initializationResults = aResult.getInitializationResults();
    	for (int key = 0; key < initializationResults.size(); key++)
    	{
    		myTsResultWriter.printXml(initializationResults.get(key), aStream);
    	}

		aStream.write("        <execution>\n");
		Hashtable<Integer, TestStepResult> executionResults = aResult.getExecutionResults();
    	for (int key = 0; key < initializationResults.size(); key++)
    	{
    		myTsResultWriter.printXml(executionResults.get(key), aStream);
    	}
		aStream.write("        </execution>\n");

		Hashtable<Integer, TestStepResult> restoreResults = aResult.getRestoreResults();
    	for (int key = 0; key < restoreResults.size(); key++)
    	{
    		myTsResultWriter.printXml(restoreResults.get(key), aStream);
    	}

    	aStream.write("        <result>" + aResult.getResult().toString() + "</result>\n");

	    printXmlLogFiles(aResult, aStream);
	    printXmlComment(aResult, aStream);

	    aStream.write("      </testCase>\n");
	    aStream.flush();
	}
}
