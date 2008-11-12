

package wf.server.controller;

import wf.db.Persistence;
import wf.exceptions.XflowException;
import wf.model.WorkItem;

import java.lang.Integer;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class InboxP {


  public  void addWorkItem (int gid, String workflowName, String procName, WorkItem workitem)
      throws XflowException {
    Integer wfId = workitem.getWorkflowId();
    Integer workitemId  = workitem.getId();
    int workflowId = wfId.intValue();
    try {
      InboxRec inboxRec = new InboxRec();
      inboxRec.setGid( gid );
      inboxRec.setProcName( procName );
      inboxRec.setTimeout( false );
      inboxRec.setTimeStarted( new Date() );
      inboxRec.setWorkflowId( workflowId );
      inboxRec.setWorkflowName( workflowName );
      inboxRec.setWorkitemId( workitemId );
      Persistence.getThreadSqlMapSession().insert( "insertInboxRecord", inboxRec );
      Persistence.getWorkItemP().updateDB (workitem);
      Persistence.getWorkItemP().savePropertiesToDB (workitem, workflowName, procName );

    } catch (Exception e) {
      throw new XflowException ("Failed to save row in inbox in database", e);
    }
  }

  public  void removeWorkItem (int gid, String procName, WorkItem workitem) throws XflowException {
    Integer wfId = workitem.getWorkflowId();
    Integer workitemId  = workitem.getId();
    int workflowId = wfId.intValue();
    try {
      InboxRec inboxRec = new InboxRec();
      inboxRec.setGid( gid );
      inboxRec.setProcName( procName );
      inboxRec.setWorkitemId( workitemId );
      inboxRec.setWorkflowId( workflowId );
      Persistence.getThreadSqlMapSession().delete( "deleteInboxRecords", inboxRec );
    } catch (Exception e) {
      throw new XflowException ("Failed to delete row in inbox from database", e );
    }
  }

  public  void removeWorkItems (Integer workflowId) throws XflowException, SQLException {
    Persistence.getThreadSqlMapSession().delete( "deleteInboxRecordsForWorkflow",  workflowId  );
  }

  public  boolean isWorkItemValid (int gid, String procName, WorkItem workitem) throws XflowException, SQLException {

    Integer wfId = workitem.getWorkflowId();
    Integer workitemId  = workitem.getId();
    int workflowId = wfId.intValue();
    InboxRec inboxRec = new InboxRec();
    inboxRec.setGid( gid );
    inboxRec.setProcName( procName );
    inboxRec.setWorkitemId( workitemId );
    inboxRec.setWorkflowId( workflowId );
    List inboxRecords = Persistence.getThreadSqlMapSession().queryForList( "selectInboxRecords", inboxRec );
    return (inboxRecords != null && inboxRecords.size() > 0 );
  }

  public  java.util.Date getTimeStarted (int workflowId, String procName) throws XflowException, SQLException {
    InboxRec inboxRec = new InboxRec();
    inboxRec.setTimeout(false);
    inboxRec.setProcName( procName );
    inboxRec.setWorkflowId( workflowId );
    return (Date) Persistence.getThreadSqlMapSession().queryForObject( "selectDateStarted", inboxRec);
  }

  public  void setTimeoutFlag (int workflowId, String procName) throws XflowException, SQLException {
    InboxRec inboxRec = new InboxRec();
    inboxRec.setTimeout(true);
    inboxRec.setProcName( procName );
    inboxRec.setWorkflowId( workflowId );
    Persistence.getThreadSqlMapSession().update( "setInboxTimeoutFlag", inboxRec );
  }

  public  boolean workitemsExist (int workflowId) throws XflowException, SQLException {
    List items = Persistence.getThreadSqlMapSession().queryForList( "", new Integer( workflowId ));
    return ( items != null && items.size() > 0 );
  }
}
