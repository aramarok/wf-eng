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

package xflow.server.controller;

import org.apache.log4j.Logger;
import xflow.common.WorkItem;
import java.lang.Integer;
import xflow.common.XflowException;
import xflow.util.Persistence;
import xflow.util.Util;

import java.sql.SQLException;
import java.util.*;

public class WorkItemP {

  private static Logger log = Logger.getLogger(WorkItemP.class);


  public WorkItem getWorkItem( Integer workitemId, String workflowName, String processName ) throws SQLException, XflowException {
    WorkItemRec workItemRec = (WorkItemRec) Persistence.getThreadSqlMapSession().queryForObject( "selectWorkItemRec", workitemId);
    if( workItemRec != null ){
      String pstr = workItemRec.getPayload();
      Object payload = null;
      WorkItem witem = new WorkItem ( workitemId );
      if (pstr != null && !pstr.equals("")) {
        payload = Util.objFromXML(pstr);
      }
      witem.setPayload ( payload );
      witem._setPayloadType( workItemRec.getPayloadType() );
      loadPropertiesFromDB (witem, workflowName, processName );
      return witem;
    }
    return null;
  }

  public  List getWorkItems (String workflowName, String processName) throws XflowException, SQLException {

    List v = new ArrayList();
    Map params = new HashMap();
    params.put( "processName", processName );
    params.put( "workflowName", workflowName );
    List in = Persistence.getThreadSqlMapSession().queryForList( "selectInboxRecordsForProcess", params );
    for (Iterator j = in.iterator(); j.hasNext();) {
      InboxRec inboxRec = (InboxRec) j.next();
      if (Persistence.getWorkflowP().isCompleted( inboxRec.getWorkflowId() ) ) {
        continue;  // Disregard if workflow has ended
      }
      WorkItem workItem = getWorkItem( inboxRec.getWorkitemId(), workflowName,  processName);
      if( workItem!= null ){
        workItem.setWorkflowId(new Integer(inboxRec.getWorkflowId()));
        v.add (workItem);
      }
    }

    return v;
  }

  public  WorkItem getNextWorkItem (String workflowName, String processName) throws XflowException, SQLException {

    WorkItem witem = null;
    log.info( "1");
    Map params = new HashMap();
    params.put( "processName", processName );
    params.put( "workflowName", workflowName );
    Integer numItems = (Integer) Persistence.getThreadSqlMapSession().queryForObject( "selectNumInboxItems", params );
    int   count = numItems.intValue();
    if (count > 0) {
      List inboxRecs = Persistence.getThreadSqlMapSession().queryForList( "selectInboxRecordsForProcess", params );
      if( inboxRecs.size() > 0 ){
        InboxRec inboxRec = (InboxRec) inboxRecs.get( 0 );
        int wfid = inboxRec.getWorkflowId();
        if (Persistence.getWorkflowP().isCompleted(wfid) == false) {
          witem = getWorkItem (inboxRec.getWorkitemId(), workflowName, processName );
          witem.setWorkflowId(new Integer(wfid));
        } else {
          log.error ("Can't load workitem: " + inboxRec.getWorkitemId());
        }
      }
    }
    return witem;
  }

  public  WorkItem getWorkItem (Integer wid, String processName) throws XflowException, SQLException {
    WorkItem witem = null;
    Integer wfId = witem.getWorkflowId();

    String workflowName = "";
    workflowName = (String) Persistence.getThreadSqlMapSession().queryForObject(  "selectGraphNameByWorkItemId", wfId );
    Map params = new HashMap();
    params.put( "processName", processName );
    params.put( "workflowName", workflowName );
    InboxRec inboxRec = (InboxRec) Persistence.getThreadSqlMapSession().queryForObject(  "selectInboxRecordsForProcess", params );
    if (inboxRec != null) {
      int wfid = inboxRec.getWorkflowId();
      witem = getWorkItem (wid, workflowName, processName );
      if( witem != null ){
        witem.setWorkflowId(new Integer(wfid));
      } else {
        log.error ("Can't load workitem: " + wid);
      }
    }

    return witem;
  }

  public  void saveDB ( WorkItem witem) throws XflowException, SQLException {
    Object payload = witem.getPayload();
    String payloadStr = "";
    String payloadType = witem.getPayloadType();
    if (payload != null) payloadStr = Util.objToXML (payload);
    Map params = new HashMap();
    params.put( "payloadStr", payloadStr );
    params.put( "payloadType", payloadType );
    Integer id = (Integer) Persistence.getThreadSqlMapSession().insert( "insertWorkItem", params );
    witem.setId(   new Integer( id.intValue() ) );
  }

  public  void updateDB (WorkItem witem) throws XflowException, SQLException {
    Integer workitemId = witem.getId();
    Object payload = witem.getPayload();
    String payloadStr = "";
    if (payload != null) {
      payloadStr = Util.objToXML (payload);
    }
    Map params = new HashMap();
    params.put( "payloadStr", payloadStr );
    params.put( "workitemId",  workitemId  );
    Persistence.getThreadSqlMapSession().update( "updateWorkItem", params );
  }


  public  void loadPropertiesFromDB (WorkItem witem, String workflowName, String procName )
      throws XflowException, SQLException {
    HashMap properties = witem.getProperties();
    Integer workitemId = witem.getId();
    Map params = new HashMap();
    params.put( "wid", workitemId );
    params.put( "procName", procName );
    params.put( "workflowName", workflowName );
    List props = Persistence.getThreadSqlMapSession().queryForList( "selectWorkItemProps", params );
    for (Iterator j = props.iterator(); j.hasNext();) {
      Map entry = (Map) j.next();
      Object value = Util.getValue( entry, "value");
      if( value!= null ){
        value = Util.objFromXML( value.toString() );
      }
      properties.put( Util.getValue( entry, "name"), value );
    }

  }

  public  void savePropertiesToDB (WorkItem witem, String workflowName, String procName )
      throws XflowException, SQLException {
    // Insert the work item properties
    HashMap properties = witem.getProperties();
    Iterator itr = properties.keySet().iterator();
    while (itr.hasNext()) {
      String key = (String)itr.next();
      Object value = properties.get(key);
      if (value == null) {
        continue;
      }
      String valueStr = Util.objToXML(value);
      Integer workItemId = witem.getId();
      Map params = new HashMap();
      params.put( "workitemId" , workItemId );
      params.put( "workflowName" , workflowName );
      params.put( "procName" , procName );
      params.put( "name" , key );
      params.put( "valueStr", valueStr  );
      Persistence.getThreadSqlMapSession().insert(  "insertWorkItemProp", params );

    }

  }

  public  void deleteDB(WorkItem wi) throws XflowException, SQLException {
    Integer workItemId = wi.getId();
    Persistence.getThreadSqlMapSession().delete( "deleteWorkitemprops", workItemId );
    Persistence.getThreadSqlMapSession().delete( "deleteWorkitem", workItemId );
  }
}

