package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.ResumeWorkflowResponse;
import xflow.protocol.ResumeWorkflowRequest;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:58:00 PM
 */
public class ResumeWorkflowRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    ResumeWorkflowRequest req = (ResumeWorkflowRequest) r;    
    if( log.isDebugEnabled() ){
      log.debug( "Servicing ResumeWorkflow request.\n\tWorkflow Id = " + req.workflowId);
    }
    int response = 0;
    String message = "OK";

    try {
      WorkflowProcessor.getInstance().resumeWorkflow ( req.workflowId);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new ResumeWorkflowResponse(response, message);
  }
}
