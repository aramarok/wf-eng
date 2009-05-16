package wf.server.controller;

import org.apache.log4j.Logger;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;

public interface ControlCereri {

    public static final Logger log = Logger.getLogger(ControlCereri.class);

    public Raspuns handle(Cerere r);

}
