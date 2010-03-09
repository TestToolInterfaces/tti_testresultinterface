/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
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
	public TestStepResultXmlWriter(File aBaseLogDir, int anIndentLevel)
	{
		super(aBaseLogDir, anIndentLevel);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);
	}

	/**
	 * @param aResult	the Test Step Result
	 * @param aStream	the stream to write the test step result in xml-format to
	 * 
	 * @throws IOException 
	 */
	public void printXml(TestStepResult aResult, OutputStreamWriter aStream) throws IOException
	{
		Trace.println(Trace.LEVEL.UTIL);
		String tag = aResult.getType().toString();
		aStream.write("          <" + tag);
		aStream.write(" sequence='" + aResult.getSequenceNr() + "'");
		aStream.write(">\n");

		String description = aResult.getDescription();
    	aStream.write("        <description>");
    	aStream.write(description);
    	aStream.write("</description>\n");
	    
    	String command = aResult.getCommand();
    	if ( ! command.isEmpty() ) { aStream.write("            <command>" + command + "</command>\n"); }
    	String script = aResult.getScript();
    	if ( ! script.isEmpty() ) { aStream.write("            <script>" + script + "</script>\n"); }
    	aStream.write("            <result>" + aResult.getResult().toString() + "</result>\n");
    	ParameterTable parameters = aResult.getParameters();
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

	    printXmlLogFiles(aResult, aStream);
		aStream.write("          </" + tag + ">\n");
	}
}
