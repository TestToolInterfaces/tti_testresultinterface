package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestCaseImpl;
import org.testtoolinterfaces.testsuite.TestStep;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;

/**
 * @author Arjan Kranenburg 
 * 
 * <testcase id=... sequence=...>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <execution>
 *   ...
 *  </execution>
 *  <restore>
 *   ...
 *  </restore>
 *  <result>...</result>
 *  <logfiles>
 *  ...
 *  </logfiles>
 *  <comment>...</comment>
 * </testcase>
 */

public class TestCaseResultXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testcase";
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_SEQUENCE = "sequence";
	
	private String myCurrentTestCaseId = "";
	private int myCurrentSequence = 0;
	private TestResult.VERDICT myResult = TestResult.UNKNOWN;
	private String myComment = "";
	private Hashtable<String, String> myLogFiles;

    private ArrayList<TestStepResult> myInitializationSteps;
    private ArrayList<TestStepResult> myExecutionSteps;
    private ArrayList<TestStepResult> myRestoreSteps;

	private static final String RESULT_ELEMENT = "result";
	private static final String COMMENT_ELEMENT = "comment";

	private ActionTypeResultXmlHandler myInitializeResultXmlHandler;
	private ExecutionResultXmlHandler myExecutionResultXmlHandler;
	private ActionTypeResultXmlHandler myRestoreResultXmlHandler;
	private GenericTagAndStringXmlHandler myResultXmlHandler;
	private LogFilesXmlHandler myLogFilesXmlHandler;
	private GenericTagAndStringXmlHandler myCommentXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param aBaseDir the baseDir of the parent script 
	 * @param aBaseLogDir a File Object to the Base log
	 * 
	 * @throws NullPointerException if aBaseLogDir is null
	 */
	public TestCaseResultXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

		this.reset();

		myInitializeResultXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.StepType.action);
		this.addStartElementHandler(TestStep.StepType.action.toString(), myInitializeResultXmlHandler);
		myInitializeResultXmlHandler.addEndElementHandler(TestStep.StepType.action.toString(), this);

		myExecutionResultXmlHandler = new ExecutionResultXmlHandler(anXmlReader);
		this.addStartElementHandler(ExecutionResultXmlHandler.START_ELEMENT, myExecutionResultXmlHandler);
		myExecutionResultXmlHandler.addEndElementHandler(ExecutionResultXmlHandler.START_ELEMENT, this);

		myRestoreResultXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.StepType.action);
		this.addStartElementHandler(TestStep.StepType.action.toString(), myRestoreResultXmlHandler);
		myRestoreResultXmlHandler.addEndElementHandler(TestStep.StepType.action.toString(), this);

		myResultXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, RESULT_ELEMENT);
		this.addStartElementHandler(RESULT_ELEMENT, myResultXmlHandler);
		myResultXmlHandler.addEndElementHandler(RESULT_ELEMENT, this);

		myLogFilesXmlHandler = new LogFilesXmlHandler(anXmlReader);
		this.addStartElementHandler(LogFilesXmlHandler.START_ELEMENT, myLogFilesXmlHandler);
		myLogFilesXmlHandler.addEndElementHandler(LogFilesXmlHandler.START_ELEMENT, this);

		myCommentXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMENT_ELEMENT);
		this.addStartElementHandler(COMMENT_ELEMENT, myCommentXmlHandler);
		myCommentXmlHandler.addEndElementHandler(COMMENT_ELEMENT, this);
	}
	
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.LEVEL.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestCaseResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.LEVEL.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_ID))
		    	{
		        	myCurrentTestCaseId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_SEQUENCE))
		    	{
		    		myCurrentSequence = Integer.valueOf( att.getValue(i) ).intValue();
		    	}
		    }
    	}
		Trace.println( Trace.LEVEL.SUITE, " )" );
    }
    
    /**
     * @throws SAXParseException 
     */
    public TestCaseResult getTestCaseResult() throws SAXParseException
    {
		Trace.println(Trace.LEVEL.SUITE);

		if ( myCurrentTestCaseId.isEmpty() )
		{
			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
		}

		TestCase testCase = new TestCaseImpl( myCurrentTestCaseId,
	                                          new Hashtable<String, String>(),
       										  "",
       										  new ArrayList<String>(),   // Requirements are not read.
       										  null,
       										  null,
       										  null,
       										  new Hashtable<String, String>() ); // testSteps are null, but added below

//		String aTestCaseId,
//        Hashtable<String, String> anAnyAttributes,
//		String aDescription,
//		ArrayList<String> aRequirementIds,
//		TestStepArrayList aPrepareSteps,
//		TestStepArrayList anExecutionSteps,
//		TestStepArrayList aRestoreSteps,
//		Hashtable<String, String> anAnyElements )

       	TestCaseResult testCaseResult = new TestCaseResult( testCase );
       	testCaseResult.setResult(myResult);
       	testCaseResult.setComment(myComment);

       	if (!myLogFiles.isEmpty())
      	{
		    for (Enumeration<String> keys = myLogFiles.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	testCaseResult.addTestLog(key, myLogFiles.get(key));
		    }
      	}

	    for (int key = 0; key < myInitializationSteps.size() ; key++)
	    {
	    	testCaseResult.addInitialization( myInitializationSteps.get(key) );
	    }

	    for (int key = 0; key < myExecutionSteps.size() ; key++)
	    {
	    	testCaseResult.addExecution( myExecutionSteps.get(key) );
	    }

	    for (int key = 0; key < myRestoreSteps.size() ; key++)
	    {
	    	testCaseResult.addRestore( myRestoreSteps.get(key) );
	    }

		return testCaseResult;
    }

	public int getSequence()
	{
		Trace.println(Trace.GETTER, "getSequence() -> " + myCurrentSequence, true);
		return myCurrentSequence;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myCurrentTestCaseId = "";
		myCurrentSequence = 0;
		myResult = TestResult.UNKNOWN;
		myComment = "";

		myLogFiles = new Hashtable<String, String>();

		myInitializationSteps = new ArrayList<TestStepResult>();
		myExecutionSteps = new ArrayList<TestStepResult>();
		myRestoreSteps = new ArrayList<TestStepResult>();
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

	/** 
	 * @param aQualifiedName the name of the childElement
	 */
	public void handleGoToChildElement(String aQualifiedName)
	{
		//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(myInitializeResultXmlHandler.getStartElement()))
    	{
    		myInitializationSteps.add( myInitializeResultXmlHandler.getActionStep() );
    		myInitializeResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(ExecutionResultXmlHandler.START_ELEMENT))
    	{
    		myExecutionSteps = myExecutionResultXmlHandler.getExecutionSteps();
    		myExecutionResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(myRestoreResultXmlHandler.getStartElement()))
    	{
    		myInitializationSteps.add( myRestoreResultXmlHandler.getActionStep() );
    		myRestoreResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(RESULT_ELEMENT))
    	{
     		myResult = TestResult.VERDICT.valueOf( myResultXmlHandler.getValue().toUpperCase() );
     		myResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(COMMENT_ELEMENT))
    	{
    		myComment = myCommentXmlHandler.getValue();
    		myCommentXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(LogFilesXmlHandler.START_ELEMENT))
    	{
    		myLogFiles = myLogFilesXmlHandler.getLogFiles();
    	}
	}
}
