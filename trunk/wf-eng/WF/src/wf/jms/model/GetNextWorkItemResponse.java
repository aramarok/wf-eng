
package wf.jms.model;

import wf.model.WorkItem;

public class GetNextWorkItemResponse extends Response {

    public WorkItem workItem;

    public GetNextWorkItemResponse (int statusCode, String statusMsg, WorkItem wi) { 
        super (statusCode, statusMsg);
        workItem = wi;
    }

    public GetNextWorkItemResponse (int statusCode, WorkItem wi) { 
        super (statusCode);
        workItem = wi;
    }

}