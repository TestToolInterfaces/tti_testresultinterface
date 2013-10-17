package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

public class TestStepSequenceResultXmlHandler extends XmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TestStepSequenceResultXmlHandler.class);

    private ArrayList<TestStepResult> mySteps;

	private ActionTypeResultXmlHandler myTestStepXmlHandler;
	private ActionTypeResultXmlHandler myActionXmlHandler;
	private ActionTypeResultXmlHandler myCheckXmlHandler;

	public TestStepSequenceResultXmlHandler(XMLReader anXmlReader,
											String aTag,
											TestInterfaceList anInterfaceList )
	{
		super(anXmlReader, aTag);
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}, {}", anXmlReader, aTag, anInterfaceList);

    	this.reset();

    	myTestStepXmlHandler = new ActionTypeResultXmlHandler(anXmlReader, "teststep", anInterfaceList);
		this.addElementHandler(myTestStepXmlHandler);

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
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, aChildXmlHandler);
    	if (aQualifiedName.equalsIgnoreCase("teststep"))
    	{
    		mySteps.add(myTestStepXmlHandler.getActionStep());
    		myTestStepXmlHandler.reset();
    	}
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
		LOG.trace(Mark.GETTER, "");
		return mySteps;
	}

	public void reset()
	{
		LOG.trace(Mark.SUITE, "");
    	mySteps = new ArrayList<TestStepResult>();
	}
}
