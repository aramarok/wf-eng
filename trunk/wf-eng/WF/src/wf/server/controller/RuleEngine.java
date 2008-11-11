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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.xml.sax.SAXException;

import wf.exceptions.XflowException;
import wf.model.WorkItem;

public class RuleEngine {

  private static Logger log = Logger.getLogger(RuleEngine.class);

  private static String clean (String exp) {
    Pattern p = Pattern.compile ("\\[ *");
    Matcher m = p.matcher (exp);
    String r = m.replaceAll("\\[");
    p = Pattern.compile (" *\\]");
    m = p.matcher (r);
    r = m.replaceAll("\\]");
    return r;
  }

  public static boolean evaluate (WorkItem witem, String r)
      throws JaxenException, IOException, ParserConfigurationException, XflowException, SAXException {
    boolean result = true;
    String rule = clean(r);
    rule = rule.substring(1);
    StringTokenizer strtok = new StringTokenizer(rule, "]");
    rule = strtok.nextToken();

    strtok = new StringTokenizer (rule, " ");
    String tok = strtok.nextToken();

    StringTokenizer strtok2 = new StringTokenizer (tok, ".");
    tok = strtok2.nextToken();

    if (tok != null && tok.equals ("property")) {
      String propName = strtok2.nextToken();
      String oper = strtok.nextToken();
      String propValue = strtok.nextToken();
      log.info ("propName = " + propName);
      log.info ("oper     = " + oper);
      log.info ("propValue = " + propValue);
      Object prop = witem.getProperty(propName);
      if (prop == null) {
        throw new XflowException ("Property does not exist: " + propName);
      }
      result = new ExpressionEval().applyRule (prop, oper, propValue);

    } else {
      String payloadType = witem.getPayloadType();
      if (payloadType == null) {
        throw new XflowException ("Payload type not defined in work item");
      }

      // If payload is XML, evaluate rule on XML payload
      if (payloadType.equals(WorkItem.XML)) {
        result = evaluateRuleOnXmlPayload ((String)witem.getPayload(), r);
        // Java Payload
      } else if (payloadType.equals(WorkItem.JAVA_OBJECT)) {
        result = new ExpressionEval().evaluateRule (witem.getPayload(), rule);
      }
    }
    return result;
  }

  public static boolean evaluateRuleOnXmlPayload (String xml, String rule) throws JaxenException, IOException, ParserConfigurationException, SAXException {
     return XPathRuleEngine.executeRule (xml, rule, null);  // Not name space aware for now
  }

}
