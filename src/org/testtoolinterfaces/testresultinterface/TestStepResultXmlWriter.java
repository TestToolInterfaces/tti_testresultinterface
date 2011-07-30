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
import org.testtoolinterfaces.testsuite.ParameterArrayList;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author arjan.kranenburg
 *
 */
public class TestStepResultXmlWriter
{
	/**
	 * @param aResult
	 * @param anIndentLevel
	 */
	public TestStepResultXmlWriter()
	{
		Trace.println(Trace.CONSTRUCTOR);

	}

	/**
	 * @param aResult	the Test Step Result
	 * @param aStream	the stream to write the test step result in xml-format to
	 * 
	 * @throws IOException 
	 */
	public void printXml( TestStepResult aResult,
	                      OutputStreamWriter aStream,
	                      File aLogDir) throws IOException
	{
		Trace.println(Trace.UTIL);
		String tag = aResult.getType().toString();
		aStream.write("    <" + tag);
		aStream.write(" sequence='" + aResult.getSequenceNr() + "'");
		aStream.write(">\n");

		String description = aResult.getDescription();
    	aStream.write("      <description>");
    	aStream.write(description);
    	aStream.write("</description>\n");
	    
    	String command = aResult.getCommand();
    	if ( ! command.isEmpty() ) { aStream.write("      <command>" + command + "</command>\n"); }

    	String script = aResult.getScript();
    	if ( ! script.isEmpty() ) { aStream.write("      <script>" + script + "</script>\n"); }

    	aStream.write("      <result>" + aResult.getResult().toString() + "</result>\n");

    	ParameterArrayList parameters = aResult.getParameters();
    	ArrayList<Parameter> params = parameters.sort();
    	for(int i=0; i<params.size(); i++)
    	{
    		Parameter param = params.get(i);
        	aStream.write("      <parameter id='" + param.getName()
        	              + "' type='" + param.getValueType().getSimpleName()
        	              + "' sequence='" + param.getIndex()
        	              + "'>"
        	              + param.getValue().toString() + "</parameter>\n");
    	}

    	String comment = aResult.getComment();
    	if ( ! comment.isEmpty() ) { aStream.write("      <comment>" + comment + "</comment>\n"); }

    	XmlWriterUtils.printXmlLogFiles(aResult.getLogs(), aStream, aLogDir.getAbsolutePath(), "  ");
		aStream.write("    </" + tag + ">\n");
	}
}
