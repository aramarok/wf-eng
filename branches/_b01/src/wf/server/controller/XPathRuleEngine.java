package wf.server.controller;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XPathRuleEngine {

	public static boolean executeRule(String xml, String expression,
			List namespaces) throws JaxenException,
			ParserConfigurationException, IOException, SAXException {

		boolean result = false;
		XPath xpath = new DOMXPath(expression);
		if (namespaces != null) {
			SimpleNamespaceContext nsc = new SimpleNamespaceContext();
			for (int i = 0; i < namespaces.size(); i++) {
				String nsStr = (String) namespaces.get(i);
				Namespace ns = Namespace.getNamespace(nsStr);
				nsc.addNamespace(ns.alias, ns.uri);
			}
			xpath.setNamespaceContext(nsc);
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader sreader = new StringReader(xml);
		InputSource is = new InputSource(sreader);
		Document doc = builder.parse(is);
		List list = xpath.selectNodes(doc);
		if (list.size() > 0) {
			result = true;
		}

		return result;
	}

	public static void main(String[] args) {
		try {
			String xmlId = args[0];
			String rule = args[1];
			List namespaces = new ArrayList();
			String xml = null;
			FileInputStream inStream = new FileInputStream(xmlId);
			int sizeOfFile = inStream.available();
			byte[] buffer = new byte[sizeOfFile];
			inStream.read(buffer);
			inStream.close();
			xml = new String(buffer);

			boolean result = XPathRuleEngine.executeRule(xml, rule, namespaces);
			System.out.println("Result is: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
