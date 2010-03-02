/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestResult;

import org.testtoolinterfaces.utils.Trace;

public abstract class TestResultXmlWriter
{
	private TestResult myResult;
//	private int myIndentLevel = 0;
	
	public TestResultXmlWriter( TestResult aResult, int anIndentLevel )
	{
	    Trace.println(Trace.CONSTRUCTOR);
		myResult = aResult;
//		myIndentLevel = anIndentLevel;
	}

	/**
	 * Checks if there are log-files and then prints them in XML format 
	 * 
	 * @param aStream   OutputStreamWriter of the stream to print the xml to
	 * @param aLogs     Hashtable of the logfiles
	 * 
	 * @throws IOException
	 */
	public void printXmlLogFiles(OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		Hashtable<String, String> logs = myResult.getLogs();
		
      	if (!logs.isEmpty())
      	{
      		aStream.write("        <logFiles>\n");
		    for (Enumeration<String> keys = logs.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	aStream.write("          <logFile");
		    	aStream.write(" type='" + key + "'");
		    	aStream.write(">" + logs.get(key));
		    	aStream.write("</logFile>\n");
		    }
  
		    aStream.write("        </logFiles>\n");
      	}
	}

	/**
	 * Checks if there is a comment and then prints it in XML format
	 * 
	 * @param aStream   OutputStreamWriter of the stream to print the xml to
	 * @param aLogs     the logfiles
	 * 
	 * @throws IOException
	 */
	public void printXmlComment(OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		String comment = myResult.getComment();
	    if ( !comment.isEmpty() )
	    {
	    	aStream.write("        <comment>");
	    	aStream.write(comment);
	    	aStream.write("</comment>\n");
	    }
	}

	public static void printXmlRequirement(OutputStreamWriter aStream, String aRequirementId) throws IOException
	{
	    Trace.println(Trace.UTIL);
    	aStream.write("        <requirementId>");
    	aStream.write(aRequirementId);
    	aStream.write("</requirementId>\n");
	}

	/**
	 * @return the Result
	 */
	protected TestResult getResult()
	{
		return myResult;
	}
}
