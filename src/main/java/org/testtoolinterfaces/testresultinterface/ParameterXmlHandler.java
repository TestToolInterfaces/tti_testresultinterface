package org.testtoolinterfaces.testresultinterface;

import org.testtoolinterfaces.testresult.ParameterResult;
import org.testtoolinterfaces.testsuite.Parameter;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.ParameterHash;
import org.testtoolinterfaces.testsuite.ParameterImpl;
import org.testtoolinterfaces.testsuite.ParameterVariable;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <parameter id=... type="..." sequence="...">
 *   ...
 * </parameter>
 * 
 */

public class ParameterXmlHandler extends GenericTagAndStringXmlHandler
{
	public static final String ELEMENT_START = "parameter";

	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_TYPE = "type";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";

	private String myParameterId;
	private String myType;
	private int mySequence;

	public ParameterXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, ELEMENT_START);

		reset();
	}

	@Override
	public void handleReturnFromChildElement( String aQualifiedName,
											  XmlHandler aChildXmlHandler )
	{
		// nop
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
		    		myParameterId = att.getValue(i);
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

	public ParameterResult getParameterResult()
	{
		Trace.println(Trace.SUITE);
		Parameter param;
		if ( myType.equalsIgnoreCase("value") )
		{
			param = new ParameterImpl( myParameterId, this.getValue(), mySequence );
		}
		else if ( myType.equalsIgnoreCase("variable") )
		{
			param = new ParameterVariable( myParameterId, this.getValue(), mySequence );
		}
		else if ( myType.equalsIgnoreCase("hash") )
		{
			param = new ParameterHash( myParameterId, new ParameterArrayList(), mySequence );
		}
		else
		{
			// unknown, but treat as a simple value
			param = new ParameterImpl( myParameterId, this.getValue(), mySequence );
		}

		ParameterResult paramResult = new ParameterResult(param);

		return paramResult;
	}

	public void reset()
	{
		Trace.println(Trace.UTIL);
		myParameterId = "";
		myType = "";
		mySequence = 0;
	}
}
