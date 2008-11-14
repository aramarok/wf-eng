package wf.server.controller;

import wf.db.Persistence;
import wf.exceptions.WorkFlowException;
import wf.model.DirectedGraph;
import wf.model.Node;
import wf.util.Util;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;
import java.util.Hashtable;


public class DirectedGraphP {

  private static Logger log = Logger.getLogger(DirectedGraphP.class);

  public DirectedGraph loadByGraphById( int gid, DirectedGraph destination) throws WorkFlowException {
    try {
      Map m =  (Map) Persistence.getThreadSqlMapSession().queryForObject( "getGraph", new Integer( gid ));
      Integer version = (Integer) Util.getValue( m, "version");
      destination.setVersion( version.intValue() );
      destination.setDescription( (String ) Util.getValue( m,"description") );
      Integer iobj = (Integer) Util.getValue( m, "nid");
      int rootNodeId = iobj.intValue();
      destination.setGraphId( gid );
      Node rootNode = new Node(rootNodeId);
      destination.setRootNode( rootNode );
      rootNode.expand();
      return destination;
    } catch (Throwable c) {
      throw new WorkFlowException ("Failed to load workflow from database.", c);
    }
  }


  public Integer getGraphId(String graphName, int version) throws SQLException {
    Map params = new Hashtable();
    params.put("name", graphName );
    if (version == -1) {
      params.put("version" , getMaxGraphVersion(  graphName ) );
    }else{
      params.put("version" , new Integer( version ) );
    }
    return (Integer) Persistence.getThreadSqlMapSession().queryForObject( "getGraphId",params  );
  }

  private Integer getMaxGraphVersion( String name) throws SQLException {
    Integer i = (Integer) Persistence.getThreadSqlMapSession().queryForObject( "getMaxGraphVersion", name  );
    if( i == null) i = new Integer( 0 );
    return i;
  }

  public int getLatestVersionNumber( String name ) throws SQLException {
    return getMaxGraphVersion( name ).intValue();
  }

  public DirectedGraph loadDirectedGraph(String name, int version) throws SQLException, WorkFlowException {
    Integer graphId = getGraphId( name, version );
    return loadByGraphById( graphId.intValue(), new DirectedGraph());
  }

  public boolean graphExistsInDB( String graphName, int version) throws SQLException {
    if (version == -1) return false;
    return ( getGraphId( graphName,  version) != null);
  }

}
