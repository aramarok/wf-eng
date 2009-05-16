package wf.server.controller;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.Nod;
import wf.server.util.PopNode;
import wf.util.Util;

public class ProcessStack {

    @SuppressWarnings("unused")
    private static Logger LOGGER = Logger.getLogger(ProcessStack.class);

    @SuppressWarnings("unchecked")
    public PopNode pop(final int workflowId, final Nod endNode)
	    throws ExceptieWF, SQLException {

	int endNodeId = endNode.getNodeId();
	PopNode popNode = null;
	Map params = new Hashtable();
	params.put("workflowId", new Integer(workflowId));
	params.put("endNodeId", new Integer(endNodeId));
	Map rec = (Map) Persistence.getThreadSqlMapSession().queryForObject(
		"selectProcStackRecord", params);
	if (rec != null) {
	    popNode = new PopNode();
	    popNode.nodeId = ((Integer) Util.getValue(rec, "cNodeId"))
		    .intValue();
	    popNode.gid = ((Integer) Util.getValue(rec, "cGid")).intValue();
	    Persistence.getThreadSqlMapSession().delete(
		    "deleteProcStackRecord", params);
	}
	return popNode;
    }

    @SuppressWarnings("unchecked")
    public void push(final Integer workflowId, final int cGid,
	    final Nod containerNode, final Nod endNode) throws ExceptieWF,
	    SQLException {

	int containerNodeId = containerNode.getNodeId();
	int endNodeId = endNode.getNodeId();

	Map params = new Hashtable();
	params.put("workflowId", workflowId);
	params.put("cGid", new Integer(cGid));
	params.put("containerNodeId", new Integer(containerNodeId));
	params.put("endNodeId", new Integer(endNodeId));
	Persistence.getThreadSqlMapSession().insert("insertProcStackRecord",
		params);

    }
}
