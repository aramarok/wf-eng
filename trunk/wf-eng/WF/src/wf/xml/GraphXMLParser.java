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

package wf.xml;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.util.*;
import java.text.*;
import java.io.*;

import wf.exceptions.XflowException;
import wf.model.DirectedGraph;
import wf.util.*;
import xflow.common.*;

public class GraphXMLParser {

	public static DirectedGraph parseGxl(String xml, String graphName)
		throws XflowException {

		// Create a graph instance
		DirectedGraph rgraph = new DirectedGraph(graphName);

		HashMap pm = new HashMap();
		HashMap rootc = new HashMap();

		// Parse the xml string
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
			throw new XflowException(e);
		}

		try {
			String rootNodeId = findRootNodeId(pm, rootc);
			if (rootNodeId == null) {
				throw new XflowException("No root node in graph");
			}
			wf.model.Node rootNode = (wf.model.Node) pm.get(rootNodeId);
			rgraph.setRootNode(rootNode);
			rootNode.traverse();
		} catch (Exception e) {
			throw new XflowException(e);
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

	// Simple test
	public static void main(String[] args) throws Exception {
		String xml =
			"<gxl><graph><node id=\"n0\"><attr name=\"Label\"><string>name=Start;type=Start</string></attr></node></graph></gxl>";
		DirectedGraph g = parseGxl(xml, args[0]);

	}
}
