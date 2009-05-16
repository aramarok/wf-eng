package wf.model;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import wf.db.Persistence;
import wf.server.controller.IBatisWork;
import wf.util.Util;
import wf.xml.WFXMLTagAndAttributeConstants;

public class Nod implements Serializable {

    public static final String AND = "And";

    public static final String CONTAINER = "Container";
    static int count = 0;
    public static final String END = "End";
    public static final String OR = "Or";
    public static final String PROCESS = "Process";
    private static final long serialVersionUID = 1L;

    public static final String START = "Start";
    private String description;
    @SuppressWarnings("unchecked")
    private List destinations;
    @SuppressWarnings("unchecked")
    private List fromNodes;
    private String name;
    private int nodeId;
    private String nodeType;
    @SuppressWarnings("unchecked")
    private HashMap properties;

    private boolean validated = false;

    public Nod() {
    }

    @SuppressWarnings("unchecked")
    public Nod(final int nodeId) {
	this.nodeId = nodeId;
	this.destinations = new ArrayList();
	this.fromNodes = new ArrayList();
	this.properties = new HashMap();
    }

    @SuppressWarnings("unchecked")
    public Nod(final String nodeName, final String nodeType) {
	this.name = nodeName;
	this.nodeType = nodeType;
	this.destinations = new ArrayList();
	this.fromNodes = new ArrayList();
	this.description = nodeName;
	this.properties = new HashMap();
    }

    @SuppressWarnings("unchecked")
    public void addDestination(final Nod node, final String rule) {
	Destinatie d = new Destinatie(node, rule);
	this.destinations.add(d);
	node.addFromNode(this);
    }

    @SuppressWarnings("unchecked")
    public void addFromNode(final Nod node) {
	this.fromNodes.add(node);
    }

    @SuppressWarnings("unchecked")
    public boolean detectCycle() {
	HashMap hashTable = new HashMap();

	Integer objKey = new Integer(this.nodeId);
	hashTable.put(objKey, this);
	boolean result = this.detectCycle(hashTable, false);
	return result;
    }

    @SuppressWarnings("unchecked")
    private boolean detectCycle(final HashMap hashTable, boolean cycleDetected) {
	if (cycleDetected) {
	    return true;
	}

	for (int i = 0; i < this.destinations.size(); i++) {
	    Destinatie d = (Destinatie) this.destinations.get(i);
	    Nod destNode = d.node;
	    Integer destNodeId = destNode.getNodeIdAsInteger();
	    Nod findNode = (Nod) hashTable.get(destNodeId);
	    if (findNode != null) {
		System.out.println("Cycle detected. From Nod: " + this.nodeId
			+ " To Nod: " + destNodeId);
		cycleDetected = true;
		break;
	    } else {
		hashTable.put(destNodeId, destNode);
		cycleDetected = destNode.detectCycle(hashTable, cycleDetected);
		hashTable.remove(destNodeId);
	    }
	}

	return cycleDetected;
    }

    @SuppressWarnings("unchecked")
    public void expand() throws Exception {

	HashMap nodeHash = new HashMap();
	this.expand(nodeHash);
    }

    @SuppressWarnings("unchecked")
    private void expand(final HashMap hashTable) throws Exception {

	IBatisWork work = new IBatisWork() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public void execute(SqlMapClient sqlMap) throws Exception {
		Integer nid = new Integer(Nod.this.nodeId);
		Nod n = (Nod) sqlMap.queryForObject("getNode", nid);
		Nod.this.name = n.getName();
		Nod.this.description = n.getDescription();
		Nod.this.nodeType = n.getNodeType();
		if (Nod.this.nodeType.equals(Nod.START)) {
		    Nod.this.name = Nod.START;
		} else if (Nod.this.nodeType.equals(Nod.END)) {
		    Nod.this.name = Nod.END;
		}
		List params = sqlMap.queryForList("getNodeProperties", nid);
		for (Iterator j = params.iterator(); j.hasNext();) {
		    Map entry = (Map) j.next();
		    Nod.this.properties
			    .put(
				    Util
					    .getValue(
						    entry,
						    WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE),
				    Util.objFromXML((String) Util.getValue(
					    entry, "value")));
		}

		List destinations = sqlMap.queryForList("getNodeDestinations",
			nid);
		for (Iterator j = destinations.iterator(); j.hasNext();) {
		    Map entry = (Map) j.next();
		    Integer destNodeId = (Integer) Util.getValue(entry,
			    "destnid");
		    String rule = (String) Util.getValue(entry, "rule");
		    Nod destNode = (Nod) hashTable.get(destNodeId);
		    if (destNode == null) {
			destNode = new Nod(destNodeId.intValue());
			hashTable.put(destNodeId, destNode);
			destNode.expand(hashTable);
		    } else {
			System.out.println("Already exists in hash: "
				+ destNodeId);
		    }
		    Nod.this.addDestination(destNode, rule);
		}
	    }

	};
	Persistence.execute(work);
    }

    public String getContainee() {
	String containee = null;
	if (this.nodeType.equals(Nod.CONTAINER)) {
	    containee = (String) this.properties
		    .get(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE);
	}
	return containee;
    }

    public int getContaineeVersion() {
	int version = -1;
	if (this.nodeType.equals(Nod.CONTAINER)) {
	    Integer iObj = (Integer) this.properties
		    .get(WFXMLTagAndAttributeConstants.CONTAINEEVERSION_ATTRIBUTE);
	    if (iObj != null) {
		version = iObj.intValue();
	    }
	}
	return version;
    }

    public String getDescription() {
	return this.description;
    }

    @SuppressWarnings("unchecked")
    public List getDestinations() {
	return this.destinations;
    }

    @SuppressWarnings("unchecked")
    public List getFromNodes() {
	return this.fromNodes;
    }

    public String getName() {
	return this.name;
    }

    public Nod getNode(final int nodeId) {
	return this.getNode(nodeId, null);
    }

    private Nod getNode(final int nodeId, Nod result) {
	if (result != null) {
	    return result;
	}

	if (this.nodeId == nodeId) {
	    System.out.println("Found " + nodeId);
	    result = this;
	} else {
	    for (int i = 0; i < this.destinations.size(); i++) {
		Destinatie d = (Destinatie) this.destinations.get(i);
		result = d.node.getNode(nodeId, result);
		if (result != null) {
		    break;
		}
	    }
	}

	return result;
    }

    public Nod getNode(final String name) {
	return this.getNode(name, null);
    }

    private Nod getNode(final String name, Nod result) {
	if (result != null) {
	    return result;
	}

	if (this.name.equals(name)) {
	    System.out.println("Found " + name);
	    result = this;
	} else {
	    for (int i = 0; i < this.destinations.size(); i++) {
		Destinatie d = (Destinatie) this.destinations.get(i);
		result = d.node.getNode(name, result);
		if (result != null) {
		    break;
		}
	    }
	}

	return result;
    }

    public int getNodeId() {
	return this.nodeId;
    }

    public Integer getNodeIdAsInteger() {
	return new Integer(this.nodeId);
    }

    public String getNodeName() {
	return this.name;
    }

    @SuppressWarnings("unchecked")
    public List getNodes() {
	List v = new ArrayList();
	HashMap map = new HashMap();
	this.getNodes(map);

	Iterator itr = map.values().iterator();
	while (itr.hasNext()) {
	    v.add(itr.next());
	}
	return v;
    }

    @SuppressWarnings("unchecked")
    private void getNodes(final HashMap map) {
	map.put(this.name, this);
	for (int i = 0; i < this.destinations.size(); i++) {
	    Destinatie d = (Destinatie) this.destinations.get(i);
	    Nod dnode = d.node;
	    dnode.getNodes(map);
	}
    }

    @SuppressWarnings("unchecked")
    public List getNodes(final String nodeType) {
	List v = new ArrayList();
	HashMap map = new HashMap();
	this.getNodes(nodeType, map);

	Iterator itr = map.values().iterator();
	while (itr.hasNext()) {
	    v.add(itr.next());
	}
	return v;
    }

    @SuppressWarnings("unchecked")
    private void getNodes(final String nType, final HashMap map) {
	if (this.nodeType.equals(nType)) {
	    map.put(this.name, this);
	}
	for (int i = 0; i < this.destinations.size(); i++) {
	    Destinatie d = (Destinatie) this.destinations.get(i);
	    Nod dnode = d.node;
	    dnode.getNodes(nType, map);
	}
    }

    public String getNodeType() {
	return this.nodeType;
    }

    public Object getProperty(final String key) {
	return this.properties.get(key);
    }

    public String getTimeoutHandler() {
	if (!this.nodeType.equals(PROCESS)) {
	    return null;
	}
	String handler = (String) this.properties
		.get(WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE);
	return handler;
    }

    public int getTimeoutMinutes() {
	if (!this.nodeType.equals(PROCESS)) {
	    return -1;
	}
	Integer tout = (Integer) this.properties
		.get(WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE);
	if (tout != null) {
	    return tout.intValue();
	} else {
	    return -1;
	}
    }

    public String getType() {
	return this.nodeType;
    }

    public boolean isValidated() {
	return this.validated;
    }

    @SuppressWarnings("unchecked")
    public void print() {
	System.out.println("Nod Id: " + this.nodeId + "\n" + "Nod Name: "
		+ this.name + "\n" + "Description: " + this.description);
	Iterator itr = this.properties.keySet().iterator();
	while (itr.hasNext()) {
	    String key = (String) itr.next();
	    Object value = this.properties.get(key);
	    System.out.println(key + " = " + value);
	}

    }

    @SuppressWarnings("unchecked")
    public void saveDB(final int gid) throws Exception {
	HashMap hash = new HashMap();
	this.saveDB(gid, hash);
	this.saveLink();
    }

    @SuppressWarnings("unchecked")
    private void saveDB(final int gid, final HashMap hash) throws Exception {
	Object o = hash.get(this.name);
	if (o != null) {
	    System.out.println("N = " + o.getClass().getName());
	    return;
	}

	IBatisWork work = new IBatisWork() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public void execute(SqlMapClient sqlMap) throws Exception {
		Map params = new Hashtable();
		params.put("gid", new Integer(gid));
		params.put("name", Nod.this.name);
		params.put("nodetype", Nod.this.nodeType);
		params.put("description", Nod.this.description);
		Integer nid = (Integer) sqlMap.insert("insertNode", params);
		Nod.this.nodeId = nid.intValue();
		hash.put(Nod.this.name, this);
		Iterator itr = Nod.this.properties.keySet().iterator();
		while (itr.hasNext()) {
		    String key = (String) itr.next();
		    Object value = Nod.this.properties.get(key);
		    if (value == null) {
			continue;
		    }
		    @SuppressWarnings("unused")
		    String valueStr = Util.objToXML(value);

		    params = new Hashtable();
		    params.put("nid", nid);
		    params.put("name", key);
		    params.put("val", value);
		    sqlMap.insert("insertNodeprop", params);
		}
		for (int i = 0; i < Nod.this.destinations.size(); i++) {
		    Destinatie d = (Destinatie) Nod.this.destinations.get(i);
		    Nod destNode = d.node;
		    destNode.saveDB(gid, hash);
		}

	    }
	};
	Persistence.execute(work);

    }

    @SuppressWarnings("unchecked")
    private void saveLink() throws Exception {

	HashMap hash = new HashMap();
	this.saveLink(hash);
    }

    @SuppressWarnings("unchecked")
    private void saveLink(final HashMap hash) throws Exception {
	Nod n = (Nod) hash.get(this.name);
	if (n != null) {
	    return;
	}
	hash.put(this.name, this);

	IBatisWork work = new IBatisWork() {
	    @Override
	    public void execute(SqlMapClient sqlMap) throws Exception {
		for (int i = 0; i < Nod.this.destinations.size(); i++) {
		    Destinatie d = (Destinatie) Nod.this.destinations.get(i);
		    Nod destNode = d.node;
		    int destNodeId = destNode.getNodeId();
		    String rule = d.rule;
		    Map params = new HashMap();
		    params.put("nid", new Integer(Nod.this.nodeId));
		    params.put("destnid", new Integer(destNodeId));
		    params.put("rule", rule);
		    sqlMap.insert("insertDestination", params);

		}
		for (int i = 0; i < Nod.this.destinations.size(); i++) {
		    Destinatie d = (Destinatie) Nod.this.destinations.get(i);
		    Nod destNode = d.node;
		    destNode.saveLink(hash);
		}
	    }
	};

	Persistence.execute(work);

    }

    @SuppressWarnings("unchecked")
    public void setContainee(final String graphName) {
	if (this.nodeType.equals(Nod.CONTAINER)) {
	    this.properties.put(
		    WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE,
		    graphName);
	}
    }

    @SuppressWarnings("unchecked")
    public void setContaineeVersion(final int version) {
	if (this.nodeType.equals(Nod.CONTAINER)) {
	    this.properties.put(
		    WFXMLTagAndAttributeConstants.CONTAINEEVERSION_ATTRIBUTE,
		    new Integer(version));
	}
    }

    public void setDescription(final String d) {
	this.description = d;
    }

    @SuppressWarnings("unchecked")
    public void setDestinations(final List destinations) {
	this.destinations = destinations;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public void setNodeId(final int nodeId) {
	this.nodeId = nodeId;
    }

    public void setNodeType(final String nodeType) {
	this.nodeType = nodeType;
    }

    @SuppressWarnings("unchecked")
    public void setProperty(final String key, final Object value) {
	this.properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public void setTimeoutHandler(final String timeoutHandler) {
	if (!this.nodeType.equals(PROCESS)) {
	    return;
	}
	this.properties.put(
		WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE,
		timeoutHandler);
    }

    @SuppressWarnings("unchecked")
    public void setTimeoutMinutes(final int timeoutMinutes) {
	if (!this.nodeType.equals(PROCESS)) {
	    return;
	}
	this.properties.put(
		WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE,
		new Integer(timeoutMinutes));
    }

    public void setType(final String type) {
	this.nodeType = type;
    }

    public void setValidated() {
	this.validated = true;
    }

    public void traverse() {
	this.print();
	if (this.destinations.size() == 0) {
	    System.out.println("No more destinations for " + this.nodeId);
	}

	for (int i = 0; i < this.destinations.size(); i++) {
	    Destinatie d = (Destinatie) this.destinations.get(i);
	    d.node.traverse();
	}
    }
}
