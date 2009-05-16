package wf.jms;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import wf.cfg.AppConfig;
import wf.exceptions.WorkFlowException;
import wf.model.WorkItem;
import wf.xml.GraphSerializer;
import wf.xml.WFXMLTagAndAttributeConstants;

public class EventsPublisher {

	private boolean doNotPublish = false;

	public boolean isDoNotPublish() {
		return doNotPublish;
	}

	public void setDoNotPublish(boolean doNotPublish) {
		this.doNotPublish = doNotPublish;
	}

	public void publishModelDeployedEvent(String workflowName,
			int workflowVersion, String user) throws Exception {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";

		Document doc = createXMLDoc();
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
		String xmlString = GraphSerializer.serialize(doc.getDocumentElement());
		publish(xmlString, AppConfig.getEventsTopic(), null);

	}

	public void publishWorkflowStartedEvent(String workflowName,
			int workflowVersion, Integer workflowId, Integer parentWorkflowId,
			String user, WorkItem witem) throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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

			Element wkItem = doc.createElementNS(docNS, "WorkItem");

			buildWorkItemXML(witem, docNS, doc, wkItem);

			event.appendChild(wkItem);
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}

	}

	private void buildWorkItemXML(WorkItem witem, String docNS, Document doc,
			Element wkItem) {
		if (doNotPublish)
			return;
		org.w3c.dom.Node n;
		Element wkItemId = doc.createElementNS(docNS, "Integer");
		int id = witem.getId().intValue();
		n = doc.createTextNode(new Integer(id).toString());
		wkItemId.appendChild(n);
		wkItem.appendChild(wkItemId);

		Element payload = doc.createElementNS(docNS, "Payload");
		String ptype = witem.getPayloadType();
		payload.setAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE, ptype);
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

	public void publishWorkflowAbortedEvent(String workflowName,
			int workflowVersion, Integer workflowId, String user)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}

	}

	public void publishWorkflowSuspendedEvent(String workflowName,
			int workflowVersion, Integer workflowId, String user)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}
	}

	public void publishWorkflowResumedEvent(String workflowName,
			int workflowVersion, Integer workflowId, String user)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}
	}

	public void publishWorkflowCompletedEvent(String workflowName,
			int workflowVersion, Integer workflowId, String user)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}
	}

	public void publishNodeTransitionEvent(String workflowName,
			int workflowVersion, Integer workflowId, String fromNodeName,
			String toNodeName, WorkItem witem) throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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

			Element wkItem = doc.createElementNS(docNS, "WorkItem");

			buildWorkItemXML(witem, docNS, doc, wkItem);

			event.appendChild(wkItem);
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}

	}

	public void publishVariableUpdatedEvent(String workflowName,
			int workflowVersion, Integer workflowId, String variableName,
			Object variableValue) throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			String type = getType(variableValue);
			n = doc.createTextNode(wf.util.HexUtil
					.hexEncodeObject(variableValue));
			var.appendChild(n);
			var.setAttribute(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE, variableName);
			var.setAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE, type);
			event.appendChild(var);
			String xmlString = GraphSerializer.serialize(doc
					.getDocumentElement());
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}
	}

	public void publishProcessTimeoutEvent(String workflowName,
			int workflowVersion, int workflowId, String processName)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		String docNS = "http://schemas.xmlsoap.org/soap/envelope/";
		try {
			Document doc = createXMLDoc();
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
			publish(xmlString, AppConfig.getEventsTopic(), null);
		} catch (Exception ex) {
			throw new WorkFlowException(ex);
		}
	}

	private String getType(Object o) {
		String name = o.getClass().getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public Document createXMLDoc() throws WorkFlowException {
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
			throw new WorkFlowException(ex);
		}
		return xmldoc;
	}

	private void publish(String msg, String topicName, List props)
			throws WorkFlowException {
		if (doNotPublish)
			return;
		try {
			JMSTopicConnection.initialize();
			JMSPublisher.send(topicName, msg, props);
		} catch (Exception e) {
			throw new WorkFlowException(e.getMessage(), e);
		}
	}
}
