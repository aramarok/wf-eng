

package wf.jms.model;


public class SetVariableResponse extends Response {


    public SetVariableResponse (int statusCode, String statusMsg) {
        super (statusCode, statusMsg);
    }

    public SetVariableResponse (int statusCode) {
        super (statusCode);
    }

}
