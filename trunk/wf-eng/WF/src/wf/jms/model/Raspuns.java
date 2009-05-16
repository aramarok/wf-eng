package wf.jms.model;

import java.io.Serializable;

public class Raspuns implements Serializable {

    public static final int EROARE = -1;

    private static final long serialVersionUID = 1L;

    public static final int SUCCES = 0;

    public int codRaspuns;
    public String mesaj;

    public Raspuns() {
    }

    public Raspuns(final int codStatus) {
	this.codRaspuns = codStatus;
	this.mesaj = null;
    }

    public Raspuns(final int codStatus, final String mesaj) {
	this.codRaspuns = codStatus;
	this.mesaj = mesaj;
    }
}
