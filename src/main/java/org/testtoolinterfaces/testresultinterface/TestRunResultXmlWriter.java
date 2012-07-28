/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestRunResult;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestRunResultXmlWriter implements TestRunResultWriter
{
	private File myXslDir;
	private String myTestEnvironment = "Unknown";
	private String myTestPhase = "Unknown";
	private File myBaseLogDir;

	private File myResultFile;
	
	private TestGroupResultXmlWriter myTgResultWriter;

	/**
	 * 
	 */
	public TestRunResultXmlWriter( Configuration aConfiguration,
	                               String anEnvironment,
	                               String aTestPhase )
	{
	    Trace.println(Trace.CONSTRUCTOR, "TestRunResultXmlWriter( aConfiguration, "
						+ anEnvironment + ", "
						+ aTestPhase + " )", true);
		myXslDir = aConfiguration.getRunXslDir();
		if (myXslDir == null)
		{
			throw new Error( "No directory specified." );
		}
		
		if (! myXslDir.isDirectory())
		{
			throw new Error( "Not a directory: " + myXslDir.getPath() );
		}
		
		myTestEnvironment = anEnvironment;
		myTestPhase = aTestPhase;
		
		myTgResultWriter = new TestGroupResultXmlWriter( aConfiguration );
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultWriter#write(org.testtoolinterfaces.testresultinterface.TestRunResult)
	 */
	public void write( TestRunResult aRunResult, File aFileName )
	{
	    Trace.println(Trace.UTIL);
		myResultFile = aFileName;
		
		if ( aRunResult == null )
		{
			return;
		}
		writeToFile(aRunResult, aFileName);
		
		aRunResult.register(this);
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultObserver#notify()
	 */
	public void notify( TestRunResult aRunResult )
	{
	    Trace.println(Trace.UTIL, "notify( " + aRunResult.getDisplayName() + " )", true);

	    if (myResultFile == null)
		{
			Warning.println("Cannot update a test-run file that is not yet written");
		}
		else
		{
			writeToFile(aRunResult, myResultFile);
		}
	}

	/**
	 * @param aRunResult
	 * @param aFileName
	 */
	private void writeToFile(TestRunResult aRunResult, File aFileName)
	{
		File logDir = aFileName.getParentFile();
        if (!logDir.exists())
        {
        	logDir.mkdir();
        }
		XmlWriterUtils.copyXsl( myXslDir, logDir );
		myBaseLogDir = logDir;

		FileWriter xmlFile;
		try
		{
			xmlFile = new FileWriter( aFileName );

			printXmlHeader( aRunResult, xmlFile, aFileName.getName() );
			printXmlTestRuns( aRunResult, xmlFile );
			
			xmlFile.flush();
		}
		catch (IOException e)
		{
			Warning.println("Saving Test Run Result XML failed: " + e.getMessage());
			Trace.print(Trace.SUITE, e);
		}
	}

	/**
	 * @param aFile
	 * @throws IOException
	 */
	public void printXmlHeader (TestRunResult aRunResult, OutputStreamWriter aStream, String aDocName) throws IOException
	{
		XmlWriterUtils.printXmlDeclaration(aStream, "testrun.xsl");

		aStream.write("<!--\n");
		aStream.write("    Document   : " + aDocName + "\n");
		aStream.write("    Created on : " + aRunResult.getStartDateString() + "\n");
		aStream.write("    Author     : " + aRunResult.getAuthor() + "\n");
		aStream.write("    Name       : " + aRunResult.getDisplayName() + "\n");
		aStream.write("-->\n");
	}
	
	/**
	 * @param aRunResult
	 * @param aStream
	 * @throws IOException
	 */
	public void printXmlTestRuns (TestRunResult aRunResult, OutputStreamWriter aStream) throws IOException
	{
		aStream.write("<testrun\n");
		aStream.write("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		aStream.write("    xsi:noNamespaceSchemaLocation=\"TestResult_Run.xsd\"\n");
		aStream.write("    xsdMain='0'\n");
		aStream.write("    xsdSub='2'\n");
		aStream.write("    xsdPatch='0'\n");
		aStream.write("    name='" + aRunResult.getDisplayName() + "'\n");
		aStream.write("    suite='" + aRunResult.getTestSuite() + "'\n");
		aStream.write("    environment='" + myTestEnvironment + "'\n");
		aStream.write("    phase='" + myTestPhase + "'\n");
		aStream.write("    author='" + aRunResult.getAuthor() + "'\n");
		aStream.write("    machine='" + aRunResult.getMachine() + "'\n");
		aStream.write("    status='" + aRunResult.getStatus() + "'\n");
		aStream.write("    startdate='" + aRunResult.getStartDateString() + "'\n");
		aStream.write("    starttime='" + aRunResult.getStartTimeString() + "'\n");
		aStream.write("    enddate='" + aRunResult.getEndDateString() + "'\n");
		aStream.write("    endtime='" + aRunResult.getEndTimeString() + "'\n");
		aStream.write("  >\n");
		
		// System Under Test
	    printXmlSut( aRunResult, aStream );
	
	    TestGroupResult tgResult = aRunResult.getTestGroup();
	    if ( tgResult != null )
	    {
			myTgResultWriter.printXml(tgResult, aStream, "  ", myBaseLogDir);
	    }
	    
	    aStream.write("</testrun>\n");
	}

	/**
	 * @param aRunResult
	 * @param aStream
	 * @throws IOException
	 */
	public void printXmlSut (TestRunResult aRunResult, OutputStreamWriter aStream) throws IOException
	{
		if (!aRunResult.getSutProduct().isEmpty())
		{
			aStream.write("  <systemundertest");
			aStream.write(" product='" + aRunResult.getSutProduct() + "'");
			aStream.write(">\n");

			aStream.write("    <version");
			aStream.write(" main='" + aRunResult.getSutVersionMainLevel() + "'");
			aStream.write(" sub='" + aRunResult.getSutVersionSubLevel() + "'");
			aStream.write(" patch='" + aRunResult.getSutVersionPatchLevel() + "'");
			aStream.write(">\n");

	      	// System Under Test logs
			printXmlLogFiles( aRunResult.getSutLogs(), aStream );
			XmlWriterUtils.printXmlLogFiles( aRunResult.getSutLogs(), aStream, myBaseLogDir.getAbsolutePath(), "      " );

	      	aStream.write("    </version>\n");
	      	aStream.write("  </systemundertest>\n");
	    }
	}

	/**
	 * Checks if there are log-files and then prints them in XML format 
	 * 
	 * @param aLogs     Hashtable of the logfiles
	 * @param aStream   OutputStreamWriter of the stream to print the xml to
	 * 
	 * @throws IOException
	 */
	public void printXmlLogFiles(Hashtable<String, String> aLogs, OutputStreamWriter aStream) throws IOException
	{
	    Trace.println(Trace.UTIL);
		
      	if (!aLogs.isEmpty())
      	{
		    for (Enumeration<String> keys = aLogs.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	String logFile = (new File(aLogs.get(key))).getCanonicalPath();
		    	String baseLogDir = myBaseLogDir.getCanonicalPath();
		    	if ( logFile.startsWith(baseLogDir) )
		    	{
		    		logFile = logFile.substring(baseLogDir.length());
			    	if ( logFile.startsWith(File.separator) )
			    	{
			    		logFile = logFile.substring(File.separator.length());
			    	}
		    	}
	    		logFile = logFile.replace('\\', '/');
		    	aStream.write("      <logfile");
		    	aStream.write(" id='" + key + "'");
		    	aStream.write(" type='text'");
		    	aStream.write(">" + logFile);
		    	aStream.write("</logfile>\n");
		    }
      	}
	}
}
