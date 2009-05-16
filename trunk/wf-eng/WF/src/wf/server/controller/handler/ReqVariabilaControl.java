package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqVariabila;
import wf.jms.model.ResVariabila;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqVariabilaControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqVariabila req = (ReqVariabila) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	Object valoare = null;

	try {
	    valoare = WorkflowProcessor.getInstance().getVariable(
		    req.workflowId, req.variableName);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}

	return new ResVariabila(raspuns, mesaj, valoare);
    }
}
