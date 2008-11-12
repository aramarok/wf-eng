

package wf.jms.model;

import java.util.List;

public class GetAllWorkflowsResponse extends Response {

    public List workflows;

    public GetAllWorkflowsResponse (int statusCode, String statusMsg, List v) {
        super (statusCode, statusMsg);
        workflows = v;
    }

    public GetAllWorkflowsResponse (int statusCode, List v) {
        super (statusCode);
        workflows = v;
    }
}
