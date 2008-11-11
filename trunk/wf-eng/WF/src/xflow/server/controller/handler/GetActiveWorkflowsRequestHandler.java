package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetActiveWorkflowsResponse;

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
