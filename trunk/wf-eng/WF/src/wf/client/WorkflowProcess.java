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
package wf.client;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

import wf.cfg.XflowConfig;
import wf.client.auth.User;
import wf.exceptions.XflowException;
import wf.jms.JMSSubscriber;
import wf.jms.JMSTopicConnection;
import wf.jms.SynchQueueMessaging;
import wf.jms.model.CompleteWorkItemRequest;
import wf.jms.model.CompleteWorkItemResponse;
import wf.jms.model.GetNextWorkItemRequest;
import wf.jms.model.GetNextWorkItemResponse;
import wf.jms.model.GetWorkItemRequest;
import wf.jms.model.GetWorkItemResponse;
import wf.jms.model.GetWorkItemsRequest;
import wf.jms.model.GetWorkItemsResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.ValidateProcessRequest;
import wf.jms.model.ValidateProcessResponse;
import wf.model.WorkItem;
import wf.util.Util;

/**
 *  A WorkflowProcess receives work items from the XFlow system and
 *  "works" on them. It is bound or associated to a node in the workflow model. 
 */
public class WorkflowProcess implements MessageListener {

  private String workflowName;
  private int    workflowVersion;
  private String procName;
  private InboxMessageListener mlistener;
  private User user;
  private JMSSubscriber subscriber;

  private static Logger log = Logger.getLogger(WorkflowProcess.class);

  public void onMessage (Message msg) {

    WorkItem workItem = null;

    try {
      BytesMessage bytesMessage = (BytesMessage)msg;
      byte[] barr = new byte[10000];
      bytesMessage.readBytes (barr);

      ByteArrayInputStream in = new ByteArrayInputStream(barr);
      ObjectInputStream sin = new ObjectInputStream(in);
      workItem = (WorkItem) sin.readObject();
    } catch(Throwable t) {
      log.error("onMessage error", t);
    }

    mlistener.onMessage (workItem);
  }

  /**
   *  WorkflowProcess constructor
   *
   *  @param    wfName      the workflow name
   *  @param    wfVersion   the workflow version - set to -1 if the latest version is to be used
   *  @param    processName the process name - must be the name of a valid process node in the workflow model
   *  @param    listener    the inbox listener for asynchronous delivery of work items - may be null
   *  @param    user        the user
   *  @exception XflowException
   */
  public WorkflowProcess (String wfName, int wfVersion, String processName, InboxMessageListener listener,
                          User user) throws XflowException {

    try {
      JMSTopicConnection.initialize();
    } catch (JMSException e) {
      throw new XflowException (e);
    }

    workflowName = wfName;
    workflowVersion = wfVersion;
    procName = processName;
    mlistener = listener;
    this.user = user;

    // Validate workflowName and processName
    ValidateProcessRequest req = new ValidateProcessRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;

    ValidateProcessResponse resp = (ValidateProcessResponse) sendRequest(req);
    if (!resp.ok) {
      throw new XflowException ("Unrecognized process name in specified workflow.");
    }


    // Start a subscription for inbox events
    if (listener != null) {
      subscriber = new JMSSubscriber(this, XflowConfig.XFLOW_TOPIC(), "ProcessName in ('" + workflowName +
          procName + "')");
    }
  }

  /**
   *  Gets a list of work items from this process's inbox.
   *
   *  @return A list of WorkItem objects
   *  @exception XflowException
   */
  public List getWorkItems () throws XflowException {

    GetWorkItemsRequest req = new GetWorkItemsRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    GetWorkItemsResponse resp = (GetWorkItemsResponse)sendRequest (req);
    return resp.workItems;
  }

  /**
   *  Gets the next work item (in First-In-First-Out order) from the inbox
   *
   *  @return A WorkItem object or null - if there are no work items.
   *  @exception XflowException
   */
  public WorkItem getNextWorkItem() throws XflowException {

    GetNextWorkItemRequest req = new GetNextWorkItemRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    GetNextWorkItemResponse resp = (GetNextWorkItemResponse)sendRequest (req);
    return resp.workItem;
  }

  /**
   *  Gets a work item with a specific work item ID from the inbox
   *
   *  @return A WorkItem object or null - if there is no such work item
   *  @exception XflowException
   */
  public WorkItem getWorkItem(Integer workItemId) throws XflowException {

    GetWorkItemRequest req = new GetWorkItemRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    req.workItemId = workItemId;
    GetWorkItemResponse resp = (GetWorkItemResponse)sendRequest (req);
    return resp.workItem;
  }

  /**
   *  Completes a work item. This is typically invoked by a workflow process after
   *  it is done with its processing of the work item.
   *
   *  @exception XflowException
   */
  public CompleteWorkItemResponse completeWorkItem(WorkItem workItem) throws XflowException {

    CompleteWorkItemRequest req = new CompleteWorkItemRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    req.workItem = workItem;
    CompleteWorkItemResponse resp = (CompleteWorkItemResponse)sendRequest (req);
    return resp;
  }

  private static Response sendRequest (Request req) throws XflowException {

    req.replyName = Util.generateUniqueStringId();
    try {
      Response resp = SynchQueueMessaging.sendRequest (req);
      if (resp.responseCode != Response.SUCCESS) {
        throw new XflowException(resp.message);
      }
      return resp;
    } catch (Exception t) {
      throw new XflowException (t);
    }
  }
}
