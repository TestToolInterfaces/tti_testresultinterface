package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.testtoolinterfaces.testresult.ParameterResult;
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
 * <teststep sequence=...>
 *  <description>
 *  ...
 *  </description>
 *  <command>
 *  ...
 *  </command>
 *  <substeps>
 *  ...
 *  </substeps>
 * </action>
 */
public class ActionTypeResultXmlHandler extends XmlHandler
{
	public static final String PARAM_SEQUENCE = "sequence";
	public static final String PARAM_INTERFACE = "interface";

	private static final String COMMAND_ELEMENT = "command";
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String DISPLAYNAME_ELEMENT = "displayName";
	private static final String RESULT_ELEMENT = "result";
	private static final String COMMENT_ELEMENT = "comment";

	private static final String SUB_STEPS_ELEMENT = "substeps";

	private GenericTagAndStringXmlHandler myCommandXmlHandler;
	private GenericTagAndStringXmlHandler myDesccriptionXmlHandler;
	private GenericTagAndStringXmlHandler myDisplayNameXmlHandler;
	private GenericTagAndStringXmlHandler myResultXmlHandler;
	private GenericTagAndStringXmlHandler myCommentXmlHandler;
	private ParameterXmlHandler myParameterResultXmlHandler;
	private LogFileXmlHandler myLogFileXmlHandler;
	private TestStepSequenceResultXmlHandler mySubstepResultXmlHandler;

	private TestInterfaceList myInterfaceList;

	private int mySequence;
	private TestInterface myInterface;
	private String myCommand;
	private String myDescription;
	private String myDisplayName;
	private String myComment;
	private TestResult.VERDICT myResult;
	private ParameterArrayList myParameters;
	private ArrayList<ParameterResult> myParameterResults;
	private Hashtable<String, String> myLogFiles = new Hashtable<String, String>();
	private ArrayList<TestStepResult> mySubStepResults = new ArrayList<TestStepResult>();

	public ActionTypeResultXmlHandler( XMLReader anXmlReader, String aTag, TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "ActionTypeResultXmlHandler( anXmlreader, " + aTag + " )", true);

		myInterfaceList = anInterfaceList;

		myCommandXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMAND_ELEMENT);
		this.addElementHandler(myCommandXmlHandler);

		myDesccriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDesccriptionXmlHandler);

		myDisplayNameXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DISPLAYNAME_ELEMENT);
		this.addElementHandler(myDisplayNameXmlHandler);

		myResultXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, RESULT_ELEMENT);
		this.addElementHandler(myResultXmlHandler);
		
		myCommentXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMENT_ELEMENT);
		this.addElementHandler(myCommentXmlHandler);
		
		myParameterResultXmlHandler = new ParameterXmlHandler(anXmlReader);
		this.addElementHandler(myParameterResultXmlHandler);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addElementHandler(myLogFileXmlHandler);
		
		mySubstepResultXmlHandler = null; // Created when needed to prevent loops
		
		reset();
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
     	if ( mySubstepResultXmlHandler == null && aQualifiedName.equalsIgnoreCase(SUB_STEPS_ELEMENT) )
     	{
     		// We'll create a TestStepSequenceResultXmlHandler for Sub steps only when we need it.
     		// Otherwise it would create an endless loop.
     		mySubstepResultXmlHandler = new TestStepSequenceResultXmlHandler( this.getXmlReader(), SUB_STEPS_ELEMENT, myInterfaceList );
   			this.addElementHandler(mySubstepResultXmlHandler);
    	}
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
    	else if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription = myDesccriptionXmlHandler.getValue();
    		myDesccriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(DISPLAYNAME_ELEMENT))
    	{
    		myDisplayName = myDisplayNameXmlHandler.getValue();
    		myDisplayNameXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(RESULT_ELEMENT))
    	{
    		myResult = TestResult.VERDICT.valueOf( myResultXmlHandler.getValue().toUpperCase() );
    		myResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(COMMENT_ELEMENT))
    	{
    		myComment = myCommentXmlHandler.getValue();
    		myCommentXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ParameterXmlHandler.ELEMENT_START))
    	{
    		ParameterResult paramResult = myParameterResultXmlHandler.getParameterResult();
    		myParameterResults.add(paramResult);
    		myParameters.add(paramResult.getParameter());
    		myParameterResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(LogFileXmlHandler.START_ELEMENT))
    	{
    		myLogFiles.put(myLogFileXmlHandler.getType(), myLogFileXmlHandler.getValue());
    		myLogFileXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(SUB_STEPS_ELEMENT))
    	{
    		mySubStepResults = mySubstepResultXmlHandler.getStepSequence();
    		mySubstepResultXmlHandler.reset();
    	}
	}

	public TestStepResult getActionStep()
	{
		Trace.println(Trace.SUITE);

		TestStep testStep = new TestStepCommand( mySequence,
												 myDescription,
		                                         myCommand,
		                                         myInterface,
		                                         myParameters );
		TestStepResult testStepResult = new TestStepResult( testStep );
		testStepResult.setDisplayName(myDisplayName);
		testStepResult.setResult( myResult );
		testStepResult.setParameterResults(myParameterResults);
		testStepResult.setComment(myComment);
      	if (!myLogFiles.isEmpty())
      	{
		    for (Enumeration<String> keys = myLogFiles.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	testStepResult.addTestLog(key, myLogFiles.get(key));
		    }
      	}
      	Iterator<TestStepResult> subStepItr = mySubStepResults.iterator();
      	while ( subStepItr.hasNext() )
      	{
      		testStepResult.addSubStep(subStepItr.next());
      	}
      	
		return testStepResult;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		myCommand = "";
		mySequence = 0;
		myInterface = myInterfaceList.getInterface( "Unknown" );

		myDescription = "";
		myDisplayName = "";
		myComment = "";
		myResult = TestResult.UNKNOWN;
		myParameters = new ParameterArrayList();
		myParameterResults = new ArrayList<ParameterResult>();

		myLogFiles = new Hashtable<String, String>();
		mySubStepResults = new ArrayList<TestStepResult>();
	}
}
