package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestRunResult;

public interface TestRunResultWriter
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestRunResult aRunResult, File aResultFile );

	/**
	 * To update a previously written result file
	 */
	public abstract void update( TestRunResult aRunResult );
}