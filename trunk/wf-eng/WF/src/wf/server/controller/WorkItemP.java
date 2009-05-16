package wf.server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;
import wf.util.Util;

public class WorkItemP {

    private static Logger log = Logger.getLogger(WorkItemP.class);

    public void deleteDB(final ItemModel wi) throws ExceptieWF, SQLException {
	Integer workItemId = wi.getId();
	Persistence.getThreadSqlMapSession().delete("deleteWorkitemprops",
		workItemId);
	Persistence.getThreadSqlMapSession().delete("deleteWorkitem",
		workItemId);
    }

    @SuppressWarnings("unchecked")
    public ItemModel getNextWorkItem(final String workflowName,
	    final String processName) throws ExceptieWF, SQLException {

	ItemModel witem = null;
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
		    witem = this.getWorkItem(inboxRec.getWorkitemId(),
			    workflowName, processName);
		    witem.setWorkflowId(new Integer(wfid));
		} else {
		    log.error("Can't load workitem: "
			    + inboxRec.getWorkitemId());
		}
	    }
	}
	return witem;
    }

    @SuppressWarnings("unchecked")
    public ItemModel getWorkItem(final Integer wid, final String processName)
	    throws ExceptieWF, SQLException {
	ItemModel witem = null;
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
	    witem = this.getWorkItem(wid, workflowName, processName);
	    if (witem != null) {
		witem.setWorkflowId(new Integer(wfid));
	    } else {
		log.error("Can't load workitem: " + wid);
	    }
	}

	return witem;
    }

    public ItemModel getWorkItem(final Integer workitemId,
	    final String workflowName, final String processName)
	    throws SQLException, ExceptieWF {
	WorkItemRec workItemRec = (WorkItemRec) Persistence
		.getThreadSqlMapSession().queryForObject("selectWorkItemRec",
			workitemId);
	if (workItemRec != null) {
	    String pstr = workItemRec.getPayload();
	    Object payload = null;
	    ItemModel witem = new ItemModel(workitemId);
	    if ((pstr != null) && !pstr.equals("")) {
		payload = Util.objFromXML(pstr);
	    }
	    witem.setPayload(payload);
	    witem._setPayloadType(workItemRec.getPayloadType());
	    this.loadPropertiesFromDB(witem, workflowName, processName);
	    return witem;
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    public List getWorkItems(final String workflowName, final String processName)
	    throws ExceptieWF, SQLException {

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
	    ItemModel workItem = this.getWorkItem(inboxRec.getWorkitemId(),
		    workflowName, processName);
	    if (workItem != null) {
		workItem.setWorkflowId(new Integer(inboxRec.getWorkflowId()));
		v.add(workItem);
	    }
	}

	return v;
    }

    @SuppressWarnings("unchecked")
    public void loadPropertiesFromDB(final ItemModel witem,
	    final String workflowName, final String procName)
	    throws ExceptieWF, SQLException {
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

    @SuppressWarnings("unchecked")
    public void saveDB(final ItemModel witem) throws ExceptieWF, SQLException {
	Object payload = witem.getPayload();
	String payloadStr = "";
	String payloadType = witem.getPayloadType();
	if (payload != null) {
	    payloadStr = Util.objToXML(payload);
	}
	Map params = new HashMap();
	params.put("payloadStr", payloadStr);
	params.put("payloadType", payloadType);
	Integer id = (Integer) Persistence.getThreadSqlMapSession().insert(
		"insertWorkItem", params);
	witem.setId(new Integer(id.intValue()));
    }

    @SuppressWarnings("unchecked")
    public void savePropertiesToDB(final ItemModel witem,
	    final String workflowName, final String procName)
	    throws ExceptieWF, SQLException {
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

    @SuppressWarnings("unchecked")
    public void updateDB(final ItemModel witem) throws ExceptieWF, SQLException {
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
}
