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
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)ARISING IN ANY WAY OUT
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
package xflow.events;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.jms.*;
import javax.naming.*;
import javax.sql.*;
import javax.xml.parsers.*;

import xflow.common.XflowException;
import xflow.messaging.JMSSubscriber;
import xflow.messaging.JMSTopicConnection;
import xflow.util.Db;
import xflow.common.XflowConfig;

/**
 * @author xzma
 * 
 * The event handler will receive the event messages asynchronously 
 * using a JMS topicsubscription and save the event infomation into
 * database.
 */
public class EventsHandler implements MessageListener {

	private JMSSubscriber subscriber;


	/**
	 * Constructor. Properties file is used for getting DB info when EventsHandler is
         * running standalone. 
	 */
	public EventsHandler(Properties props) {
		try {
			JMSTopicConnection.initialize();
			//EventsPersistence.init(props);
			subscriber = new JMSSubscriber(this, XflowConfig.XFLOW_EVENT_TOPIC(), null);
		} catch (XflowException e) {
			e.printStackTrace();
			System.out.println("Can't set up JMS Subscription");
		} catch (JMSException e){
			e.printStackTrace();
		}
	}

	
	/**
	 * the method defined from MessageListener interdace, it monitors 
	 * possible incoming message events
	 */
	public void onMessage(Message evt) {
		String evtXML = null;
		System.out.println("Got a message...");
		try {
			if (evt instanceof TextMessage) {
				evtXML = ((TextMessage) evt).getText();
                                System.out.println (evtXML);
			} else {
				System.out.println("Message not recognized.");
				return;
			}
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println("Cannot get text message from Received message");
		}

		try {
			//namespace of SOAP-ENV
			String docNS = "http://schemas.xmlsoap.org/soap/envelope/";

			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader sreader = new StringReader(evtXML);
			InputSource is = new InputSource(sreader);
			Document doc = builder.parse(is);

			NodeList els = doc.getElementsByTagNameNS(docNS, "Body");
			Element body = (Element) els.item(0); //assumming certain format

			els = body.getChildNodes();
			Element event;
			els = body.getElementsByTagName("WorkflowSuspendedEvent");
			if (els != null && els.item(0) != null) {
				event = (Element) els.item(0);
				insertSupendedEvent(event); //insert into DB
			} else if (
				(els = body.getElementsByTagName("ProcessTimedOutEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertTimeOutEvent(event);
			} else if (
				(els = body.getElementsByTagName("ModelDeployedEvent")) != null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertDeployedEvent(event);
			} else if (
				(els = body.getElementsByTagName("VariableUpdatedEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertUpdatedEvent(event);
			} else if (
				(els = body.getElementsByTagName("WorkflowAbortedEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertAbortedEvent(event);
			} else if (
				(els = body.getElementsByTagName("WorkflowResumedEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertResumedEvent(event);
			} else if (
				(els = body.getElementsByTagName("WorkflowCompletedEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertCompletedEvent(event);
			} else if (
				(els = body.getElementsByTagName("WorkflowStartedEvent"))
					!= null
					&& els.item(0) != null) {
				event = (Element) els.item(0);
				insertStartedEvent(event);
			} else if (
				(els = body.getElementsByTagName("NodeTransitionEvent"))
					!= null
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

	/**
	 * inserts the following parameters into NodeTransitionEvent Table in DB
	 * @param eventId
	 * @param fname
	 * @param ftype
	 * @param tname
	 * @param ttype
	 */
	private void insertNodeTransitionEventTable(
		int eventId,
		String fname,
		String ftype,
		String tname,
		String ttype) {
		String query =
			"INSERT INTO evt_NodeTransitionEvent VALUES("
				+ eventId
				+ ",'"
				+ fname
				+ "','"
				+ ftype
				+ "','"
				+ tname
				+ "','"
				+ ttype
				+ "')";
				System.out.println(query);
		executeQuery(query);
	}

	/**
	 * 
	 * @param eventId
	 * @param workItemInternalId: if = -1, auto-incremented 
	 * @param workItemId
	 * @param payloadType
	 * @param payload
	 * @return workItemInternalId, if it is -1 when passed in, 
	 *  return last modified value(by auto-increment) in db
	 */
	private int insertEventWorkItemTable(
		int eventId,
		int workItemInternalId,
		int workItemId,
		String payloadType,
		String payload) {

		Integer internalId = new Integer(workItemInternalId);

		String query =
			"INSERT INTO evt_EventWorkItem VALUES("
				+ eventId
				+ ","
				+ ((workItemInternalId == -1) ? "null" : internalId.toString())
				+ ","
				+ workItemId
				+ ",'"
				+ payloadType
				+ "','"
				+ payload
				+ "')";
		return executeQuery(query, "workItemInternalId", "evt_EventWorkItem");

	}

	/**
	 * insert work item properties into WorkItemPropertiesTable
	 * @param workItenInternalId
	 * @param pname
	 * @param ptype
	 * @param pvalue
	 */
	private void insertEventWorkItemPropertiesTable(
		int workItenInternalId,
		String pname,
		String ptype,
		String pvalue) {
		String query =
			"INSERT INTO evt_EventWorkItemProperties VALUES("
				+ workItenInternalId
				+ ",'"
				+ pname
				+ "','"
				+ ptype
				+ "','"
				+ pvalue
				+ "')";
		executeQuery(query);
	}

	/**
	 * @param event
	 */
	private void insertUpdatedEvent(Element event) {
		int eventId;
		Element var = (Element) event.getElementsByTagName("Variable").item(0);

		String name = var.getAttribute("name");
		String type = var.getAttribute("type");
		String value = var.getFirstChild().getNodeValue();

		eventId = insertEventTable(event);

		insertVariableUpdateEventTable(eventId, name, type, value);
	}
	
	/**
	 * @param eventId
	 * @param name
	 * @param type
	 * @param value
	 */
	private void insertVariableUpdateEventTable(
		int eventId,
		String name,
		String type,
		String value) {
		String query =
			"INSERT INTO evt_VariableUpdateEvent VALUES("
				+ eventId
				+ ",'"
				+ name
				+ "','"
				+ type
				+ "','"
				+ value
				+ "')";
		executeQuery(query);
	}
	
	/**
	 * insert timeout event into DB, first insert event info into event table, 
	 * get the assigned eventId, then insert timeout info into timeout table
	 * @param event
	 */
	private void insertTimeOutEvent(Element event) {
		int eventId;
		String processName =
			event
				.getElementsByTagName("ProcessName")
				.item(0)
				.getFirstChild()
				.getNodeValue();

		eventId = insertEventTable(event);
		insertProcessTimedOutTable(eventId, processName);
	}

	/**
	 * 
	 * @param eventId
	 * @param processName
	 */
	private void insertProcessTimedOutTable(int eventId, String processName) {
		String query =
			"INSERT INTO evt_ProcessTimedOutEvent VALUES("
				+ eventId
				+ ",'"
				+ processName
				+ "')";
		executeQuery(query);
	}

	/**
	 * @param event
	 */
	private void insertCompletedEvent(Element event) {
		insertEventTable(event);
	}
	
	/**
	 * 
	 * @param event
	 */
	private void insertResumedEvent(Element event) {
		insertEventTable(event);
	}
	/**
	 * 
	 * @param event
	 */
	private void insertDeployedEvent(Element event) {
		insertEventTable(event);
	}
	
	/**
	 * @param event
	 */
	private void insertAbortedEvent(Element event) {
		insertEventTable(event);
	}

	/**
	 * 
	 * @param event
	 */
	private void insertSupendedEvent(Element event) {
		insertEventTable(event);
	}
	
	/**
	 * insert event information into data base
	 * @param event
	 * @return eventId, this id is used by may operation
	 */
	private int insertEventTable(Element event) {
		String eventId = null;
		String eventType, workflowName, user;
		String timestamp;
		int workflowVersion, workflowInstanceId, parentWorkflowInstanceId;
		String[] info;

		info = retriveEventInfo(event);

                for(int i=0; i<info.length; i++) {
		    System.out.println("in info "+info[i]);
                }
		eventType = info[0];
		timestamp = info[1];
		workflowName = info[2];
		workflowVersion = Integer.parseInt(info[3]);
		workflowInstanceId = (info[4] == null) ? -1 : Integer.parseInt(info[4]);
		parentWorkflowInstanceId =
			(info[5] == null) ? -1 : Integer.parseInt(info[5]);
                if (info[6] == null) {
                    user = "system";
                } else {
		    user = info[6];
		}

		String query =
			"INSERT INTO evt_event VALUES(null, "
				+ "'"
				+ eventType
				+ "','"
				+ timestamp
				+ "','"
				+ workflowName
				+ "',"
				+ workflowVersion
				+ ","
				+ workflowInstanceId
				+ ","
				+ parentWorkflowInstanceId
				+ ","
				+ ((user == null) ? "null)" : ("'" + user + "')"));

		System.out.println(query);

		java.sql.Connection conn = null;
		try {
			//now create a query and update db
			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);
			ResultSet rs =
				st.executeQuery("SELECT max(eventId), eventId from evt_event");
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
                System.out.println ("eventId is " + eventId);
		return Integer.parseInt(eventId);
	}

	/**
	 * given the query string, execute the query on DB
	 * @param query
	 */
	private void executeQuery(String query) {
		System.out.println(query);
		java.sql.Connection conn = null;
		try {
			//now create a query and update db
			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);
			st.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
			    Db.returnConnection (conn);
			}
		}
	}

	/**
	 * execute a query by given query string on table "tableName", return the largest 
	 * index for specific colume. It can be use to get the last inserted entry if the 
	 * column is auto-incremented
	 * @param query
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	private int executeQuery(
		String query,
		String columnName,
		String tableName) {
		System.out.println(query);
		java.sql.Connection conn = null;
		String lastMod = null;
		try {
			//now create a query and update db
			conn = Db.getConnection();
			Statement st = conn.createStatement();
			st.execute(query);

			ResultSet rs =
				st.executeQuery(
					"SELECT max("
						+ columnName
						+ "),"
						+ columnName
						+ " from "
						+ tableName);
			while (rs.next()) { // actually only has one row
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
                            Db.returnConnection (conn);
			}
		}
		return Integer.parseInt(lastMod);
	}

	/**
	 * get information for event from soap body element--event
	 * @param event
	 * @return
	 */
	private String[] retriveEventInfo(Element event) {
		String info[] = new String[7];

		info[0] = event.getLocalName();
		info[1] =
			event
				.getElementsByTagName("Timestamp")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		info[2] =
			event
				.getElementsByTagName("WorkflowName")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		info[3] =
			event
				.getElementsByTagName("WorkflowVersion")
				.item(0)
				.getFirstChild()
				.getNodeValue();


                if (event.getElementsByTagName("WorkflowInstanceId").item(0) != null) {
		    info[4] = event
				.getElementsByTagName("WorkflowInstanceId")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		} else {
                    info[4] = "-1";
                }

		Node pwi, user;
		if ((pwi =
			event.getElementsByTagName("ParentWorkflowInstanceId").item(0))
			!= null
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

	/**
	 * involves inserting information into 3 tables:event, eventworkitem, and
	 * eventworkitemproperties table.
	 * @param event
	 */
	private void insertNodeTransitionEvent(Element event) {
		int eventId, workItemInternalId;
		Element workItem =
			(Element) event.getElementsByTagName("WorkItem").item(0);
		String[] info = retriveWorkItemInfo(workItem);

		int workItemId = Integer.parseInt(info[0]);
		String payloadType = info[1];
		String payload = info[2];

		eventId = insertEventTable(event);
		workItemInternalId =
			insertEventWorkItemTable(
				eventId,
				-1,
				workItemId,
				payloadType,
				payload);
                info = retrieveTransitionInfo (event);
                String fromName = info[0];
                String fromType = info[1];
                String toName = info[2];
                String toType = info[3];
		insertNodeTransitionEventTable(eventId, fromName, fromType, toName, toType);

		Element ps =
			(Element) workItem.getElementsByTagName("Properties").item(0);
		NodeList proplist = ps.getElementsByTagName("Property");
		for (int i = 0; i < proplist.getLength(); i++) {
			String pname, ptype, pvalue;
			Element p = (Element) proplist.item(i);
			info = retriveWorkItemPropertyInfo(p);
			pname = info[0];
			ptype = info[1];
			pvalue = info[2];

			insertEventWorkItemPropertiesTable(
				workItemInternalId,
				pname,
				ptype,
				pvalue);

		}
	}

	/**
	 * involves inserting information into 2 tables: event and eventworkitem
	 * @param event
	 */
	private void insertStartedEvent(Element event) {
		int eventId, workItemInternalId;
		Element workItem =
			(Element) event.getElementsByTagName("WorkItem").item(0);
		String[] info = retriveWorkItemInfo(workItem);

		int workItemId = Integer.parseInt(info[0]);
		String payloadType = info[1];
		String payload = info[2];

		eventId = insertEventTable(event);

		workItemInternalId =
			insertEventWorkItemTable(
				eventId,
				-1,
				workItemId,
				payloadType,
				payload);
		Element ps =
			(Element) workItem.getElementsByTagName("Properties").item(0);
		NodeList proplist = ps.getElementsByTagName("Property");
		for (int i = 0; i < proplist.getLength(); i++) {
			String pname, ptype, pvalue;
			Element p = (Element) proplist.item(i);
			info = retriveWorkItemPropertyInfo(p);
			pname = info[0];
			ptype = info[1];
			pvalue = info[2];

			insertEventWorkItemPropertiesTable(
				workItemInternalId,
				pname,
				ptype,
				pvalue);

		}
	}

	/**
	 * get information from property node
	 * @param property
	 * @return
	 */
	private String[] retriveWorkItemPropertyInfo(Element property) {
		String info[] = new String[3];
		info[0] =
			property
				.getElementsByTagName("Name")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		info[1] =
			property
				.getElementsByTagName("Type")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		info[2] =
			property
				.getElementsByTagName("Value")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		return info;
	}
	
	/**
	 * get information from workitem node
	 * @param workItem
	 * @return
	 */
	private String[] retriveWorkItemInfo(Element workItem) {
		String info[] = new String[3];

		info[0] =
			workItem
				.getElementsByTagName("Integer")
				.item(0)
				.getFirstChild()
				.getNodeValue();
		Element pld =
			(Element) workItem.getElementsByTagName("Payload").item(0);

		info[1] = pld.getAttribute("type");

		NodeList plist = pld.getChildNodes();
		info[2] = "";
		for (int i = 0; i < plist.getLength(); i++) {
			info[2] += plist.item(i).toString();
		}
		return info;
	}
	
	/**
	 * get transistion information from event node 
	 * @param event
	 * @return
	 */
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

        public static void main (String[] args) throws XflowException, JMSException {
    
            String propFileName = args[0];
            if (propFileName == null) {
                System.out.println ("Usage: xflow.events.EventsHandler <properties file name>");
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
