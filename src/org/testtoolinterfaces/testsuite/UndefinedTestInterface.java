/**
 * 
 */
package org.testtoolinterfaces.testsuite;

import java.util.ArrayList;

/**
 * @author Arjan Kranenburg
 *
 * Class for Unknown TestInterfaces
 * This can be used when Test Case Result files are read and where interfaces
 * are mentioned that are not known within this application.
 * This is not necessarily an error, since other applications can have these
 * interfaces defined.
 * 
 * It serves only as place-holder for the interface name.
 * It does not have any commands and the other functions are only implemented
 * to satisfy the interface.
 * The constructor and getInterfaceName() are the only methods that should be
 * used.
 */
public class UndefinedTestInterface implements TestInterface
{
	String myName;
	/**
	 * 
	 */
	public UndefinedTestInterface( String aName )
	{
		myName = aName;
	}

	/** 
	 * Creates an parameter with the name and the value
	 * The object type is String
	 * @param aName
	 * @param aType - ignored
	 * @param aValue
	 */
	@Deprecated
	@Override
	public ParameterImpl createParameter(String aName, String aType, String aValue)
					 throws TestSuiteException
	{
		ParameterImpl param = new ParameterImpl(aName, aValue);
		return param;
	}

	/**
	 * Will return an ampty list
	 */
	@Deprecated
	@Override
	public ArrayList<String> getCommands()
	{
		return new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testsuite.TestInterface#getInterfaceName()
	 */
	@Override
	public String getInterfaceName()
	{
		return myName;
	}

	/**
	 * Will always return false
	 */
	@Deprecated
	@Override
	public boolean hasCommand(String aCommand)
	{
		return false;
	}

	/**
	 * Will always return true
	 */
	@Deprecated
	public boolean verifyParameters( String aCommand,
									 ParameterArrayList aParameters )
				   throws TestSuiteException
	{
		return true;
	}

}
