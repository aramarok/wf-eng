package wf.server.controller;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.model.DirectedGraph;
import wf.model.Nod;
import wf.util.Util;

public class DirectedGraphP {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(DirectedGraphP.class);

    @SuppressWarnings("unchecked")
    public Integer getGraphId(final String graphName, final int version)
	    throws SQLException {
	Map params = new Hashtable();
	params.put("name", graphName);
	if (version == -1) {
	    params.put("version", this.getMaxGraphVersion(graphName));
	} else {
	    params.put("version", new Integer(version));
	}
	return (Integer) Persistence.getThreadSqlMapSession().queryForObject(
		"getGraphId", params);
    }

    public int getLatestVersionNumber(final String name) throws SQLException {
	return this.getMaxGraphVersion(name).intValue();
    }

    private Integer getMaxGraphVersion(final String name) throws SQLException {
	Integer i = (Integer) Persistence.getThreadSqlMapSession()
		.queryForObject("getMaxGraphVersion", name);
	if (i == null) {
	    i = new Integer(0);
	}
	return i;
    }

    public boolean graphExistsInDB(final String graphName, final int version)
	    throws SQLException {
	if (version == -1) {
	    return false;
	}
	return (this.getGraphId(graphName, version) != null);
    }

    @SuppressWarnings("unchecked")
    public DirectedGraph loadByGraphById(final int gid,
	    final DirectedGraph destination) throws ExceptieWF {
	try {
	    Map m = (Map) Persistence.getThreadSqlMapSession().queryForObject(
		    "getGraph", new Integer(gid));
	    Integer version = (Integer) Util.getValue(m, "version");
	    destination.setVersion(version.intValue());
	    destination
		    .setDescription((String) Util.getValue(m, "description"));
	    Integer iobj = (Integer) Util.getValue(m, "nid");
	    int rootNodeId = iobj.intValue();
	    destination.setGraphId(gid);
	    Nod rootNode = new Nod(rootNodeId);
	    destination.setRootNode(rootNode);
	    rootNode.expand();
	    return destination;
	} catch (Throwable c) {
	    throw new ExceptieWF("Failed to load workflow from database.", c);
	}
    }

    public DirectedGraph loadDirectedGraph(final String name, final int version)
	    throws SQLException, ExceptieWF {
	Integer graphId = this.getGraphId(name, version);
	return this.loadByGraphById(graphId.intValue(), new DirectedGraph());
    }

}
