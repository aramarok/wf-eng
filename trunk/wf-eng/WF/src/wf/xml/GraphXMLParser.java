

package wf.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import wf.exceptions.WorkFlowException;
import wf.model.DirectedGraph;

public class GraphXMLParser {

	public static DirectedGraph parseGxl(String xml, String graphName)
		throws WorkFlowException {

		
		DirectedGraph rgraph = new DirectedGraph(graphName);

		HashMap pm = new HashMap();
		HashMap rootc = new HashMap();

		
		try {
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sreader = new StringReader(xml);
			InputSource is = new InputSource(sreader);
			Document doc = builder.parse(is);

			NodeList els = doc.getElementsByTagName("node");
			int count = els.getLength();
			for (int i = 0; i < count; i++) {
				Element el = (Element) els.item(i);
				String nodeId = el.getAttribute("id");
				NodeList els2 = el.getElementsByTagName("string");
				Element e = (Element) els2.item(0);
				org.w3c.dom.Node node = e.getFirstChild();
				String nodeString = node.getNodeValue();
				wf.model.Node gnode = getNode(nodeString);

				System.out.println(
					"Putting: "
						+ nodeId
						+ " : "
						+ gnode.getName()
						+ " "
						+ gnode.getNodeType());
				pm.put(nodeId, gnode);
			}

			els = doc.getElementsByTagName("edge");
			count = els.getLength();
			for (int i = 0; i < count; i++) {
				Element el = (Element) els.item(i);
				String fromNodeId = el.getAttribute("from");
				String toNodeId = el.getAttribute("to");
				rootc.put(toNodeId, toNodeId);
				wf.model.Node fromNode =
					(wf.model.Node) pm.get(fromNodeId);
				wf.model.Node toNode = (wf.model.Node) pm.get(toNodeId);

				System.out.println(
					fromNode.getName() + " to " + toNode.getName());
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
			wf.model.Node rootNode = (wf.model.Node) pm.get(rootNodeId);
			rgraph.setRootNode(rootNode);
			rootNode.traverse();
		} catch (Exception e) {
			throw new WorkFlowException(e);
		}

		return rgraph;
	}

	public static String findRootNodeId(HashMap pm, HashMap rootc)
		throws Exception {

		String result = null;

		Iterator itr = pm.keySet().iterator();
		while (itr.hasNext()) {
			String nid = (String) itr.next();
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
		String xml =
			"<gxl><graph><node id=\"n0\"><attr name=\"Label\"><string>name=Start;type=Start</string></attr></node></graph></gxl>";
		DirectedGraph g = parseGxl(xml, args[0]);

	}
}
