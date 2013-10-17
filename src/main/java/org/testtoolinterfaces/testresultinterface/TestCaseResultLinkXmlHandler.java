package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.SingleResult.VERDICT;
import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.impl.TestCaseResultLinkImpl;
import org.testtoolinterfaces.testsuite.TestCaseLink;
import org.testtoolinterfaces.testsuite.impl.TestLinkImpl;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
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
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseResultLinkXmlHandler.class);

    public static final String ELEMENT_START = "testcaselink";

	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";

	private static final String ELEMENT_LINK = "link";
	private static final String ELEMENT_VERDICT = "verdict";

	private String myTestCaseId;
	private String myType;
	private int mySequence=0;

	private File myTcLink;
	private VERDICT myResult;

	private GenericTagAndStringXmlHandler myTestCaseLinkXmlHandler;
	private GenericTagAndStringXmlHandler myVerdictXmlHandler;

	public TestCaseResultLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, ELEMENT_START);
		
		reset();

		myTestCaseLinkXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_LINK);
		this.addElementHandler(myTestCaseLinkXmlHandler);

		myVerdictXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_VERDICT);
		this.addElementHandler(myVerdictXmlHandler);
	}

	@Override
	public void handleReturnFromChildElement( String aQualifiedName,
	                                          XmlHandler aChildXmlHandler )
	{
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, aChildXmlHandler);
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
	public void processElementAttributes(String aQualifiedName, Attributes att)
	{
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);

		if (aQualifiedName.equalsIgnoreCase(TestGroupResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				LOG.trace(Mark.SUITE, "{} = {}", att.getQName(i), att.getValue(i) );
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
	}

	public TestCaseResultLink getTestCaseResultLink()
	{
		LOG.trace(Mark.SUITE, "");
		TestCaseLink tcLink = new TestCaseLink( myTestCaseId,
		                                        mySequence++,
		                                        new TestLinkImpl("unknown", myType) );
		TestCaseResultLink tcResultLink = new TestCaseResultLinkImpl(tcLink, myResult, myTcLink);
		return tcResultLink;
	}

	public void reset()
	{
		LOG.trace(Mark.UTIL, "");
		myTestCaseId = "";
		myType = "";
//		mySequence = 0;

		myTcLink = new File( "unknown" );
		myResult = VERDICT.UNKNOWN;
	}

	@Override
	public void handleCharacters(String aValue)
	{
		// NOP
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
		// NOP
	}

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// NOP
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
		// NOP
	}
}
