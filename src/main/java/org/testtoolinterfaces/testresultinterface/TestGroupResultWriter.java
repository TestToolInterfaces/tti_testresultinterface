package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestGroupResult;
import org.testtoolinterfaces.testresult.TestGroupResultObserver;

public interface TestGroupResultWriter extends TestGroupResultObserver
{
	/**
	 * To write the result.
	 * Note the result may not be final: make sure to register this Observer at the
	 *  TestGroupResult to be notified of updates.
	 */
	public abstract void write( TestGroupResult aTestGroupResult, File aResultFile );
}