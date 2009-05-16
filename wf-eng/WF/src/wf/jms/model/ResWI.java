package wf.jms.model;

import wf.model.ItemModel;

public class ResWI extends Raspuns {

    private static final long serialVersionUID = 1L;

    public ItemModel workItem;

    public ResWI(final int cod, final ItemModel item) {
	super(cod);
	this.workItem = item;
    }

    public ResWI(final int cod, final String mesaj, final ItemModel item) {
	super(cod, mesaj);
	this.workItem = item;
    }

}
