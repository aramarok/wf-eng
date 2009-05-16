package wf.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
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

public class GraphXMLParser {

    private static Logger log = Logger.getLogger(GraphXMLParser.class);

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
		    throw new Exception("Graph has more than one root node");
		}
	    }
	}
	return result;
    }

    public static wf.model.Nod getNode(final String nodeString) {

	StringTokenizer strtok = new StringTokenizer(nodeString, ";");
	String nodeName = null;
	String nodeType = null;
	String containee = null;

	while (strtok.hasMoreTokens()) {
	    String tok1 = strtok.nextToken();
	    StringTokenizer strtok2 = new StringTokenizer(tok1, "=");
	    String lhs = strtok2.nextToken();
	    String rhs = strtok2.nextToken();
	    lhs = lhs.trim();
	    rhs = rhs.trim();
	    if (lhs.equals(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE)) {
		nodeName = rhs;
	    } else if (lhs.equals(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE)) {
		nodeType = rhs;
		if (nodeType.equals(wf.model.Nod.AND)) {
		    nodeName = "And";
		}
	    } else if (lhs
		    .equals(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE)) {
		containee = rhs;
	    }
	}

	wf.model.Nod gnode = new wf.model.Nod(nodeName, nodeType);
	if (containee != null) {
	    gnode.setContainee(containee);
	}
	return gnode;
    }

    public static void main(final String[] args) throws Exception {
	String xml = "<gxl><graph><node id=\"n0\"><attr name=\"Label\"><string>name=Start;type=Start</string></attr></node></graph></gxl>";
	DirectedGraph g = parseGxl(xml, args[0]);
	log.info(g.toXML());
    }

    public static DirectedGraph parseGxl(final String xml,
	    final String graphName) throws ExceptieWF {

	DirectedGraph rgraph = new DirectedGraph(graphName);

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
		    .getElementsByTagName(WFXMLTagAndAttributeConstants.NODE_TAG);
	    int count = elements.getLength();
	    for (int i = 0; i < count; i++) {
		Element element = (Element) elements.item(i);
		String nodeId = element
			.getAttribute(WFXMLTagAndAttributeConstants.ID_ATTRIBUTE);
		NodeList elements2 = element
			.getElementsByTagName(WFXMLTagAndAttributeConstants.STRING_TAG);
		Element elem = (Element) elements2.item(0);
		org.w3c.dom.Node node = elem.getFirstChild();
		String nodeString = node.getNodeValue();
		wf.model.Nod gnode = getNode(nodeString);

		System.out.println("Putting: " + nodeId + " : "
			+ gnode.getName() + " " + gnode.getNodeType());
		pm.put(nodeId, gnode);
	    }

	    elements = doc
		    .getElementsByTagName(WFXMLTagAndAttributeConstants.EDGE_TAG);
	    count = elements.getLength();
	    for (int i = 0; i < count; i++) {
		Element el = (Element) elements.item(i);
		String fromNodeId = el
			.getAttribute(WFXMLTagAndAttributeConstants.FROM_ATTRIBUTE);
		String toNodeId = el
			.getAttribute(WFXMLTagAndAttributeConstants.TO_ATTRIBUTE);
		rootc.put(toNodeId, toNodeId);
		wf.model.Nod fromNode = pm.get(fromNodeId);
		wf.model.Nod toNode = pm.get(toNodeId);

		System.out.println(fromNode.getName() + " to "
			+ toNode.getName());
		NodeList els2 = el
			.getElementsByTagName(WFXMLTagAndAttributeConstants.STRING_TAG);
		Element e = (Element) els2.item(0);
		String rule = null;
		if (e != null) {
		    org.w3c.dom.Node node = e.getFirstChild();
		    rule = node.getNodeValue();
		    System.out.println(rule);
		}

		fromNode.addDestination(toNode, rule);
	    }

	} catch (Exception e) {
	    throw new ExceptieWF(e);
	}

	try {
	    String rootNodeId = findRootNodeId(pm, rootc);
	    if (rootNodeId == null) {
		throw new ExceptieWF("No root node in graph");
	    }
	    wf.model.Nod rootNode = pm.get(rootNodeId);
	    rgraph.setRootNode(rootNode);
	    rootNode.traverse();
	} catch (Exception e) {
	    throw new ExceptieWF(e);
	}

	return rgraph;
    }
}
