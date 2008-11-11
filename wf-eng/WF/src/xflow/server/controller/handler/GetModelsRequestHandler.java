package xflow.server.controller.handler;

import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.GetModelsResponse;
import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;

import java.util.List;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 7:05:34 PM
 */
public class GetModelsRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    if( log.isDebugEnabled() ){
      log.debug( "Servicing GetModels request");
    }
    int response = 0;
    String message = "OK";
    List models = null;
    try {
      models = WorkflowProcessor.getInstance().getModels();
      response = Response.SUCCESS;
    } catch (Exception e) {
      response = Response.FAILURE; e.printStackTrace();
      message = e.getMessage();
    }
    return new GetModelsResponse(response, message, models);
  }
}
