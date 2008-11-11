package wf.server.controller.handler;

import wf.jms.model.GetWorkflowsByNameRequest;
import wf.jms.model.GetWorkflowsByNameResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;


import java.util.List;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:27:31 PM
 */
public class GetWorkflowsByNameRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetWorkflowsByNameRequest req = (GetWorkflowsByNameRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetWorkflowsByName request");
    }
    int response = 0;
    String message = "OK";
    List workflows = null;
    try {
      workflows = WorkflowProcessor.getInstance().getWorkflowsByName( req.name);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetWorkflowsByNameResponse(response, message, workflows);
  }
}
