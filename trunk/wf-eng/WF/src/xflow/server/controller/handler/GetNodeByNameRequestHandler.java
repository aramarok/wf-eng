package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetNodeByNameResponse;
import xflow.protocol.GetNodeByNameRequest;
import xflow.common.Node;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:18:41 PM
 */
public class GetNodeByNameRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetNodeByNameRequest req = (GetNodeByNameRequest) r;
    if( log.isDebugEnabled() ){
      log.debug ("Servicing GetNodeByName request");
    }
    int response = 0;
    String message = "OK";
    Node node = null;
    try {
      node = WorkflowProcessor.getInstance().getNodeByName( req.workflowName, req.version, req.nodeName);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetNodeByNameResponse(response, message, node);
  }
}
