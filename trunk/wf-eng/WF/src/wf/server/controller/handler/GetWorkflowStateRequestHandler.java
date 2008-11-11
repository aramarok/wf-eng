package wf.server.controller.handler;

import wf.jms.model.GetWorkflowStateRequest;
import wf.jms.model.GetWorkflowStateResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.model.WorkflowState;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

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
