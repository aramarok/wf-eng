package wf.jms.model;

import java.util.List;

public class ResWFActive extends Raspuns {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List activeWorkflows;

    @SuppressWarnings("unchecked")
    public ResWFActive(final int cod, final List lista) {
	super(cod);
	this.activeWorkflows = lista;
    }

    @SuppressWarnings("unchecked")
    public ResWFActive(final int cod, final String mesaj, final List lista) {
	super(cod, mesaj);
	this.activeWorkflows = lista;
    }
}
