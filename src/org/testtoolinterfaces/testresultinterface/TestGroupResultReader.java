package org.testtoolinterfaces.testresultinterface;

import java.io.IOError;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.utils.Trace;
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
	public TestGroupResult readTgResultFile( TestGroupResultLink aTestGroupResultLink )
	{
		Trace.println(Trace.SUITE, "readTgResultFile( " + aTestGroupResultLink.getId() + " )", true);

		// create a parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
        TestGroupResult testGroupResult;
		try
		{
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a handler
			TestGroupResultXmlHandler handler = new TestGroupResultXmlHandler(xmlReader, myInterfaceList);

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

	        // parse the document
	        xmlReader.parse(aTestGroupResultLink.getLink().getAbsolutePath());
	        
	        testGroupResult = handler.getTestGroupResult();
		}
		catch (Exception e)
		{
			Trace.print(Trace.SUITE, e);
			throw new IOError( e );
		}

		return testGroupResult;
	}
}
