package xflow.server.controller.handler;


import xflow.protocol.Response;
import xflow.protocol.AbortWorkflowResponse;
import xflow.protocol.Request;
import xflow.protocol.AbortWorkflowRequest;
import xflow.server.controller.WorkflowProcessor;
import xflow.server.controller.RequestHandler;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 1:03:38 PM
 */
public class AbortWorkflowRequestHandler implements RequestHandler{

  public Response handle ( Request r) {
    AbortWorkflowRequest req = (AbortWorkflowRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing AbortWorkflow request. Workflow Id = " + req.workflowId) ;
    }
    int response = 0;
    String message = "OK";

    try {
      WorkflowProcessor.getInstance().abortWorkflow (req.workflowId, req.user.getName());
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new AbortWorkflowResponse(response, message);
  }

}
