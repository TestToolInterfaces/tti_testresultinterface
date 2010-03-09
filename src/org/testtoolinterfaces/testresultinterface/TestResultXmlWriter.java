/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestResult;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

public abstract class TestResultXmlWriter
{
	private int myIndentLevel = 0;
	private String myBaseLogDir = "";
	
	public TestResultXmlWriter(File aBaseLogDir, int anIndentLevel)
	{
	    Trace.println(Trace.CONSTRUCTOR);
		myIndentLevel = anIndentLevel;
		try
		{
			myBaseLogDir = aBaseLogDir.getCanonicalPath();
		}
		catch (IOException e)
		{
			Warning.println( "Cannot relate logfiles to base logdir. Using full path-names" );
		}
	}

	/**
	 * Checks if there are log-files and then prints them in XML format 
	 * 
	 * @param aTestResult   Test Result(s)
	 * @param aStream   	OutputStreamWriter of the stream to print the xml to
	 * 
	 * @throws IOException
	 */
	public void printXmlLogFiles(TestResult aTestResult, OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		Hashtable<String, String> logs = aTestResult.getLogs();
		
      	if (!logs.isEmpty())
      	{
      		aStream.write("        <logFiles>\n");
		    for (Enumeration<String> keys = logs.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	String logFile = (new File(logs.get(key))).getCanonicalPath();
		    	if ( logFile.startsWith(myBaseLogDir) )
		    	{
		    		logFile = logFile.substring(myBaseLogDir.length());
			    	if ( logFile.startsWith(File.separator) )
			    	{
			    		logFile = logFile.substring(File.separator.length());
			    	}
		    	}
	    		logFile = logFile.replace('\\', '/');
		    	aStream.write("          <logFile");
		    	aStream.write(" type='" + key + "'");
		    	aStream.write(">" + logFile);
		    	aStream.write("</logFile>\n");
		    }
  
		    aStream.write("        </logFiles>\n");
      	}
	}

	/**
	 * Checks if there is a comment and then prints it in XML format
	 * 
	 * @param aStream		the OutputStreamWriter of the stream to write the xml to
	 * @param aTestResult	the Test Result(s)
	 * 
	 * @throws IOException
	 */
	protected void printXmlComment(TestResult aTestResult, OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		String comment = aTestResult.getComment();
	    if ( !comment.isEmpty() )
	    {
	    	aStream.write("        <comment>");
	    	aStream.write(comment);
	    	aStream.write("</comment>\n");
	    }
	}

	protected static void printXmlRequirement(OutputStreamWriter aStream, String aRequirementId) throws IOException
	{
	    Trace.println(Trace.UTIL);
    	aStream.write("        <requirementId>");
    	aStream.write(aRequirementId);
    	aStream.write("</requirementId>\n");
	}

	/**
	 * @return the Base Log Dir
	 */
	protected File getBaseLogDir()
	{
		return new File(myBaseLogDir);
	}

	protected int getIndentLevel()
	{
		return myIndentLevel;
	}

}
