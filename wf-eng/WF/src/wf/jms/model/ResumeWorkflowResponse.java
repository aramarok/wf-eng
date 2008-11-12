
package wf.jms.model;


public class ResumeWorkflowResponse extends Response {

    public ResumeWorkflowResponse (int statusCode, String statusMsg) { 
        super (statusCode, statusMsg);
    }

    public ResumeWorkflowResponse (int statusCode) { 
        super (statusCode);
    }
}
