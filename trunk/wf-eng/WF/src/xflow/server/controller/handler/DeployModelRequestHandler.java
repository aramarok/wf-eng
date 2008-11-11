package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.server.controller.WorkflowProcessor;
import xflow.protocol.Response;
import xflow.protocol.Request;
import xflow.protocol.DeployModelResponse;
import xflow.protocol.DeployModelRequest;


/**
 * User: kosta
 * Date: Jul 24, 2004
 * Time: 1:20:13 PM
 */
public class DeployModelRequestHandler implements RequestHandler{

  public Response handle(Request r) {
    DeployModelRequest req = (DeployModelRequest) r;
    if( log.isDebugEnabled() ){
      log.debug( "Servicing DeployModel request:" + req.type + req.xml );
    }

    int response = 0;
    String msg = "OK";
    try {
      WorkflowProcessor.getInstance().deployModel (req.xml, req.type, req.user.getName());
      response = Response.SUCCESS;
    } catch (Exception e) {
      e.printStackTrace();
      response = Response.FAILURE;
      msg = e.getMessage();
    }

    return new DeployModelResponse(response, msg);

  }

}
