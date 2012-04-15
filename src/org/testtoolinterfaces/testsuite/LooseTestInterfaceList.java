/**
 * 
 */
package org.testtoolinterfaces.testsuite;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author Arjan Kranenburg
 *
 * Uses the interfaceList to look for TestInterfaces.
 * Creates an UndefinedInterface if it does not exist.
 */
public class LooseTestInterfaceList implements TestInterfaceList
{
	TestInterfaceList myTestInterfaces = null;
	Hashtable<String, TestInterface> myList;

	/**
	 * 
	 */
	public LooseTestInterfaceList()
	{
		myList = new Hashtable<String, TestInterface>();
	}

	/**
	 * @param myTestInterfaces
	 */
	public LooseTestInterfaceList(TestInterfaceList aTestInterfaces)
	{
		myTestInterfaces = aTestInterfaces;
		myList = new Hashtable<String, TestInterface>();
	}

	/* (non-Javadoc)
	 * @see org.testtoolinterfaces.testsuite.TestInterfaceList#getInterface(java.lang.String)
	 */
	@Override
	public TestInterface getInterface(String anInterfaceName)
	{
		TestInterface testInterface = null;
		if ( myTestInterfaces != null )
		{
			testInterface = myTestInterfaces.getInterface(anInterfaceName);
		}
		
		if (testInterface == null)
		{
			testInterface = myList.get(anInterfaceName);
			if (testInterface == null)
			{
				testInterface = new UndefinedTestInterface( anInterfaceName );
				myList.put(anInterfaceName, testInterface);
			}
		}

		return testInterface;
	}

	@Override
	public Iterator<TestInterface> iterator()
	{
		// TODO not sure if this is sufficient. The local myList is not included
		return myTestInterfaces.iterator();
	}
}
