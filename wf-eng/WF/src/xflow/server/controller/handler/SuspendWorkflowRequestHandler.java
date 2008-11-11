package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.SuspendWorkflowResponse;
import xflow.protocol.SuspendWorkflowRequest;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 3:17:26 PM
 */
public class SuspendWorkflowRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    SuspendWorkflowRequest req = (SuspendWorkflowRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing SuspendWorkflow request." +
          "\n\tWorkflow Id = " + req.workflowId);
    }
    int response = 0;
    String message = "OK";
    try {
      WorkflowProcessor.getInstance().suspendWorkflow ( req.workflowId);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new SuspendWorkflowResponse(response, message);
  }
}
