package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetProcessNodesResponse;
import xflow.protocol.GetProcessNodesRequest;

import java.util.List;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:21:49 PM
 */
public class GetProcessNodesRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetProcessNodesRequest req = (GetProcessNodesRequest) r;

    if( log.isDebugEnabled() ){
      log.debug ("Servicing GetProcessNodes request.\n\t" +
          "Workflow Id = " + req.workflowId );
    }
    int response = 0;
    String message = "OK";
    List nodes = null;

    try {
      nodes = WorkflowProcessor.getInstance().getProcessNodes ( req.workflowId);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetProcessNodesResponse(response, message, nodes);
  }
}
