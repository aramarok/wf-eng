package wf.jms.model;

import java.io.Serializable;
import wf.client.auth.Utilizator;

public abstract class Cerere implements Serializable {

    public String numeRaspuns;
    public Utilizator utilizator;

    public Raspuns service() {
	throw new RuntimeException("hmmm");
    };
}
