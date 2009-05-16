package wf.jms.model;

import java.util.List;

public class ResWFDupaNume extends Raspuns {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List workflows;

    @SuppressWarnings("unchecked")
    public ResWFDupaNume(final int cod, final List lista) {
	super(cod);
	this.workflows = lista;
    }

    @SuppressWarnings("unchecked")
    public ResWFDupaNume(final int cod, final String mesaj, final List v) {
	super(cod, mesaj);
	this.workflows = v;
    }
}
