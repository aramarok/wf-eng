package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqDeployModel;
import wf.jms.model.ResDeployModel;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqDeployModelControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqDeployModel req = (ReqDeployModel) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().deployModel(req.xml, req.type,
		    req.utilizator.getName());
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    e.printStackTrace();
	    raspuns = Raspuns.EROARE;
	    mesaj = e.getMessage();
	}

	return new ResDeployModel(raspuns, mesaj);

    }

}
