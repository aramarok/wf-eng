

package wf.jms.model;

import java.util.List;

import wf.model.WorkflowModel;

public class GetModelsResponse extends Response {
    
    public List models;

    public GetModelsResponse (int statusCode, String statusMsg, List v) { 
        super (statusCode, statusMsg);
        models = v;
    }

    public GetModelsResponse (int statusCode, List v) {
        super (statusCode);
        models = v;
    }
}
