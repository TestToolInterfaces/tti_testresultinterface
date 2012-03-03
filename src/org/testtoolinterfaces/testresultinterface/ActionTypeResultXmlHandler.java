package org.testtoolinterfaces.testresultinterface;

import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;

/**
 * @author Arjan Kranenburg 
 * 
 * <action sequence=...>
 *  <description>
 *  ...
 *  </description>
 *  <command>
 *  ...
 *  </command>
 * </action>
 */
public class ActionTypeResultXmlHandler extends XmlHandler
{
	public static final String PARAM_SEQUENCE = "sequence";
	public static final String PARAM_INTERFACE = "interface";

	private static final String COMMAND_ELEMENT = "command";
	private static final String RESULT_ELEMENT = "result";

	private GenericTagAndStringXmlHandler myCommandXmlHandler;
	private GenericTagAndStringXmlHandler myResultXmlHandler;
	private LogFileXmlHandler myLogFileXmlHandler;

	private TestInterfaceList myInterfaceList;

	private int mySequence = 0;
	private TestInterface myInterface;
	private String myCommand = "";
	private TestResult.VERDICT myResult = TestResult.UNKNOWN;
	private Hashtable<String, String> myLogFiles = new Hashtable<String, String>();

	public ActionTypeResultXmlHandler( XMLReader anXmlReader, String aTag, TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "ActionTypeResultXmlHandler( anXmlreader, " + aTag + " )", true);

		myInterfaceList = anInterfaceList;
		myInterface = anInterfaceList.getInterface( "Unknown" );

		myCommandXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMAND_ELEMENT);
		this.addElementHandler(COMMAND_ELEMENT, myCommandXmlHandler);

		myResultXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, RESULT_ELEMENT);
		this.addElementHandler(RESULT_ELEMENT, myResultXmlHandler);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addElementHandler(LogFileXmlHandler.START_ELEMENT, myLogFileXmlHandler);
}

    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.println(Trace.SUITE, "processElementAttributes( "
				+ aQualifiedName + " )", true );
    	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
		    	if (att.getQName(i).equalsIgnoreCase(PARAM_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.ALL, "        mySequence -> " + mySequence);
    	    	}
		    	else if (att.getQName(i).equalsIgnoreCase(PARAM_INTERFACE))
		    	{
		    		String interfaceName = att.getValue(i);
		    		myInterface = myInterfaceList.getInterface(interfaceName);
		    		Trace.println( Trace.ALL, "        myInterface -> " + myInterface.getInterfaceName());
    	    	}
		    }
    	}
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
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(COMMAND_ELEMENT))
    	{
    		myCommand = myCommandXmlHandler.getValue();
    		myCommandXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(RESULT_ELEMENT))
    	{
    		myResult = TestResult.VERDICT.valueOf( myResultXmlHandler.getValue().toUpperCase() );
    		myResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(LogFileXmlHandler.START_ELEMENT))
    	{
    		myLogFiles.put(myLogFileXmlHandler.getType(), myLogFileXmlHandler.getValue());
    		myLogFileXmlHandler.reset();
    	}
	}

	public TestStepResult getActionStep()
	{
		Trace.println(Trace.SUITE);

		TestStep testStep = new TestStepCommand(       mySequence,
		                                               "", // Description
		                                               myCommand,
		                                               myInterface,
		                                               new ParameterArrayList() );
		TestStepResult testStepResult = new TestStepResult( testStep );
		testStepResult.setResult( myResult );
      	if (!myLogFiles.isEmpty())
      	{
		    for (Enumeration<String> keys = myLogFiles.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	testStepResult.addTestLog(key, myLogFiles.get(key));
		    }
      	}

		return testStepResult;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		mySequence = 0;
	}
}
