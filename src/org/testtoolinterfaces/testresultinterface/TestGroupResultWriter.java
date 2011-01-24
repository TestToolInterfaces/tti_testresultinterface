package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultObserver;

public interface TestGroupResultWriter extends TestGroupResultObserver
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestGroupResult aTestGroupResult, File aResultFile );
}