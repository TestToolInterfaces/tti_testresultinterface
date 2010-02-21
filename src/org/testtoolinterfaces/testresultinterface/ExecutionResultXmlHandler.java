package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestStep;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;

/**
 * @author Arjan Kranenburg 
 * 
 * <execution>
 *  <action>
 *  ...
 *  </action>
 *  <check>
 *  ...
 *  </check>
 * <execution>
 * 
 */
public class ExecutionResultXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "execution";
	
    private ArrayList<TestStepResult> mySteps;

	private ActionTypeResultXmlHandler myActionXmlHandler;
	private ActionTypeResultXmlHandler myCheckXmlHandler;

	public ExecutionResultXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

    	this.reset();

    	myActionXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.ActionType.action);
		this.addStartElementHandler(TestStep.ActionType.action.toString(), myActionXmlHandler);
		myActionXmlHandler.addEndElementHandler(TestStep.ActionType.action.toString(), this);

		myCheckXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.ActionType.check);
		this.addStartElementHandler(TestStep.ActionType.check.toString(), myCheckXmlHandler);
		myCheckXmlHandler.addEndElementHandler(TestStep.ActionType.check.toString(), this);
	}

    public void processElementAttributes(String qualifiedName, Attributes att)
    {
    	//nop
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		//nop
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
    	//nop
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.action.toString()))
    	{
    		mySteps.add(myActionXmlHandler.getActionStep());
    		myActionXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.check.toString()))
    	{
    		mySteps.add(myCheckXmlHandler.getActionStep());
    		myCheckXmlHandler.reset();
    	}
	}

	/**
	 * @return
	 */
	public ArrayList<TestStepResult> getExecutionSteps()
	{
		Trace.println(Trace.LEVEL.GETTER);
		return mySteps;
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);
    	mySteps = new ArrayList<TestStepResult>();
	}
}
