package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testresult.TestResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Arjan Kranenburg 
 * 
 * <testgroup id=... startdate="..." starttime="..." enddate="..." endtime="..." [any]="...">
 *  <description>
 *   ...
 *  </description>
 *  <requirement>
 *   ...
 *  </requirement>
 *  <prepare>
 *   ...
 *  </prepare>
 *  <testcaselink>
 *   ...
 *  </testcaselink>
 *  <restore>
 *   ...
 *  </restore>
 *  <summary>
 *   ...
 *  </summary>
 *  <logfiles>
 *  ...
 *  </logfiles>
 *  <[any]>...</[any]>
 * </testgroup>
 */

public class TestGroupResultXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "testgroup";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_STARTDATE = "startdate";
	private static final String ATTRIBUTE_STARTTIME = "starttime";
	private static final String ATTRIBUTE_ENDDATE   = "enddate";
	private static final String ATTRIBUTE_ENDTIME   = "endtime";

	private static final String ELEMENT_DESCRIPTION = "description";
	private static final String ELEMENT_REQUIREMENT = "requirement";
	private static final String ELEMENT_PREPARE = "prepare";
	private static final String ELEMENT_RESTORE = "restore";

	private String myTestGroupId = "";
	private int myCurrentSequence = 0;
	private TestResult.VERDICT myResult = TestResult.UNKNOWN;
	private String myComment = "";
	private Hashtable<String, String> myLogFiles;

    private ArrayList<TestStepResult> myPrepareSteps;
    private ArrayList<TestCaseResultLink> myTestCaseResultLinks;
    private ArrayList<TestGroupResultLink> myTestGroupResultLinks;
    private ArrayList<TestStepResult> myRestoreSteps;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myRequirementIdXmlHandler;

	private TestStepSequenceResultXmlHandler myPrepareResultXmlHandler;
	private TestCaseResultLinkXmlHandler myTestCaseResultLinkXmlHandler;
	private TestGroupResultLinkXmlHandler myTestGroupResulLinkXmlHandler;
	private TestStepSequenceResultXmlHandler myRestoreResultXmlHandler;
	private SummaryResultXmlHandler mySummaryXmlHandler;
	private LogFilesXmlHandler myLogFilesXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param aBaseDir the baseDir of the parent script 
	 * @param aBaseLogDir a File Object to the Base log
	 * 
	 * @throws NullPointerException if aBaseLogDir is null
	 */
	public TestGroupResultXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

		this.reset();

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_DESCRIPTION);
		this.addStartElementHandler(ELEMENT_DESCRIPTION, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(ELEMENT_DESCRIPTION, this);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_REQUIREMENT);
		this.addStartElementHandler(ELEMENT_REQUIREMENT, myRequirementIdXmlHandler);
		myRequirementIdXmlHandler.addEndElementHandler(ELEMENT_REQUIREMENT, this);

		myPrepareResultXmlHandler = new TestStepSequenceResultXmlHandler(anXmlReader, ELEMENT_PREPARE);
		this.addStartElementHandler(ELEMENT_PREPARE, myPrepareResultXmlHandler);
		myPrepareResultXmlHandler.addEndElementHandler(ELEMENT_PREPARE, this);

		myTestCaseResultLinkXmlHandler = new TestCaseResultLinkXmlHandler(anXmlReader);
		this.addStartElementHandler(TestCaseResultLinkXmlHandler.ELEMENT_START, myTestCaseResultLinkXmlHandler);
		myTestCaseResultLinkXmlHandler.addEndElementHandler(TestCaseResultLinkXmlHandler.ELEMENT_START, this);

		myTestGroupResulLinkXmlHandler = new TestGroupResultLinkXmlHandler(anXmlReader);
		this.addStartElementHandler(TestGroupResultLinkXmlHandler.ELEMENT_START, myTestGroupResulLinkXmlHandler);
		myTestGroupResulLinkXmlHandler.addEndElementHandler(TestGroupResultLinkXmlHandler.ELEMENT_START, this);

		myRestoreResultXmlHandler = new TestStepSequenceResultXmlHandler(anXmlReader, ELEMENT_RESTORE);
		this.addStartElementHandler(ELEMENT_RESTORE, myRestoreResultXmlHandler);
		myRestoreResultXmlHandler.addEndElementHandler(ELEMENT_RESTORE, this);

		mySummaryXmlHandler = new SummaryResultXmlHandler(anXmlReader);
		this.addStartElementHandler(SummaryResultXmlHandler.ELEMENT_START, mySummaryXmlHandler);
		mySummaryXmlHandler.addEndElementHandler(SummaryResultXmlHandler.ELEMENT_START, this);

		myLogFilesXmlHandler = new LogFilesXmlHandler(anXmlReader);
		this.addStartElementHandler(LogFilesXmlHandler.START_ELEMENT, myLogFilesXmlHandler);
		myLogFilesXmlHandler.addEndElementHandler(LogFilesXmlHandler.START_ELEMENT, this);
	}
	
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestGroupResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.LEVEL.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myTestGroupId = att.getValue(i);
		    	}
// TODO startdate, starttime, etc.
		    }
    	}
		Trace.println( Trace.LEVEL.SUITE, " )" );
    }
    
    /**
     * @throws SAXParseException 
     */
    public TestGroupResult getTestGroupResult() throws SAXParseException
    {
		Trace.println(Trace.LEVEL.SUITE);

		if ( myTestGroupId.isEmpty() )
		{
			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
		}

		TestGroup testGroup = new TestGroupImpl( myTestGroupId,
	                                          new Hashtable<String, String>(),
       										  "",
       										  new ArrayList<String>(),   // Requirements are not read.
       										  null,
       										  null,
       										  null,
       										  new Hashtable<String, String>() ); // testSteps are null, but added below

       	TestGroupResult testGroupResult = new TestGroupResult( testGroup );
       	testGroupResult.setResult(myResult);
       	testGroupResult.setComment(myComment);

       	if (!myLogFiles.isEmpty())
      	{
		    for (Enumeration<String> keys = myLogFiles.keys(); keys.hasMoreElements();)
		    {
		    	String key = keys.nextElement();
		    	testGroupResult.addTestLog(key, myLogFiles.get(key));
		    }
      	}

	    for (int key = 0; key < myPrepareSteps.size() ; key++)
	    {
	    	testGroupResult.addInitialization( myPrepareSteps.get(key) );
	    }

	    for (int key = 0; key < myTestGroupResultLinks.size() ; key++)
	    {
	    	testGroupResult.addTestGroup( myTestGroupResultLinks.get(key) );
	    }

	    for (int key = 0; key < myTestCaseResultLinks.size() ; key++)
	    {
	    	testGroupResult.addTestCase( myTestCaseResultLinks.get(key) );
	    }

	    for (int key = 0; key < myRestoreSteps.size() ; key++)
	    {
	    	testGroupResult.addRestore( myRestoreSteps.get(key) );
	    }

		return testGroupResult;
    }

	public int getSequence()
	{
		Trace.println(Trace.GETTER, "getSequence() -> " + myCurrentSequence, true);
		return myCurrentSequence;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myTestGroupId = "";
		myCurrentSequence = 0;
		myResult = TestResult.UNKNOWN;
		myComment = "";

		myLogFiles = new Hashtable<String, String>();

		myPrepareSteps = new ArrayList<TestStepResult>();
		myTestGroupResultLinks = new ArrayList<TestGroupResultLink>();
		myTestCaseResultLinks = new ArrayList<TestCaseResultLink>();
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
		//TODO description, requirements, etc
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(myPrepareResultXmlHandler.getStartElement()))
    	{
    		myPrepareSteps = myPrepareResultXmlHandler.getStepSequence();
    		myPrepareResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(TestCaseResultLinkXmlHandler.ELEMENT_START))
    	{
    		myTestCaseResultLinks.add( myTestCaseResultLinkXmlHandler.getTestCaseResultLink() );
    		myTestCaseResultLinkXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(TestGroupResultLinkXmlHandler.ELEMENT_START))
    	{
    		myTestGroupResultLinks.add( myTestGroupResulLinkXmlHandler.getTestGroupResultLink() );
    		myTestGroupResulLinkXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(myRestoreResultXmlHandler.getStartElement()))
    	{
    		myRestoreSteps = myRestoreResultXmlHandler.getStepSequence();
    		myRestoreResultXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(LogFilesXmlHandler.START_ELEMENT))
    	{
    		myLogFiles = myLogFilesXmlHandler.getLogFiles();
    	}
	}
}
