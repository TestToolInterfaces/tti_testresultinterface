package org.testtoolinterfaces.testresultinterface;
/**
 * 
 */

import java.io.File;

import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan Kranenburg
 *
 */
public class Configuration
{
	private File myRunXslDir;
	private File myGroupXslDir;
	private File myCaseXslDir;

	/**
	 * @param aRunXslDir
	 * @param aGroupXslDir
	 * @param aCaseXslDir
	 */
	public Configuration( File aRunXslDir,
	                      File aGroupXslDir,
	                      File aCaseXslDir )
	{
	    Trace.println(Trace.CONSTRUCTOR);

	    myRunXslDir = aRunXslDir;
	    myGroupXslDir = aGroupXslDir;
	    myCaseXslDir = aCaseXslDir;
	}

	/**
	 * @return the RunXslDir
	 */
	public File getRunXslDir()
	{
		return myRunXslDir;
	}

	/**
	 * @return the GroupXslDir
	 */
	public File getGroupXslDir()
	{
		return myGroupXslDir;
	}

	/**
	 * @return the CaseXslDir
	 */
	public File getCaseXslDir()
	{
		return myCaseXslDir;
	}
}
