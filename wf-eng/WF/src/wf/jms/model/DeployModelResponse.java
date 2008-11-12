
package wf.jms.model;


public class DeployModelResponse extends Response {

    public DeployModelResponse (int statusCode, String statusMsg) { 
        super (statusCode, statusMsg);
    }

    public DeployModelResponse (int statusCode) { 
        super (statusCode);
    }
}
