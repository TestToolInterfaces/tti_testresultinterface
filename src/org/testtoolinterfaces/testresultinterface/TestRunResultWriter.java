package org.testtoolinterfaces.testresultinterface;

import org.testtoolinterfaces.testresult.TestRunResult;


public interface TestRunResultWriter
{
	/**
	 * 
	 */
	public abstract void write( TestRunResult aRunResult );

}