/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOError;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.testtoolinterfaces.testresult.TestRunResult;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.Warning;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestRunResultXmlWriter implements TestRunResultWriter
{
	private String myTestEnvironment = "Unknown";
	private String myTestPhase = "Unknown";
	private File myBaseLogDir = new File( "" );
	
	private File myFileName;
	private TestGroupResultXmlWriter myTgResultWriter;
	private TestRunResult myRunResult;

	/**
	 * 
	 */
	public TestRunResultXmlWriter( File aFileName,
								   File anXslSourceDir,
								   String anEnvironment,
								   String aTestPhase )
	{
	    Trace.println(Trace.CONSTRUCTOR, "TestRunResultXmlWriter( " + aFileName.getName() + " )", true);
		if (anXslSourceDir == null)
		{
			throw new Error( "No directory specified." );
		}

		if (! anXslSourceDir.isDirectory())
		{
			throw new Error( "Not a directory: " + anXslSourceDir.getAbsolutePath() );
		}

		myFileName = aFileName;
		myTestEnvironment = anEnvironment;
		myTestPhase = aTestPhase;

		File logDir = aFileName.getParentFile();
        if (!logDir.exists())
        {
        	logDir.mkdir();
        }
		copyXsl( anXslSourceDir, logDir );
		myBaseLogDir = logDir;

		myTgResultWriter = new TestGroupResultXmlWriter( myBaseLogDir, 1 );
	}

	@Override
	public void setResult(TestRunResult aRunResult)
	{
		myRunResult = aRunResult;
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultWriter#write(org.testtoolinterfaces.testresultinterface.TestRunResult)
	 */
	public void write()
	{
		if ( myRunResult == null )
		{
			return;
		}

		FileWriter xmlFile;
		try
		{
			xmlFile = new FileWriter( myFileName );

			printXmlHeader( myRunResult, xmlFile, myFileName.getName() );
			printXmlTestRuns( myRunResult, xmlFile );
			
			xmlFile.flush();
		}
		catch (IOException e)
		{
			Warning.println("Saving XML failed: " + e.getMessage());
			Trace.print(Trace.LEVEL.SUITE, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testresultinterface.TestRunResultWriter#intermediateWrite()
	 */
	public void intermediateWrite()
	{
		write();
	}

	/**
	 * @param aFile
	 * @throws IOException
	 */
	public void printXmlHeader (TestRunResult aRunResult, OutputStreamWriter aStream, String aDocName) throws IOException
	{
		aStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		aStream.write("<?xml-stylesheet type=\"text/xsl\" href=\"testLog.xsl\"?>\n\n");

		aStream.write("<!--\n");
		aStream.write("    Document   : " + aDocName + "\n");
		aStream.write("    Created on : " + aRunResult.getStartDateString() + "\n");
		aStream.write("    Author     : " + aRunResult.getAuthor() + "\n");
		aStream.write("    Name       : " + aRunResult.getDisplayName() + "\n");
		aStream.write("-->\n");
	}
	
	/**
	 * @param aFile
	 * @throws IOException
	 */
	public void printXmlTestRuns (TestRunResult aRunResult, OutputStreamWriter aStream) throws IOException
	{
		aStream.write("<testrun");
		aStream.write(" name='" + aRunResult.getDisplayName() + "'");
		aStream.write(" suite='" + aRunResult.getTestSuite() + "'");
		aStream.write(" environment='" + myTestEnvironment + "'");
		aStream.write(" phase='" + myTestPhase + "'");
		aStream.write(" author='" + aRunResult.getAuthor() + "'");
		aStream.write(" machine='" + aRunResult.getMachine() + "'");
		aStream.write(" created='" + aRunResult.getStartDateString() + "'");
		aStream.write(" startdate='" + aRunResult.getStartDateString() + "'");
		aStream.write(" starttime='" + aRunResult.getStartTimeString() + "'");
		aStream.write(" enddate='" + aRunResult.getEndDateString() + "'");
		aStream.write(" endtime='" + aRunResult.getEndTimeString() + "'");
		aStream.write(">\n");
		
		// System Under Test
	    printXmlSut( aRunResult, aStream );
	    
		myTgResultWriter.printXml(aRunResult.getTestGroup(), aStream);
	    
	    aStream.write("</testrun>\n");
	}

	/**
	 * @param aFile
	 * @throws IOException
	 */
	public void printXmlSut (TestRunResult aRunResult, OutputStreamWriter aStream) throws IOException
	{
		if (!aRunResult.getSutProduct().isEmpty())
		{
			aStream.write("    <systemundertest");
			aStream.write(" product='" + aRunResult.getSutProduct() + "'");
			aStream.write(">\n");

			aStream.write("      <version");
			aStream.write(" mainLevel='" + aRunResult.getSutVersionMainLevel() + "'");
			aStream.write(" subLevel='" + aRunResult.getSutVersionSubLevel() + "'");
			aStream.write(" patchLevel='" + aRunResult.getSutVersionPatchLevel() + "'");
			aStream.write(">\n");

	      	// System Under Test logs
			printXmlLogFiles( aRunResult.getSutLogs(), aStream );

	      	aStream.write("      </version>\n");
	      	aStream.write("    </systemundertest>\n");
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
	    Trace.println(Trace.LEVEL.UTIL);
		
      	if (!aLogs.isEmpty())
      	{
      		aStream.write("        <logFiles>\n");
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
		    	aStream.write("          <logFile");
		    	aStream.write(" type='" + key + "'");
		    	aStream.write(">" + logFile);
		    	aStream.write("</logFile>\n");
		    }
  
		    aStream.write("        </logFiles>\n");
      	}
	}

	private void copyXsl(File aSourceDir, File aTargetLogDir)
	{
	    Trace.println(Trace.LEVEL.UTIL, "copyXsl( " + aTargetLogDir.getName() + " )", true);
		if ( aSourceDir == null )
		{
			return;
		}
		
        File[] files = aSourceDir.listFiles();
        for (int i=0; i<files.length; i++)
        {
        	File srcFile = files[i];
        	if (!srcFile.isDirectory())
        	{
            	File tgtFile = new File(aTargetLogDir + File.separator + srcFile.getName());

            	FileChannel inChannel = null;
            	FileChannel outChannel = null;
            	try
            	{
            		inChannel = new FileInputStream(srcFile).getChannel();
            		outChannel = new FileOutputStream(tgtFile).getChannel();
                	inChannel.transferTo(0, inChannel.size(), outChannel);
                } 
                catch (IOException e)
                {
                	throw new IOError( e );
                }
                finally
                {
	                if (inChannel != null) try
					{
						inChannel.close();
					}
					catch (IOException exc)
					{
	                	throw new IOError( exc );
					}
	                if (outChannel != null) try
					{
						outChannel.close();
					}
					catch (IOException exc)
					{
	                	throw new IOError( exc );
					}
	            }
        	}
        }
	}
}
