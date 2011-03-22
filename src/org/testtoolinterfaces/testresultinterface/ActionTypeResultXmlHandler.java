package org.testtoolinterfaces.testresultinterface;

import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.ParameterArrayList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepCommand;
import org.testtoolinterfaces.testsuite.TestStepSimple;
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

	private static final String COMMAND_ELEMENT = "command";
	private static final String RESULT_ELEMENT = "result";

	private GenericTagAndStringXmlHandler myCommandXmlHandler;
	private GenericTagAndStringXmlHandler myResultXmlHandler;
	private LogFileXmlHandler myLogFileXmlHandler;

	private int myCurrentSequence = 0;
	private String myCommand = "";
	private TestResult.VERDICT myResult = TestResult.UNKNOWN;
	private Hashtable<String, String> myLogFiles = new Hashtable<String, String>();

	public ActionTypeResultXmlHandler( XMLReader anXmlReader, TestStep.StepType aTag )
	{
		super(anXmlReader, aTag.toString());
		Trace.println(Trace.CONSTRUCTOR, "ActionTypeResultXmlHandler( anXmlreader, " + aTag + " )", true);

		myCommandXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMAND_ELEMENT);
		this.addStartElementHandler(COMMAND_ELEMENT, myCommandXmlHandler);
		myCommandXmlHandler.addEndElementHandler(COMMAND_ELEMENT, this);

		myResultXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, RESULT_ELEMENT);
		this.addStartElementHandler(RESULT_ELEMENT, myResultXmlHandler);
		myResultXmlHandler.addEndElementHandler(RESULT_ELEMENT, this);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addStartElementHandler(LogFileXmlHandler.START_ELEMENT, myLogFileXmlHandler);
		myLogFileXmlHandler.addEndElementHandler(LogFileXmlHandler.START_ELEMENT, this);
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
		    		myCurrentSequence = Integer.valueOf( att.getValue(i) ).intValue();
		    		Trace.println( Trace.LEVEL.ALL, "        myCurrentSequence -> " + myCurrentSequence);
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

		TestStep.StepType action = TestStep.StepType.valueOf(this.getStartElement());
		TestStepSimple testStep = new TestStepCommand( action, myCurrentSequence, "", myCommand, "", new ParameterArrayList() );
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
		myCurrentSequence = 0;
	}
}
