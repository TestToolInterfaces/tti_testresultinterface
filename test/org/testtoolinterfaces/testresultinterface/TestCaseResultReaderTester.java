package org.testtoolinterfaces.testresultinterface;


import java.io.File;
import java.net.URISyntaxException;
import java.util.Hashtable;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.LooseTestInterfaceList;
import org.testtoolinterfaces.testsuite.TestInterfaceList;


public class TestCaseResultReaderTester extends TestCase
{
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		System.out.println("==========================================================================");
		System.out.println(this.getName() + ":");
		
	}

	/**
	 * Test Cases
	 */
	public void testCase_testCaseResultInfo()
	{
		TestCaseResult tcResult = parseFile( "testCaseResult.xml" );

    	Assert.assertEquals("Incorrect id", "tc_001", tcResult.getId());
    	Assert.assertEquals("Incorrect sequence number", 0, tcResult.getSequenceNr());
    	Assert.assertEquals("Incorrect description", "Test Case description", tcResult.getDescription());
    	Assert.assertEquals("Incorrect comment", "The test result was checked and found OK by Arjan.", tcResult.getComment());
	}
	
	public void testCase_testCaseResultPrepare()
	{
		TestCaseResult tcResult = parseFile( "testCaseResult.xml" );
		Hashtable<Integer, TestStepResult> prepareResults = tcResult.getPrepareResults();
    	Assert.assertEquals("Incorrect number of prepare results", 1, prepareResults.size());
	}
	
	public void testCase_testCaseResultExecution()
	{
		TestCaseResult tcResult = parseFile( "testCaseResult.xml" );
		Hashtable<Integer, TestStepResult> executionResults = tcResult.getExecutionResults();
    	Assert.assertEquals("Incorrect number of execution results", 5, executionResults.size());
	}
	
	public void testCase_testCaseResultRestore()
	{
		TestCaseResult tcResult = parseFile( "testCaseResult.xml" );
		Hashtable<Integer, TestStepResult> restoreResults = tcResult.getRestoreResults();
    	Assert.assertEquals("Incorrect number of execution results", 4, restoreResults.size());
	}

	private TestCaseResult parseFile( String aFileName )
	{
		TestCaseResult tcResult = null;
		try
		{
			File jarFile = new File(  this.getClass().getProtectionDomain()
			   						.getCodeSource()
			   						.getLocation()
			   						.toURI() );
			File testXmlFilesDir = new File ( jarFile.getParent(),  "test" + File.separator + 
												"org" + File.separator +
												"testtoolinterfaces" + File.separator +
												"testresultinterface" + File.separator +
												"testXmlFiles");

			File tcResultFile = new File(testXmlFilesDir, aFileName);
			TestInterfaceList interfaceList = new LooseTestInterfaceList();
			TestCaseResultReader tcResultReader = new TestCaseResultReader(interfaceList);
			tcResult = tcResultReader.readTcResultFile( tcResultFile );
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		
		return tcResult;
	}
}
