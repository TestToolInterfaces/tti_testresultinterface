/**
 * 
 */
package org.testtoolinterfaces.testresultinterface;

import java.io.File;
import java.util.Hashtable;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.LooseTestInterfaceList;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan
 *
 * Simpel tool to print the Test Case Result File
 * It serves as well as a debug tool for the structure of the result file.
 */
public class TCResultPrinter
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Trace.getInstance().addBaseClass("org.testtoolinterfaces");
		System.out.println( "Starting Pretty printing for:" );

		String requestedFileName = args[0];
		System.out.println( requestedFileName );
		File requestedFile = new File( requestedFileName );
		
		TestCaseResult tcResult = readFile(requestedFile);
		
		printTestCase(tcResult);
	}

	/**
	 * @param aTcResult
	 */
	private static void printTestCase(TestCaseResult aTcResult)
	{
		System.out.println( "ID:              " + aTcResult.getId() );
		System.out.println( "Description:" );
		System.out.println( aTcResult.getDescription() );
		System.out.println( "Sequence Number: " + aTcResult.getSequenceNr() );
		System.out.println( "=================== Preparation =====================" );
		Hashtable<Integer, TestStepResult> prepResults = aTcResult.getPrepareResults();
		System.out.println( "=================== Execution =======================" );
		System.out.println( "=================== Cleanup =========================" );
		System.out.println( "=====================================================" );
		System.out.println( "Overall Result:  " + aTcResult.getResult() );
		System.out.println( "Comment:" );
		System.out.println( aTcResult.getComment() );
	}

	/**
	 * Reads the TestCaseResult File
	 * 
	 * @param aRequestedFile
	 */
	private static TestCaseResult readFile(File aRequestedFile)
	{
		TestInterfaceList interfaceList = new LooseTestInterfaceList();
		TestCaseResultReader tcResultReader = new TestCaseResultReader( interfaceList );
		
		TestCaseResult tcResult = tcResultReader.readTcResultFile( aRequestedFile );
		return tcResult;
	}
}
