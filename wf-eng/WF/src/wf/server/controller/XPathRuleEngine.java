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

package wf.server.controller;

import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *   Rule engine to evaluate XPath expressions.
 *
 */
public class XPathRuleEngine {


  public static boolean executeRule (String xml, String expression, List namespaces) throws  JaxenException, ParserConfigurationException, IOException, SAXException{

    boolean result = false;
    XPath xpath = new DOMXPath(expression);
    if (namespaces != null) {
      SimpleNamespaceContext nsc = new SimpleNamespaceContext();
      for (int i = 0; i < namespaces.size(); i++) {
        String nsStr = (String)namespaces.get(i);
        Namespace ns = Namespace.getNamespace (nsStr);
        nsc.addNamespace (ns.alias, ns.uri);
      }
      xpath.setNamespaceContext (nsc);
    }

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    StringReader sreader = new StringReader (xml);
    InputSource is = new InputSource(sreader);
    Document doc = builder.parse(is);
    List list = xpath.selectNodes (doc);
    if (list.size() > 0) {
      result = true;
    }


    return result;
  }

  /**
   *   Unit tester
   *
   **/
  public static void main (String[] args) {
    try {
      String xmlId = args[0];
      String rule = args[1];
      //String ns = args[2];
      List namespaces = new ArrayList();
      //namespaces.add (ns);
      String xml = null;
      FileInputStream inStream = new FileInputStream(xmlId);
      int sizeOfFile = inStream.available();
      byte[] buffer = new byte[sizeOfFile];
      inStream.read(buffer);
      inStream.close();
      xml = new String(buffer);

      boolean result = XPathRuleEngine.executeRule (xml, rule, namespaces);
      System.out.println ("Result is: " + result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


