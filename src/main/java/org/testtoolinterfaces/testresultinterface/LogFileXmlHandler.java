package org.testtoolinterfaces.testresultinterface;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan Kranenburg 
 * 
 * <logfile type="...">...</logfile>
 * 
 */
public class LogFileXmlHandler extends GenericTagAndStringXmlHandler
{
	public static final String START_ELEMENT = "logfile";
	public static final String PARAM_TYPE = "type";
	
    private String myType = "";

	public LogFileXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.println(Trace.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(PARAM_TYPE))
		    	{
		    		myType = att.getValue(i);
		    		Trace.println( Trace.ALL, "        myType -> " + myType );
    	    	}
		    }
    	}
    }

	public String getType()
	{
		Trace.println(Trace.GETTER);
		return myType;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
    	super.reset();
    	myType = "";
	}
}
