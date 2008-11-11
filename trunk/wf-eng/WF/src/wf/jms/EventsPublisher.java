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
package wf.jms;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import wf.cfg.XflowConfig;
import wf.exceptions.XflowException;
import wf.model.WorkItem;
import wf.xml.XflowGraphSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Iterator;
import java.util.List;

/**
 * @author xzma
 * The methods in this class are called by the XFlow server to publish an event.
 */
public class EventsPublisher {

  private boolean doNotPublish = false;

  public boolean isDoNotPublish() {
    return doNotPublish;
  }

  public void setDoNotPublish(boolean doNotPublish) {
    this.doNotPublish = doNotPublish;
  }


  public void publishModelDeployedEvent(
      String workflowName,
      int workflowVersion,
      String user)
      throws Exception {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";


    Document doc = createXMLDoc();
    Element body =
        (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

    Element event = doc.createElementNS(docNS, "ModelDeployedEvent");
    event.setAttribute("xmlns", "http://xflow.net/events");
    body.appendChild(event);

    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
    org.w3c.dom.Node n =
        doc.createTextNode(wf.util.DateUtil.getTimestamp());
    timeStamp.appendChild(n);
    event.appendChild(timeStamp);

    Element usere = doc.createElementNS(docNS, "User");
    if (user == null) {
      event.appendChild(usere);
    } else {
      n = doc.createTextNode(user);
      usere.appendChild(n);
      event.appendChild(usere);
    }

    Element wkfn = doc.createElementNS(docNS, "WorkflowName");
    if (workflowName == null) {
      event.appendChild(wkfn);
    } else {
      n = doc.createTextNode(workflowName);
      wkfn.appendChild(n);
      event.appendChild(wkfn);
    }

    Element version = doc.createElementNS(docNS, "WorkflowVersion");

    n = doc.createTextNode(new Integer(workflowVersion).toString());
    version.appendChild(n);
    event.appendChild(version);

    //publish the xml string
    String xmlString =
        XflowGraphSerializer.serialize(doc.getDocumentElement());
    publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
    //System.out.println(XflowGraphSerializer.serialize(body));
    //System.out.println(xmlString);

  }

  public void publishWorkflowStartedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      Integer parentWorkflowId,
      String user,
      WorkItem witem)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "WorkflowStartedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element usere = doc.createElementNS(docNS, "User");
      if (user == null) {
        event.appendChild(usere);
      } else {
        n = doc.createTextNode(user);
        usere.appendChild(n);
        event.appendChild(usere);
      }

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode( (workflowId).toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      Element pId =
          doc.createElementNS(docNS, "ParentWorkflowInstanceId");
      if (parentWorkflowId.intValue() == -1) {
        event.appendChild(pId);
      } else {
        n = doc.createTextNode( (parentWorkflowId).toString());
        pId.appendChild(n);
        event.appendChild(pId);
      }

      Element wkItem = doc.createElementNS(docNS, "WorkItem");

      buildWorkItemXML(witem, docNS, doc, wkItem);

      event.appendChild(wkItem);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }

  }

  private void buildWorkItemXML(
      WorkItem witem,
      String docNS,
      Document doc,
      Element wkItem) {
    if( doNotPublish ) return;
    org.w3c.dom.Node n;
    Element wkItemId = doc.createElementNS(docNS, "Integer");
    int id = witem.getId().intValue();
    n = doc.createTextNode(new Integer(id).toString());
    wkItemId.appendChild(n);
    wkItem.appendChild(wkItemId);

    Element payload = doc.createElementNS(docNS, "Payload");
    String ptype = witem.getPayloadType();
    payload.setAttribute("type", ptype);
    Object pd = witem.getPayload();
    if (pd == null) {
      wkItem.appendChild(payload);
    } else {
      n = doc.createTextNode(wf.util.HexUtil.hexEncodeObject(pd));
      payload.appendChild(n);
      wkItem.appendChild(payload);
    }
    Element props = doc.createElementNS(docNS, "Properties");

    Iterator it = witem.getProperties().keySet().iterator();
    while (it.hasNext()) {
      Element prop = doc.createElementNS(docNS, "Property");
      String name = (String) it.next();
      Object value = witem.getProperty(name);
      String type = getType(value);
      String vl = wf.util.HexUtil.hexEncodeObject(value);

      Element nm = doc.createElementNS(docNS, "Name");
      n = doc.createTextNode(name);
      nm.appendChild(n);
      prop.appendChild(nm);

      Element tp = doc.createElementNS(docNS, "Type");
      n = doc.createTextNode(type);
      tp.appendChild(n);
      prop.appendChild(tp);

      Element v = doc.createElementNS(docNS, "Value");
      n = doc.createTextNode(vl);
      v.appendChild(n);
      prop.appendChild(v);

      props.appendChild(prop);
    }

    wkItem.appendChild(props);

  }

  public void publishWorkflowAbortedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String user)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "WorkflowAbortedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element usere = doc.createElementNS(docNS, "User");
      if (user == null) {
        event.appendChild(usere);
      } else {
        n = doc.createTextNode(user);
        usere.appendChild(n);
        event.appendChild(usere);
      }

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode(workflowId.toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }

  }

  public void publishWorkflowSuspendedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String user)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event =
          doc.createElementNS(docNS, "WorkflowSuspendedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element usere = doc.createElementNS(docNS, "User");
      if (user == null) {
        event.appendChild(usere);
      } else {
        n = doc.createTextNode(user);
        usere.appendChild(n);
        event.appendChild(usere);
      }

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode(workflowId.toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
  }

  public void publishWorkflowResumedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String user)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "WorkflowResumedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element usere = doc.createElementNS(docNS, "User");
      if (user == null) {
        event.appendChild(usere);
      } else {
        n = doc.createTextNode(user);
        usere.appendChild(n);
        event.appendChild(usere);
      }

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode(workflowId.toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
  }

  public void publishWorkflowCompletedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String user)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event =
          doc.createElementNS(docNS, "WorkflowCompletedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element usere = doc.createElementNS(docNS, "User");
      if (user == null) {
        event.appendChild(usere);
      } else {
        n = doc.createTextNode(user);
        usere.appendChild(n);
        event.appendChild(usere);
      }

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode( workflowId.toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
  }

  public void publishNodeTransitionEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String fromNodeName,
      String toNodeName,
      WorkItem witem)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "NodeTransitionEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode( (workflowId).toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      Element from = doc.createElementNS(docNS, "From");
      from.setAttribute("nodeId", "0");  // obsolete
      from.setAttribute("nodeName", fromNodeName);
      from.setAttribute("nodeType", ""); // obsolete
      event.appendChild(from);

      Element to = doc.createElementNS(docNS, "To");
      to.setAttribute("nodeId", "0"); // obsolete
      to.setAttribute("nodeName", toNodeName);
      to.setAttribute("nodeType", ""); // obsolete
      event.appendChild(to);

      Element wkItem = doc.createElementNS(docNS, "WorkItem");

      buildWorkItemXML(witem, docNS, doc, wkItem);

      event.appendChild(wkItem);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }

  }

  public void publishVariableUpdatedEvent(
      String workflowName,
      int workflowVersion,
      Integer workflowId,
      String variableName,
      Object variableValue)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "VariableUpdatedEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode( workflowId.toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      Element var = doc.createElementNS(docNS, "Variable");
      String type = getType(variableValue);
      n =
          doc.createTextNode(
              wf.util.HexUtil.hexEncodeObject(variableValue));
      var.appendChild(n);
      var.setAttribute("name", variableName);
      var.setAttribute("type", type);
      event.appendChild(var);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
  }

  public void publishProcessTimeoutEvent(
      String workflowName,
      int workflowVersion,
      int workflowId,
      String processName)
      throws XflowException {
    if( doNotPublish ) return;
    String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
    try {
      Document doc = createXMLDoc();
      Element body =
          (Element) doc.getElementsByTagNameNS(docNS, "Body").item(0);

      Element event = doc.createElementNS(docNS, "ProcessTimedOutEvent");
      event.setAttribute("xmlns", "http://xflow.net/events");
      body.appendChild(event);

      Element timeStamp = doc.createElementNS(docNS, "Timestamp");
      org.w3c.dom.Node n =
          doc.createTextNode(wf.util.DateUtil.getTimestamp());
      timeStamp.appendChild(n);
      event.appendChild(timeStamp);

      Element wkfn = doc.createElementNS(docNS, "WorkflowName");
      if (workflowName == null) {
        event.appendChild(wkfn);
      } else {
        n = doc.createTextNode(workflowName);
        wkfn.appendChild(n);
        event.appendChild(wkfn);
      }

      Element version = doc.createElementNS(docNS, "WorkflowVersion");
      n = doc.createTextNode(new Integer(workflowVersion).toString());
      version.appendChild(n);
      event.appendChild(version);

      Element wkfId = doc.createElementNS(docNS, "WorkflowInstanceId");
      n = doc.createTextNode(new Integer(workflowId).toString());
      wkfId.appendChild(n);
      event.appendChild(wkfId);

      Element pname = doc.createElementNS(docNS, "ProcessName");
      n = doc.createTextNode(processName);
      pname.appendChild(n);
      event.appendChild(pname);

      //publish the xml string
      String xmlString =
          XflowGraphSerializer.serialize(doc.getDocumentElement());
      publish(xmlString, XflowConfig.XFLOW_EVENT_TOPIC(), null);
      //System.out.println(XflowGraphSerializer.serialize(body));
      //System.out.println(xmlString);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
  }

  private String getType(Object o) {
    String name = o.getClass().getName();
    return name.substring(name.lastIndexOf(".") + 1);
  }

  public Document createXMLDoc() throws XflowException {
    Document xmldoc = null;
    Element e = null;
    String ns_env = "http://schemas.xmlsoap.org/soap/envelope/";
    String ns_xsi = "http://www.w3c.org/2001/XMLSchema-instance";
    String ns_enc = "http://schemas.xmlsoap.org/soap/encoding/";
    String ns_xsd = "http://www.w3c.org/2001/XMLSchema";
    try {
      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      DOMImplementation impl = builder.getDOMImplementation();
            
      xmldoc = impl.createDocument(ns_env, "SOAP-ENV:Envelope", null);
      Element root = xmldoc.getDocumentElement();
      root.setAttribute("xmlns:SOAP-ENC", ns_enc);
      root.setAttribute("xmlns:xsi", ns_xsi);
      root.setAttribute("xmlns:SOAP-ENV", ns_env);
      root.setAttribute("xmlns:xsd", ns_xsd);
      e = xmldoc.createElementNS(ns_env, "SOAP-ENV:Header");
      root.appendChild(e);
      e = xmldoc.createElementNS(ns_env, "SOAP-ENV:Body");
      root.appendChild(e);
    } catch (Exception ex) {
      throw new XflowException(ex);
    }
    return xmldoc;
  }

  private void publish(String msg, String topicName, List props)
      throws XflowException {
    if( doNotPublish ) return;
    try {
      JMSTopicConnection.initialize();
      JMSPublisher.send (topicName, msg, props);
    } catch (Exception e) {
      throw new XflowException(e.getMessage(), e );
    }
  }
}
