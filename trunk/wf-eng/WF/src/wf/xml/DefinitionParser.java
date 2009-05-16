package wf.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import wf.exceptions.ExceptieWF;
import wf.model.DirectedGraph;
import wf.model.Nod;

public class DefinitionParser {

    private static Logger log = Logger.getLogger(DefinitionParser.class);

    public static String findRootNodeId(final HashMap<String, Nod> pm,
	    final HashMap<String, String> rootc) throws Exception {

	String result = null;

	Iterator<String> itr = pm.keySet().iterator();
	while (itr.hasNext()) {
	    String nid = itr.next();
	    if (rootc.get(nid) == null) {
		if (result == null) {
		    result = nid;
		} else {
		    throw new Exception(
			    "Error: the graph has more than one root node");
		}
	    }
	}
	return result;
    }

    public static void main(final String[] args) throws Exception {
	String xml = "<wf name=\"Test\"><nodes><node id=\"StartNode\" type=\"Start\"/><node id=\"P1\" type=\"Process\"/><node id=\"EndNode\" type=\"End\"/></nodes><transitions><transition from=\"StartNode\" to=\"P1\"/><transition from=\"P1\" to=\"EndNode\"/></transitions></wf>";
	DirectedGraph g = parse(xml);
	g.getRootNode().traverse();
    }

    public static DirectedGraph parse(final String xml) throws ExceptieWF {

	String graphName = null;
	DirectedGraph rgraph = null;

	HashMap<String, Nod> pm = new HashMap<String, Nod>();
	HashMap<String, String> rootc = new HashMap<String, String>();
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();

	    StringReader sreader = new StringReader(xml);
	    InputSource is = new InputSource(sreader);
	    Document doc = builder.parse(is);

	    NodeList elements = doc
		    .getElementsByTagName(WFXMLTagAndAttributeConstants.WF_TAG);
	    Element element = (Element) elements.item(0);
	    if (element != null) {
		graphName = element
			.getAttribute(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE);
		if (graphName == null) {
		    throw new ExceptieWF(
			    "wf name was not found in the xml file");
		}
		rgraph = new DirectedGraph(graphName);
	    } else {
		throw new ExceptieWF(
			"<wf> element was not found in the xml file");
	    }

	    elements = doc
		    .getElementsByTagName(WFXMLTagAndAttributeConstants.NODE_TAG);
	    int count = elements.getLength();
	    for (int i = 0; i < count; i++) {
		element = (Element) elements.item(i);
		String nodeName = element
			.getAttribute(WFXMLTagAndAttributeConstants.ID_ATTRIBUTE);
		if (nodeName == null) {
		    throw new ExceptieWF(
			    "node id is not defined in the xml file");
		}
		String nodeType = element
			.getAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE);
		if (nodeType == null) {
		    throw new ExceptieWF(
			    "node type is not defined in the xml file");
		}
		wf.model.Nod gnode = new wf.model.Nod(nodeName, nodeType);

		String timeOut = element
			.getAttribute(WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE);
		if ((timeOut != null) && !timeOut.equals("")) {
		    Integer dTimeOut = new Integer(timeOut);
		    gnode.setTimeoutMinutes(dTimeOut.intValue());
		    String timeOutHandler = element
			    .getAttribute(WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE);
		    if (timeOutHandler != null) {
			gnode.setTimeoutHandler(timeOutHandler);
		    }
		}
		log.info("Putting: " + nodeName + " : " + gnode.getName() + " "
			+ gnode.getNodeType());
		pm.put(nodeName, gnode);
		if (nodeType.equals(wf.model.Nod.CONTAINER)) {
		    String containee = element
			    .getAttribute(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE);
		    if (containee == null) {
			throw new ExceptieWF(
				"containee is not defined for container process");
		    }
		    gnode.setContainee(containee);
		}
	    }

	    elements = doc
		    .getElementsByTagName(WFXMLTagAndAttributeConstants.TRANSITION_TAG);
	    count = elements.getLength();
	    for (int i = 0; i < count; i++) {
		element = (Element) elements.item(i);
		String fromNodeName = element
			.getAttribute(WFXMLTagAndAttributeConstants.FROM_ATTRIBUTE);
		String toNodeName = element
			.getAttribute(WFXMLTagAndAttributeConstants.TO_ATTRIBUTE);
		rootc.put(toNodeName, toNodeName);
		log.info("parse a transition from node [" + fromNodeName
			+ "] to node [" + toNodeName + "]");
		wf.model.Nod fromNode = pm.get(fromNodeName);
		if (fromNode == null) {
		    throw new ExceptieWF("from node [" + fromNodeName
			    + "] is undefined");
		}
		wf.model.Nod toNode = pm.get(toNodeName);
		if (toNode == null) {
		    throw new ExceptieWF("to node [" + toNode
			    + "] is undefined");
		}

		log.info(fromNode.getName() + " to " + toNode.getName());
		NodeList els2 = element
			.getElementsByTagName(WFXMLTagAndAttributeConstants.RULE_TAG);
		Element e = (Element) els2.item(0);
		String rule = null;
		if (e != null) {
		    org.w3c.dom.Node node = e.getFirstChild();
		    rule = node.getNodeValue();
		    log.info("Rule: " + rule);
		}

		fromNode.addDestination(toNode, rule);
	    }

	} catch (Exception e) {
	    throw new ExceptieWF(e);
	}

	try {
	    String rootNodeId = findRootNodeId(pm, rootc);
	    if (rootNodeId == null) {
		throw new ExceptieWF("Root node was not found in graph");
	    }
	    wf.model.Nod rootNode = pm.get(rootNodeId);
	    rgraph.setRootNode(rootNode);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ExceptieWF(e);
	}

	return rgraph;
    }
}
