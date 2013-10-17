package org.testtoolinterfaces.testresultinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Mark;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <logfile type="...">...</logfile>
 * 
 */
public class LogFileXmlHandler extends GenericTagAndStringXmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(LogFileXmlHandler.class);

    public static final String START_ELEMENT = "logfile";
	public static final String PARAM_TYPE = "type";
	
    private String myType = "";

	public LogFileXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		LOG.trace(Mark.CONSTRUCTOR, "{}", anXmlReader);
	}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
    	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(PARAM_TYPE))
		    	{
		    		myType = att.getValue(i);
		    		LOG.trace(Mark.SUITE, "myType -> {}", myType );
    	    	}
		    }
    	}
    }

	public String getType()
	{
		LOG.trace(Mark.GETTER, "");
		return myType;
	}

	public void reset()
	{
		LOG.trace(Mark.SUITE, "");
    	super.reset();
    	myType = "";
	}
}
