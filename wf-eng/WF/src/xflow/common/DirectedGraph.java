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
package xflow.common;

import java.sql.*;
import java.util.*;
import java.io.*;
import xflow.util.*;
import xflow.server.controller.DirectedGraphP;
import xflow.server.controller.IBatisWork;

import com.ibatis.sqlmap.client.SqlMapClient;

public class DirectedGraph implements Serializable {

  //private static Logger log = Logger.getLogger(DirectedGraph.class);

  private Node rootNode;
  private int graphId;
  private String name;
  private String description;
  private int version;

  private Map startNode = new HashMap();
  private Map endNode = new HashMap();

  /**
   *   Constructor
   */
  public DirectedGraph() {
  }

  /**
   *   Creates a new instance of DirectedGraph with name
   *   @param name The name of the graph
   */
  public DirectedGraph(String name) {
    this.name = name;
    description = name;
    version = -1;
  }

  /**
   *   Creates a new instance of DirectedGraph with name and version
   *   @param name The name of the graph
   *   @param vers The version of the graph
   */
  public DirectedGraph(String name, int vers) {
    this.name = name;
    description = name;
    version = vers;
  }

  // Accessor methods

  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String d) {
    description = d;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int v) {
    version = v;
  }

  public int getGraphId() {
    return graphId;
  }

  public void setGraphId(int i) {
    graphId = i;
  }

  /**
   *  Sets the root node of this graph.
   *  @param node the root node
   */
  public void setRootNode(Node node) {
    rootNode = node;
  }


  /**
   *  Gets the root node of this graph
   *  @return Node, the root node
   *
   */
  public Node getRootNode() {
    return rootNode;
  }

  /**
   *  Retrieves a graph from the Database, including all of its nodes.
   */
  public void loadDB() throws XflowException, SQLException {
    DirectedGraphP directGraphP = Persistence.getDirectGraphP();
    Integer gid = directGraphP.getGraphId( name, version );
    directGraphP.loadByGraphById( gid.intValue(), this );
  }





  /**
   *  Retrieves a graph from the Database using graph ID, including all of its nodes.
   */
  public static DirectedGraph loadByGraphId(int gid) throws XflowException {
    DirectedGraph res = new DirectedGraph();
    Persistence.getDirectGraphP().loadByGraphById( gid, res );
    return res;
  }




  /**
   *  Save the graph and all its nodes to the Database.
   */
  public void saveDB() throws Exception {

    IBatisWork work = new IBatisWork(){

      public void execute(SqlMapClient sqlMap) throws Exception {

        // Make sure that this graph does not already exist in the database
        if (Persistence.getDirectGraphP().graphExistsInDB( name, version )) {
          throw new XflowException(
              "There is already a graph called " + name + " version " + version + " in the database");
        }
        graphId = Util.generateUniqueIntId();
        // Save the nodes - starting from rootNode - this
        // recursively saves all of the nodes reachable from the root node
        rootNode.saveDB( graphId );
        // Get the last version number and bump it up by 1
        version = Persistence.getDirectGraphP().getLatestVersionNumber( name ) + 1;
        // Now save the graph table
        int rootNodeId = rootNode.getNodeId();
        Map params = new HashMap();
          params.put( "gid",new Integer( graphId ) );
          params.put( "name", name );
          params.put( "description", description );
          params.put( "version", new Integer( version) );
          params.put( "nid", new Integer( rootNodeId ) );
        sqlMap.insert( "insertGraph", params );
      }
    };
    Persistence.execute( work );
  }

  /**
   *  Gets a node given the node Id
   *  @return Node
   *
   */
  public Node getNode(int nodeId) {
    return rootNode.getNode(nodeId);
  }

  /**
   * Validate a DirectedGraph
   * @throws XflowException
   */
  public void validate() throws XflowException {
    validate(rootNode);
  }

  /**
   * Helper function of validate()
   * @param node
   * @throws XflowException
   */
  private void validate(Node node) throws XflowException {

    if (node.isValidated()) {
      return;
    } else {
      node.setValidated();
    }

    String type = node.getNodeType();
    if (type.equals(Node.START)) {
      validateStart(node);
    } else if (type.equals(Node.END)) {
      validateEnd(node);
    } else if (type.equals(Node.AND)) {
      validateAND(node);
    } else if (type.equals(Node.PROCESS)) {
      validateProcess(node);
    } else if (type.equals(Node.CONTAINER)) {
      validateContainer(node);
    } else if (type.equals(Node.OR)) {
      validateOR(node);
    }
    if (startNode.size() == 0) { // rule#1
      throw new XflowException("there is no Start node in the graph");
    }
    if (endNode.size() == 0) { //rule #3
      throw new XflowException("there is no End node in the graph");
    }
  }

  private void validateStart(Node node) throws XflowException {
    startNode.put(node.getName(), node);
    if (startNode.size() != 1) { // rule #1
      throw new XflowException("More than one Start node in the graph");
    } else { //only one start node
      if (node.getFromNodes().size()!=0){ //rule #2
        throw new XflowException("No nodes should go into Start node");
      } else {
        List destinations = node.getDestinations();
        if(destinations.size()==0){ // rule #2
          throw new XflowException("Start node should has at lease one node out");
        } else {
          for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination) destinations.get (i);
            String ntype = d.node.getNodeType();
            if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)){
              validate(d.node);
            } else { // rule #2
              throw new XflowException("Start node should go into Container" +
                  " or Process node.");
            }
          }
        }
      }
    }

  }

  private void validateEnd(Node node) throws XflowException {
    endNode.put(node.getName(), node);
    if (endNode.size() != 1) { // rule #3
      throw new XflowException("More than one End node in the graph");
    } else {
      if (node.getDestinations().size()!=0){ //rule #4
        throw new XflowException("No nodes should go out from End node");
      }
    }

  }

  private void validateAND(Node node) throws XflowException {
    if (node.getFromNodes().size()<2){ //rule #5
      throw new XflowException("AND node should have at least 2 nodes in");
    } else {
      List destinations = node.getDestinations();
      if(destinations.size()==0){ // rule #6
        throw new XflowException("AND node should has at lease one node out");
      } else {
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          String ntype = d.node.getNodeType();
          if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
              ntype.equals(Node.END)||ntype.equals(Node.AND)||
              ntype.equals(Node.OR)){
            validate(d.node);
          } else { // rule #7
            throw new XflowException("AND node should go into a Container," +
                " a Process, an AND, an OR or an End node.");
          }
        }
      }
    }
  }


  private void validateOR(Node node) throws XflowException {
    if (node.getFromNodes().size()<2){ //rule #5
      throw new XflowException("OR node should have at least 2 nodes in");
    } else {
      List destinations = node.getDestinations();
      if(destinations.size()==0){ // rule #6
        throw new XflowException("OR node should has at lease one node out");
      } else {
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          String ntype = d.node.getNodeType();
          if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
              ntype.equals(Node.END)||ntype.equals(Node.AND)||
              ntype.equals(Node.OR)){
            validate(d.node);
          } else { // rule #7
            throw new XflowException("OR node should go into a Container," +
                " a Process, an AND, an OR or an End node.");
          }
        }
      }
    }
  }


  private void validateProcess(Node node) throws XflowException {
    List destinations = node.getDestinations();
    if(destinations.size()==0){ // rule #8
      throw new XflowException("Process node should has at lease one node out");
    } else {
      for (int i = 0; i < destinations.size(); i++) {
        Destination d = (Destination) destinations.get (i);
        String ntype = d.node.getNodeType();
        if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
            ntype.equals(Node.END)||ntype.equals(Node.AND)||
            ntype.equals(Node.OR)){
          validate(d.node);
        } else { // rule #9
          throw new XflowException("Process node should go into a Container," +
              " a Process, an AND, an OR or an End node.");
        }
      }
    }
  }

  private void validateContainer(Node node) throws XflowException {
    List destinations = node.getDestinations();
    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      String ntype = d.node.getNodeType();
      if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
          ntype.equals(Node.END)||ntype.equals(Node.AND)||
          ntype.equals(Node.OR)){
        validate(d.node);
      } else { // rule #11
        throw new XflowException("Container node should go into a Container," +
            " a Process, an AND, an OR or End node.");
      }
    }
  }

  public String toXML () throws XflowException {
    return XflowGraphSerializer.serialize(this);
  }

  /**
   *  Gets a node given the node name
   *  @return Node
   *
   */
  public Node getNode(String nodeName) {
    return rootNode.getNode(nodeName);
  }

  public Node getEndNode() {
    return rootNode.getNode(Node.END);
    // NB. End nodes always have the name "End"
  }

  public List getNodes(String nodeType) {
    return rootNode.getNodes(nodeType);
  }

  public List getAllNodes() {
    return rootNode.getNodes();
  }
}
