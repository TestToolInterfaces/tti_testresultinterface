package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestCaseResult;
import org.testtoolinterfaces.testresult.observer.TestCaseResultObserver;

public interface TestCaseResultWriter extends TestCaseResultObserver
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestCaseResult aTestCaseResult, File aResultFile );
}