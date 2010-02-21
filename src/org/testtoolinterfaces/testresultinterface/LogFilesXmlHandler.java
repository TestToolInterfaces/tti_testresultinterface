package org.testtoolinterfaces.testresultinterface;

import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;

/**
 * @author Arjan Kranenburg 
 * 
 * <logFiles>
 *   <logFile type="...">...</logFile>
 *   <logFile type="...">...</logFile>
 *   ...
 * </logFiles>
 * 
 */
public class LogFilesXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "logFiles";
	
	private Hashtable<String, String> myLogFiles = new Hashtable<String, String>();

	private LogFileXmlHandler myLogFileXmlHandler;

	public LogFilesXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addStartElementHandler(LogFileXmlHandler.START_ELEMENT, myLogFileXmlHandler);
		myLogFileXmlHandler.addEndElementHandler(LogFileXmlHandler.START_ELEMENT, this);
	}

    public void processElementAttributes(String qualifiedName, Attributes att)
    {
    	//nop
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		//nop
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
    	//nop
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(LogFileXmlHandler.START_ELEMENT))
    	{
    		myLogFiles.put(myLogFileXmlHandler.getType(), myLogFileXmlHandler.getValue());
    		myLogFileXmlHandler.reset();
    	}
	}

	/**
	 * @return
	 */
	public Hashtable<String, String> getLogFiles()
	{
		Trace.println(Trace.GETTER);
		return myLogFiles;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		myLogFiles = new Hashtable<String, String>();
	}
}
