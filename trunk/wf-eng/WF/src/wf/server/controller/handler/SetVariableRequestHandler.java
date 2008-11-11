package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.SetVariableRequest;
import wf.jms.model.SetVariableResponse;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 3:13:58 PM
 */
public class SetVariableRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    SetVariableRequest req =(SetVariableRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "In SetVariableRequest:service." +
          "\n\tworkflowId = " + req.workflowId +
          "\n\tvariableName = " + req.variableName +
          "\n\tvariableValue = " + req.variableValue);
    }    
    int response = 0;
    String message = "OK";
    try {
      WorkflowProcessor.getInstance().setVariable (req.workflowId, req.variableName, req.variableValue);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new SetVariableResponse(response, message);
  }
}
