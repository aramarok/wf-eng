package wf.webservice;

import java.util.ArrayList;
import java.util.List;
import wf.client.ProcesWF;
import wf.client.WorkflowManager;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.model.ItemModel;
import wf.model.StareWF;

public class WfService {

    public void abortWorkflow(final int workflowId, final Utilizator user) {

	System.out.println("Anulare workflow");
	try {
	    WorkflowManager.anuleazaWorkflow(new Integer(workflowId), user);
	    System.out.println("Workflow Aborted");
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
    }

    public void completeWorkItem(final String wfName, final String processName,
	    final ItemModel witem, final Utilizator user) {
	try {
	    ProcesWF wfp = new ProcesWF(wfName, -1, processName, null, user);
	    wfp.completeWorkItem(witem);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
    }

    public void deployModel(final String xml, final String type,
	    final Utilizator user) {
	try {
	    WorkflowManager.incarcaModel(xml, type, user);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
    }

    @SuppressWarnings("unchecked")
    public List getActiveWorkflows(final Utilizator user) {
	try {
	    return WorkflowManager.getInstanteActiveWorkflow(user);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public ItemModel getNextWorkItem(final String wfName,
	    final String processName, final Utilizator user) {
	ItemModel wi = null;
	try {
	    ProcesWF wfp = new ProcesWF(wfName, -1, processName, null, user);
	    wi = wfp.getNextWorkItem();
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
	return wi;
    }

    public Object getVariable(final int workflowId, final String variableName,
	    final Utilizator user) {
	try {
	    return WorkflowManager.getVariabila(new Integer(workflowId),
		    variableName, user);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	    return "ERROR";
	}
    }

    public StareWF getWorkflowState(final int workflowId, final Utilizator user) {
	try {
	    return WorkflowManager.getStareWorkflow(new Integer(workflowId),
		    user);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public ItemModel getWorkItem(final String wfName, final String processName,
	    final int id, final Utilizator user) {
	ItemModel wi = null;
	try {
	    ProcesWF wfp = new ProcesWF(wfName, -1, processName, null, user);
	    wi = wfp.getWorkItem(new Integer(id));
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
	return wi;
    }

    @SuppressWarnings("unchecked")
    public List getWorkItems(final String wfName, final String processName,
	    final Utilizator user) {
	List v = null;
	try {
	    ProcesWF wfp = new ProcesWF(wfName, -1, processName, null, user);
	    v = wfp.getWorkItems();
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
	return v;
    }

    public void setVariable(final int workflowId, final String variableName,
	    final Object variableValue, final Utilizator user) {
	try {
	    WorkflowManager.setVariabila(new Integer(workflowId), variableName,
		    variableValue, user);
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}
    }

    public Integer startWorkflow(final String workflowName, final int version,
	    final ItemModel witem, final Utilizator user) {
	Integer wfId = null;
	try {
	    System.out.println("**** workflowName: " + workflowName);
	    System.out.println("**** version: " + version);
	    System.out.println("**** workitem: " + witem);
	    System.out.println("**** utilizator: " + user);
	    if (version == -1) {
		wfId = WorkflowManager.pornesteWorkflow(workflowName, witem, user);
	    } else {
		wfId = WorkflowManager.pornesteWorkflow(workflowName, version,
			witem, user);
	    }
	    System.out.println("Workflow Started");
	} catch (ExceptieWF e) {
	    e.printStackTrace();
	}

	return wfId;
    }

    public String xxx() {
	return "XXX";
    }

    @SuppressWarnings("unchecked")
    public List yyy() {
	List v = new ArrayList();
	v.add("aaa");
	v.add("bbb");
	return v;
    }

}
