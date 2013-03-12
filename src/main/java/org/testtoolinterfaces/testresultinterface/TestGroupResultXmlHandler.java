package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResultLink;
import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultLink;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testresult.impl.TestGroupResultImpl;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
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
 *  <logfile>
 *  ...
 *  </logfile>
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

	private String myTestGroupId;
	private int myCurrentSequence;
	private String myDescription;
	private ArrayList<String> myRequirements;
	private String myComment;
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
	private LogFileXmlHandler myLogFileXmlHandler;

	/**
	 * @param anXmlReader the xmlReader
	 * @param aBaseDir the baseDir of the parent script 
	 * @param aBaseLogDir a File Object to the Base log
	 * 
	 * @throws NullPointerException if aBaseLogDir is null
	 */
	public TestGroupResultXmlHandler( XMLReader anXmlReader, TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);

		this.reset();

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_DESCRIPTION);
		this.addElementHandler(myDescriptionXmlHandler);

     	myRequirementIdXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ELEMENT_REQUIREMENT);
		this.addElementHandler(myRequirementIdXmlHandler);

		myPrepareResultXmlHandler = new TestStepSequenceResultXmlHandler(anXmlReader, ELEMENT_PREPARE, anInterfaceList);
		this.addElementHandler(myPrepareResultXmlHandler);

		myTestCaseResultLinkXmlHandler = new TestCaseResultLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestCaseResultLinkXmlHandler);

		myTestGroupResulLinkXmlHandler = new TestGroupResultLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestGroupResulLinkXmlHandler);

		myRestoreResultXmlHandler = new TestStepSequenceResultXmlHandler(anXmlReader, ELEMENT_RESTORE, anInterfaceList);
		this.addElementHandler(myRestoreResultXmlHandler);

		mySummaryXmlHandler = new SummaryResultXmlHandler(anXmlReader);
		this.addElementHandler(mySummaryXmlHandler);

		myLogFileXmlHandler = new LogFileXmlHandler(anXmlReader);
		this.addElementHandler(myLogFileXmlHandler);
	}
	
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
    	if (aQualifiedName.equalsIgnoreCase(TestGroupResultXmlHandler.START_ELEMENT))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.print( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_ID))
		    	{
		        	myTestGroupId = att.getValue(i);
		    	}
// TODO startdate, starttime, etc.
		    }
    	}
		Trace.println( Trace.SUITE, " )" );
    }
    
    /**
     * @throws SAXParseException 
     */
    public TestGroupResult getTestGroupResult() throws SAXParseException
    {
		Trace.println(Trace.SUITE);

		if ( myTestGroupId.isEmpty() )
		{
			throw new SAXParseException("Unknown TestCase ID", new LocatorImpl());
		}

		TestGroup testGroup = new TestGroupImpl( myTestGroupId,
	                                          myDescription,
	                                          0,
	                                          myRequirements,
       										  null,
       										  null,
       										  null);
		
       	TestGroupResult testGroupResult = new TestGroupResultImpl( testGroup );
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
	    	testGroupResult.addTestExecItemResultLink( myTestGroupResultLinks.get(key) );
	    }

	    for (int key = 0; key < myTestCaseResultLinks.size() ; key++)
	    {
	    	testGroupResult.addTestExecItemResultLink( myTestCaseResultLinks.get(key) );
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
		myDescription = "";
		myRequirements = new ArrayList<String>();
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
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(ELEMENT_DESCRIPTION))
    	{
    		myDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_REQUIREMENT))
    	{
    		myRequirements.add( myRequirementIdXmlHandler.getValue() );
    		myRequirementIdXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_PREPARE))
    	{
    		myPrepareSteps = myPrepareResultXmlHandler.getStepSequence();
    		myPrepareResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestCaseResultLinkXmlHandler.ELEMENT_START))
    	{
    		myTestCaseResultLinks.add( myTestCaseResultLinkXmlHandler.getTestCaseResultLink() );
    		myTestCaseResultLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestGroupResultLinkXmlHandler.ELEMENT_START))
    	{
    		myTestGroupResultLinks.add( myTestGroupResulLinkXmlHandler.getTestGroupResultLink() );
    		myTestGroupResulLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELEMENT_RESTORE))
    	{
    		myRestoreSteps = myRestoreResultXmlHandler.getStepSequence();
    		myRestoreResultXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(LogFileXmlHandler.START_ELEMENT))
    	{
    		myLogFiles.put(myLogFileXmlHandler.getType(), myLogFileXmlHandler.getValue());
    		myLogFileXmlHandler.reset();
    	}
	}
}
