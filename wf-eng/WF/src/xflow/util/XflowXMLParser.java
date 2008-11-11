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
import java.util.*;
import java.io.*;
import xflow.common.*;
import org.apache.log4j.Logger;

public class XflowXMLParser {

    private static Logger log = Logger.getLogger(XflowXMLParser.class);

    public static DirectedGraph parse (String xml) throws XflowException {

        String graphName = null;
        DirectedGraph rgraph = null;

        HashMap pm = new HashMap();
        HashMap rootc = new HashMap();

        // Parse the xml string
        try {
   	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();

            StringReader sreader = new StringReader (xml);
            InputSource is = new InputSource(sreader);
	    Document doc = builder.parse(is);

            NodeList els = doc.getElementsByTagName ("xflow");
            Element el = (Element)els.item(0);
            if (el != null) {
                graphName = el.getAttribute ("name");
                if (graphName == null) {
                    throw new XflowException ("xflow name attribute not found in XML file.");
                }
                rgraph = new DirectedGraph (graphName);
            } else {
                throw new XflowException ("<xflow> element not found in XML file.");
            }

            els = doc.getElementsByTagName ("node");
            int count = els.getLength();
            for (int i = 0; i < count; i++) {
                el = (Element)els.item(i);
                String nodeName = el.getAttribute ("id");
                if (nodeName == null) {
                    throw new XflowException ("node id is not defined.");
                }
                String nodeType = el.getAttribute("type");
                if (nodeType == null) {
                    throw new XflowException ("node type is not defined.");
		}
                xflow.common.Node gnode = new xflow.common.Node(nodeName, nodeType);

                String timeOut = el.getAttribute("timeoutMinutes");
                if (timeOut != null && !timeOut.equals("")) {
                    Integer dTimeOut = new Integer(timeOut);
                    gnode.setTimeoutMinutes(dTimeOut.intValue());
                    String timeOutHandler = el.getAttribute("timeoutHandler");
                    if (timeOutHandler != null) {
                        gnode.setTimeoutHandler(timeOutHandler);
		    }
		}
                log.info ("Putting: " + nodeName + " : " + 
                                     gnode.getName() + " " + gnode.getNodeType());
                pm.put (nodeName, gnode);
                if (nodeType.equals(xflow.common.Node.CONTAINER)) {
                    String containee = el.getAttribute("containee");
                    if (containee == null) {
                        throw new XflowException ("Containee not defined for container process");
                    }
                    gnode.setContainee (containee);
		}
            }

            els = doc.getElementsByTagName ("transition");
            count = els.getLength();
            for (int i = 0; i < count; i++) {
                el = (Element)els.item(i);
                String fromNodeName = el.getAttribute ("from");
                String toNodeName = el.getAttribute ("to");
                rootc.put (toNodeName, toNodeName);
                log.info ( "parse transition from [" + fromNodeName + "] to [" + toNodeName + "]");
                xflow.common.Node fromNode = (xflow.common.Node) pm.get (fromNodeName);
                if( fromNode == null) throw new  XflowException( fromNodeName + " undefined!");
                xflow.common.Node toNode = (xflow.common.Node) pm.get (toNodeName);
                if( toNode == null) throw new  XflowException( toNode + " undefined!");

                log.info (fromNode.getName() + " to " + toNode.getName());
                NodeList els2 = el.getElementsByTagName ("rule");
                Element e = (Element) els2.item(0);
                String rule = null;
                if (e != null) {
                    org.w3c.dom.Node node = e.getFirstChild();
                    rule = node.getNodeValue();
                    log.info (rule);
		}

                fromNode.addDestination (toNode, rule);
            }

        } catch (Exception e) {
            throw new XflowException(e);
        }
        
        try {
           String rootNodeId = findRootNodeId (pm, rootc);
           if (rootNodeId == null) {
               throw new XflowException ("No root node in graph");           
	   }
           xflow.common.Node rootNode = (xflow.common.Node) pm.get(rootNodeId);
           rgraph.setRootNode (rootNode);
           // rootNode.traverse();
        } catch (Exception e) {
            e.printStackTrace();
            throw new XflowException(e);
        }

        return rgraph;
    }

    public static String findRootNodeId (HashMap pm, HashMap rootc) throws Exception {

        String result = null;

        Iterator itr = pm.keySet().iterator();
        while (itr.hasNext()) {
            String nid = (String) itr.next();
            if (rootc.get(nid) == null) {
		if (result == null) {
                    result = nid;
                } else {
                    throw new Exception ("Graph has more than one root node");
                }
            }
        }
        return result;
    }

    // Simple test
    public static void main (String[] args) throws Exception {
        String xml = "<xflow name=\"Test\"><nodes><node id=\"StartNode\" type=\"Start\"/><node id=\"P1\" type=\"Process\"/><node id=\"EndNode\" type=\"End\"/></nodes><transitions><transition from=\"StartNode\" to=\"P1\"/><transition from=\"P1\" to=\"EndNode\"/></transitions></xflow>"; 
        DirectedGraph g = parse (xml);
        g.getRootNode().traverse();
    }
}
