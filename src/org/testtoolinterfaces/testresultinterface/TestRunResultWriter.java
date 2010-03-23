package org.testtoolinterfaces.testresultinterface;

import org.testtoolinterfaces.testresult.TestRunResult;

public interface TestRunResultWriter
{
	/**
	 * To write the end result
	 */
	public abstract void write();

	/**
	 * To write an intermediate result
	 */
	public abstract void intermediateWrite();

	/**
	 * To set the run result object for later intermediate writes.
	 * @param aRunResult
	 */
	public abstract void setResult( TestRunResult aRunResult );
}