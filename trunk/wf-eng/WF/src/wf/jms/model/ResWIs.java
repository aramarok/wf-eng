package wf.jms.model;

import java.util.List;

public class ResWIs extends Raspuns {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public List workItems;

    @SuppressWarnings("unchecked")
    public ResWIs(final int cod, final List lista) {
	super(cod);
	this.workItems = lista;
    }

    @SuppressWarnings("unchecked")
    public ResWIs(final int cod, final String mesaj, final List lista) {
	super(cod, mesaj);
	this.workItems = lista;
    }

}
