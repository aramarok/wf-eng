package wf.server.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;

public class InboxP {

    public void addWorkItem(final int gid, final String workflowName,
	    final String procName, final ItemModel workitem) throws ExceptieWF {
	Integer wfId = workitem.getWorkflowId();
	Integer workitemId = workitem.getId();
	int workflowId = wfId.intValue();
	try {
	    InboxRec inboxRec = new InboxRec();
	    inboxRec.setGid(gid);
	    inboxRec.setProcName(procName);
	    inboxRec.setTimeout(false);
	    inboxRec.setTimeStarted(new Date());
	    inboxRec.setWorkflowId(workflowId);
	    inboxRec.setWorkflowName(workflowName);
	    inboxRec.setWorkitemId(workitemId);
	    Persistence.getThreadSqlMapSession().insert("insertInboxRecord",
		    inboxRec);
	    Persistence.getWorkItemP().updateDB(workitem);
	    Persistence.getWorkItemP().savePropertiesToDB(workitem,
		    workflowName, procName);

	} catch (Exception e) {
	    throw new ExceptieWF("Nu am putut salva un rand in baza de date", e);
	}
    }

    public java.util.Date getTimeStarted(final int workflowId,
	    final String procName) throws ExceptieWF, SQLException {
	InboxRec inboxRec = new InboxRec();
	inboxRec.setTimeout(false);
	inboxRec.setProcName(procName);
	inboxRec.setWorkflowId(workflowId);
	return (Date) Persistence.getThreadSqlMapSession().queryForObject(
		"selectDateStarted", inboxRec);
    }

    @SuppressWarnings("unchecked")
    public boolean isWorkItemValid(final int gid, final String procName,
	    final ItemModel workitem) throws ExceptieWF, SQLException {

	Integer wfId = workitem.getWorkflowId();
	Integer workitemId = workitem.getId();
	int workflowId = wfId.intValue();
	InboxRec inboxRec = new InboxRec();
	inboxRec.setGid(gid);
	inboxRec.setProcName(procName);
	inboxRec.setWorkitemId(workitemId);
	inboxRec.setWorkflowId(workflowId);
	List inboxRecords = Persistence.getThreadSqlMapSession().queryForList(
		"selectInboxRecords", inboxRec);
	return ((inboxRecords != null) && (inboxRecords.size() > 0));
    }

    public void removeWorkItem(final int gid, final String procName,
	    final ItemModel workitem) throws ExceptieWF {
	Integer wfId = workitem.getWorkflowId();
	Integer workitemId = workitem.getId();
	int workflowId = wfId.intValue();
	try {
	    InboxRec inboxRec = new InboxRec();
	    inboxRec.setGid(gid);
	    inboxRec.setProcName(procName);
	    inboxRec.setWorkitemId(workitemId);
	    inboxRec.setWorkflowId(workflowId);
	    Persistence.getThreadSqlMapSession().delete("deleteInboxRecords",
		    inboxRec);
	} catch (Exception e) {
	    throw new ExceptieWF("NU am putut sterge un rand din bd",
		    e);
	}
    }

    public void removeWorkItems(final Integer workflowId) throws ExceptieWF,
	    SQLException {
	Persistence.getThreadSqlMapSession().delete(
		"deleteInboxRecordsForWorkflow", workflowId);
    }

    public void setTimeoutFlag(final int workflowId, final String procName)
	    throws ExceptieWF, SQLException {
	InboxRec inboxRec = new InboxRec();
	inboxRec.setTimeout(true);
	inboxRec.setProcName(procName);
	inboxRec.setWorkflowId(workflowId);
	Persistence.getThreadSqlMapSession().update("setInboxTimeoutFlag",
		inboxRec);
    }

    @SuppressWarnings("unchecked")
    public boolean workitemsExist(final int workflowId) throws ExceptieWF,
	    SQLException {
	List items = Persistence.getThreadSqlMapSession().queryForList("",
		new Integer(workflowId));
	return ((items != null) && (items.size() > 0));
    }
}
