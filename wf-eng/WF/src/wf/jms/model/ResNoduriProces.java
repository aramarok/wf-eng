package wf.jms.model;

import java.util.List;

public class ResNoduriProces extends Raspuns {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List nodes = null;

    public ResNoduriProces(final int cod) {
	super(cod);
    }

    @SuppressWarnings("unchecked")
    public ResNoduriProces(final int cod, final String mesaj, final List n) {
	super(cod, mesaj);
	this.nodes = n;
    }
}
