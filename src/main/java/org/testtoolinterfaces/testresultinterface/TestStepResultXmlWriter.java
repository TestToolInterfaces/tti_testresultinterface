/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.SingleResult.VERDICT;
import org.testtoolinterfaces.testresult.TestStepCommandResult;
import org.testtoolinterfaces.testresult.TestStepIterationResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testresult.TestStepResultBase;
import org.testtoolinterfaces.testresult.TestStepScriptResult;
import org.testtoolinterfaces.testresult.TestStepSelectionResult;
import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.ParameterHash;
import org.testtoolinterfaces.testsuite.ParameterImpl;
import org.testtoolinterfaces.testsuite.ParameterVariable;
import org.testtoolinterfaces.utils.Mark;

/**
 * @author arjan.kranenburg
 *
 */
public class TestStepResultXmlWriter
{
    private static final Logger LOG = LoggerFactory.getLogger(TestStepResultXmlWriter.class);

    private int myIndentLevel = 0;
	private TestStepResultXmlWriter mySubTestStepResultXmlWriter;

	/**
	 * 
	 */
	public TestStepResultXmlWriter()
	{
		this( 4 );
	}

	/**
	 * @param anIndent
	 */
	public TestStepResultXmlWriter( int anIndentLevel )
	{
		LOG.trace(Mark.CONSTRUCTOR, "{}", anIndentLevel);
		myIndentLevel = anIndentLevel;
	}

	/**
	 * @param aResult	the Test Step Result
	 * @param aStream	the stream to write the test step result in xml-format to
	 * 
	 * @throws IOException 
	 */
	public void printXml( TestStepResultBase aResult,
	                      OutputStreamWriter aStream,
	                      File aLogDir) throws IOException
	{
		LOG.trace(Mark.UTIL, "{}, {}, {}", aResult, aStream, aLogDir);
		String indent = repeat( ' ', myIndentLevel );
		printOpeningTag(aStream, indent, aResult);
    	printStepContent(aResult, aStream, aLogDir, indent);
		aStream.write( indent + "</teststep>\n");
	}

	/**
	 * @param aResult
	 * @param aStream
	 * @param aLogDir
	 * @param indent
	 * @throws IOException
	 */
	private void printStepContent(TestStepResultBase aResult,
			OutputStreamWriter aStream, File aLogDir, String indent)
			throws IOException {
		printDescription(aStream, indent, aResult.getDescription());
    	printDisplayname(aStream, indent, aResult.getDisplayName());

    	if ( aResult instanceof TestStepResult ) {
        	if ( aResult instanceof TestStepCommandResult ) {
            	printCommand(aStream, indent, (TestStepCommandResult) aResult);
        	} 
        	else if ( aResult instanceof TestStepScriptResult ) {
            	printScript(aStream, indent, (TestStepScriptResult) aResult);
        	}
        	else if ( aResult instanceof TestStepSelectionResult ) {
            	printSelection(aStream, indent, (TestStepSelectionResult) aResult, aLogDir);
        	}

        	printSubTestStep( (TestStepResult) aResult, aStream, aLogDir );
        	printParameters(aStream, indent, ((TestStepResult) aResult).getParameters());
    	}
    	else if ( aResult instanceof TestStepIterationResult ) {
        	printIteration(aStream, indent, (TestStepIterationResult) aResult, aLogDir);
    	}

    	printResult(aStream, indent, aResult.getResult());
    	printComment(aStream, indent, aResult.getComment());

    	XmlWriterUtils.printXmlLogFiles(aResult.getLogs(), aStream, aLogDir.getAbsolutePath(), indent );
	}

	private void printIteration(OutputStreamWriter aStream, String indent,
			TestStepIterationResult aResult, File aLogDir) throws IOException {
		aStream.write( indent + "  <foreach>\n");
		aStream.write( indent + "    <item>" + aResult.getItemName() + "</item>\n");
		aStream.write( indent + "    <list>" + aResult.getListName() + "</list>\n");

		Hashtable<Integer, List<TestStepResultBase>> testStepResultListTable = aResult.getTestResultSequenceTable();
		Hashtable<Integer, Object> testValueTable = aResult.getIterationValues();
		Hashtable<Integer, TestStepResult> untilTestStepTable = aResult.getUntilResults();

		int i = 0;
		while ( i < aResult.getSize() ) {
			List<TestStepResultBase> testStepResultList = testStepResultListTable.get(i);
			Object testValue = testValueTable.get(i);
			TestStepResult untilTestStepResult = untilTestStepTable.get(i);

			aStream.write( indent + "    <do itemValue=\"" + testValue.toString() + "\">\n");

	    	Iterator<TestStepResultBase> subStepResultsItr = testStepResultList.iterator();
	    	while (subStepResultsItr.hasNext())
	    	{
	    		TestStepResultBase tsResult = subStepResultsItr.next();
				this.getSubTestStepResultWriter().printXml(tsResult, aStream, aLogDir);
	    	}

			if( untilTestStepResult != null ) {
		    	aStream.write( indent + "      <until>\n");
				this.getSubTestStepResultWriter().printXml(untilTestStepResult, aStream, aLogDir);
				aStream.write( indent + "      </until>\n");
			}

			aStream.write( indent + "    </do>\n");
			i++;
		}

		aStream.write( indent + "  </foreach>\n");
	}

	private void printSelection(OutputStreamWriter aStream, String parentIndent,
			TestStepSelectionResult aResult, File aLogDir) throws IOException {
		String indent = parentIndent + "  ";
    	aStream.write( indent + "<ifstep>\n");
//		this.getSubTestStepResultWriter().printXml(aResult.getIfStepResult(), aStream, aLogDir);
    	printStepContent(aResult.getIfStepResult(), aStream, aLogDir, indent);

		aStream.write( indent + "</ifstep>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param aResult
	 * @throws IOException
	 */
	private void printScript(OutputStreamWriter aStream, String indent,
			TestStepScriptResult aResult) throws IOException {
		aStream.write( indent + "  <script>" + aResult.getScript() + "</script>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param aResult
	 * @throws IOException
	 */
	private void printCommand(OutputStreamWriter aStream, String indent,
			TestStepCommandResult aResult) throws IOException {
		aStream.write( indent + "  <command>" + aResult.getCommand() + "</command>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param comment
	 * @throws IOException
	 */
	private void printComment(OutputStreamWriter aStream, String indent,
			String comment) throws IOException {
		if ( ! comment.isEmpty() ) { aStream.write( indent + "  <comment>" + comment + "</comment>\n"); }
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param aResult
	 * @throws IOException
	 */
	private void printParameters(OutputStreamWriter aStream, String indent,
			ParameterArrayList parameters) throws IOException {
    	ArrayList<Parameter> params = parameters.sort();
    	for(int i=0; i<params.size(); i++)
    	{
    		Parameter param = params.get(i);
        	aStream.write( indent + "  <parameter id='" + param.getName() + "' " );
    		if (ParameterImpl.class.isInstance(param))
    		{
    			aStream.write( "type='value' sequence='" + param.getIndex() + "'>\n" );
            	aStream.write( indent + "    <value>"    			
            	               + ((ParameterImpl) param).getValue().toString()
            	               + "</value>\n" );
   			
    		}
    		else if (ParameterVariable.class.isInstance(param))
    		{
            	aStream.write( "type='variable' sequence='" + param.getIndex() + "'>\n" );
                aStream.write( indent + "    <value>"    			
            	               + ((ParameterVariable) param).getVariableName()
             	               + "</value>\n" );
    		}
    		else if (ParameterHash.class.isInstance(param))
    		{
    			// TODO print the sub-parameters
            	aStream.write( "type='hash' sequence='" + param.getIndex() + "'>\n" );
            	aStream.write( indent + "    <value>"
     	               + ((ParameterHash) param).size() + " sub-parameters"
      	               + "</value>\n" );
    		}
    		else
    		{
            	aStream.write( "type='unknown' sequence='" + param.getIndex() + "'>\n" );
            	aStream.write( indent + "    <value/>\n" );
    		}
        	aStream.write( indent + "  </parameter>\n" );
    	}
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param verdict
	 * @throws IOException
	 */
	private void printResult(OutputStreamWriter aStream, String indent,
			VERDICT verdict) throws IOException {
		aStream.write( indent + "  <result>" + verdict.toString() + "</result>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param displayname
	 * @throws IOException
	 */
	private void printDisplayname(OutputStreamWriter aStream, String indent,
			String displayname) throws IOException {
		aStream.write( indent + "  <displayName>" + displayname + "</displayName>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param description
	 * @throws IOException
	 */
	private void printDescription(OutputStreamWriter aStream, String indent,
			String description) throws IOException {
		aStream.write( indent + "  <description>");
    	aStream.write(description);
    	aStream.write("</description>\n");
	}

	/**
	 * @param aStream
	 * @param indent
	 * @param aResult
	 * @throws IOException
	 */
	private void printOpeningTag(OutputStreamWriter aStream, String indent,
			TestStepResultBase aResult) throws IOException {
		aStream.write( indent + "<teststep" );
		aStream.write(" sequence='" + aResult.getSequenceNr() + "'");
		aStream.write(">\n");
	}
	
	private void printSubTestStep( TestStepResult aResult,
	    	                       OutputStreamWriter aStream,
	    	                       File aLogDir ) throws IOException
	{
		LOG.trace(Mark.UTIL, "{}, {}, {}", aResult, aStream, aLogDir);

		ArrayList<TestStepResultBase> subStepResults = aResult.getSubSteps();
		String indent = repeat( ' ', myIndentLevel + 2 );
		
		if ( subStepResults.size() > 0 )
		{
	    	aStream.write( indent + "<substeps>\n");

	    	Iterator<TestStepResultBase> subStepResultsItr = subStepResults.iterator();
	    	while (subStepResultsItr.hasNext())
	    	{
				TestStepResultBase tsResult = subStepResultsItr.next();
				this.getSubTestStepResultWriter().printXml(tsResult, aStream, aLogDir);
	    	}
			
	    	aStream.write( indent + "</substeps>\n");
		}	
	}

	private TestStepResultXmlWriter getSubTestStepResultWriter() {
		if ( mySubTestStepResultXmlWriter == null )
		{
			mySubTestStepResultXmlWriter = new TestStepResultXmlWriter( myIndentLevel + 4 );
		}
		return mySubTestStepResultXmlWriter;
	}

	private static String repeat(char c,int i)
	{
		String str = "";
		for(int j = 0; j < i; j++)
		{
			str = str+c;
		}
		return str;
	}
}
