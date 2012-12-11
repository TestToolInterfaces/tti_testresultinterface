package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.ResultSummary;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testsuite.TestGroupLink;
import org.testtoolinterfaces.testsuite.TestLinkImpl;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <testgrouplink id=... type="..." sequence="...">
 *  <link>
 *   ...
 *  </link>
 *  <summary>
 *   ...
 *  </summary>
 * </testgrouplink>
 * 
 */

public class TestGroupResultLinkXmlHandler extends XmlHandler
{
	public static final String ELEMENT_START = "testgrouplink";

	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";

	private static final String ELEMENT_LINK = "link";

	private String myTestGroupId;
	private String myType;
	private int mySequence=0;

	private File myTgResultLink;
	private ResultSummary myResult;

	private GenericTagAndStringXmlHandler myTestGroupLinkXmlHandler;
	private SummaryResultXmlHandler mySummaryXmlHandler;

	public TestGroupResultLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, ELEMENT_START);

		reset();

		myTestGroupLinkXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_LINK);
		this.addElementHandler(myTestGroupLinkXmlHandler);

		mySummaryXmlHandler = new SummaryResultXmlHandler(anXmlReader);
		this.addElementHandler(mySummaryXmlHandler);
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName,
												XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(ELEMENT_LINK))
    	{
    		myTgResultLink = new File( myTestGroupLinkXmlHandler.getValue() );
    		myTestGroupLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(SummaryResultXmlHandler.ELEMENT_START))
    	{
    		myResult = mySummaryXmlHandler.getSummary();
    		mySummaryXmlHandler.reset();
    	}
	}

	@Override
	public void processElementAttributes(String aQualifiedName, Attributes att)
	{
		Trace.print(Trace.SUITE, "processElementAttributes( " + aQualifiedName + ", attributes )", true );

		if (aQualifiedName.equalsIgnoreCase(TestGroupResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		    		myTestGroupId = att.getValue(i);
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

    	Trace.println( Trace.SUITE, " )" );
	}

	public TestGroupResultLink getTestGroupResultLink()
	{
		Trace.println(Trace.SUITE);
		TestGroupLink tgLink = new TestGroupLink( myTestGroupId,
		                                          mySequence++,
		                                          new TestLinkImpl("unknown", myType),
		                                          new Hashtable<String, String>(),
		                                          new Hashtable<String, String>() );
		TestGroupResultLink tgResultLink = new TestGroupResultLink(tgLink, myResult, myTgResultLink);
		return tgResultLink;
	}

	public void reset()
	{
		Trace.println(Trace.UTIL);
		myTestGroupId = "";
		myType = "";
//		mySequence = 0;

		myTgResultLink = new File( "unknown" );
		myResult = new ResultSummary(0, 0, 0, 0);
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
