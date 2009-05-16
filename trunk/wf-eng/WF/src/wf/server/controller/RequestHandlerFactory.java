package wf.server.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import wf.db.Persistence;
import wf.jms.model.Cerere;
import wf.jms.model.ReqStartWF;

public class RequestHandlerFactory {

    private static Logger log = Logger.getLogger(RequestHandlerFactory.class);

    public static void main(final String[] args) {
	RequestHandlerFactory f = new RequestHandlerFactory();
	ReqStartWF req = new ReqStartWF();
	ControlCereri rh = f.getHandlerFor(req);
	System.out.println("rh = " + rh);
    }

    @SuppressWarnings("unchecked")
    Map registeredHandlers = new HashMap();

    public RequestHandlerFactory() {

    }

    @SuppressWarnings( { "unchecked" })
    public ControlCereri getHandlerFor(final Cerere r) {
	ControlCereri rh = (ControlCereri) this.registeredHandlers.get(r
		.getClass());
	if (rh != null) {
	    return rh;
	}
	Class requestClass = r.getClass();
	String className = requestClass.getName();
	if (log.isDebugEnabled()) {
	    log.debug("instantiaza: " + className);
	}
	String baseName = className.substring(className.lastIndexOf('.') + 1);
	String handlerName = "wf.server.controller.handler." + baseName
		+ "Control";
	try {
	    Class handlerClass = this.getClass().getClassLoader().loadClass(
		    handlerName);
	    this.register(requestClass, handlerClass);
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	}

	rh = (ControlCereri) this.registeredHandlers.get(r.getClass());
	if (rh == null) {
	    log.info("Handler for " + r.getClass().getName()
		    + " should be defined at this point!!");
	}
	return rh;
    }

    @SuppressWarnings("unchecked")
    private void register(final Class aClass, final Class handler) {
	this.registeredHandlers.put(aClass, Persistence
		.enhanceInstanceOfClass(handler));
    }

}
