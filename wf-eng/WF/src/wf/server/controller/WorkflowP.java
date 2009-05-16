package wf.server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.StareWF;
import wf.server.util.ProcessWithTimeout;
import wf.util.Util;

public class WorkflowP {

    private static Logger log = Logger.getLogger(WorkflowP.class);

    @SuppressWarnings("unchecked")
    public void abortWorkflow(final Integer workflowId) throws SQLException {
	Map params = new Hashtable();
	params.put("workflowId", workflowId);
	params.put("timeEnded", new Date());
	Persistence.getThreadSqlMapSession().update("abortWorkflow", params);
    }

    @SuppressWarnings("unchecked")
    public boolean existsOrtab(final Integer workflowId, final int nodeId)
	    throws SQLException {
	Map params = new Hashtable();
	params.put("workflowId", (workflowId));
	params.put("nodeId", new Integer(nodeId));
	Integer wfId = (Integer) Persistence.getThreadSqlMapSession()
		.queryForObject("selectOrtab", params);
	return (wfId != null);
    }

    @SuppressWarnings("unchecked")
    public List getActiveWorkflows() throws SQLException {
	return Persistence.getThreadSqlMapSession().queryForList(
		"activeWorkflowIDs", null);
    }

    @SuppressWarnings("unchecked")
    public List getAllWorkflows() throws SQLException {
	return Persistence.getThreadSqlMapSession().queryForList(
		"allWorkflowIDs", null);
    }

    public int getGraphId(final Integer workflowId) throws SQLException {
	Object result = Persistence.getThreadSqlMapSession().queryForObject(
		"selectWorkflowGid", workflowId);
	return ((Integer) result).intValue();
    }

    @SuppressWarnings("unchecked")
    public List getModels() throws SQLException {
	return Persistence.getThreadSqlMapSession().queryForList("getModels",
		null);
    }

    @SuppressWarnings( { "unchecked", "unchecked", "unchecked" })
    public List getProcessesWithTimeouts() throws ExceptieWF, SQLException {
	List v = new ArrayList();
	String pName = null;
	Integer pId;
	int pToutMinutes = -1;
	String pThdl = null;

	List nodesWithTimeout = Persistence.getThreadSqlMapSession()
		.queryForList("selectNodesWithTimeout", null);
	for (Iterator j = nodesWithTimeout.iterator(); j.hasNext();) {
	    HashMap map = (HashMap) j.next();
	    pName = (String) map.get("name");
	    pId = ((Integer) map.get("nid"));
	    String vstr = (String) map.get("value");
	    Object value = Util.objFromXML(vstr);
	    Integer iValue = (Integer) value;
	    pToutMinutes = iValue.intValue();

	    pThdl = null;
	    vstr = (String) Persistence.getThreadSqlMapSession()
		    .queryForObject("selectTimeoutHandler", pId);
	    if (vstr != null) {
		value = Util.objFromXML(vstr);
		pThdl = (String) value;
	    }

	    List workflowIDs = Persistence.getThreadSqlMapSession()
		    .queryForList("selectWorkflowIdByNodeId", pId);
	    for (Iterator k = workflowIDs.iterator(); k.hasNext();) {
		Integer wf_id = (Integer) k.next();
		int wfId = wf_id.intValue();
		ProcessWithTimeout pto = new ProcessWithTimeout();
		pto.workflowId = wfId;
		pto.processName = pName;
		pto.timeoutMinutes = pToutMinutes;
		pto.timeoutHandler = pThdl;
		v.add(pto);
	    }

	}

	return v;
    }

    @SuppressWarnings("unchecked")
    public List getSuspendedWorkflows() throws SQLException {
	return Persistence.getThreadSqlMapSession().queryForList(
		"suspendedWorkflowIDs", null);
    }

    @SuppressWarnings("unchecked")
    public Object getVariable(final Integer workflowId, final String name)
	    throws SQLException {
	Map params = new Hashtable();
	params.put("workflowId", workflowId);
	params.put("name", name);
	Object result = Persistence.getThreadSqlMapSession().queryForObject(
		"getVariable", params);
	if (result == null) {
	    return null;
	}
	return Util.objFromXML((String) result);
    }

    @SuppressWarnings("unchecked")
    public List getWorkflowsByName(final String name) throws SQLException {
	return Persistence.getThreadSqlMapSession().queryForList(
		"getWorkflowIDsByName", name);
    }

    @SuppressWarnings("unchecked")
    public StareWF getWorkflowState(final Integer workflowId)
	    throws ExceptieWF, SQLException {

	StareWF state = (StareWF) Persistence.getThreadSqlMapSession()
		.queryForObject("getWorkflowState", workflowId);
	if (state == null) {
	    return null;
	}
	if (state.isActive) {
	    state.timeEnded = null;
	}
	if ((state.state == null) || state.state.equals("")) {
	    if (state.isActive) {
		state.state = "RUNNING";
	    } else {
		state.state = "COMPLETED";
	    }
	}
	List procStateRecords = Persistence.getThreadSqlMapSession()
		.queryForList("selectProcessStateRecords", workflowId);
	for (Iterator j = procStateRecords.iterator(); j.hasNext();) {
	    ProcessStateRec stateRec = (ProcessStateRec) j.next();
	    state.activeProcesses.add(stateRec.makeProcessState());
	}
	List wfVars = Persistence.getThreadSqlMapSession().queryForList(
		"selectWorkwlowVariables", workflowId);
	for (Iterator j = wfVars.iterator(); j.hasNext();) {
	    WorkflowVariable wfVar = (WorkflowVariable) j.next();
	    state.variables.put(wfVar.getName(), Util.objFromXML(wfVar
		    .getValue()));
	}
	return state;
    }

    @SuppressWarnings("unchecked")
    public void insertOrtab(final Integer workflowId, final int nodeId)
	    throws SQLException {
	Map params = new Hashtable();
	params.put("workflowId", (workflowId));
	params.put("nodeId", new Integer(nodeId));
	Persistence.getThreadSqlMapSession().insert("insertOrtab", params);
    }

    public boolean isCompleted(final int workflowId) throws SQLException {
	Object res = Persistence.getThreadSqlMapSession().queryForObject(
		"isCompleted", new Integer(workflowId));
	return (res != null);
    }

    public void resumeWorkflow(final Integer workflowId) throws SQLException {
	Persistence.getThreadSqlMapSession().update("resumeWorkflow",
		workflowId);
    }

    @SuppressWarnings("unchecked")
    public Integer saveNewWorkflow(final int graphId,
	    final String workflowName, final String initiator,
	    final int parentWorkflowId) throws ExceptieWF {
	Map params = new Hashtable();
	if (parentWorkflowId != -1) {
	    params.put("pWfId", new Integer(parentWorkflowId));
	}
	params.put("graphId", new Integer(graphId));
	params.put("initiator", initiator);
	Date timeStarted = new Date();
	params.put("timeStarted", timeStarted);
	params.put("timeStartedSql", new java.sql.Date(timeStarted.getTime()));
	Object result = null;
	try {
	    result = Persistence.getThreadSqlMapSession().insert(
		    "insertWorkflow", params);
	} catch (SQLException e) {
	    throw new ExceptieWF(e);
	}
	return ((Integer) result);
    }

    @SuppressWarnings("unchecked")
    public void setCompleted(final Integer workflowId) throws SQLException {
	Map params = new Hashtable();
	params.put("workflowId", (workflowId));
	params.put("timeEnded", new Date());
	Persistence.getThreadSqlMapSession().update("setCompleted", params);
    }

    @SuppressWarnings("unchecked")
    public void setVariable(final Integer workflowId, final String name,
	    final Object value) throws SQLException {
	if (log.isDebugEnabled()) {
	    log.debug("Hex Encoding: " + value);
	}
	String valueStr = Util.objToXML(value);
	if (log.isDebugEnabled()) {
	    log.debug("String to be stored: " + valueStr);
	}
	Map params = new Hashtable();
	params.put("workflowId", workflowId);
	params.put("name", name);
	params.put("varVal", valueStr);
	Persistence.getThreadSqlMapSession()
		.delete("deleteWorkflowVar", params);
	Persistence.getThreadSqlMapSession()
		.insert("insertWorkflowVar", params);
    }

    public void suspendWorkflow(final Integer workflowId) throws ExceptieWF,
	    SQLException {
	Persistence.getThreadSqlMapSession().update("suspendWorkflow",
		workflowId);
    }
}
