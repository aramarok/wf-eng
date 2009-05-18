package wf.jms;

import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import wf.cfg.AppConfig;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;
import wf.xml.GraphSerializer;
import wf.xml.WFXMLTagAndAttributeConstants;

public class PublisherEvenimente {

    private boolean doNotPublish = false;

    @SuppressWarnings("unchecked")
    private void buildWorkItemXML(final ItemModel witem, final String docNS,
	    final Document doc, final Element wkItem) {
	if (this.doNotPublish) {
	    return;
	}
	org.w3c.dom.Node n;
	Element wkItemId = doc.createElementNS(docNS, "Integer");
	int id = witem.getId().intValue();
	n = doc.createTextNode(new Integer(id).toString());
	wkItemId.appendChild(n);
	wkItem.appendChild(wkItemId);

	Element payload = doc.createElementNS(docNS, "Payload");
	String ptype = witem.getPayloadType();
	payload.setAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE,
		ptype);
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
	    String type = this.getType(value);
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

    public Document createXMLDoc() throws ExceptieWF {
	Document xmldoc = null;
	Element e = null;
	String ns_env = "http://schemas.xmlsoap.org/soap/envelope/";
	String ns_xsi = "http://www.w3c.org/2001/XMLSchema-instance";
	String ns_enc = "http://schemas.xmlsoap.org/soap/encoding/";
	String ns_xsd = "http://www.w3c.org/2001/XMLSchema";
	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory
		    .newInstance();
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
	    throw new ExceptieWF(ex);
	}
	return xmldoc;
    }

    private String getType(final Object o) {
	String name = o.getClass().getName();
	return name.substring(name.lastIndexOf(".") + 1);
    }

    public boolean isDoNotPublish() {
	return this.doNotPublish;
    }

    @SuppressWarnings("unchecked")
    private void publish(final String msg, final String topicName,
	    final List props) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	try {
	    JMSTopicConnection.initialize();
	    JMSPublisher.send(topicName, msg, props);
	} catch (Exception e) {
	    throw new ExceptieWF(e.getMessage(), e);
	}
    }

    public void publishModelDeployedEvent(final String workflowName,
	    final int workflowVersion, final String user) throws Exception {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";

	Document doc = this.createXMLDoc();
	Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		.item(0);

	Element event = doc.createElementNS(docNS, "ModelDeployedEvent");
	event.setAttribute("xmlns", "http://wf.net/events");
	body.appendChild(event);

	Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	org.w3c.dom.Node n = doc
		.createTextNode(wf.util.DateUtil.getTimestamp());
	timeStamp.appendChild(n);
	event.appendChild(timeStamp);

	Element usere = doc.createElementNS(docNS, "Utilizator");
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
	String xmlString = GraphSerializer.serialize(doc.getDocumentElement());
	this.publish(xmlString, AppConfig.getEventsTopic(), null);

    }

    public void publishNodeTransitionEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String fromNodeName, final String toNodeName,
	    final ItemModel witem) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "NodeTransitionEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
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
	    n = doc.createTextNode((workflowId).toString());
	    wkfId.appendChild(n);
	    event.appendChild(wkfId);

	    Element from = doc.createElementNS(docNS, "From");
	    from.setAttribute("nodeId", "0");
	    from.setAttribute("nodeName", fromNodeName);
	    from.setAttribute("nodeType", "");
	    event.appendChild(from);

	    Element to = doc.createElementNS(docNS, "To");
	    to.setAttribute("nodeId", "0");
	    to.setAttribute("nodeName", toNodeName);
	    to.setAttribute("nodeType", "");
	    event.appendChild(to);

	    Element wkItem = doc.createElementNS(docNS, "ItemModel");

	    this.buildWorkItemXML(witem, docNS, doc, wkItem);

	    event.appendChild(wkItem);
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}

    }

    public void publishProcessTimeoutEvent(final String workflowName,
	    final int workflowVersion, final int workflowId,
	    final String processName) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "ProcessTimedOutEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
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
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}
    }

    public void publishVariableUpdatedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String variableName, final Object variableValue)
	    throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "VariableUpdatedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
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
	    n = doc.createTextNode(workflowId.toString());
	    wkfId.appendChild(n);
	    event.appendChild(wkfId);

	    Element var = doc.createElementNS(docNS, "Variable");
	    String type = this.getType(variableValue);
	    n = doc.createTextNode(wf.util.HexUtil
		    .hexEncodeObject(variableValue));
	    var.appendChild(n);
	    var.setAttribute(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE,
		    variableName);
	    var
		    .setAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE,
			    type);
	    event.appendChild(var);
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}
    }

    public void publishWorkflowAbortedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String user) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "WorkflowAbortedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
	    timeStamp.appendChild(n);
	    event.appendChild(timeStamp);

	    Element usere = doc.createElementNS(docNS, "Utilizator");
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
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}

    }

    public void publishWorkflowCompletedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String user) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc
		    .createElementNS(docNS, "WorkflowCompletedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
	    timeStamp.appendChild(n);
	    event.appendChild(timeStamp);

	    Element usere = doc.createElementNS(docNS, "Utilizator");
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
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}
    }

    public void publishWorkflowResumedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String user) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "WorkflowResumedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
	    timeStamp.appendChild(n);
	    event.appendChild(timeStamp);

	    Element usere = doc.createElementNS(docNS, "Utilizator");
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
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}
    }

    public void publishWorkflowStartedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final Integer parentWorkflowId, final String user,
	    final ItemModel witem) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc.createElementNS(docNS, "WorkflowStartedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
	    timeStamp.appendChild(n);
	    event.appendChild(timeStamp);

	    Element usere = doc.createElementNS(docNS, "Utilizator");
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
	    n = doc.createTextNode((workflowId).toString());
	    wkfId.appendChild(n);
	    event.appendChild(wkfId);

	    Element pId = doc
		    .createElementNS(docNS, "ParentWorkflowInstanceId");
	    if (parentWorkflowId.intValue() == -1) {
		event.appendChild(pId);
	    } else {
		n = doc.createTextNode((parentWorkflowId).toString());
		pId.appendChild(n);
		event.appendChild(pId);
	    }

	    Element wkItem = doc.createElementNS(docNS, "ItemModel");

	    this.buildWorkItemXML(witem, docNS, doc, wkItem);

	    event.appendChild(wkItem);
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}

    }

    public void publishWorkflowSuspendedEvent(final String workflowName,
	    final int workflowVersion, final Integer workflowId,
	    final String user) throws ExceptieWF {
	if (this.doNotPublish) {
	    return;
	}
	String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
	try {
	    Document doc = this.createXMLDoc();
	    Element body = (Element) doc.getElementsByTagNameNS(docNS, "Body")
		    .item(0);

	    Element event = doc
		    .createElementNS(docNS, "WorkflowSuspendedEvent");
	    event.setAttribute("xmlns", "http://wf.net/events");
	    body.appendChild(event);

	    Element timeStamp = doc.createElementNS(docNS, "Timestamp");
	    org.w3c.dom.Node n = doc.createTextNode(wf.util.DateUtil
		    .getTimestamp());
	    timeStamp.appendChild(n);
	    event.appendChild(timeStamp);

	    Element usere = doc.createElementNS(docNS, "Utilizator");
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
	    String xmlString = GraphSerializer.serialize(doc
		    .getDocumentElement());
	    this.publish(xmlString, AppConfig.getEventsTopic(), null);
	} catch (Exception ex) {
	    throw new ExceptieWF(ex);
	}
    }

    public void setDoNotPublish(final boolean doNotPublish) {
	this.doNotPublish = doNotPublish;
    }
}
