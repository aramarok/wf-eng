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

import wf.exceptions.WorkFlowException;
import wf.model.DirectedGraph;
import wf.model.Node;

public class GraphXMLParser {

	private static Logger log = Logger.getLogger(GraphXMLParser.class);
	
	public static DirectedGraph parseGxl(String xml, String graphName)
			throws WorkFlowException {

		DirectedGraph rgraph = new DirectedGraph(graphName);

		HashMap<String, Node> pm = new HashMap<String, Node>();
		HashMap<String, String> rootc = new HashMap<String, String>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sreader = new StringReader(xml);
			InputSource is = new InputSource(sreader);
			Document doc = builder.parse(is);

			NodeList elements = doc.getElementsByTagName("node");
			int count = elements.getLength();
			for (int i = 0; i < count; i++) {
				Element element = (Element) elements.item(i);
				String nodeId = element.getAttribute("id");
				NodeList elements2 = element.getElementsByTagName("string");
				Element elem = (Element) elements2.item(0);
				org.w3c.dom.Node node = elem.getFirstChild();
				String nodeString = node.getNodeValue();
				wf.model.Node gnode = getNode(nodeString);

				System.out.println("Putting: " + nodeId + " : "
						+ gnode.getName() + " " + gnode.getNodeType());
				pm.put(nodeId, gnode);
			}

			elements = doc.getElementsByTagName("edge");
			count = elements.getLength();
			for (int i = 0; i < count; i++) {
				Element el = (Element) elements.item(i);
				String fromNodeId = el.getAttribute("from");
				String toNodeId = el.getAttribute("to");
				rootc.put(toNodeId, toNodeId);
				wf.model.Node fromNode = pm.get(fromNodeId);
				wf.model.Node toNode = pm.get(toNodeId);

				System.out.println(fromNode.getName() + " to "
						+ toNode.getName());
				NodeList els2 = el.getElementsByTagName("string");
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
			throw new WorkFlowException(e);
		}

		try {
			String rootNodeId = findRootNodeId(pm, rootc);
			if (rootNodeId == null) {
				throw new WorkFlowException("No root node in graph");
			}
			wf.model.Node rootNode = pm.get(rootNodeId);
			rgraph.setRootNode(rootNode);
			rootNode.traverse();
		} catch (Exception e) {
			throw new WorkFlowException(e);
		}

		return rgraph;
	}

	public static String findRootNodeId(HashMap<String, Node> pm, HashMap<String, String> rootc)
			throws Exception {

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

	public static wf.model.Node getNode(String nodeString) {

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
			if (lhs.equals("name")) {
				nodeName = rhs;
			} else if (lhs.equals("type")) {
				nodeType = rhs;
				if (nodeType.equals(wf.model.Node.AND)) {
					nodeName = "And";
				}
			} else if (lhs.equals("containee")) {
				containee = rhs;
			}
		}

		wf.model.Node gnode = new wf.model.Node(nodeName, nodeType);
		if (containee != null) {
			gnode.setContainee(containee);
		}
		return gnode;
	}

	public static void main(String[] args) throws Exception {
		String xml = "<gxl><graph><node id=\"n0\"><attr name=\"Label\"><string>name=Start;type=Start</string></attr></node></graph></gxl>";
		DirectedGraph g = parseGxl(xml, args[0]);
		log.info(g.toXML());
	}
}
