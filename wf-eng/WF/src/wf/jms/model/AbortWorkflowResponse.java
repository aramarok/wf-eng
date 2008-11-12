
package wf.jms.model;


public class AbortWorkflowResponse extends Response {

    public AbortWorkflowResponse (int statusCode, String statusMsg) { 
        super (statusCode, statusMsg);
    }

    public AbortWorkflowResponse (int statusCode) { 
        super (statusCode);
    }
}
