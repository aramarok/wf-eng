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
import wf.exceptions.ExceptieWF;
import wf.xml.WFXMLTagAndAttributeConstants;

public class ManagerEvenimente implements MessageListener {

    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {

	String fisier = args[0];
	if (fisier == null) {
	    System.exit(0);
	}

	Properties proprietati = new Properties();
	try {
	    FileInputStream fail = new FileInputStream(fisier);
	    proprietati.load(fail);
	    fail.close();
	} catch (FileNotFoundException ex) {
	    return;
	} catch (IOException e) {
	    return;
	}

	new ManagerEvenimente(proprietati);
    }

    @SuppressWarnings("unused")
    private InregistrareJMS subs;

    public ManagerEvenimente(final Properties props) {
	try {
	    ConexiuneTopicJMS.initialize();

	    this.subs = new InregistrareJMS(this, AppConfig.getEventsTopic(),
		    null);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	} catch (JMSException e) {
	    e.printStackTrace();
	}
    }

    private void executeQuery(final String interogare) {

	java.sql.Connection conexiune = null;
	try {
	    conexiune = Db.getConnection();
	    Statement stmt = conexiune.createStatement();
	    stmt.execute(interogare);
	    stmt.close();
	} catch (Exception e) {
	} finally {
	    if (conexiune != null) {
		Db.returnConnection(conexiune);
	    }
	}
    }

    private int executeQuery(final String interogare, final String coloana,
	    final String tabela) {

	java.sql.Connection conexiune = null;
	String ex = null;
	try {

	    conexiune = Db.getConnection();
	    Statement stmt = conexiune.createStatement();
	    stmt.execute(interogare);

	    ResultSet rs = stmt.executeQuery("SELECT max(" + coloana + "),"
		    + coloana + " from " + tabela);
	    while (rs.next()) {
		try {
		    ex = rs.getString(coloana).trim();
		} catch (NumberFormatException e) {
		    System.err.println("bad event ID");
		}
	    }
	    rs.close();
	    stmt.close();
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	} finally {
	    if (conexiune != null) {
		Db.returnConnection(conexiune);
	    }
	}
	return Integer.parseInt(ex);
    }

    private void insertAbortedEvent(final Element eveniment) {
	this.insertEventTable(eveniment);
    }

    private void insertCompletedEvent(final Element eveniment) {
	this.insertEventTable(eveniment);
    }

    private void insertDeployedEvent(final Element eveniment) {
	this.insertEventTable(eveniment);
    }

    private int insertEventTable(final Element eveniment) {
	String idEveniment = null, tipEveniment, numeWF, utilizator, data;
	int versiuneWF, idInstanta, wfParinte;
	String[] extra;

	extra = this.retriveEventInfo(eveniment);

	for (String element : extra) {
	    System.out.println("in info " + element);
	}
	tipEveniment = extra[0];
	data = extra[1];
	numeWF = extra[2];
	versiuneWF = Integer.parseInt(extra[3]);
	idInstanta = (extra[4] == null) ? -1 : Integer.parseInt(extra[4]);
	wfParinte = (extra[5] == null) ? -1 : Integer.parseInt(extra[5]);
	if (extra[6] == null) {
	    utilizator = "system";
	} else {
	    utilizator = extra[6];
	}

	String query = "INSERT INTO evt_event VALUES(null, " + "'"
		+ tipEveniment + "','" + data + "','" + numeWF + "',"
		+ versiuneWF + "," + idInstanta + "," + wfParinte + ","
		+ ((utilizator == null) ? "null)" : ("'" + utilizator + "')"));

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
		    idEveniment = rs.getString("eventId").trim();
		    System.out.println(idEveniment);
		    rs.next();
		} catch (NumberFormatException e) {
		    System.err.println(e.getMessage());
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
	System.out.println("evenimentul e " + idEveniment);
	return Integer.parseInt(idEveniment);
    }

    private void insertEventWorkItemPropertiesTable(final int idWI,
	    final String pname, final String ptype, final String pvalue) {
	String query = "INSERT INTO evt_EventWorkItemProperties VALUES(" + idWI
		+ ",'" + pname + "','" + ptype + "','" + pvalue + "')";
	this.executeQuery(query);
    }

    private int insertEventWorkItemTable(final int eventId,
	    final int workItemInternalId, final int workItemId,
	    final String payloadType, final String payload) {

	Integer internalId = new Integer(workItemInternalId);

	String query = "INSERT INTO evt_EventWorkItem VALUES(" + eventId + ","
		+ ((workItemInternalId == -1) ? "null" : internalId.toString())
		+ "," + workItemId + ",'" + payloadType + "','" + payload
		+ "')";
	return this.executeQuery(query, "workItemInternalId",
		"evt_EventWorkItem");

    }

    private void insertNodeTransitionEvent(final Element event) {
	int eventId, workItemInternalId;
	Element workItem = (Element) event.getElementsByTagName("ItemModel")
		.item(0);
	String[] info = this.retriveWorkItemInfo(workItem);

	int workItemId = Integer.parseInt(info[0]);
	String payloadType = info[1];
	String payload = info[2];

	eventId = this.insertEventTable(event);
	workItemInternalId = this.insertEventWorkItemTable(eventId, -1,
		workItemId, payloadType, payload);
	info = this.retrieveTransitionInfo(event);
	String fromName = info[0];
	String fromType = info[1];
	String toName = info[2];
	String toType = info[3];
	this.insertNodeTransitionEventTable(eventId, fromName, fromType,
		toName, toType);

	Element ps = (Element) workItem.getElementsByTagName("Properties")
		.item(0);
	NodeList proplist = ps.getElementsByTagName("Property");
	for (int i = 0; i < proplist.getLength(); i++) {
	    String pname, ptype, pvalue;
	    Element p = (Element) proplist.item(i);
	    info = this.retriveWorkItemPropertyInfo(p);
	    pname = info[0];
	    ptype = info[1];
	    pvalue = info[2];

	    this.insertEventWorkItemPropertiesTable(workItemInternalId, pname,
		    ptype, pvalue);

	}
    }

    private void insertNodeTransitionEventTable(final int eventId,
	    final String fname, final String ftype, final String tname,
	    final String ttype) {
	String query = "INSERT INTO evt_NodeTransitionEvent VALUES(" + eventId
		+ ",'" + fname + "','" + ftype + "','" + tname + "','" + ttype
		+ "')";
	System.out.println(query);
	this.executeQuery(query);
    }

    private void insertProcessTimedOutTable(final int eventId,
	    final String processName) {
	String query = "INSERT INTO evt_ProcessTimedOutEvent VALUES(" + eventId
		+ ",'" + processName + "')";
	this.executeQuery(query);
    }

    private void insertResumedEvent(final Element event) {
	this.insertEventTable(event);
    }

    private void insertStartedEvent(final Element event) {
	int eventId, workItemInternalId;
	Element workItem = (Element) event.getElementsByTagName("ItemModel")
		.item(0);
	String[] info = this.retriveWorkItemInfo(workItem);

	int workItemId = Integer.parseInt(info[0]);
	String payloadType = info[1];
	String payload = info[2];

	eventId = this.insertEventTable(event);

	workItemInternalId = this.insertEventWorkItemTable(eventId, -1,
		workItemId, payloadType, payload);
	Element ps = (Element) workItem.getElementsByTagName("Properties")
		.item(0);
	NodeList proplist = ps.getElementsByTagName("Property");
	for (int i = 0; i < proplist.getLength(); i++) {
	    String pname, ptype, pvalue;
	    Element p = (Element) proplist.item(i);
	    info = this.retriveWorkItemPropertyInfo(p);
	    pname = info[0];
	    ptype = info[1];
	    pvalue = info[2];

	    this.insertEventWorkItemPropertiesTable(workItemInternalId, pname,
		    ptype, pvalue);

	}
    }

    private void insertSupendedEvent(final Element event) {
	this.insertEventTable(event);
    }

    private void insertTimeOutEvent(final Element event) {
	int eventId;
	String processName = event.getElementsByTagName("ProcessName").item(0)
		.getFirstChild().getNodeValue();

	eventId = this.insertEventTable(event);
	this.insertProcessTimedOutTable(eventId, processName);
    }

    private void insertUpdatedEvent(final Element event) {
	int eventId;
	Element var = (Element) event.getElementsByTagName("Variable").item(0);

	String name = var
		.getAttribute(WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE);
	String type = var
		.getAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE);
	String value = var.getFirstChild().getNodeValue();

	eventId = this.insertEventTable(event);

	this.insertVariableUpdateEventTable(eventId, name, type, value);
    }

    private void insertVariableUpdateEventTable(final int eventId,
	    final String name, final String type, final String value) {
	String query = "INSERT INTO evt_VariableUpdateEvent VALUES(" + eventId
		+ ",'" + name + "','" + type + "','" + value + "')";
	this.executeQuery(query);
    }

    public void onMessage(final Message evt) {
	String evtXML = null;
	System.out.println("am mesaj...");
	try {
	    if (evt instanceof TextMessage) {
		evtXML = ((TextMessage) evt).getText();
		System.out.println(evtXML);
	    } else {
		System.out.println("Mesaj aiurea.");
		return;
	    }
	} catch (JMSException e) {
	    e.printStackTrace();
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
	    if ((els != null) && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertSupendedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("ProcessTimedOutEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertTimeOutEvent(event);
	    } else if (((els = body.getElementsByTagName("ModelDeployedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertDeployedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("VariableUpdatedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertUpdatedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("WorkflowAbortedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertAbortedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("WorkflowResumedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertResumedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("WorkflowCompletedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertCompletedEvent(event);
	    } else if (((els = body
		    .getElementsByTagName("WorkflowStartedEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertStartedEvent(event);
	    } else if (((els = body.getElementsByTagName("NodeTransitionEvent")) != null)
		    && (els.item(0) != null)) {
		event = (Element) els.item(0);
		this.insertNodeTransitionEvent(event);
	    } else {
		System.err.println("unknown event types");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private String[] retrieveTransitionInfo(final Element event) {
	String info[] = new String[4];

	Element from = (Element) event.getElementsByTagName("From").item(0);
	Element to = (Element) event.getElementsByTagName("To").item(0);

	info[0] = from.getAttribute("nodeName");
	info[1] = from.getAttribute("nodeType");

	info[2] = to.getAttribute("nodeName");
	info[3] = to.getAttribute("nodeType");

	return info;
    }

    private String[] retriveEventInfo(final Element event) {
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
	if (((pwi = event.getElementsByTagName("ParentWorkflowInstanceId")
		.item(0)) != null)
		&& (pwi.getFirstChild() != null)) {
	    info[5] = pwi.getFirstChild().getNodeValue();
	} else {
	    info[5] = null;
	}

	if (((user = event.getElementsByTagName("Utilizator").item(0)) != null)
		&& (user.getFirstChild() != null)) {
	    info[6] = user.getFirstChild().getNodeValue();
	} else {
	    info[6] = null;
	}
	return info;
    }

    private String[] retriveWorkItemInfo(final Element workItem) {
	String info[] = new String[3];

	info[0] = workItem.getElementsByTagName("Integer").item(0)
		.getFirstChild().getNodeValue();
	Element pld = (Element) workItem.getElementsByTagName("Payload")
		.item(0);

	info[1] = pld
		.getAttribute(WFXMLTagAndAttributeConstants.TYPE_ATTRIBUTE);

	NodeList plist = pld.getChildNodes();
	info[2] = "";
	for (int i = 0; i < plist.getLength(); i++) {
	    info[2] += plist.item(i).toString();
	}
	return info;
    }

    private String[] retriveWorkItemPropertyInfo(final Element property) {
	String info[] = new String[3];
	info[0] = property.getElementsByTagName("Name").item(0).getFirstChild()
		.getNodeValue();
	info[1] = property.getElementsByTagName("Type").item(0).getFirstChild()
		.getNodeValue();
	info[2] = property.getElementsByTagName("Value").item(0)
		.getFirstChild().getNodeValue();
	return info;
    }
}
