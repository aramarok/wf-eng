

package wf.jms.model;

import java.util.List;

public class GetProcessNodesResponse extends Response {

    public List nodes = null;

    public GetProcessNodesResponse (int statusCode, String statusMsg, List n) {
        super (statusCode, statusMsg);
        nodes = n;
    }

    public GetProcessNodesResponse (int statusCode) { 
        super (statusCode);
    }
}
