package com.ho.test.xml.parsing.sax;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Use JAXP SAX Parser to display all persons: title, names...
 * 
 * @author Huseyin OZVEREN
 * 
 */
public class TestParsingXmlWithSAX {
	
	private String currentElement;
	private int peopleCount = 1;

	// Constructor
	public TestParsingXmlWithSAX() {
		try {
			// Create a SAX parser factory
			SAXParserFactory factory = SAXParserFactory.newInstance(); 
			
			// Obtain a SAX parser
			SAXParser saxParser = factory.newSAXParser();
			
			// XML Stream
			InputStream xmlStream = TestParsingXmlWithSAX.class.getResourceAsStream("people.xml");
			
			// Parse the given XML document using the callback handler
			saxParser.parse(xmlStream, new MySaxHandler()); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Entry main method
	public static void main(String args[]) {
		new TestParsingXmlWithSAX();
	}

	/*
	 * Inner class for the Callback Handlers.
	 */
	class MySaxHandler extends DefaultHandler {
		
		// Callback to handle element start tag
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			currentElement = qName;
			if (currentElement.equals("person")) {
				System.out.println("Person " + peopleCount);
				peopleCount++;
				String personId = attributes.getValue("ID");
				System.out.println("\tID:\t" + personId);
			}
		}

		// Callback to handle element end tag
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			currentElement = "";
		}

		// Callback to handle the character text data inside an element
		@Override
		public void characters(char[] chars, int start, int length) throws SAXException {
			if (currentElement.equals("title")) {
				System.out.println("\tTitle:\t" + new String(chars, start, length));
				
			} else if (currentElement.equals("name")) {
				System.out.println("\tName:\t" + new String(chars, start, length));
			}
		}
	}
}
