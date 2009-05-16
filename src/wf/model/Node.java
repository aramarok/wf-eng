package wf.model;

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

import com.ibatis.sqlmap.client.SqlMapClient;

public class Node implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PROCESS = "Process";
	public static final String AND = "And";
	public static final String OR = "Or";
	public static final String START = "Start";
	public static final String END = "End";
	public static final String CONTAINER = "Container";

	private int nodeId;
	private String nodeType;
	private String name;
	private String description;
	private List destinations;
	private List fromNodes;
	private HashMap properties;
	private boolean validated = false;

	static int count = 0;

	public Node() {
	}

	public Node(String nodeName, String nodeType) {
		this.name = nodeName;
		this.nodeType = nodeType;
		destinations = new ArrayList();
		fromNodes = new ArrayList();
		description = nodeName;
		properties = new HashMap();
	}

	public Node(int nodeId) {
		this.nodeId = nodeId;
		destinations = new ArrayList();
		fromNodes = new ArrayList();
		properties = new HashMap();
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public void setDestinations(List destinations) {
		this.destinations = destinations;
	}

	public Integer getNodeIdAsInteger() {
		return new Integer(nodeId);
	}

	public String getType() {
		return nodeType;
	}

	public void setType(String type) {
		this.nodeType = type;
	}

	public String getNodeName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNodeType() {
		return nodeType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		description = d;
	}

	public String getContainee() {
		String containee = null;
		if (nodeType.equals(Node.CONTAINER)) {
			containee = (String) properties.get(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE);
		}
		return containee;
	}

	public void setContainee(String graphName) {
		if (nodeType.equals(Node.CONTAINER)) {
			properties.put(WFXMLTagAndAttributeConstants.CONTAINEE_ATTRIBUTE, graphName);
		}
	}

	public int getContaineeVersion() {
		int version = -1;
		if (nodeType.equals(Node.CONTAINER)) {
			Integer iObj = (Integer) properties.get(WFXMLTagAndAttributeConstants.CONTAINEEVERSION_ATTRIBUTE);
			if (iObj != null) {
				version = iObj.intValue();
			}
		}
		return version;
	}

	public void setContaineeVersion(int version) {
		if (nodeType.equals(Node.CONTAINER)) {
			properties.put(WFXMLTagAndAttributeConstants.CONTAINEEVERSION_ATTRIBUTE, new Integer(version));
		}
	}

	private void expand(final HashMap hashTable) throws Exception {

		IBatisWork work = new IBatisWork() {

			public void execute(SqlMapClient sqlMap) throws Exception {
				Integer nid = new Integer(nodeId);
				Node n = (Node) sqlMap.queryForObject("getNode", nid);
				name = n.getName();
				description = n.getDescription();
				nodeType = n.getNodeType();
				if (nodeType.equals(Node.START)) {
					name = Node.START;
				} else if (nodeType.equals(Node.END)) {
					name = Node.END;
				}
				List params = sqlMap.queryForList("getNodeProperties", nid);
				for (Iterator j = params.iterator(); j.hasNext();) {
					Map entry = (Map) j.next();
					properties
							.put(Util.getValue(entry, WFXMLTagAndAttributeConstants.NAME_ATTRIBUTE), Util
									.objFromXML((String) Util.getValue(entry,
											"value")));
				}

				List destinations = sqlMap.queryForList("getNodeDestinations",
						nid);
				for (Iterator j = destinations.iterator(); j.hasNext();) {
					Map entry = (Map) j.next();
					Integer destNodeId = (Integer) Util.getValue(entry,
							"destnid");
					String rule = (String) Util.getValue(entry, "rule");
					Node destNode = (Node) hashTable.get(destNodeId);
					if (destNode == null) {
						destNode = new Node(destNodeId.intValue());
						hashTable.put(destNodeId, destNode);
						destNode.expand(hashTable);
					} else {
						System.out.println("Already exists in hash: "
								+ destNodeId);
					}
					addDestination(destNode, rule);
				}
			}

		};
		Persistence.execute(work);
	}

	public void expand() throws Exception {

		HashMap nodeHash = new HashMap();
		expand(nodeHash);
	}

	private void saveLink(final HashMap hash) throws Exception {
		Node n = (Node) hash.get(name);
		if (n != null)
			return;
		hash.put(name, this);

		IBatisWork work = new IBatisWork() {
			public void execute(SqlMapClient sqlMap) throws Exception {
				for (int i = 0; i < destinations.size(); i++) {
					Destination d = (Destination) destinations.get(i);
					Node destNode = d.node;
					int destNodeId = destNode.getNodeId();
					String rule = d.rule;
					Map params = new HashMap();
					params.put("nid", new Integer(nodeId));
					params.put("destnid", new Integer(destNodeId));
					params.put("rule", rule);
					sqlMap.insert("insertDestination", params);

				}
				for (int i = 0; i < destinations.size(); i++) {
					Destination d = (Destination) destinations.get(i);
					Node destNode = d.node;
					destNode.saveLink(hash);
				}
			}
		};

		Persistence.execute(work);

	}

	private void saveLink() throws Exception {

		HashMap hash = new HashMap();
		saveLink(hash);
	}

	public void saveDB(int gid) throws Exception {
		HashMap hash = new HashMap();
		saveDB(gid, hash);
		saveLink();
	}

	private void saveDB(final int gid, final HashMap hash) throws Exception {
		Object o = hash.get(name);
		if (o != null) {
			System.out.println("N = " + o.getClass().getName());
			return;
		}

		IBatisWork work = new IBatisWork() {
			public void execute(SqlMapClient sqlMap) throws Exception {
				Map params = new Hashtable();
				params.put("gid", new Integer(gid));
				params.put("name", name);
				params.put("nodetype", nodeType);
				params.put("description", description);
				Integer nid = (Integer) sqlMap.insert("insertNode", params);
				nodeId = nid.intValue();
				hash.put(name, this);
				Iterator itr = properties.keySet().iterator();
				while (itr.hasNext()) {
					String key = (String) itr.next();
					Object value = properties.get(key);
					if (value == null) {
						continue;
					}
					String valueStr = Util.objToXML(value);

					params = new Hashtable();
					params.put("nid", nid);
					params.put("name", key);
					params.put("val", value);
					sqlMap.insert("insertNodeprop", params);
				}
				for (int i = 0; i < destinations.size(); i++) {
					Destination d = (Destination) destinations.get(i);
					Node destNode = d.node;
					destNode.saveDB(gid, hash);
				}

			}
		};
		Persistence.execute(work);

	}

	private boolean detectCycle(HashMap hashTable, boolean cycleDetected) {
		if (cycleDetected) {
			return true;
		}

		for (int i = 0; i < destinations.size(); i++) {
			Destination d = (Destination) destinations.get(i);
			Node destNode = d.node;
			Integer destNodeId = destNode.getNodeIdAsInteger();
			Node findNode = (Node) hashTable.get(destNodeId);
			if (findNode != null) {
				System.out.println("Cycle detected. From Node: " + nodeId
						+ " To Node: " + destNodeId);
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

	public boolean detectCycle() {
		HashMap hashTable = new HashMap();

		Integer objKey = new Integer(nodeId);
		hashTable.put(objKey, this);
		boolean result = detectCycle(hashTable, false);
		return result;
	}

	public void traverse() {
		this.print();
		if (destinations.size() == 0) {
			System.out.println("No more destinations for " + nodeId);
		}

		for (int i = 0; i < destinations.size(); i++) {
			Destination d = (Destination) destinations.get(i);
			d.node.traverse();
		}
	}

	public void print() {
		System.out.println("Node Id: " + nodeId + "\n" + "Node Name: " + name
				+ "\n" + "Description: " + description);
		Iterator itr = properties.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Object value = properties.get(key);
			System.out.println(key + " = " + value);
		}

	}

	private Node getNode(int nodeId, Node result) {
		if (result != null) {
			return result;
		}

		if (this.nodeId == nodeId) {
			System.out.println("Found " + nodeId);
			result = this;
		} else {
			for (int i = 0; i < destinations.size(); i++) {
				Destination d = (Destination) destinations.get(i);
				result = d.node.getNode(nodeId, result);
				if (result != null)
					break;
			}
		}

		return result;
	}

	public Node getNode(int nodeId) {
		return getNode(nodeId, null);
	}

	private Node getNode(String name, Node result) {
		if (result != null) {
			return result;
		}

		if (this.name.equals(name)) {
			System.out.println("Found " + name);
			result = this;
		} else {
			for (int i = 0; i < destinations.size(); i++) {
				Destination d = (Destination) destinations.get(i);
				result = d.node.getNode(name, result);
				if (result != null)
					break;
			}
		}

		return result;
	}

	public Node getNode(String name) {
		return getNode(name, null);
	}

	public void addDestination(Node node, String rule) {
		Destination d = new Destination(node, rule);
		destinations.add(d);
		node.addFromNode(this);
	}

	public List getDestinations() {
		return destinations;
	}

	public void addFromNode(Node node) {
		fromNodes.add(node);
	}

	public List getFromNodes() {
		return fromNodes;
	}

	public List getNodes(String nodeType) {
		List v = new ArrayList();
		HashMap map = new HashMap();
		getNodes(nodeType, map);

		Iterator itr = map.values().iterator();
		while (itr.hasNext()) {
			v.add(itr.next());
		}
		return v;
	}

	private void getNodes(String nType, HashMap map) {
		if (nodeType.equals(nType)) {
			map.put(name, this);
		}
		for (int i = 0; i < destinations.size(); i++) {
			Destination d = (Destination) destinations.get(i);
			Node dnode = d.node;
			dnode.getNodes(nType, map);
		}
	}

	public List getNodes() {
		List v = new ArrayList();
		HashMap map = new HashMap();
		getNodes(map);

		Iterator itr = map.values().iterator();
		while (itr.hasNext()) {
			v.add(itr.next());
		}
		return v;
	}

	private void getNodes(HashMap map) {
		map.put(name, this);
		for (int i = 0; i < destinations.size(); i++) {
			Destination d = (Destination) destinations.get(i);
			Node dnode = d.node;
			dnode.getNodes(map);
		}
	}

	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}

	public void setTimeoutMinutes(int timeoutMinutes) {
		if (!nodeType.equals(PROCESS)) {
			return;
		}
		properties.put(WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE, new Integer(timeoutMinutes));
	}

	public int getTimeoutMinutes() {
		if (!nodeType.equals(PROCESS)) {
			return -1;
		}
		Integer tout = (Integer) properties.get(WFXMLTagAndAttributeConstants.TIMEOUTMINUTES_ATTRIBUTE);
		if (tout != null) {
			return tout.intValue();
		} else {
			return -1;
		}
	}

	public void setTimeoutHandler(String timeoutHandler) {
		if (!nodeType.equals(PROCESS)) {
			return;
		}
		properties.put(WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE, timeoutHandler);
	}

	public String getTimeoutHandler() {
		if (!nodeType.equals(PROCESS)) {
			return null;
		}
		String handler = (String) properties.get(WFXMLTagAndAttributeConstants.TIMEOUTHANDLER_ATTRIBUTE);
		return handler;
	}

	public void setValidated() {
		validated = true;
	}

	public boolean isValidated() {
		return validated;
	}
}
