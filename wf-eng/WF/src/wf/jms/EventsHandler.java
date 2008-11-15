package wf.jms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import wf.cfg.AppConfig;
import wf.db.Db;
import wf.exceptions.WorkFlowException;

public class EventsHandler implements MessageListener {

	private JMSSubscriber subscriber;

	public EventsHandler(Properties props) {
		try {
			JMSTopicConnection.initialize();

			subscriber = new JMSSubscriber(this, AppConfig.getEventsTopic(),
					null);
		} catch (WorkFlowException e) {
			e.printStackTrace();
			System.out.println("Can't set up JMS Subscription");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message evt) {
		String evtXML = null;
		System.out.println("Got a message...");
		try {
			if (evt instanceof TextMessage) {
				evtXML = ((TextMessage) evt).getText();
				System.out.println(evtXML);
			} else {
				System.out.println("Message not recognized.");
				return;
			}
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Cannot get text message from Received message");
		}

		try {

			String docNS = "http://schemas.xmlsoap.org/soap/envelope/";

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sreader = new StringReader(evtXML);
			InputSource is = new InputSource(sreader);
			Document doc = builder.parse(is);

			NodeList els = doc.getElementsByTagNameNS(docNS, "Body");
			Element body = (Element) els.item(0);

			els = body.getChildNodes();
			Element event;
			els = body.getElementsByTagName("WorkflowSuspendedEvent");
			if (els != null && els.item(0) != null) {
				event = (Element) els.item(0);
				insertSupendedEvent(event);
			} else if ((els = body.getElementsByTagName("ProcessTimedOutEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertTimeOutEvent(event);
			} else if ((els = body.getElementsByTagName("ModelDeployedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertDeployedEvent(event);
			} else if ((els = body.getElementsByTagName("VariableUpdatedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertUpdatedEvent(event);
			} else if ((els = body.getElementsByTagName("WorkflowAbortedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertAbortedEvent(event);
			} else if ((els = body.getElementsByTagName("WorkflowResumedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertResumedEvent(event);
			} else if ((els = body
					.getElementsByTagName("WorkflowCompletedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertCompletedEvent(event);
			} else if ((els = body.getElementsByTagName("WorkflowStartedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertStartedEvent(event);
			} else if ((els = body.getElementsByTagName("NodeTransitionEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertNodeTransitionEvent(event);
			} else {
				System.err.println("unknown event types");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertNodeTransitionEventTable(int eventId, String fname,
			String ftype, String tname, String ttype) {
		String query = "INSERT INTO evt_NodeTransitionEvent VALUES(" + eventId
				+ ",'" + fname + "','" + ftype + "','" + tname + "','" + ttype
				+ "')";
		System.out.println(query);
		executeQuery(query);
	}

	private int insertEventWorkItemTable(int eventId, int workItemInternalId,
			int workItemId, String payloadType, String payload) {

		Integer internalId = new Integer(workItemInternalId);

		String query = "INSERT INTO evt_EventWorkItem VALUES(" + eventId + ","
				+ ((workItemInternalId == -1) ? "null" : internalId.toString())
				+ "," + workItemId + ",'" + payloadType + "','" + payload
				+ "')";
		return executeQuery(query, "workItemInternalId", "evt_EventWorkItem");

	}

	private void insertEventWorkItemPropertiesTable(int workItenInternalId,
			String pname, String ptype, String pvalue) {
		String query = "INSERT INTO evt_EventWorkItemProperties VALUES("
				+ workItenInternalId + ",'" + pname + "','" + ptype + "','"
				+ pvalue + "')";
		executeQuery(query);
	}

	private void insertUpdatedEvent(Element event) {
		int eventId;
		Element var = (Element) event.getElementsByTagName("Variable").item(0);

		String name = var.getAttribute("name");
		String type = var.getAttribute("type");
		String value = var.getFirstChild().getNodeValue();

		eventId = insertEventTable(event);

		insertVariableUpdateEventTable(eventId, name, type, value);
	}

	private void insertVariableUpdateEventTable(int eventId, String name,
			String type, String value) {
		String query = "INSERT INTO evt_VariableUpdateEvent VALUES(" + eventId
				+ ",'" + name + "','" + type + "','" + value + "')";
		executeQuery(query);
	}

	private void insertTimeOutEvent(Element event) {
		int eventId;
		String processName = event.getElementsByTagName("ProcessName").item(0)
				.getFirstChild().getNodeValue();

		eventId = insertEventTable(event);
		insertProcessTimedOutTable(eventId, processName);
	}

	private void insertProcessTimedOutTable(int eventId, String processName) {
		String query = "INSERT INTO evt_ProcessTimedOutEvent VALUES(" + eventId
				+ ",'" + processName + "')";
		executeQuery(query);
	}

	private void insertCompletedEvent(Element event) {
		insertEventTable(event);
	}

	private void insertResumedEvent(Element event) {
		insertEventTable(event);
	}

	private void insertDeployedEvent(Element event) {
		insertEventTable(event);
	}

	private void insertAbortedEvent(Element event) {
		insertEventTable(event);
	}

	private void insertSupendedEvent(Element event) {
		insertEventTable(event);
	}

	private int insertEventTable(Element event) {
		String eventId = null;
		String eventType, workflowName, user;
		String timestamp;
		int workflowVersion, workflowInstanceId, parentWorkflowInstanceId;
		String[] info;

		info = retriveEventInfo(event);

		for (int i = 0; i < info.length; i++) {
			System.out.println("in info " + info[i]);
		}
		eventType = info[0];
		timestamp = info[1];
		workflowName = info[2];
		workflowVersion = Integer.parseInt(info[3]);
		workflowInstanceId = (info[4] == null) ? -1 : Integer.parseInt(info[4]);
		parentWorkflowInstanceId = (info[5] == null) ? -1 : Integer
				.parseInt(info[5]);
		if (info[6] == null) {
			user = "system";
		} else {
			user = info[6];
		}

		String query = "INSERT INTO evt_event VALUES(null, " + "'" + eventType
				+ "','" + timestamp + "','" + workflowName + "',"
				+ workflowVersion + "," + workflowInstanceId + ","
				+ parentWorkflowInstanceId + ","
				+ ((user == null) ? "null)" : ("'" + user + "')"));

		System.out.println(query);

		java.sql.Connection conn = null;
		try {

			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);
			ResultSet rs = st
					.executeQuery("SELECT max(eventId), eventId from evt_event");
			while (rs.next()) {
				try {
					eventId = rs.getString("eventId").trim();
					System.out.println(eventId);
					rs.next();
				} catch (NumberFormatException e) {
					System.err.println("bad event ID");
				}
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				Db.returnConnection(conn);
			}
		}
		System.out.println("eventId is " + eventId);
		return Integer.parseInt(eventId);
	}

	private void executeQuery(String query) {
		System.out.println(query);
		java.sql.Connection conn = null;
		try {

			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);
			st.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				Db.returnConnection(conn);
			}
		}
	}

	private int executeQuery(String query, String columnName, String tableName) {
		System.out.println(query);
		java.sql.Connection conn = null;
		String lastMod = null;
		try {

			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);

			ResultSet rs = st.executeQuery("SELECT max(" + columnName + "),"
					+ columnName + " from " + tableName);
			while (rs.next()) {
				try {
					lastMod = rs.getString(columnName).trim();
				} catch (NumberFormatException e) {
					System.err.println("bad event ID");
				}
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				Db.returnConnection(conn);
			}
		}
		return Integer.parseInt(lastMod);
	}

	private String[] retriveEventInfo(Element event) {
		String info[] = new String[7];

		info[0] = event.getLocalName();
		info[1] = event.getElementsByTagName("Timestamp").item(0)
				.getFirstChild().getNodeValue();
		info[2] = event.getElementsByTagName("WorkflowName").item(0)
				.getFirstChild().getNodeValue();
		info[3] = event.getElementsByTagName("WorkflowVersion").item(0)
				.getFirstChild().getNodeValue();

		if (event.getElementsByTagName("WorkflowInstanceId").item(0) != null) {
			info[4] = event.getElementsByTagName("WorkflowInstanceId").item(0)
					.getFirstChild().getNodeValue();
		} else {
			info[4] = "-1";
		}

		Node pwi, user;
		if ((pwi = event.getElementsByTagName("ParentWorkflowInstanceId").item(
				0)) != null
				&& pwi.getFirstChild() != null) {
			info[5] = pwi.getFirstChild().getNodeValue();
		} else {
			info[5] = null;
		}

		if ((user = event.getElementsByTagName("User").item(0)) != null
				&& user.getFirstChild() != null) {
			info[6] = user.getFirstChild().getNodeValue();
		} else {
			info[6] = null;
		}
		return info;
	}

	private void insertNodeTransitionEvent(Element event) {
		int eventId, workItemInternalId;
		Element workItem = (Element) event.getElementsByTagName("WorkItem")
				.item(0);
		String[] info = retriveWorkItemInfo(workItem);

		int workItemId = Integer.parseInt(info[0]);
		String payloadType = info[1];
		String payload = info[2];

		eventId = insertEventTable(event);
		workItemInternalId = insertEventWorkItemTable(eventId, -1, workItemId,
				payloadType, payload);
		info = retrieveTransitionInfo(event);
		String fromName = info[0];
		String fromType = info[1];
		String toName = info[2];
		String toType = info[3];
		insertNodeTransitionEventTable(eventId, fromName, fromType, toName,
				toType);

		Element ps = (Element) workItem.getElementsByTagName("Properties")
				.item(0);
		NodeList proplist = ps.getElementsByTagName("Property");
		for (int i = 0; i < proplist.getLength(); i++) {
			String pname, ptype, pvalue;
			Element p = (Element) proplist.item(i);
			info = retriveWorkItemPropertyInfo(p);
			pname = info[0];
			ptype = info[1];
			pvalue = info[2];

			insertEventWorkItemPropertiesTable(workItemInternalId, pname,
					ptype, pvalue);

		}
	}

	private void insertStartedEvent(Element event) {
		int eventId, workItemInternalId;
		Element workItem = (Element) event.getElementsByTagName("WorkItem")
				.item(0);
		String[] info = retriveWorkItemInfo(workItem);

		int workItemId = Integer.parseInt(info[0]);
		String payloadType = info[1];
		String payload = info[2];

		eventId = insertEventTable(event);

		workItemInternalId = insertEventWorkItemTable(eventId, -1, workItemId,
				payloadType, payload);
		Element ps = (Element) workItem.getElementsByTagName("Properties")
				.item(0);
		NodeList proplist = ps.getElementsByTagName("Property");
		for (int i = 0; i < proplist.getLength(); i++) {
			String pname, ptype, pvalue;
			Element p = (Element) proplist.item(i);
			info = retriveWorkItemPropertyInfo(p);
			pname = info[0];
			ptype = info[1];
			pvalue = info[2];

			insertEventWorkItemPropertiesTable(workItemInternalId, pname,
					ptype, pvalue);

		}
	}

	private String[] retriveWorkItemPropertyInfo(Element property) {
		String info[] = new String[3];
		info[0] = property.getElementsByTagName("Name").item(0).getFirstChild()
				.getNodeValue();
		info[1] = property.getElementsByTagName("Type").item(0).getFirstChild()
				.getNodeValue();
		info[2] = property.getElementsByTagName("Value").item(0)
				.getFirstChild().getNodeValue();
		return info;
	}

	private String[] retriveWorkItemInfo(Element workItem) {
		String info[] = new String[3];

		info[0] = workItem.getElementsByTagName("Integer").item(0)
				.getFirstChild().getNodeValue();
		Element pld = (Element) workItem.getElementsByTagName("Payload")
				.item(0);

		info[1] = pld.getAttribute("type");

		NodeList plist = pld.getChildNodes();
		info[2] = "";
		for (int i = 0; i < plist.getLength(); i++) {
			info[2] += plist.item(i).toString();
		}
		return info;
	}

	private String[] retrieveTransitionInfo(Element event) {
		String info[] = new String[4];

		Element from = (Element) event.getElementsByTagName("From").item(0);
		Element to = (Element) event.getElementsByTagName("To").item(0);

		info[0] = from.getAttribute("nodeName");
		info[1] = from.getAttribute("nodeType");

		info[2] = to.getAttribute("nodeName");
		info[3] = to.getAttribute("nodeType");

		return info;
	}

	public static void main(String[] args) throws WorkFlowException,
			JMSException {

		String propFileName = args[0];
		if (propFileName == null) {
			System.out
					.println("Usage: xflow.events.EventsHandler <properties file name>");
			System.exit(0);
		}

		Properties props = new Properties();
		try {
			FileInputStream fi = new FileInputStream(propFileName);
			props.load(fi);
			fi.close();
		} catch (FileNotFoundException fx) {
			System.out.print("Property file not found: " + fx.getMessage());
			return;
		} catch (IOException e) {
			System.out.print("Failed to read property file: " + e.getMessage());
			return;
		}

		new EventsHandler(props);
	}
}
