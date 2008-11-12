
package wf.jms.model;


public class CompleteWorkItemResponse extends Response {

    public CompleteWorkItemResponse (int statusCode, String statusMsg) {
        super (statusCode, statusMsg);
    }

    public CompleteWorkItemResponse (int statusCode) { 
        super (statusCode);
    }

}
