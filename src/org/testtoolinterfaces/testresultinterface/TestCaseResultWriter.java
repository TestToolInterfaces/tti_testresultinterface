package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestCaseResult;

public interface TestCaseResultWriter
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestCaseResult aTestCaseResult, File aResultFile );

	/**
	 * To write an intermediate result
	 */
	public abstract void update( TestCaseResult aTestCaseResult );
}