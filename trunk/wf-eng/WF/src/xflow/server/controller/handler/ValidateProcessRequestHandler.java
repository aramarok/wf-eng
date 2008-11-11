package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.ValidateProcessResponse;
import xflow.protocol.ValidateProcessRequest;

/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 12:59:11 PM
 */
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
