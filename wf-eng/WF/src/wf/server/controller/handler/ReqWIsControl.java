package wf.server.controller.handler;

import java.util.List;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqWIs;
import wf.jms.model.ResWIs;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqWIsControl implements ControlCereri {

    @SuppressWarnings("unchecked")
    public Raspuns handle(final Cerere cerere) {
	ReqWIs req = (ReqWIs) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	List itemi = null;

	try {
	    WorkflowProcessor.getInstance().getWorkItems(req.workflowName,
		    req.processName);
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResWIs(raspuns, mesaj, itemi);
    }
}
