package wf.server.controller;

import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.WorkFlowException;
import wf.model.WorkItem;
import wf.util.Util;
import java.lang.Integer;
import java.sql.SQLException;
import java.util.*;

public class WorkItemP {

	private static Logger log = Logger.getLogger(WorkItemP.class);

	public WorkItem getWorkItem(Integer workitemId, String workflowName,
			String processName) throws SQLException, WorkFlowException {
		WorkItemRec workItemRec = (WorkItemRec) Persistence
				.getThreadSqlMapSession().queryForObject("selectWorkItemRec",
						workitemId);
		if (workItemRec != null) {
			String pstr = workItemRec.getPayload();
			Object payload = null;
			WorkItem witem = new WorkItem(workitemId);
			if (pstr != null && !pstr.equals("")) {
				payload = Util.objFromXML(pstr);
			}
			witem.setPayload(payload);
			witem._setPayloadType(workItemRec.getPayloadType());
			loadPropertiesFromDB(witem, workflowName, processName);
			return witem;
		}
		return null;
	}

	public List getWorkItems(String workflowName, String processName)
			throws WorkFlowException, SQLException {

		List v = new ArrayList();
		Map params = new HashMap();
		params.put("processName", processName);
		params.put("workflowName", workflowName);
		List in = Persistence.getThreadSqlMapSession().queryForList(
				"selectInboxRecordsForProcess", params);
		for (Iterator j = in.iterator(); j.hasNext();) {
			InboxRec inboxRec = (InboxRec) j.next();
			if (Persistence.getWorkflowP()
					.isCompleted(inboxRec.getWorkflowId())) {
				continue;
			}
			WorkItem workItem = getWorkItem(inboxRec.getWorkitemId(),
					workflowName, processName);
			if (workItem != null) {
				workItem.setWorkflowId(new Integer(inboxRec.getWorkflowId()));
				v.add(workItem);
			}
		}

		return v;
	}

	public WorkItem getNextWorkItem(String workflowName, String processName)
			throws WorkFlowException, SQLException {

		WorkItem witem = null;
		log.info("1");
		Map params = new HashMap();
		params.put("processName", processName);
		params.put("workflowName", workflowName);
		Integer numItems = (Integer) Persistence.getThreadSqlMapSession()
				.queryForObject("selectNumInboxItems", params);
		int count = numItems.intValue();
		if (count > 0) {
			List inboxRecs = Persistence.getThreadSqlMapSession().queryForList(
					"selectInboxRecordsForProcess", params);
			if (inboxRecs.size() > 0) {
				InboxRec inboxRec = (InboxRec) inboxRecs.get(0);
				int wfid = inboxRec.getWorkflowId();
				if (Persistence.getWorkflowP().isCompleted(wfid) == false) {
					witem = getWorkItem(inboxRec.getWorkitemId(), workflowName,
							processName);
					witem.setWorkflowId(new Integer(wfid));
				} else {
					log.error("Can't load workitem: "
							+ inboxRec.getWorkitemId());
				}
			}
		}
		return witem;
	}

	public WorkItem getWorkItem(Integer wid, String processName)
			throws WorkFlowException, SQLException {
		WorkItem witem = null;
		Integer wfId = witem.getWorkflowId();

		String workflowName = "";
		workflowName = (String) Persistence.getThreadSqlMapSession()
				.queryForObject("selectGraphNameByWorkItemId", wfId);
		Map params = new HashMap();
		params.put("processName", processName);
		params.put("workflowName", workflowName);
		InboxRec inboxRec = (InboxRec) Persistence.getThreadSqlMapSession()
				.queryForObject("selectInboxRecordsForProcess", params);
		if (inboxRec != null) {
			int wfid = inboxRec.getWorkflowId();
			witem = getWorkItem(wid, workflowName, processName);
			if (witem != null) {
				witem.setWorkflowId(new Integer(wfid));
			} else {
				log.error("Can't load workitem: " + wid);
			}
		}

		return witem;
	}

	public void saveDB(WorkItem witem) throws WorkFlowException, SQLException {
		Object payload = witem.getPayload();
		String payloadStr = "";
		String payloadType = witem.getPayloadType();
		if (payload != null)
			payloadStr = Util.objToXML(payload);
		Map params = new HashMap();
		params.put("payloadStr", payloadStr);
		params.put("payloadType", payloadType);
		Integer id = (Integer) Persistence.getThreadSqlMapSession().insert(
				"insertWorkItem", params);
		witem.setId(new Integer(id.intValue()));
	}

	public void updateDB(WorkItem witem) throws WorkFlowException, SQLException {
		Integer workitemId = witem.getId();
		Object payload = witem.getPayload();
		String payloadStr = "";
		if (payload != null) {
			payloadStr = Util.objToXML(payload);
		}
		Map params = new HashMap();
		params.put("payloadStr", payloadStr);
		params.put("workitemId", workitemId);
		Persistence.getThreadSqlMapSession().update("updateWorkItem", params);
	}

	public void loadPropertiesFromDB(WorkItem witem, String workflowName,
			String procName) throws WorkFlowException, SQLException {
		HashMap properties = witem.getProperties();
		Integer workitemId = witem.getId();
		Map params = new HashMap();
		params.put("wid", workitemId);
		params.put("procName", procName);
		params.put("workflowName", workflowName);
		List props = Persistence.getThreadSqlMapSession().queryForList(
				"selectWorkItemProps", params);
		for (Iterator j = props.iterator(); j.hasNext();) {
			Map entry = (Map) j.next();
			Object value = Util.getValue(entry, "value");
			if (value != null) {
				value = Util.objFromXML(value.toString());
			}
			properties.put(Util.getValue(entry, "name"), value);
		}

	}

	public void savePropertiesToDB(WorkItem witem, String workflowName,
			String procName) throws WorkFlowException, SQLException {
		HashMap properties = witem.getProperties();
		Iterator itr = properties.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Object value = properties.get(key);
			if (value == null) {
				continue;
			}
			String valueStr = Util.objToXML(value);
			Integer workItemId = witem.getId();
			Map params = new HashMap();
			params.put("workitemId", workItemId);
			params.put("workflowName", workflowName);
			params.put("procName", procName);
			params.put("name", key);
			params.put("valueStr", valueStr);
			Persistence.getThreadSqlMapSession().insert("insertWorkItemProp",
					params);

		}

	}

	public void deleteDB(WorkItem wi) throws WorkFlowException, SQLException {
		Integer workItemId = wi.getId();
		Persistence.getThreadSqlMapSession().delete("deleteWorkitemprops",
				workItemId);
		Persistence.getThreadSqlMapSession().delete("deleteWorkitem",
				workItemId);
	}
}
