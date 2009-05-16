package wf.jms.model;

import wf.model.Nod;

public class ResNodDupaNume extends Raspuns {

    private static final long serialVersionUID = 1L;

    public Nod node;

    public ResNodDupaNume(final int cod, final Nod n) {
	super(cod);
	this.node = n;
    }

    public ResNodDupaNume(final int cod, final String mesaj, final Nod n) {
	super(cod, mesaj);
	this.node = n;
    }
}
