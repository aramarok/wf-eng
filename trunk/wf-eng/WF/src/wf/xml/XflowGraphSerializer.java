

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

import wf.exceptions.XflowException;
import wf.model.Destination;
import wf.model.DirectedGraph;

public class XflowGraphSerializer {

    private static Logger log = Logger.getLogger(XflowGraphSerializer.class);
    private static String template = "<xflow><nodes></nodes><transitions></transitions></xflow>";

    public static String serialize (DirectedGraph dg) throws XflowException {

        String graphName = dg.getName();
        String result = null;
        try {
   	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();

            StringReader sreader = new StringReader (template);
            InputSource is = new InputSource(sreader);
	    Document doc = builder.parse(is);

            NodeList els = doc.getElementsByTagName ("xflow");
            Element xflowEl = (Element)els.item(0);
            xflowEl.setAttribute ("name", graphName);

            els = doc.getElementsByTagName ("nodes");
            Element nodesEl = (Element)els.item(0);
            List gnodes = dg.getAllNodes();
            for (int i = 0; i < gnodes.size(); i++) {
                wf.model.Node gnode = (wf.model.Node)gnodes.get(i);
                String nodeName = gnode.getName();
                String nodeType = gnode.getNodeType();
                Element nodeEl = doc.createElement("node");
                nodesEl.appendChild (nodeEl);
                nodeEl.setAttribute ("id", nodeName);
                nodeEl.setAttribute ("type", nodeType);
                if (nodeType.equals(wf.model.Node.CONTAINER)) {
                    String containee = gnode.getContainee();
                    nodeEl.setAttribute ("containee", containee);
                    int    containeeVersion = gnode.getContaineeVersion();
                    if (containeeVersion != -1) {
                        nodeEl.setAttribute ("containeeVersion", ""+containeeVersion);
		    }
                }
                if (nodeType.equals(wf.model.Node.PROCESS)) {
                    int timeoutMinutes = gnode.getTimeoutMinutes();
                    if (timeoutMinutes != -1) {
                        nodeEl.setAttribute ("timeoutMinutes", ""+timeoutMinutes);
		    }
                    String timeoutHandler = gnode.getTimeoutHandler();                  
                    if (timeoutHandler != null) {
                        nodeEl.setAttribute ("timeoutHandler", timeoutHandler);
		    }
                }
            }
            els = doc.getElementsByTagName ("transitions");
            Element transitionsEl = (Element)els.item(0);

            wf.model.Node rootNode = dg.getRootNode();
            serializeTransitions (doc, transitionsEl, rootNode);

            result = serialize (doc.getDocumentElement());
        } catch (Exception e) {
            throw new XflowException(e);
        }
        return result;
    }

    public static String serialize (Element element) throws XflowException {

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
	    throw new XflowException (e);
        }
       
	return serialized;
    }

    private static void serializeTransitions (Document doc, Element transitionsEl, wf.model.Node gnode) {

        List destinations = gnode.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination) destinations.get(i);
            Element transEl = doc.createElement("transition");
            transitionsEl.appendChild (transEl);
            transEl.setAttribute ("from", gnode.getName());
            transEl.setAttribute ("to", d.node.getName());
            if (d.rule != null) {
                Element ruleEl = doc.createElement("rule");
                Text textEl = doc.createTextNode(d.rule);
                ruleEl.appendChild(textEl);
                transEl.appendChild(ruleEl);
            }

	    serializeTransitions (doc, transitionsEl, d.node);
        }
    }
    public static void main (String[] args) throws XflowException {

        DirectedGraph dg = new DirectedGraph("test");
        wf.model.Node gnode1 = new wf.model.Node("Start", wf.model.Node.START);
        wf.model.Node gnode2 = new wf.model.Node("P1", wf.model.Node.PROCESS);
        gnode2.setTimeoutMinutes(12);
        gnode2.setTimeoutHandler("MyHandler");
        wf.model.Node gnode3 = new wf.model.Node("P2", wf.model.Node.PROCESS);
        wf.model.Node gnode4 = new wf.model.Node("P3", wf.model.Node.CONTAINER);
        gnode4.setContainee("MyContainee");
        wf.model.Node gnode5 = new wf.model.Node("End", wf.model.Node.END);

        dg.setRootNode (gnode1);
        gnode1.addDestination (gnode2, null);
        gnode2.addDestination (gnode3, null);
        gnode3.addDestination (gnode4, null);
        gnode4.addDestination (gnode5, "//book/detail/inventory[@copies > 50]");

        System.out.println (dg.toXML());
    }
}
