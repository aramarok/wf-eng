package wf.server.controller.handler;

import wf.jms.model.GetVariableRequest;
import wf.jms.model.GetVariableResponse;
import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 2:25:07 PM
 */
public class GetVariableRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    GetVariableRequest req = (GetVariableRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "In GetVariableRequest:service\n\t" +
          "workflowId = " + req.workflowId +
          "variableName = " + req.variableName);
    }

    int response = 0;
    String message = "OK";
    Object value = null;
    try {
      value = WorkflowProcessor.getInstance().getVariable (req.workflowId, req.variableName);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }

    return new GetVariableResponse(response, message, value);
  }
}
