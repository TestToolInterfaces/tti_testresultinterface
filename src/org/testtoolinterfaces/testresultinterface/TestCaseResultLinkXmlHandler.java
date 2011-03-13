package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.TestResult.VERDICT;
import org.testtoolinterfaces.testsuite.TestCaseLink;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <testcaselink id=... type="..." sequence="...">
 *  <link>
 *   ...
 *  </link>
 *  <verdict>
 *   ...
 *  </verdict>
 * </testcaselink>
 * 
 */

public class TestCaseResultLinkXmlHandler extends XmlHandler
{
	public static final String ELEMENT_START = "testcaselink";

	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";

	private static final String ELEMENT_LINK = "link";
	private static final String ELEMENT_VERDICT = "verdict";

	private String myTestCaseId;
	private String myType;
	private int mySequence;

	private File myTcLink;
	private VERDICT myResult;

	private GenericTagAndStringXmlHandler myTestCaseLinkXmlHandler;
	private GenericTagAndStringXmlHandler myVerdictXmlHandler;

	public TestCaseResultLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, ELEMENT_START);
		
		reset();

		myTestCaseLinkXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_LINK);
		this.addStartElementHandler(ELEMENT_LINK, myTestCaseLinkXmlHandler);
		myTestCaseLinkXmlHandler.addEndElementHandler(ELEMENT_LINK, this);

		myVerdictXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_VERDICT);
		this.addStartElementHandler(ELEMENT_VERDICT, myVerdictXmlHandler);
		myVerdictXmlHandler.addEndElementHandler(ELEMENT_VERDICT, this);
	}

	@Override
	public void handleReturnFromChildElement( String aQualifiedName,
	                                          XmlHandler aChildXmlHandler )
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(ELEMENT_LINK))
    	{
    		myTcLink = new File( myTestCaseLinkXmlHandler.getValue() );
    		myTestCaseLinkXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(ELEMENT_VERDICT))
    	{
    		myResult = VERDICT.valueOf( myVerdictXmlHandler.getValue().toUpperCase() );
    		myVerdictXmlHandler.reset();
    	}
	}

	@Override
	public void processElementAttributes(String aQualifiedName, Attributes att)	throws SAXParseException
	{
		Trace.print(Trace.SUITE, "processElementAttributes( " + aQualifiedName + ", attributes )", true );

		if (aQualifiedName.equalsIgnoreCase(TestGroupResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.LEVEL.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		    		myTestCaseId = att.getValue(i);
		    	}
		    	else if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
		    		myType = att.getValue(i);
		    	}
		    	else if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    	}
		    }
    	}

    	Trace.println( Trace.LEVEL.SUITE, " )" );
	}

	public TestCaseResultLink getTestCaseResultLink()
	{
		Trace.println(Trace.SUITE);
		TestCaseLink tcLink = new TestCaseLink( myTestCaseId, myType, mySequence, new File("unknown"), new Hashtable<String, String>() );
		TestCaseResultLink tcResultLink = new TestCaseResultLink(tcLink, myResult, myTcLink);
		return tcResultLink;
	}

	public void reset()
	{
		Trace.println(Trace.UTIL);
		myTestCaseId = "";
		myType = "";
		mySequence = 0;

		myTcLink = new File( "unknown" );
		myResult = VERDICT.UNKNOWN;
	}

	@Override
	public void handleCharacters(String aValue) throws SAXParseException
	{
		// NOP
	}

	@Override
	public void handleEndElement(String aQualifiedName)
														throws SAXParseException
	{
		// NOP
	}

	@Override
	public void handleGoToChildElement(String aQualifiedName)
																throws SAXParseException
	{
		// NOP
	}

	@Override
	public void handleStartElement(String aQualifiedName) throws SAXParseException
	{
		// NOP
	}
}
