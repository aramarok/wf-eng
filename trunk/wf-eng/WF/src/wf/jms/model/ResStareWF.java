package wf.jms.model;

import wf.model.StareWF;

public class ResStareWF extends Raspuns {

    private static final long serialVersionUID = 1L;

    public StareWF workflowState;

    public ResStareWF(final int cod, final StareWF stare) {
	super(cod);
	this.workflowState = stare;
    }

    public ResStareWF(final int statusCode, final String mesaj,
	    final StareWF stare) {
	super(statusCode, mesaj);
	this.workflowState = stare;
    }
}
