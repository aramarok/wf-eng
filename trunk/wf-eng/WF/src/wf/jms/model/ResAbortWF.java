package wf.jms.model;

public class ResAbortWF extends Raspuns {

    private static final long serialVersionUID = 1L;

    public ResAbortWF(final int cod) {
	super(cod);
    }

    public ResAbortWF(final int cod, final String mesaj) {
	super(cod, mesaj);
    }
}
