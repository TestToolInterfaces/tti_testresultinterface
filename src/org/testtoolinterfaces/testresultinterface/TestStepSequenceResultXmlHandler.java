package org.testtoolinterfaces.testresultinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testresult.TestStepResult;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class TestStepSequenceResultXmlHandler extends XmlHandler
{

	public TestStepSequenceResultXmlHandler(XMLReader anXmlReader,
											String aTag)
	{
		super(anXmlReader, aTag.toString());
	}

	@Override
	public void handleCharacters(String aValue) throws SAXParseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEndElement(String aQualifiedName)
														throws SAXParseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleGoToChildElement(String aQualifiedName)
																throws SAXParseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName,
												XmlHandler aChildXmlHandler)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleStartElement(String aQualifiedName)
															throws SAXParseException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processElementAttributes(String qualifiedName, Attributes att)
																				throws SAXParseException
	{
		// TODO Auto-generated method stub
		
	}

	public ArrayList<TestStepResult> getStepSequence()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
