/*
* ====================================================================
*
* XFLOW - Process Management System
* Copyright (C) 2003 Rob Tan
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions, and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions, and the disclaimer that follows
*    these conditions in the documentation and/or other materials
*    provided with the distribution.
*
* 3. The name "XFlow" must not be used to endorse or promote products
*    derived from this software without prior written permission.  For
*    written permission, please contact rcktan@yahoo.com
*
* 4. Products derived from this software may not be called "XFlow", nor
*    may "XFlow" appear in their name, without prior written permission
*    from the XFlow Project Management (rcktan@yahoo.com)
*
* In addition, we request (but do not require) that you include in the
* end-user documentation provided with the redistribution and/or in the
* software itself an acknowledgement equivalent to the following:
*     "This product includes software developed by the
*      XFlow Project (http://xflow.sourceforge.net/)."
* Alternatively, the acknowledgment may be graphical using the logos
* available at http://xflow.sourceforge.net/
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
*
* ====================================================================
* This software consists of voluntary contributions made by many
* individuals on behalf of the XFlow Project and was originally
* created by Rob Tan (rcktan@yahoo.com)
* For more information on the XFlow Project, please see:
*           <http://xflow.sourceforge.net/>.
* ====================================================================
*/
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

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * The Node class represents a node in a directed graph.
 * A directed graph is used to represent a workflow model.
 */
public class Node implements Serializable {

 // private static Logger log = Logger.getLogger(Node.class);

  public static final String PROCESS = "Process";
  public static final String AND = "And";
  public static final String OR = "Or";
  public static final String START = "Start";
  public static final String END = "End";
  public static final String CONTAINER = "Container";

  private int     nodeId;
  private String  nodeType;
  private String  name;
  private String  description;
  private List  destinations;
  private List  fromNodes;
  private HashMap properties;
  private boolean validated = false;

  static int count = 0;

  public Node() {
  }

  /**
   *  Constructs a new node
   *  @param  nodeName
   */
  public Node (String nodeName, String nodeType) {
    this.name = nodeName;
    this.nodeType = nodeType;
    destinations = new ArrayList();
    fromNodes = new ArrayList();
    description = nodeName;
    properties = new HashMap();
  }


  /**
   *  Constructs a new node
   *  @param  nodeId
   */
  public Node (int nodeId) {
    this.nodeId  = nodeId;
    destinations = new ArrayList();
    fromNodes = new ArrayList();
    properties = new HashMap();
  }

  /**
   *  Returns the node ID
   *  @return int nodeId
   */
  public int getNodeId () {
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

  /**
   *  Returns the node ID
   *  @return Integer nodeId
   */
  public Integer getNodeIdAsInteger () {
    return new Integer(nodeId);
  }



  public String getType() {
    return nodeType;
  }

  public void setType(String type) {
    this.nodeType = type;
  }


  /**
   * @deprecated  use getName
   *  Returns the node name
   *  @return String node name
   */
  public String getNodeName () {
    return name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   *  Returns the node type
   *  @return String node type
   */
  public String getNodeType () {
    return nodeType;
  }

  /**
   *  Returns node description
   *  @return description
   */
  public String getDescription () {
    return description;
  }

  /**
   *  Sets the node description
   *  @param  d
   */
  public void setDescription (String d) {
    description = d;
  }

  /**
   *  Returns the node's containee graph name. Only valid if node's type is CONTAINER
   *  @return containee
   */
  public String getContainee () {
    String containee = null;
    if (nodeType.equals(Node.CONTAINER)) {
      containee = (String)properties.get ("containee");
    }
    return containee;
  }


  /**
   *  Sets the node's containee graph name. Only valid if node's type is CONTAINER
   *  @param  graphName
   */
  public void setContainee(String graphName) {
    if (nodeType.equals(Node.CONTAINER)) {
      properties.put ("containee", graphName);
    }
  }

  /**
   *  Returns the node's containee graph version. Only valid if node's type is CONTAINER
   *  @return containeeVersion
   */
  public int getContaineeVersion () {
    int version = -1;
    if (nodeType.equals(Node.CONTAINER)) {
      Integer iObj = (Integer)properties.get ("containeeVersion");
      if (iObj != null) {
        version = iObj.intValue();
      }
    }
    return version;
  }

  /**
   *  Sets the node's containee graph version. Only valid if node's type is CONTAINER
   *  @param  version
   */
  public void setContaineeVersion(int version) {
    if (nodeType.equals(Node.CONTAINER)) {
      properties.put ("containeeVersion", new Integer(version));
    }
  }

  /**
   *  Recursively loads this node and all nodes reachable from this node
   *  from database.
   *
   *  @param  hashTable HashMap The hash table of all processed nodes so far
   */
  private void expand ( final HashMap hashTable ) throws Exception {

    //System.out.println ("Expanding " + nodeId);

    IBatisWork work = new IBatisWork(){

      public void execute(SqlMapClient sqlMap) throws Exception {
        Integer nid = new Integer( nodeId );
        Node n = (Node) sqlMap.queryForObject( "getNode", nid);
        name = n.getName();
        description = n.getDescription();
        nodeType    = n.getNodeType();
        if (nodeType.equals(Node.START)) {
          name = Node.START;
        } else if (nodeType.equals(Node.END)) {
          name = Node.END;
        }
        List params = sqlMap.queryForList( "getNodeProperties", nid );
        for (Iterator j = params.iterator(); j.hasNext();) {
          Map entry = (Map) j.next();
          properties.put (Util.getValue( entry, "name"), Util.objFromXML( (String )Util.getValue( entry, "value")));
        }

        List destinations = sqlMap.queryForList( "getNodeDestinations", nid );
        for (Iterator j = destinations.iterator(); j.hasNext();) {
          Map entry = (Map) j.next();
          Integer destNodeId = (Integer) Util.getValue( entry, "destnid");
          String rule =(String ) Util.getValue( entry, "rule" );
          Node destNode = (Node) hashTable.get(destNodeId);
          if (destNode == null) {
            // 1. Create the node
            destNode = new Node(destNodeId.intValue() );
            hashTable.put (destNodeId, destNode);
            destNode.expand( hashTable);
          } else {
            // Don't process this node - we have already processed it
            System.out.println ("Already exists in hash: " + destNodeId);
          }

          // 2. Add the destination to this node
          addDestination (destNode, rule);
        }
      }

    };
    Persistence.execute( work );
  }

  /**
   *  Recursively loads this node and all nodes reachable from this node
   *  from database.
   *
   */
  public void expand () throws Exception {

    HashMap nodeHash = new HashMap();
    expand ( nodeHash);
  }


  /**
   *  Recursively saves the links between a node and its destinations.
   *
   *  @param  hash of already processed nodes
   */
  private void saveLink (final HashMap hash) throws Exception {

    // Already saved links for this node
    Node n = (Node) hash.get(name);
    if (n != null)
      return;

    // Put this node in hash table - this marks the node as "processed"
    hash.put (name, this);

    IBatisWork work = new IBatisWork(){
      public void execute(SqlMapClient sqlMap) throws Exception {
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          Node destNode = d.node;
          int destNodeId = destNode.getNodeId();
          String rule = d.rule;
          Map params = new HashMap();
          params.put( "nid", new Integer( nodeId ) );
          params.put( "destnid", new Integer( destNodeId ) );
          params.put( "rule", rule );
          sqlMap.insert( "insertDestination", params);
          // Now recurse and save links of destination nodes

        }
         for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination) destinations.get (i);
            Node destNode = d.node;
            destNode.saveLink (hash );
          }
      }
    };

    Persistence.execute( work );

  }

  /**
   *  Recursively saves the links between a node and its destinations.
   */
  private void saveLink () throws Exception {

    HashMap hash = new HashMap();
    saveLink (hash);
  }

  /**
   *  Recursively saves a node, its destinations and all links
   *  between nodes to the database.
   *
   *  @param  gid The graph ID
   */
  public void saveDB (int gid) throws Exception {

    // First save the node and all nodes reachable by it
    HashMap hash = new HashMap();
    saveDB (gid, hash);
    saveLink ();
  }

  /**
   *  Recursively saves a node, its destinations and all links
   *  between nodes to the database.
   *
   *  @param  gid The graph ID
   *  @param  hash The hash table of all processed nodes
   */
  private void saveDB (final int gid, final HashMap hash) throws Exception {

    // Already saved this node
    Object o = hash.get(name);
   // Node n = (Node) o;
    if (o != null) {
      System.out.println("N = " +  o.getClass().getName() );
      return;
    }

    IBatisWork work = new IBatisWork(){
      public void execute(SqlMapClient sqlMap) throws Exception {
        Map params = new Hashtable();
        params.put( "gid", new Integer( gid) );
        params.put( "name", name );
        params.put( "nodetype", nodeType );
        params.put( "description", description );
        Integer nid = (Integer) sqlMap.insert( "insertNode", params);
        nodeId = nid.intValue();
        hash.put (name, this);
        Iterator itr = properties.keySet().iterator();
        while (itr.hasNext()) {
          String key = (String)itr.next();
          Object value = properties.get(key);
          if (value == null) {
            continue;
          }
          String valueStr = Util.objToXML(value);


          params = new Hashtable();
          params.put( "nid",nid );
          params.put( "name", key );
          params.put( "val", value );
          sqlMap.insert( "insertNodeprop",params);
        }
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          Node destNode = d.node;
          destNode.saveDB(gid, hash);
        }

      }
    };
    Persistence.execute( work );


  }

  /**
   *  Detects if the graph contains cycles.

   *  @param  hashTable - contains the nodes already visited
   *  @param cycleDetected - true if cycle has been detected.
   *
   *  @return boolean true if cycle detected.
   *
   */
  private boolean detectCycle (HashMap hashTable, boolean cycleDetected) {

    // We have found a cycle - rewind the recursion
    if (cycleDetected) {
      return true;
    }

    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      Node destNode = d.node;
      Integer destNodeId = destNode.getNodeIdAsInteger();

      // Is destination node already in the list of nodes we came from?
      // If yes, we have a cycle.
      Node findNode = (Node) hashTable.get(destNodeId);
      if (findNode != null) {
        // We've got a cycle. Unwind
        System.out.println
            ("Cycle detected. From Node: " + nodeId +
            " To Node: " + destNodeId);
        cycleDetected = true;
        break; // Get out
      } else {
        // No cycle detected - continue the graph traversal
        hashTable.put (destNodeId, destNode);
        cycleDetected = destNode.detectCycle (hashTable,cycleDetected);
        hashTable.remove (destNodeId);
      }
    }

    return cycleDetected;
  }

  /**
   *  Detects if the graph contains cycles.
   */
  public boolean detectCycle () {
    HashMap hashTable = new HashMap();

    Integer objKey = new Integer (nodeId);
    hashTable.put (objKey, this);
    boolean result = detectCycle (hashTable, false);
    return result;
  }

  /**
   *  Recursively traverses all the nodes of a graph.
   *  Useful for debugging.
   */
  public void traverse () {
    this.print();
    if (destinations.size() == 0) {
      System.out.println ("No more destinations for " + nodeId);
    }

    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      d.node.traverse();
    }
  }

  /**
   *  Prints out node id and description of node.
   *  Useful for debugging.
   */
  public void print () {
    System.out.println ("Node Id: " + nodeId + "\n" +
        "Node Name: " + name + "\n" +
        "Description: " + description);
    Iterator itr = properties.keySet().iterator();
    while (itr.hasNext()) {
      String key = (String)itr.next();
      Object value = properties.get(key);
      System.out.println (key + " = " + value);
    }

  }


  /**
   *  Finds a node within a graph
   *
   *  @param   nodeId  The node ID for finding
   *  @param   result The result
   *
   *  @return Node the result, null if not found
   */
  private Node getNode (int nodeId, Node result) {
    if (result != null) {
      return result;
    }

    if (this.nodeId == nodeId) {
      System.out.println ("Found " + nodeId);
      result = this;
    } else {
      for (int i = 0; i < destinations.size(); i++) {
        Destination d = (Destination) destinations.get (i);
        result = d.node.getNode(nodeId, result);
        if (result != null)
          break;
      }
    }

    return result;
  }

  /**
   *  Finds and returns a node within a graph given a node ID
   *
   *  @param   nodeId  The node ID for finding
   *  @return Node the result, null if not found
   */
  public Node getNode (int nodeId) {
    return getNode (nodeId, null);
  }

  /**
   *  Finds a node within a graph
   *
   *  @param   name  The node name for finding
   *  @param   result The result
   *
   *  @return Node the result, null if not found
   */
  private Node getNode (String name, Node result) {
    if (result != null) {
      return result;
    }

    if (this.name.equals(name)) {
      System.out.println ("Found " + name);
      result = this;
    } else {
      for (int i = 0; i < destinations.size(); i++) {
        Destination d = (Destination) destinations.get (i);
        result = d.node.getNode(name, result);
        if (result != null)
          break;
      }
    }

    return result;
  }

  /**
   *  Finds and returns a node within a graph given a node name
   *
   *  @param   name  The node name for finding
   *  @return Node the result, null if not found
   */
  public Node getNode (String name) {
    return getNode (name, null);
  }


  /**
   *  Adds a destination and a rule to evaluate a workflowobject's
   *  transition to this destination.
   *
   *  @param  node  The destination node
   *  @param  rule  The rule for reaching this destination
   *
   */
  public void addDestination (Node node, String rule) {
    Destination d = new Destination(node, rule);
    destinations.add (d);
    node.addFromNode (this);
  }

  /**
   * @return List - this node's list of destinations
   */
  public List getDestinations () {
    return destinations;
  }

  /**
   *  Adds a fromNode to this node
   *
   *  @param  node  The from node
   *
   */
  public void addFromNode (Node node) {
    fromNodes.add (node);
  }

  /**
   * @return List - this node's list of from nodes
   */
  public List getFromNodes () {
    return fromNodes;
  }

  /**
   * @return List - all descendant nodes of specified type
   */
  public List getNodes (String nodeType) {
    List v = new ArrayList();
    HashMap map = new HashMap();
    getNodes (nodeType, map);

    Iterator itr = map.values().iterator();
    while (itr.hasNext()) {
      v.add (itr.next());
    }
    return v;
  }

  private void getNodes (String nType, HashMap map) {
    if (nodeType.equals(nType)) {
      map.put(name, this);
    }
    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      Node dnode = d.node;
      dnode.getNodes (nType, map);
    }
  }

  /**
   * @return List - all descendant nodes
   */
  public List getNodes () {
    List v = new ArrayList();
    HashMap map = new HashMap();
    getNodes (map);

    Iterator itr = map.values().iterator();
    while (itr.hasNext()) {
      v.add (itr.next());
    }
    return v;
  }

  private void getNodes (HashMap map) {
    map.put (name, this);
    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      Node dnode = d.node;
      dnode.getNodes (map);
    }
  }

  /**
   *  Sets a property on a node
   *
   *  @param key   the property name
   *  @param value the property value - must be serializable
   *
   */
  public void setProperty (String key, Object value) {
    properties.put(key, value);
  }

  /**
   *  Gets a node's property
   *
   *  @param key   the property name
   *  @return the  property value
   */
  public Object getProperty (String key) {
    return properties.get(key);
  }

  /**
   *  Sets the timeout value for a Process node
   *
   *  @param timeoutMinutes the timeout in minutes
   *
   */
  public void setTimeoutMinutes (int timeoutMinutes) {
    if (!nodeType.equals(PROCESS)) {
      return;
    }
    properties.put("timeoutMinutes", new Integer(timeoutMinutes));
  }

  /**
   *  Gets the timeout for a Process node
   *
   *  @return the timeout in minutes
   */
  public int getTimeoutMinutes () {
    if (!nodeType.equals(PROCESS)) {
      return -1;
    }
    Integer tout = (Integer)properties.get("timeoutMinutes");
    if (tout != null) {
      return tout.intValue();
    } else {
      return -1;
    }
  }

  /**
   *  Sets the timeout handler for a Process node
   *
   *  @param timeoutHandler the name of the timeout handler (a workflow name)
   *
   */
  public void setTimeoutHandler (String timeoutHandler) {
    if (!nodeType.equals(PROCESS)) {
      return;
    }
    properties.put("timeoutHandler", timeoutHandler);
  }

  /**
   *  Gets the timeout handler for a Process node
   *
   *  @return the timeout handler name
   */
  public String getTimeoutHandler () {
    if (!nodeType.equals(PROCESS)) {
      return null;
    }
    String handler = (String)properties.get("timeoutHandler");
    return handler;
  }


  public void setValidated() {
    validated = true;
  }

  public boolean isValidated() {
    return validated;
  }
}
