/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterTable;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author arjan.kranenburg
 *
 */
public class TestStepResultXmlWriter extends TestResultXmlWriter
{
	/**
	 * @param aResult
	 * @param anIndentLevel
	 */
	public TestStepResultXmlWriter(TestStepResult aResult, int anIndentLevel)
	{
		super(aResult, anIndentLevel);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);
	}

	/**
	 * @param aStream
	 * @throws IOException 
	 */
	public void printXml(OutputStreamWriter aStream) throws IOException
	{
		Trace.println(Trace.LEVEL.UTIL);
		TestStepResult result = (TestStepResult) this.getResult();
		String tag = result.getType().toString();
		aStream.write("          <" + tag);
		aStream.write(" sequence='" + result.getSequenceNr() + "'");
		aStream.write(">\n");

		String description = result.getDescription();
    	aStream.write("        <description>");
    	aStream.write(description);
    	aStream.write("</description>\n");
	    
    	String command = result.getCommand();
    	if ( ! command.isEmpty() ) { aStream.write("            <command>" + command + "</command>\n"); }
    	String script = result.getScript();
    	if ( ! script.isEmpty() ) { aStream.write("            <script>" + script + "</script>\n"); }
    	aStream.write("            <result>" + result.getResult().toString() + "</result>\n");
    	ParameterTable parameters = result.getParameters();
    	ArrayList<Parameter> params = parameters.getOrderedList();
    	for(int i=0; i<params.size(); i++)
    	{
    		Parameter param = params.get(i);
        	aStream.write("            <parameter id='" + param.getName()
        	              + "' type='" + param.getValueType().getSimpleName()
        	              + "' sequence='" + param.getIndex()
        	              + "'>"
        	              + param.getValue().toString() + "</parameter>\n");
    	}

	    printXmlLogFiles(aStream);
		aStream.write("          </" + tag + ">\n");
	}
}
