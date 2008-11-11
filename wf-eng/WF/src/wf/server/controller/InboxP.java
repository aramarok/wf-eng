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
