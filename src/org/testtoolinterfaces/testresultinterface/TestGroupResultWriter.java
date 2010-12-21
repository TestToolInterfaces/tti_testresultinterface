package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestGroupResult;

public interface TestGroupResultWriter
{
	/**
	 * To write the end result
	 */
	public abstract void write( TestGroupResult aTestGroupResult, File aResultFile );

	/**
	 * To write an intermediate result
	 */
	public abstract void update( TestGroupResult aTestGroupResult );
}