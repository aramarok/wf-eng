package wf.server.controller;

import wf.jms.model.Request;
import wf.jms.model.Response;

import org.apache.log4j.Logger;


public interface RequestHandler {

  public static final Logger log = Logger.getLogger( RequestHandler.class );

  public Response handle( Request r );

}
