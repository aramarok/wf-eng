package wf.server.controller.handler;

import wf.jms.model.GetActiveWorkflowsResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

import java.util.List;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 1:23:44 PM
 */
public class GetActiveWorkflowsRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetActiveWorkflows request");
    }
    int response = 0;
    String message = "OK";
    List activeWorkflows = null;
    try {
      activeWorkflows = WorkflowProcessor.getInstance().getActiveWorkflows();
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetActiveWorkflowsResponse(response, message, activeWorkflows);
  }
}
