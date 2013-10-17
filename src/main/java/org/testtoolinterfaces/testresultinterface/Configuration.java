package org.testtoolinterfaces.testresultinterface;
/**
 * 
 */

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.utils.Mark;

/**
 * @author Arjan Kranenburg
 *
 */
public class Configuration
{
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

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
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}, {}", 
				aRunXslDir, aGroupXslDir, aCaseXslDir);

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
