
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
    ValidateProcessRequest req = new ValidateProcessRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;

    ValidateProcessResponse resp = (ValidateProcessResponse) sendRequest(req);
    if (!resp.ok) {
      throw new XflowException ("Unrecognized process name in specified workflow.");
    }
    if (listener != null) {
      subscriber = new JMSSubscriber(this, XflowConfig.XFLOW_TOPIC(), "ProcessName in ('" + workflowName +
          procName + "')");
    }
  }

  
  public List getWorkItems () throws XflowException {

    GetWorkItemsRequest req = new GetWorkItemsRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    GetWorkItemsResponse resp = (GetWorkItemsResponse)sendRequest (req);
    return resp.workItems;
  }

  
  public WorkItem getNextWorkItem() throws XflowException {

    GetNextWorkItemRequest req = new GetNextWorkItemRequest();
    req.workflowName = workflowName;
    req.workflowVersion = workflowVersion;
    req.processName = procName;
    req.user = user;
    GetNextWorkItemResponse resp = (GetNextWorkItemResponse)sendRequest (req);
    return resp.workItem;
  }

  
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
