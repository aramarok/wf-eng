package wf.jms.model;

import java.util.List;

public class ResModeleDisponibile extends Raspuns {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List models;

    @SuppressWarnings("unchecked")
    public ResModeleDisponibile(final int cod, final List v) {
	super(cod);
	this.models = v;
    }

    @SuppressWarnings("unchecked")
    public ResModeleDisponibile(final int cod, final String mesaj, final List v) {
	super(cod, mesaj);
	this.models = v;
    }
}
