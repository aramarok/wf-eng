package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetAllWorkflowsResponse;

import java.util.List;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 1:53:41 PM
 */
public class GetAllWorkflowsRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetAllWorkflows request");
    }
    int response = 0;
    String message = "OK";
    List workflows = null;
    try {
      workflows = WorkflowProcessor.getInstance().getAllWorkflows();
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetAllWorkflowsResponse(response, message, workflows);
  }
}
