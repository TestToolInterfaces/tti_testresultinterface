package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestCaseImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
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
 *  <description>...</description>
 *  <requirement>...</requirement>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <execute>
 *   ...
 *  </execute>
 *  <restore>
 *   ...
 *  </restore>
 *  <result>...</result>
 *  <logfile>
 *  ...
 *  </logfile>
 *  <comment>...</comment>
 * </testcase>
 */

public class TestCaseResultXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testcase";
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_SEQUENCE = "sequence";
	
	private String myTestCaseId;
	private int mySequence;
	private String myDescription;
	private ArrayList<String> myRequirements;
	private TestResult.VERDICT myResult;
	private String myComment;
	private Hashtable<String, String> myLogFiles;

    private ArrayList<TestStepResult> myPrepareSteps;
    private ArrayList<TestStepResult> myExecutionSteps;
    private ArrayList<TestStepResult> myRestoreSteps;

	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String ELEMENT_REQUIREMENT = "requirement";
	private static final String PREPARE_ELEMENT = "prepare";
	private static final String EXECUTE_ELEMENT = "execute";
	private static final String RESTORE_ELEMENT = "restore";
	private static final String RESULT_ELEMENT = "result";
	private static final String COMMENT_ELEMENT = "comment";

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;
	private TestStepSequenceResultXmlHandler myPrepareResultXmlHandler;
	private TestStepSequenceResultXmlHandler myExecutionResultXmlHandler;
	private TestStepSequenceResultXmlHandler myRestoreResultXmlHandler;
	private GenericTagAndStringXmlHandler myResultXmlHandler;
	private LogFileXmlHandler myLogFileXmlHandler;
	private GenericTagAndStringXmlHandler myCommentXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param aBaseDir the baseDir of the parent script 
	 * @param aBaseLogDir a File Object to the Base log
	 * 
	 * @throws NullPointerException if aBaseLogDir is null
	 */
	public TestCaseResultXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		this.reset();

		myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDescriptionXmlHandler);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_REQUIREMENT);
		this.addElementHandler(myRequirementIdXmlHandler);

		myPrepareResultXmlHandler = new TestStepSequenceResultXmlHandler( anXmlReader,
		                                                                  PREPARE_ELEMENT,
		                                                                  anInterfaceList );
		this.addElementHandler(myPrepareResultXmlHandler);

		myExecutionResultXmlHandler = new TestStepSequenceResultXmlHandler( anXmlReader,
		                                                                    EXECUTE_ELEMENT,
		                                                                    anInterfaceList );
		this.addElementHandler(myExecutionResultXmlHandler);

		myRestoreResultXmlHandler = new TestStepSequenceResultXmlHandler( anXmlReader,
		                                                                  RESTORE_ELEMENT,
		                                                                  anInterfaceList );
		this.addElementHandler(myRestoreResultXmlHandler);

		myResultXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, RESULT_ELEMENT);
		this.addElementHandler(myResultXmlHandler);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addElementHandler(myLogFileXmlHandler);

		myCommentXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, COMMENT_ELEMENT);
		this.addElementHandler(myCommentXmlHandler);
	}
	
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestCaseResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_ID))
		    	{
		        	myTestCaseId = att.getValue(i);
		    	}
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_SEQUENCE))
		    	{
		    		mySequence = Integer.valueOf( att.getValue(i) ).intValue();
		    	}
		    }
    	}
		Trace.println( Trace.SUITE, " )" );
    }
    
    /**
     * @throws SAXParseException 
     */
    public TestCaseResult getTestCaseResult() throws SAXParseException
    {
		Trace.println(Trace.SUITE);

		if ( myTestCaseId.isEmpty() )
		{
			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
		}

		TestCase testCase = new TestCaseImpl( myTestCaseId,
	                                          myDescription,
	                                          0, //sequence
	                                          myRequirements,
       										  null, //prepare
       										  null, //execution
       										  null, //restore
	                                          new Hashtable<String, String>(),   // anyAttributes
       										  new Hashtable<String, String>() ); // anyElements 

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

       	for( TestStepResult tsResult : myPrepareSteps )
       	{
       		testCaseResult.addInitialization(tsResult);
       	}

       	for( TestStepResult tsResult : myExecutionSteps )
       	{
       		testCaseResult.addExecution(tsResult);
       	}

       	for( TestStepResult tsResult : myRestoreSteps )
       	{
       		testCaseResult.addRestore(tsResult);
       	}

		return testCaseResult;
    }

	public int getSequence()
	{
		Trace.println(Trace.GETTER, "getSequence() -> " + mySequence, true);
		return mySequence;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myTestCaseId = "";
		mySequence = 0;
		myDescription = "";
		myRequirements = new ArrayList<String>();
		myResult = TestResult.UNKNOWN;
		myComment = "";

		myLogFiles = new Hashtable<String, String>();

		myPrepareSteps = new ArrayList<TestStepResult>();
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
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_REQUIREMENT))
    	{
    		myRequirements.add( myRequirementIdXmlHandler.getValue() );
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(PREPARE_ELEMENT))
    	{
    		myPrepareSteps = myPrepareResultXmlHandler.getStepSequence();
    		myPrepareResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(EXECUTE_ELEMENT))
    	{
    		myExecutionSteps = myExecutionResultXmlHandler.getStepSequence();
    		myExecutionResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(RESTORE_ELEMENT))
    	{
    		myRestoreSteps = myRestoreResultXmlHandler.getStepSequence() ;
    		myRestoreResultXmlHandler.reset();
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
    	else if (aQualifiedName.equalsIgnoreCase(LogFileXmlHandler.START_ELEMENT))
    	{
    		myLogFiles.put(myLogFileXmlHandler.getType(), myLogFileXmlHandler.getValue());
    		myLogFileXmlHandler.reset();
    	}
	}
}
