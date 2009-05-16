package wf.server.controller;

import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.WorkFlowException;
import wf.model.Node;
import wf.server.util.PopNode;
import wf.util.Util;
import java.sql.SQLException;
import java.util.Map;
import java.util.Hashtable;

public class ProcessStack {

	private static Logger log = Logger.getLogger(ProcessStack.class);

	public void push(Integer workflowId, int cGid, Node containerNode,
			Node endNode) throws WorkFlowException, SQLException {

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

	public PopNode pop(int workflowId, Node endNode) throws WorkFlowException,
			SQLException {

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
}
