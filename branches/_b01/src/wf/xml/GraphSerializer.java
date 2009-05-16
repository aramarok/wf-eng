package wf.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import wf.exceptions.WorkFlowException;
import wf.model.Destination;
import wf.model.DirectedGraph;

public class GraphSerializer {

	private static Logger log = Logger.getLogger(GraphSerializer.class);
	private static String template_test = "<wf><nodes></nodes><transitions></transitions></wf>";

	@SuppressWarnings("unchecked")
	public static String serialize(DirectedGraph dg) throws WorkFlowException {

		String graphName = dg.getName();
		String result = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sReader = new StringReader(template_test);
			InputSource is = new InputSource(sReader);
			Document doc = builder.parse(is);

			NodeList elements = doc.getElementsByTagName(WFXMLTagAndAttributeConstants.WF_TAG);
			Element wfElement = (Element) elements.item(0);
			wfElement.setAttribute(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE, graphName);

			elements = doc.getElementsByTagName(WFXMLTagAndAttributeConstants.NODES_TAG);
			Element nodesElement = (Element) elements.item(0);
			List lNodes = dg.getAllNodes();
			for (int i = 0; i < lNodes.size(); i++) {
				wf.model.Node gnode = (wf.model.Node) lNodes.get(i);
				String nodeName = gnode.getName();
				String nodeType = gnode.getNodeType();
				Element nodeElement = doc.createElement(WFXMLTagAndAttributeConstants.NODE_TAG);
				nodesElement.appendChild(nodeElement);
				nodeElement.setAttribute(WFXMLTagAndAttributeConstants.ID_ATTRIBUTE, nodeName);
				nodeElement.setAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE, nodeType);
				if (nodeType.equals(wf.model.Node.CONTAINER)) {
					String containee = gnode.getContainee();
					nodeElement.setAttribute(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE, containee);
					int containeeVersion = gnode.getContaineeVersion();
					if (containeeVersion != -1) {
						nodeElement.setAttribute(WFXMLTagAndAttributeConstants.CONTAINEEVERSION_ATTRIBUTE, ""
								+ containeeVersion);
					}
				}
				if (nodeType.equals(wf.model.Node.PROCESS)) {
					int timeoutMinutes = gnode.getTimeoutMinutes();
					if (timeoutMinutes != -1) {
						nodeElement.setAttribute(WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE, ""
								+ timeoutMinutes);
					}
					String timeoutHandler = gnode.getTimeoutHandler();
					if (timeoutHandler != null) {
						nodeElement.setAttribute(WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE, timeoutHandler);
					}
				}
			}
			elements = doc.getElementsByTagName(WFXMLTagAndAttributeConstants.TRANSITIONS_TAG);
			Element transitionsEl = (Element) elements.item(0);

			wf.model.Node rootNode = dg.getRootNode();
			serializeTransitions(doc, transitionsEl, rootNode);

			result = serialize(doc.getDocumentElement());
		} catch (Exception e) {
			throw new WorkFlowException(e);
		}
		return result;
	}

	public static String serialize(Element element) throws WorkFlowException {

		String serialized = null;

		try {
			element.normalize();
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(element);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			serialized = writer.toString();
		} catch (Exception e) {
			throw new WorkFlowException(e);
		}

		return serialized;
	}

	@SuppressWarnings("unchecked")
	private static void serializeTransitions(Document doc,
			Element transitionsEl, wf.model.Node gnode) {

		List destinations = gnode.getDestinations();
		for (int i = 0; i < destinations.size(); i++) {
			Destination d = (Destination) destinations.get(i);
			Element transEl = doc.createElement(WFXMLTagAndAttributeConstants.TRANSITION_TAG);
			transitionsEl.appendChild(transEl);
			transEl.setAttribute(WFXMLTagAndAttributeConstants.FROM_ATTRIBUTE, gnode.getName());
			transEl.setAttribute(WFXMLTagAndAttributeConstants.TO_ATTRIBUTE, d.node.getName());
			if (d.rule != null) {
				Element ruleEl = doc.createElement(WFXMLTagAndAttributeConstants.RULE_TAG);
				Text textEl = doc.createTextNode(d.rule);
				ruleEl.appendChild(textEl);
				transEl.appendChild(ruleEl);
			}

			serializeTransitions(doc, transitionsEl, d.node);
		}
	}

	public static void main(String[] args) throws WorkFlowException {

		DirectedGraph dg = new DirectedGraph("test");
		wf.model.Node gnode1 = new wf.model.Node("Start", wf.model.Node.START);
		wf.model.Node gnode2 = new wf.model.Node("P1", wf.model.Node.PROCESS);
		gnode2.setTimeoutMinutes(12);
		gnode2.setTimeoutHandler("MyHandler");
		wf.model.Node gnode3 = new wf.model.Node("P2", wf.model.Node.PROCESS);
		wf.model.Node gnode4 = new wf.model.Node("P3", wf.model.Node.CONTAINER);
		gnode4.setContainee("MyContainee");
		wf.model.Node gnode5 = new wf.model.Node("End", wf.model.Node.END);

		dg.setRootNode(gnode1);
		gnode1.addDestination(gnode2, null);
		gnode2.addDestination(gnode3, null);
		gnode3.addDestination(gnode4, null);
		gnode4.addDestination(gnode5, "//book/detail/inventory[@copies > 50]");

		log.info(dg.toXML());
	}
}
