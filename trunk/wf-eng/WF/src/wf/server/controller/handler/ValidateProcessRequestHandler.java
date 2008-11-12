package wf.server.controller.handler;

import wf.jms.model.Request;
import wf.jms.model.Response;
import wf.jms.model.ValidateProcessRequest;
import wf.jms.model.ValidateProcessResponse;
import wf.server.controller.RequestHandler;
import wf.server.controller.WorkflowProcessor;


public class ValidateProcessRequestHandler implements RequestHandler{
  public Response handle(Request r) {
    ValidateProcessRequest req = (ValidateProcessRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing ValidateProcessRequest request");
    }
    int response = 0;
    String message = "OK";
    boolean ok = false;

    try {
      ok = WorkflowProcessor.getInstance().validateProcess ( req.workflowName, req.workflowVersion, req.processName);
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new ValidateProcessResponse(response, message, ok);
  }

}
