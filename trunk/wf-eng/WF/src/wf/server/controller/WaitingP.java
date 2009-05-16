package wf.server.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.Nod;

public class WaitingP {

    private static Logger log = Logger.getLogger(WaitingP.class);

    @SuppressWarnings("unchecked")
    public void addProcess(final Integer workflowId, final int destNodeId,
	    final int fromNodeId) throws ExceptieWF, SQLException {

	Map params = new HashMap();
	params.put("workflowId", workflowId);
	params.put("destNodeId", new Integer(destNodeId));
	params.put("fromNodeId", new Integer(fromNodeId));
	Persistence.getThreadSqlMapSession().insert("insertWaitingRecord",
		params);
    }

    @SuppressWarnings("unchecked")
    public boolean allProcessesArrived(final List fromNodes,
	    final Integer workflowId, final int destNodeId) throws ExceptieWF,
	    SQLException {
	int count = fromNodes.size();
	Map params = new HashMap();
	params.put("workflowId", workflowId);
	params.put("destNodeId", new Integer(destNodeId));
	List nodeIDs = new ArrayList();
	for (int i = 0; i < count; i++) {
	    Nod node = (Nod) fromNodes.get(i);
	    nodeIDs.add(node.getNodeIdAsInteger());
	}
	params.put("fromNodeIDs", nodeIDs);
	int rcount = 0;
	Integer cnt = (Integer) Persistence.getThreadSqlMapSession()
		.queryForObject("countArrived", params);
	if (cnt != null) {
	    rcount = cnt.intValue();
	}
	log.info("Nodes arrived count: " + rcount + " fromNodes count = "
		+ count);
	return rcount == count;
    }

    public void removeProcesses(final Integer wfId) throws ExceptieWF,
	    SQLException {
	Persistence.getThreadSqlMapSession().delete(
		"removeProcessesForWorkflow", wfId);

    }

    @SuppressWarnings("unchecked")
    public void removeProcesses(final List fromNodes, final Integer workflowId)
	    throws ExceptieWF, SQLException {
	Map params = new HashMap();
	params.put("workflowId", workflowId);
	for (int i = 0; i < fromNodes.size(); i++) {
	    Nod node = (Nod) fromNodes.get(i);
	    params.put("fromNodeId", new Integer(node.getNodeId()));
	    Persistence.getThreadSqlMapSession().delete(
		    "deleteProcFromWaiting", params);
	}

    }

}
