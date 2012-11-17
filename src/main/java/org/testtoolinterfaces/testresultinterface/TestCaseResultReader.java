package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.io.IOError;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testsuite.LooseTestInterfaceList;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class TestCaseResultReader
{
	private TestInterfaceList myInterfaceList;

	/**
	 * 
	 */
	public TestCaseResultReader( TestInterfaceList anInterfaceList )
	{
		Trace.println(Trace.CONSTRUCTOR);
		myInterfaceList = anInterfaceList;
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestCaseResult readTcResultFile( TestCaseResultLink aTestCaseResultLink )
	{
		Trace.println(Trace.SUITE, "readTcResultFile( " + aTestCaseResultLink.getId() + " )", true);

		return readTcResultFile( aTestCaseResultLink.getLink() );
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestCaseResult readTcResultFile( File aTestCaseResultFile ) {
		Trace.println(Trace.SUITE, "readTcResultFile( " + aTestCaseResultFile.getName() + " )", true);

		TestCaseResult testCaseResult;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestCaseResultXmlHandler handler = new TestCaseResultXmlHandler(xmlReader, myInterfaceList);

			handler.parse(xmlReader, aTestCaseResultFile);
			testCaseResult = handler.getTestCaseResult();
		} catch (Exception e) {
			Trace.print(Trace.SUITE, e);
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
