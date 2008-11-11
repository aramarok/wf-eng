package xflow.server.controller.handler;

import xflow.server.controller.RequestHandler;
import xflow.protocol.Response;
import xflow.protocol.Request;

/**
 * User: kosta
 * Date: Jul 18, 2004
 * Time: 7:59:26 PM
 */
public class DefaultRequestHandler implements RequestHandler {

  public Response handle(Request r) {
    return r.service();
  }
}
