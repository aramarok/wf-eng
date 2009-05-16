package wf.jms.model;

import wf.model.ItemModel;

public class ResUrmWI extends Raspuns {

    private static final long serialVersionUID = 1L;

    public ItemModel workItem;

    public ResUrmWI(final int cod, final ItemModel item) {
	super(cod);
	this.workItem = item;
    }

    public ResUrmWI(final int cod, final String mesaj, final ItemModel wi) {
	super(cod, mesaj);
	this.workItem = wi;
    }

}
