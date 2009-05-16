package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.server.controller.ControlCereri;

public class Control implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	return cerere.service();
    }
}
