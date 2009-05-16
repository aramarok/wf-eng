package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqWI;
import wf.jms.model.ResWI;
import wf.model.ItemModel;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqWIControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {

	ReqWI req = (ReqWI) cerere;
	int raspuns = 0;
	String mesaj = "OK";
	ItemModel item = null;

	try {
	    item = WorkflowProcessor.getInstance().getWorkItem(req.workItemId,
		    req.processName);
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}

	return new ResWI(raspuns, mesaj, item);
    }
}
