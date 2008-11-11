/*
 * ====================================================================
 *
 * XFLOW - Process Management System
 * Copyright (C) 2003 Rob Tan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions, and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions, and the disclaimer that follows 
 *    these conditions in the documentation and/or other materials 
 *    provided with the distribution.
 *
 * 3. The name "XFlow" must not be used to endorse or promote products
 *    derived from this software without prior written permission.  For
 *    written permission, please contact rcktan@yahoo.com
 * 
 * 4. Products derived from this software may not be called "XFlow", nor
 *    may "XFlow" appear in their name, without prior written permission
 *    from the XFlow Project Management (rcktan@yahoo.com)
 * 
 * In addition, we request (but do not require) that you include in the 
 * end-user documentation provided with the redistribution and/or in the 
 * software itself an acknowledgement equivalent to the following:
 *     "This product includes software developed by the
 *      XFlow Project (http://xflow.sourceforge.net/)."
 * Alternatively, the acknowledgment may be graphical using the logos 
 * available at http://xflow.sourceforge.net/
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * ====================================================================
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the XFlow Project and was originally 
 * created by Rob Tan (rcktan@yahoo.com)
 * For more information on the XFlow Project, please see:
 *           <http://xflow.sourceforge.net/>.
 * ====================================================================
 */

package xflow.util;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.*;
import java.text.*;
import java.io.*;
import xflow.util.*;
import xflow.common.*;
import org.apache.log4j.Logger;

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

            // Serialize nodes            
            List gnodes = dg.getAllNodes();
            for (int i = 0; i < gnodes.size(); i++) {
                xflow.common.Node gnode = (xflow.common.Node)gnodes.get(i);
                String nodeName = gnode.getName();
                String nodeType = gnode.getNodeType();
                Element nodeEl = doc.createElement("node");
                nodesEl.appendChild (nodeEl);
                nodeEl.setAttribute ("id", nodeName);
                nodeEl.setAttribute ("type", nodeType);
                if (nodeType.equals(xflow.common.Node.CONTAINER)) {
                    String containee = gnode.getContainee();
                    nodeEl.setAttribute ("containee", containee);
                    int    containeeVersion = gnode.getContaineeVersion();
                    if (containeeVersion != -1) {
                        nodeEl.setAttribute ("containeeVersion", ""+containeeVersion);
		    }
                }
                if (nodeType.equals(xflow.common.Node.PROCESS)) {
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

            // Serialize transitions            
            els = doc.getElementsByTagName ("transitions");
            Element transitionsEl = (Element)els.item(0);

            xflow.common.Node rootNode = dg.getRootNode();
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
  
            // Use a Transformer for output
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

    private static void serializeTransitions (Document doc, Element transitionsEl, xflow.common.Node gnode) {

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

    // Simple test
    public static void main (String[] args) throws XflowException {

        DirectedGraph dg = new DirectedGraph("test");
        xflow.common.Node gnode1 = new xflow.common.Node("Start", xflow.common.Node.START);
        xflow.common.Node gnode2 = new xflow.common.Node("P1", xflow.common.Node.PROCESS);
        gnode2.setTimeoutMinutes(12);
        gnode2.setTimeoutHandler("MyHandler");
        xflow.common.Node gnode3 = new xflow.common.Node("P2", xflow.common.Node.PROCESS);
        xflow.common.Node gnode4 = new xflow.common.Node("P3", xflow.common.Node.CONTAINER);
        gnode4.setContainee("MyContainee");
        xflow.common.Node gnode5 = new xflow.common.Node("End", xflow.common.Node.END);

        dg.setRootNode (gnode1);
        gnode1.addDestination (gnode2, null);
        gnode2.addDestination (gnode3, null);
        gnode3.addDestination (gnode4, null);
        gnode4.addDestination (gnode5, "//book/detail/inventory[@copies > 50]");

        System.out.println (dg.toXML());
    }
}
