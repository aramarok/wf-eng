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
import wf.exceptions.WorkFlowException;
import wf.model.DirectedGraph;

public class DefinitionParser {

	private static Logger log = Logger.getLogger(DefinitionParser.class);

	public static DirectedGraph parse(String xml) throws WorkFlowException {

		String graphName = null;
		DirectedGraph rgraph = null;

		HashMap pm = new HashMap();
		HashMap rootc = new HashMap();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sreader = new StringReader(xml);
			InputSource is = new InputSource(sreader);
			Document doc = builder.parse(is);

			NodeList els = doc.getElementsByTagName("wf");
			Element el = (Element) els.item(0);
			if (el != null) {
				graphName = el.getAttribute("name");
				if (graphName == null) {
					throw new WorkFlowException(
							"wf name attribute not found in XML file.");
				}
				rgraph = new DirectedGraph(graphName);
			} else {
				throw new WorkFlowException(
						"<wf> element not found in XML file.");
			}

			els = doc.getElementsByTagName("node");
			int count = els.getLength();
			for (int i = 0; i < count; i++) {
				el = (Element) els.item(i);
				String nodeName = el.getAttribute("id");
				if (nodeName == null) {
					throw new WorkFlowException("node id is not defined.");
				}
				String nodeType = el.getAttribute("type");
				if (nodeType == null) {
					throw new WorkFlowException("node type is not defined.");
				}
				wf.model.Node gnode = new wf.model.Node(nodeName, nodeType);

				String timeOut = el.getAttribute("timeoutMinutes");
				if (timeOut != null && !timeOut.equals("")) {
					Integer dTimeOut = new Integer(timeOut);
					gnode.setTimeoutMinutes(dTimeOut.intValue());
					String timeOutHandler = el.getAttribute("timeoutHandler");
					if (timeOutHandler != null) {
						gnode.setTimeoutHandler(timeOutHandler);
					}
				}
				log.info("Putting: " + nodeName + " : " + gnode.getName() + " "
						+ gnode.getNodeType());
				pm.put(nodeName, gnode);
				if (nodeType.equals(wf.model.Node.CONTAINER)) {
					String containee = el.getAttribute("containee");
					if (containee == null) {
						throw new WorkFlowException(
								"Containee not defined for container process");
					}
					gnode.setContainee(containee);
				}
			}

			els = doc.getElementsByTagName("transition");
			count = els.getLength();
			for (int i = 0; i < count; i++) {
				el = (Element) els.item(i);
				String fromNodeName = el.getAttribute("from");
				String toNodeName = el.getAttribute("to");
				rootc.put(toNodeName, toNodeName);
				log.info("parse transition from [" + fromNodeName + "] to ["
						+ toNodeName + "]");
				wf.model.Node fromNode = (wf.model.Node) pm.get(fromNodeName);
				if (fromNode == null)
					throw new WorkFlowException(fromNodeName + " undefined!");
				wf.model.Node toNode = (wf.model.Node) pm.get(toNodeName);
				if (toNode == null)
					throw new WorkFlowException(toNode + " undefined!");

				log.info(fromNode.getName() + " to " + toNode.getName());
				NodeList els2 = el.getElementsByTagName("rule");
				Element e = (Element) els2.item(0);
				String rule = null;
				if (e != null) {
					org.w3c.dom.Node node = e.getFirstChild();
					rule = node.getNodeValue();
					log.info(rule);
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
		} catch (Exception e) {
			e.printStackTrace();
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

	public static void main(String[] args) throws Exception {
		String xml = "<wf name=\"Test\"><nodes><node id=\"StartNode\" type=\"Start\"/><node id=\"P1\" type=\"Process\"/><node id=\"EndNode\" type=\"End\"/></nodes><transitions><transition from=\"StartNode\" to=\"P1\"/><transition from=\"P1\" to=\"EndNode\"/></transitions></wf>";
		DirectedGraph g = parse(xml);
		g.getRootNode().traverse();
	}
}
