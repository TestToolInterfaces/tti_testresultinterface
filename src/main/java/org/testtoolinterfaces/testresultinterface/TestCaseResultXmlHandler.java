package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.SingleResult.VERDICT;
import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testresult.impl.TestCaseResultImpl;
import org.testtoolinterfaces.testsuite.TestCase;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.impl.TestCaseImpl;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

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
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseResultXmlHandler.class);

    public static final String START_ELEMENT = "testcase";
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_SEQUENCE = "sequence";
	
	private String myTestCaseId;
	private int mySequence;
	private String myDescription;
	private ArrayList<String> myRequirements;
	private VERDICT myResult;
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
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anXmlReader, anInterfaceList);

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
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
    	if (aQualifiedName.equalsIgnoreCase(TestCaseResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				LOG.trace(Mark.SUITE, "{} = {}", att.getQName(i), att.getValue(i) );
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
    }
    
    /**
     * @throws SAXParseException 
     */
    public TestCaseResult getTestCaseResult() throws SAXParseException
    {
		LOG.trace(Mark.SUITE, "");

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
       										  null );
		
       	TestCaseResult testCaseResult = new TestCaseResultImpl( testCase );
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
		LOG.trace(Mark.GETTER, "");
		return mySequence;
	}

	public void reset()
	{
		LOG.trace(Mark.SUITE, "");

		myTestCaseId = "";
		mySequence = 0;
		myDescription = "";
		myRequirements = new ArrayList<String>();
		myResult = VERDICT.UNKNOWN;
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
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, aChildXmlHandler);
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
     		myResult = VERDICT.valueOf( myResultXmlHandler.getValue().toUpperCase() );
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
