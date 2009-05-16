package wf.model;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wf.db.Persistence;
import wf.exceptions.ExceptieWF;
import wf.server.controller.DirectedGraphP;
import wf.server.controller.IBatisWork;
import wf.util.Util;
import wf.xml.GraphSerializer;

public class DirectedGraph implements Serializable {

    private static final long serialVersionUID = 1L;

    public static DirectedGraph loadByGraphId(final int gid) throws ExceptieWF {
	DirectedGraph res = new DirectedGraph();
	Persistence.getDirectGraphP().loadByGraphById(gid, res);
	return res;
    }

    private String description;
    @SuppressWarnings("unchecked")
    private final Map endNode = new HashMap();
    private int graphId;
    private String name;

    private Nod rootNode;
    @SuppressWarnings("unchecked")
    private final Map startNode = new HashMap();

    private int version;

    public DirectedGraph() {
    }

    public DirectedGraph(final String name) {
	this.name = name;
	this.description = name;
	this.version = -1;
    }

    public DirectedGraph(final String name, final int vers) {
	this.name = name;
	this.description = name;
	this.version = vers;
    }

    @SuppressWarnings("unchecked")
    public List getAllNodes() {
	return this.rootNode.getNodes();
    }

    public String getDescription() {
	return this.description;
    }

    public Nod getEndNode() {
	return this.rootNode.getNode(Nod.END);
    }

    public int getGraphId() {
	return this.graphId;
    }

    public String getName() {
	return this.name;
    }

    public Nod getNode(final int nodeId) {
	return this.rootNode.getNode(nodeId);
    }

    public Nod getNode(final String nodeName) {
	return this.rootNode.getNode(nodeName);
    }

    @SuppressWarnings("unchecked")
    public List getNodes(final String nodeType) {
	return this.rootNode.getNodes(nodeType);
    }

    public Nod getRootNode() {
	return this.rootNode;
    }

    public int getVersion() {
	return this.version;
    }

    public void loadDB() throws ExceptieWF, SQLException {
	DirectedGraphP directGraphP = Persistence.getDirectGraphP();
	Integer gid = directGraphP.getGraphId(this.name, this.version);
	directGraphP.loadByGraphById(gid.intValue(), this);
    }

    public void saveDB() throws Exception {

	IBatisWork work = new IBatisWork() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public void execute(SqlMapClient sqlMap) throws Exception {
		if (Persistence.getDirectGraphP().graphExistsInDB(
			DirectedGraph.this.name, DirectedGraph.this.version)) {
		    throw new ExceptieWF("There is already a graph called "
			    + DirectedGraph.this.name + " version "
			    + DirectedGraph.this.version + " in the database");
		}
		DirectedGraph.this.graphId = Util.generateUniqueIntId();
		DirectedGraph.this.rootNode.saveDB(DirectedGraph.this.graphId);
		DirectedGraph.this.version = Persistence.getDirectGraphP()
			.getLatestVersionNumber(DirectedGraph.this.name) + 1;
		int rootNodeId = DirectedGraph.this.rootNode.getNodeId();
		Map params = new HashMap();
		params.put("gid", new Integer(DirectedGraph.this.graphId));
		params.put("name", DirectedGraph.this.name);
		params.put("description", DirectedGraph.this.description);
		params.put("version", new Integer(DirectedGraph.this.version));
		params.put("nid", new Integer(rootNodeId));
		sqlMap.insert("insertGraph", params);
	    }
	};
	Persistence.execute(work);
    }

    public void setDescription(final String d) {
	this.description = d;
    }

    public void setGraphId(final int i) {
	this.graphId = i;
    }

    public void setName(final String n) {
	this.name = n;
    }

    public void setRootNode(final Nod node) {
	this.rootNode = node;
    }

    public void setVersion(final int v) {
	this.version = v;
    }

    public String toXML() throws ExceptieWF {
	return GraphSerializer.serialize(this);
    }

    public void validate() throws ExceptieWF {
	this.validate(this.rootNode);
    }

    private void validate(final Nod node) throws ExceptieWF {

	if (node.isValidated()) {
	    return;
	} else {
	    node.setValidated();
	}

	String type = node.getNodeType();
	if (type.equals(Nod.START)) {
	    this.validateStart(node);
	} else if (type.equals(Nod.END)) {
	    this.validateEnd(node);
	} else if (type.equals(Nod.AND)) {
	    this.validateAND(node);
	} else if (type.equals(Nod.PROCESS)) {
	    this.validateProcess(node);
	} else if (type.equals(Nod.CONTAINER)) {
	    this.validateContainer(node);
	} else if (type.equals(Nod.OR)) {
	    this.validateOR(node);
	}
	if (this.startNode.size() == 0) {
	    throw new ExceptieWF("there is no Start node in the graph");
	}
	if (this.endNode.size() == 0) {
	    throw new ExceptieWF("there is no End node in the graph");
	}
    }

    @SuppressWarnings("unchecked")
    private void validateAND(final Nod node) throws ExceptieWF {
	if (node.getFromNodes().size() < 2) {
	    throw new ExceptieWF("AND node should have at least 2 nodes in");
	} else {
	    List destinations = node.getDestinations();
	    if (destinations.size() == 0) {
		throw new ExceptieWF(
			"AND node should has at lease one node out");
	    } else {
		for (int i = 0; i < destinations.size(); i++) {
		    Destinatie d = (Destinatie) destinations.get(i);
		    String ntype = d.node.getNodeType();
		    if (ntype.equals(Nod.CONTAINER)
			    || ntype.equals(Nod.PROCESS)
			    || ntype.equals(Nod.END) || ntype.equals(Nod.AND)
			    || ntype.equals(Nod.OR)) {
			this.validate(d.node);
		    } else {
			throw new ExceptieWF(
				"AND node should go into a Container,"
					+ " a Process, an AND, an OR or an End node.");
		    }
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void validateContainer(final Nod node) throws ExceptieWF {
	List destinations = node.getDestinations();
	for (int i = 0; i < destinations.size(); i++) {
	    Destinatie d = (Destinatie) destinations.get(i);
	    String ntype = d.node.getNodeType();
	    if (ntype.equals(Nod.CONTAINER) || ntype.equals(Nod.PROCESS)
		    || ntype.equals(Nod.END) || ntype.equals(Nod.AND)
		    || ntype.equals(Nod.OR)) {
		this.validate(d.node);
	    } else {
		throw new ExceptieWF(
			"Container node should go into a Container,"
				+ " a Process, an AND, an OR or End node.");
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void validateEnd(final Nod node) throws ExceptieWF {
	this.endNode.put(node.getName(), node);
	if (this.endNode.size() != 1) {
	    throw new ExceptieWF("More than one End node in the graph");
	} else {
	    if (node.getDestinations().size() != 0) {
		throw new ExceptieWF("No nodes should go out from End node");
	    }
	}

    }

    @SuppressWarnings("unchecked")
    private void validateOR(final Nod node) throws ExceptieWF {
	if (node.getFromNodes().size() < 2) {
	    throw new ExceptieWF("OR node should have at least 2 nodes in");
	} else {
	    List destinations = node.getDestinations();
	    if (destinations.size() == 0) {
		throw new ExceptieWF("OR node should has at lease one node out");
	    } else {
		for (int i = 0; i < destinations.size(); i++) {
		    Destinatie d = (Destinatie) destinations.get(i);
		    String ntype = d.node.getNodeType();
		    if (ntype.equals(Nod.CONTAINER)
			    || ntype.equals(Nod.PROCESS)
			    || ntype.equals(Nod.END) || ntype.equals(Nod.AND)
			    || ntype.equals(Nod.OR)) {
			this.validate(d.node);
		    } else {
			throw new ExceptieWF(
				"OR node should go into a Container,"
					+ " a Process, an AND, an OR or an End node.");
		    }
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void validateProcess(final Nod node) throws ExceptieWF {
	List destinations = node.getDestinations();
	if (destinations.size() == 0) {
	    throw new ExceptieWF(
		    "Process node should has at lease one node out");
	} else {
	    for (int i = 0; i < destinations.size(); i++) {
		Destinatie d = (Destinatie) destinations.get(i);
		String ntype = d.node.getNodeType();
		if (ntype.equals(Nod.CONTAINER) || ntype.equals(Nod.PROCESS)
			|| ntype.equals(Nod.END) || ntype.equals(Nod.AND)
			|| ntype.equals(Nod.OR)) {
		    this.validate(d.node);
		} else {
		    throw new ExceptieWF(
			    "Process node should go into a Container,"
				    + " a Process, an AND, an OR or an End node.");
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void validateStart(final Nod node) throws ExceptieWF {
	this.startNode.put(node.getName(), node);
	if (this.startNode.size() != 1) {
	    throw new ExceptieWF("More than one Start node in the graph");
	} else {
	    if (node.getFromNodes().size() != 0) {
		throw new ExceptieWF("No nodes should go into Start node");
	    } else {
		List destinations = node.getDestinations();
		if (destinations.size() == 0) {
		    throw new ExceptieWF(
			    "Start node should has at lease one node out");
		} else {
		    for (int i = 0; i < destinations.size(); i++) {
			Destinatie d = (Destinatie) destinations.get(i);
			String ntype = d.node.getNodeType();
			if (ntype.equals(Nod.CONTAINER)
				|| ntype.equals(Nod.PROCESS)) {
			    this.validate(d.node);
			} else {
			    throw new ExceptieWF(
				    "Start node should go into Container"
					    + " or Process node.");
			}
		    }
		}
	    }
	}

    }
}
