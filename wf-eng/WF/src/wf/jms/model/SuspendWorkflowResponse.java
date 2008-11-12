
package wf.jms.model;


public class SuspendWorkflowResponse extends Response {

    public SuspendWorkflowResponse (int statusCode, String statusMsg) { 
        super (statusCode, statusMsg);
    }

    public SuspendWorkflowResponse (int statusCode) { 
        super (statusCode);
    }
}
