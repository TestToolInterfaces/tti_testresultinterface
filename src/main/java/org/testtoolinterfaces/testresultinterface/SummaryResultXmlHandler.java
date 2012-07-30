package org.testtoolinterfaces.testresultinterface;

import org.testtoolinterfaces.testresult.ResultSummary;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <summary>
 *  <totaltestcases>...</totaltestcases>
 *  <totalpassed>...</totalpassed>
 *  <totalfailed>...</totalfailed>
 *  <totalunknown>...</totalunknown>
 *  <totalerror>...</totalerror>
 * </summary>
 * 
 */

public class SummaryResultXmlHandler extends XmlHandler
{
	public static final String ELEMENT_START = "summary";

	private static final String ELEMENT_TOTAL = "totaltestcases";
	private static final String ELEMENT_PASSED = "totalpassed";
	private static final String ELEMENT_FAILED = "totalfailed";
	private static final String ELEMENT_UNKNOWN = "totalunknown";
	private static final String ELEMENT_ERROR = "totalerror";

	private int myTotal;
	private int myPassed;
	private int myFailed;
	private int myUnknown;
	private int myError;

	private GenericTagAndStringXmlHandler myTotalXmlHandler;
	private GenericTagAndStringXmlHandler myPassedXmlHandler;
	private GenericTagAndStringXmlHandler myFailedXmlHandler;
	private GenericTagAndStringXmlHandler myUnknownXmlHandler;
	private GenericTagAndStringXmlHandler myErrorXmlHandler;

	public SummaryResultXmlHandler(XMLReader anXmlReader)
	{
		super(anXmlReader, ELEMENT_START);

		reset();

		myTotalXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_TOTAL);
		this.addElementHandler(myTotalXmlHandler);

		myPassedXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_PASSED);
		this.addElementHandler(myPassedXmlHandler);

		myFailedXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_FAILED);
		this.addElementHandler(myFailedXmlHandler);

		myUnknownXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_UNKNOWN);
		this.addElementHandler(myUnknownXmlHandler);

		myErrorXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_ERROR);
		this.addElementHandler(myErrorXmlHandler);
	}

	@Override
	public void handleReturnFromChildElement( String aQualifiedName,
	                                          XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
		if ( ! aChildXmlHandler.getClass().equals(GenericTagAndStringXmlHandler.class) )
		{
			throw new Error( "ChildXmlHandler (" + aChildXmlHandler.getClass().toString() + ") must be of type GenericTagAndStringXmlHandler" );
		}
		GenericTagAndStringXmlHandler childXmlHandler = (GenericTagAndStringXmlHandler) aChildXmlHandler;

		int value = Integer.valueOf( childXmlHandler.getValue() ).intValue();
		aChildXmlHandler.reset();

		if ( value < 0 )
		{
			throw new Error( aQualifiedName + " can not be less than zero: " + value );
		}

		if (aQualifiedName.equalsIgnoreCase(ELEMENT_TOTAL))
    	{
    		myTotal = value;
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_PASSED))
    	{
    		myPassed = value;
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_FAILED))
    	{
    		myFailed = value;
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_UNKNOWN))
    	{
    		myUnknown = value;
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_ERROR))
    	{
    		myError = value;
    	}
	}

	public ResultSummary getSummary()
	{
		Trace.println(Trace.SUITE);

		// We only use 4 of the 5.
		// We derive 'unknown' to make the 5 match, but only if the resulting unknown is positive
		int unknown = myTotal - myPassed - myFailed - myError;
		if ( unknown >= 0 )
		{
			return new ResultSummary( myPassed, myFailed, unknown, myError );
		}

		return new ResultSummary( myPassed, myFailed, myUnknown, myError );
	}

	public void reset()
	{
		Trace.println(Trace.UTIL);
		myTotal = 0;
		myPassed = 0;
		myFailed = 0;
		myUnknown = 0;
		myError = 0;
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

	@Override
	public void processElementAttributes(String qualifiedName, Attributes att)
	{
		// NOP
	}
}
