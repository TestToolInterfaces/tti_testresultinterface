package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
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
											String aTag,
											TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR);

    	this.reset();

    	//TODO Suspicious: replaced TestStep.StepType.action with "action"
    	myActionXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, "action", anInterfaceList);
		this.addElementHandler(myActionXmlHandler);

    	//TODO Suspicious: replaced TestStep.StepType.check with "check"
		myCheckXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, "check", anInterfaceList);
		this.addElementHandler(myCheckXmlHandler);
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
    	//TODO Suspicious: replaced TestStep.StepType.action with "action"
    	if (aQualifiedName.equalsIgnoreCase("action"))
    	{
    		mySteps.add(myActionXmlHandler.getActionStep());
    		myActionXmlHandler.reset();
    	}
    	//TODO Suspicious: replaced TestStep.StepType.check with "check"
    	if (aQualifiedName.equalsIgnoreCase("check"))
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
