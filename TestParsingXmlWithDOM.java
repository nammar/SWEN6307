package edu.birzeit.cs.parsers;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Use JAXP DOM Parser to display all persons: title, names...
 * 
 * @author Huseyin OZVEREN
 */
public class TestParsingXmlWithDOM {

	public static void main(String[] args) throws Exception {

		// Create a DOM parser factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Obtain a DOM builder
		DocumentBuilder docBuilder = factory.newDocumentBuilder();

		// XML Stream
		InputStream xmlStream = TestParsingXmlWithDOM.class.getResourceAsStream("people.xml");
		
		// Parse the given XML document 
		// in order to build a DOM tree representing the XML document
		Document doc = docBuilder.parse(xmlStream);

		// Return all the person elements as NodeList
		//NodeList personNodes = doc.getElementsByTagName("person"); 
		// Return the root element
		//Element root = doc.getDocumentElement();  

		// Get a list of all elements in the document
		// The wild card * matches all tags
		NodeList list = doc.getElementsByTagName("*");

		int peopleCount = 0;
		for (int i = 0; i < list.getLength(); i++) {
			
			// Get the elements person (attribute ID), title, names...
			Element element = (Element) list.item(i);
			String nodeName = element.getNodeName();
			
			if (nodeName.equals("person")) {
				peopleCount++;
				System.out.println("PERSON " + peopleCount);
				String personId = element.getAttribute("ID");
				System.out.println("\tID:\t" + personId);
			
			} else if (nodeName.equals("title")) {
				System.out.println("\tTitle:\t" + element.getChildNodes().item(0).getNodeValue());

			} else if (nodeName.equals("name")) {
				System.out.println("\tName:\t" + element.getChildNodes().item(0).getNodeValue());
			}
		} // end-for
	}
	
}
