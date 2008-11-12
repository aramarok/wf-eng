

package wf.jms.model;


public class StartWorkflowResponse extends Response {

    public Integer workflowId;

    public StartWorkflowResponse (int statusCode, String statusMsg, Integer workflowId) {
        super (statusCode, statusMsg);
        this.workflowId = workflowId;
    }

    public StartWorkflowResponse (int statusCode, int id) { 
        super (statusCode);
        workflowId = new Integer(id);
    }
}
