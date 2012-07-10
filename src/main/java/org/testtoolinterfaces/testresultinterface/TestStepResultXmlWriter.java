/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.ParameterHash;
import org.testtoolinterfaces.testsuite.ParameterImpl;
import org.testtoolinterfaces.testsuite.ParameterVariable;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author arjan.kranenburg
 *
 */
public class TestStepResultXmlWriter
{
	TestStepResultXmlWriter mySubTestStepResultXmlWriter;

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
		aStream.write("    <teststep" );
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
    	aStream.write("      <displayName>" + aResult.getDisplayName() + "</displayName>\n");

    	printSubTestStep( aResult, aStream, aLogDir );
    	
    	aStream.write("      <result>" + aResult.getResult().toString() + "</result>\n");

    	ParameterArrayList parameters = aResult.getParameters();
    	ArrayList<Parameter> params = parameters.sort();
    	for(int i=0; i<params.size(); i++)
    	{
    		Parameter param = params.get(i);
        	aStream.write("      <parameter id='" + param.getName() + "' " );
    		if (ParameterImpl.class.isInstance(param))
    		{
    			aStream.write( "type='value' sequence='" + param.getIndex() + "'>"
            	               + ((ParameterImpl) param).getValue().toString() );
   			
    		}
    		else if (ParameterVariable.class.isInstance(param))
    		{
            	aStream.write( "type='variable' sequence='" + param.getIndex() + "'>"
            	               + ((ParameterVariable) param).getVariableName() );
    		}
    		else if (ParameterHash.class.isInstance(param))
    		{
    			// TODO print the sub-parameters
            	aStream.write( "type='hash' sequence='" + param.getIndex() + "'>"
            	               + ((ParameterHash) param).size() + " sub-parameters" );
    		}
    		else
    		{
            	aStream.write( "type='unknown' sequence='" + param.getIndex() + "'>" );
    		}
        	aStream.write("</parameter>\n" );
    	}

    	String comment = aResult.getComment();
    	if ( ! comment.isEmpty() ) { aStream.write("      <comment>" + comment + "</comment>\n"); }

    	XmlWriterUtils.printXmlLogFiles(aResult.getLogs(), aStream, aLogDir.getAbsolutePath(), "  ");
		aStream.write("    </teststep>\n");
	}
	
	private void printSubTestStep( TestStepResult aResult,
	    	                       OutputStreamWriter aStream,
	    	                       File aLogDir ) throws IOException
	{
		Trace.println(Trace.UTIL);

		Hashtable<Integer, TestStepResult> subSteps = aResult.getSubSteps();
		if ( subSteps.size() > 0 )
		{
	    	aStream.write("      <substeps>\n");
	    	if ( mySubTestStepResultXmlWriter == null )
	    	{
	    		mySubTestStepResultXmlWriter = new TestStepResultXmlWriter();
	    	}

			for (int key = 0; key < subSteps.size(); key++)
	    	{
				TestStepResult tsResult = subSteps.get(key);
				if ( tsResult != null )
				{
					mySubTestStepResultXmlWriter.printXml(subSteps.get(key), aStream, aLogDir);
				}
	    	}
			
	    	aStream.write("      </substeps>\n");
		}	
	}
}
