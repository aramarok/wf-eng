package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.ResumeWorkflowRequest;
import wf.jms.model.ResumeWorkflowResponse;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

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
