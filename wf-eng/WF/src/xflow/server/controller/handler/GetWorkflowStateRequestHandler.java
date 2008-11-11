package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetWorkflowStateResponse;
import xflow.protocol.GetWorkflowStateRequest;
import xflow.common.WorkflowState;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 5:04:43 PM
 */
public class GetWorkflowStateRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    GetWorkflowStateRequest req = (GetWorkflowStateRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetWorkflowState request. Workflow Id = " + req.workflowId);
    }

    WorkflowState wfState = null;
    int response = 0;
    String message = "OK";
    try {
      wfState = WorkflowProcessor.getInstance().getWorkflowState( req.workflowId);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }

    return new GetWorkflowStateResponse(response, message, wfState);
  }
}
