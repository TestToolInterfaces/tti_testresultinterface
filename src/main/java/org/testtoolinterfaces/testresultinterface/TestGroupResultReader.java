package org.testtoolinterfaces.testresultinterface;

import java.io.IOError;

import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

public class TestGroupResultReader
{
	private TestInterfaceList myInterfaceList;
	/**
	 */
	public TestGroupResultReader( TestInterfaceList anInterfaceList )
	{
		Trace.println(Trace.CONSTRUCTOR);
		myInterfaceList = anInterfaceList;
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestGroupResult readTgResultFile( TestGroupResultLink aTestGroupResultLink ) {
		Trace.println(Trace.SUITE, "readTgResultFile( " + aTestGroupResultLink.getId() + " )", true);

		TestGroupResult testGroupResult;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestGroupResultXmlHandler handler = new TestGroupResultXmlHandler(xmlReader, myInterfaceList);

			handler.parse(xmlReader, aTestGroupResultLink.getLink());
			testGroupResult = handler.getTestGroupResult();
		} catch (Exception e) {
			Trace.print(Trace.SUITE, e);
			throw new IOError( e );
		}
		
		return testGroupResult;
	}
}
