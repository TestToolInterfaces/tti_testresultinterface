package org.testtoolinterfaces.testresultinterface;

import java.io.File;

import org.testtoolinterfaces.testresult.TestRunResult;


public interface TestRunResultWriter
{

	public abstract void print( TestRunResult aRunResult );

	/**
	 * 
	 */
	public abstract void writeToFile( TestRunResult aRunResult, File aFileName);

}