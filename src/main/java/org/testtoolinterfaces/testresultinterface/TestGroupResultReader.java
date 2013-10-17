package org.testtoolinterfaces.testresultinterface;

import java.io.IOError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.impl.TestCaseImpl;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class TestGroupResultReader
{
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseImpl.class);

    private TestInterfaceList myInterfaceList;
	/**
	 */
	public TestGroupResultReader( TestInterfaceList anInterfaceList )
	{
		LOG.trace(Mark.CONSTRUCTOR, "{}", anInterfaceList);
		myInterfaceList = anInterfaceList;
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestGroupResult readTgResultFile( TestGroupResultLink aTestGroupResultLink ) {
		LOG.trace(Mark.SUITE, "{}", aTestGroupResultLink);

		TestGroupResult testGroupResult;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestGroupResultXmlHandler handler = new TestGroupResultXmlHandler(xmlReader, myInterfaceList);

			handler.parse(xmlReader, aTestGroupResultLink.getLink());
			testGroupResult = handler.getTestGroupResult();
		} catch (Exception e) {
			LOG.trace(Mark.SUITE, "", e);
			throw new IOError( e );
		}
		
		return testGroupResult;
	}
}
