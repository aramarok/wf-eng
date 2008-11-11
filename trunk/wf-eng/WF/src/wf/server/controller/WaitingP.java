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

import org.apache.log4j.Logger;

import wf.db.Persistence;
import wf.exceptions.XflowException;
import wf.model.Node;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingP {

  private static Logger log = Logger.getLogger(WaitingP.class);

  public  void addProcess (Integer workflowId, int destNodeId, int fromNodeId) throws XflowException, SQLException {

    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    params.put( "destNodeId", new Integer( destNodeId) );
    params.put( "fromNodeId", new Integer( fromNodeId) );
    Persistence.getThreadSqlMapSession().insert( "insertWaitingRecord", params );
  }

  public  void removeProcesses (List fromNodes, Integer workflowId) throws XflowException, SQLException {
    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    for (int i = 0; i < fromNodes.size(); i++) {
       Node node = (Node) fromNodes.get(i);
      params.put( "fromNodeId", new Integer( node.getNodeId() ) );
      Persistence.getThreadSqlMapSession().delete( "deleteProcFromWaiting", params );
    }

  }

  public  void removeProcesses (Integer wfId) throws XflowException, SQLException {
    Persistence.getThreadSqlMapSession().delete( "removeProcessesForWorkflow", wfId );

  }

  public  boolean allProcessesArrived (List fromNodes, Integer workflowId, int destNodeId)
      throws XflowException, SQLException {
    int count = fromNodes.size();
    Map params = new HashMap();
    params.put( "workflowId", workflowId );
    params.put( "destNodeId", new Integer( destNodeId ) );
    List nodeIDs = new ArrayList();
    for (int i = 0; i < count; i++) {
      Node node = (Node) fromNodes.get(i);
      nodeIDs.add( node.getNodeIdAsInteger() );
    }
    params.put( "fromNodeIDs", nodeIDs );
    int rcount = 0;
    Integer cnt = (Integer) Persistence.getThreadSqlMapSession().queryForObject( "countArrived", params );
    if( cnt != null ) rcount = cnt.intValue();
    log.info ("Nodes arrived count: " + rcount + " fromNodes count = " + count);
    return rcount == count;
  }


}
