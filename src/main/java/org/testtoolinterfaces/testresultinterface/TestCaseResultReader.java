package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.IOError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.SingleResult.VERDICT;
import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.impl.TestCaseResultError;
import org.testtoolinterfaces.testsuite.LooseTestInterfaceList;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class TestCaseResultReader
{
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseResultReader.class);
	private TestInterfaceList myInterfaceList;

	/**
	 * 
	 */
	public TestCaseResultReader( TestInterfaceList anInterfaceList )
	{
		LOG.trace(Mark.CONSTRUCTOR, "{}", anInterfaceList);
		myInterfaceList = anInterfaceList;
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestCaseResult readTcResultFile( TestCaseResultLink aTestCaseResultLink )
	{
		LOG.trace(Mark.SUITE, "{}", aTestCaseResultLink);

		if ( aTestCaseResultLink.getResult() == VERDICT.ERROR ) {
			TestSuiteException tse = new TestSuiteException(aTestCaseResultLink.getComment());
			return new TestCaseResultError(aTestCaseResultLink, tse);
		}
		
		if ( aTestCaseResultLink.getLink() == null ) {
			TestSuiteException tse = new TestSuiteException("Link is not set: " + aTestCaseResultLink.getId());
			return new TestCaseResultError(aTestCaseResultLink, tse);
		}
		return readTcResultFile( aTestCaseResultLink.getLink() );
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestCaseResult readTcResultFile( File aTestCaseResultFile ) {
		LOG.trace(Mark.SUITE, "{}", aTestCaseResultFile);

		TestCaseResult testCaseResult;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestCaseResultXmlHandler handler = new TestCaseResultXmlHandler(xmlReader, myInterfaceList);

			handler.parse(xmlReader, aTestCaseResultFile);
			testCaseResult = handler.getTestCaseResult();
		} catch (Exception e) {
			LOG.trace(Mark.SUITE, "", e);
			throw new IOError( e );
		}
		
		return testCaseResult;
	}

	
	/**
	 * Reads the TestCaseResults.
	 * 
	 * @param aTestInterfaceList
	 * @param tcResultLink
	 * @return a TestCaseResult
	 */
	public static TestCaseResult read( TestInterfaceList aTestInterfaceList, TestCaseResultLink tcResultLink )
	{
		TestCaseResultReader tcResultReader = new TestCaseResultReader( aTestInterfaceList );
		return tcResultReader.readTcResultFile(tcResultLink);
	}

	/**
	 * Reads the TestCaseResults.
	 * 
	 * @param aTestInterfaceList
	 * @param tcResultFile
	 * @return a TestCaseResult
	 */
	public static TestCaseResult read( TestInterfaceList aTestInterfaceList, File tcResultFile )
	{
		TestCaseResultReader tcResultReader = new TestCaseResultReader( aTestInterfaceList );
		return tcResultReader.readTcResultFile(tcResultFile);
	}

	/**
	 * Reads the TestCaseResults without verifying if the commands belonged to an interface
	 * @param tcResultLink
	 * @return a TestCaseResult
	 */
	public static TestCaseResult read( TestCaseResultLink tcResultLink )
	{
		return TestCaseResultReader.read(new LooseTestInterfaceList(), tcResultLink);
	}

	/**
	 * Reads the TestCaseResults without verifying if the commands belonged to an interface
	 * @param tcResultFile
	 * @return a TestCaseResult
	 */
	public static TestCaseResult read( File tcResultFile )
	{
		return TestCaseResultReader.read(new LooseTestInterfaceList(), tcResultFile);
	}
}
