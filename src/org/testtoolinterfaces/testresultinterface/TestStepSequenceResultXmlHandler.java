package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

public class TestStepSequenceResultXmlHandler extends XmlHandler
{
    private ArrayList<TestStepResult> mySteps;

	private ActionTypeResultXmlHandler myActionXmlHandler;
	private ActionTypeResultXmlHandler myCheckXmlHandler;

	public TestStepSequenceResultXmlHandler(XMLReader anXmlReader,
											String aTag)
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR);

    	this.reset();

    	myActionXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.StepType.action);
		this.addStartElementHandler(TestStep.StepType.action.toString(), myActionXmlHandler);
		myActionXmlHandler.addEndElementHandler(TestStep.StepType.action.toString(), this);

		myCheckXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, TestStep.StepType.check);
		this.addStartElementHandler(TestStep.StepType.check.toString(), myCheckXmlHandler);
		myCheckXmlHandler.addEndElementHandler(TestStep.StepType.check.toString(), this);
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
    	//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName,
												XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(TestStep.StepType.action.toString()))
    	{
    		mySteps.add(myActionXmlHandler.getActionStep());
    		myActionXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(TestStep.StepType.check.toString()))
    	{
    		mySteps.add(myCheckXmlHandler.getActionStep());
    		myCheckXmlHandler.reset();
    	}
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void processElementAttributes(String qualifiedName, Attributes att)
	{
    	//nop
	}

	/**
	 * @return
	 */
	public ArrayList<TestStepResult> getStepSequence()
	{
		Trace.println(Trace.GETTER);
		return mySteps;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
    	mySteps = new ArrayList<TestStepResult>();
	}
}
