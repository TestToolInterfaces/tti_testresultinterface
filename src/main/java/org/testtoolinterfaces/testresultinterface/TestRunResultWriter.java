package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestRunResult;
import org.testtoolinterfaces.testresult.observer.TestRunResultObserver;

public interface TestRunResultWriter extends TestRunResultObserver
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestRunResult aRunResult, File aResultFile );
}