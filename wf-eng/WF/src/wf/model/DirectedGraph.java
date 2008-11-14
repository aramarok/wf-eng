
package wf.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wf.db.Persistence;
import wf.exceptions.WorkFlowException;
import wf.server.controller.DirectedGraphP;
import wf.server.controller.IBatisWork;
import wf.util.Util;
import wf.xml.GraphSerializer;

import com.ibatis.sqlmap.client.SqlMapClient;

public class DirectedGraph implements Serializable {

  private Node rootNode;
  private int graphId;
  private String name;
  private String description;
  private int version;

  private Map startNode = new HashMap();
  private Map endNode = new HashMap();

  
  public DirectedGraph() {
  }

  
  public DirectedGraph(String name) {
    this.name = name;
    description = name;
    version = -1;
  }

  
  public DirectedGraph(String name, int vers) {
    this.name = name;
    description = name;
    version = vers;
  }

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

  
  public void setRootNode(Node node) {
    rootNode = node;
  }


  
  public Node getRootNode() {
    return rootNode;
  }

  
  public void loadDB() throws WorkFlowException, SQLException {
    DirectedGraphP directGraphP = Persistence.getDirectGraphP();
    Integer gid = directGraphP.getGraphId( name, version );
    directGraphP.loadByGraphById( gid.intValue(), this );
  }





  
  public static DirectedGraph loadByGraphId(int gid) throws WorkFlowException {
    DirectedGraph res = new DirectedGraph();
    Persistence.getDirectGraphP().loadByGraphById( gid, res );
    return res;
  }




  
  public void saveDB() throws Exception {

    IBatisWork work = new IBatisWork(){

      public void execute(SqlMapClient sqlMap) throws Exception {
        if (Persistence.getDirectGraphP().graphExistsInDB( name, version )) {
          throw new WorkFlowException(
              "There is already a graph called " + name + " version " + version + " in the database");
        }
        graphId = Util.generateUniqueIntId();
        rootNode.saveDB( graphId );
        version = Persistence.getDirectGraphP().getLatestVersionNumber( name ) + 1;
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

  
  public Node getNode(int nodeId) {
    return rootNode.getNode(nodeId);
  }

  
  public void validate() throws WorkFlowException {
    validate(rootNode);
  }

  
  private void validate(Node node) throws WorkFlowException {

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
    if (startNode.size() == 0) {
      throw new WorkFlowException("there is no Start node in the graph");
    }
    if (endNode.size() == 0) {
      throw new WorkFlowException("there is no End node in the graph");
    }
  }

  private void validateStart(Node node) throws WorkFlowException {
    startNode.put(node.getName(), node);
    if (startNode.size() != 1) {
      throw new WorkFlowException("More than one Start node in the graph");
    } else {
      if (node.getFromNodes().size()!=0){
        throw new WorkFlowException("No nodes should go into Start node");
      } else {
        List destinations = node.getDestinations();
        if(destinations.size()==0){
          throw new WorkFlowException("Start node should has at lease one node out");
        } else {
          for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination) destinations.get (i);
            String ntype = d.node.getNodeType();
            if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)){
              validate(d.node);
            } else {
              throw new WorkFlowException("Start node should go into Container" +
                  " or Process node.");
            }
          }
        }
      }
    }

  }

  private void validateEnd(Node node) throws WorkFlowException {
    endNode.put(node.getName(), node);
    if (endNode.size() != 1) {
      throw new WorkFlowException("More than one End node in the graph");
    } else {
      if (node.getDestinations().size()!=0){
        throw new WorkFlowException("No nodes should go out from End node");
      }
    }

  }

  private void validateAND(Node node) throws WorkFlowException {
    if (node.getFromNodes().size()<2){
      throw new WorkFlowException("AND node should have at least 2 nodes in");
    } else {
      List destinations = node.getDestinations();
      if(destinations.size()==0){
        throw new WorkFlowException("AND node should has at lease one node out");
      } else {
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          String ntype = d.node.getNodeType();
          if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
              ntype.equals(Node.END)||ntype.equals(Node.AND)||
              ntype.equals(Node.OR)){
            validate(d.node);
          } else {
            throw new WorkFlowException("AND node should go into a Container," +
                " a Process, an AND, an OR or an End node.");
          }
        }
      }
    }
  }


  private void validateOR(Node node) throws WorkFlowException {
    if (node.getFromNodes().size()<2){
      throw new WorkFlowException("OR node should have at least 2 nodes in");
    } else {
      List destinations = node.getDestinations();
      if(destinations.size()==0){
        throw new WorkFlowException("OR node should has at lease one node out");
      } else {
        for (int i = 0; i < destinations.size(); i++) {
          Destination d = (Destination) destinations.get (i);
          String ntype = d.node.getNodeType();
          if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
              ntype.equals(Node.END)||ntype.equals(Node.AND)||
              ntype.equals(Node.OR)){
            validate(d.node);
          } else {
            throw new WorkFlowException("OR node should go into a Container," +
                " a Process, an AND, an OR or an End node.");
          }
        }
      }
    }
  }


  private void validateProcess(Node node) throws WorkFlowException {
    List destinations = node.getDestinations();
    if(destinations.size()==0){
      throw new WorkFlowException("Process node should has at lease one node out");
    } else {
      for (int i = 0; i < destinations.size(); i++) {
        Destination d = (Destination) destinations.get (i);
        String ntype = d.node.getNodeType();
        if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
            ntype.equals(Node.END)||ntype.equals(Node.AND)||
            ntype.equals(Node.OR)){
          validate(d.node);
        } else {
          throw new WorkFlowException("Process node should go into a Container," +
              " a Process, an AND, an OR or an End node.");
        }
      }
    }
  }

  private void validateContainer(Node node) throws WorkFlowException {
    List destinations = node.getDestinations();
    for (int i = 0; i < destinations.size(); i++) {
      Destination d = (Destination) destinations.get (i);
      String ntype = d.node.getNodeType();
      if (ntype.equals(Node.CONTAINER)||ntype.equals(Node.PROCESS)||
          ntype.equals(Node.END)||ntype.equals(Node.AND)||
          ntype.equals(Node.OR)){
        validate(d.node);
      } else {
        throw new WorkFlowException("Container node should go into a Container," +
            " a Process, an AND, an OR or End node.");
      }
    }
  }

  public String toXML () throws WorkFlowException {
    return GraphSerializer.serialize(this);
  }

  
  public Node getNode(String nodeName) {
    return rootNode.getNode(nodeName);
  }

  public Node getEndNode() {
    return rootNode.getNode(Node.END);
  }

  public List getNodes(String nodeType) {
    return rootNode.getNodes(nodeType);
  }

  public List getAllNodes() {
    return rootNode.getNodes();
  }
}
