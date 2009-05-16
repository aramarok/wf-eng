package wf.test;

import java.io.Serializable;

public class ABC implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer a;
    String b;
    Double c;

    public ABC(final int i, final String s, final double d) {
	this.a = new Integer(i);
	this.b = new String(s);
	this.c = new Double(d);
    }

    @Override
    public String toString() {
	return "a = " + this.a + " b = " + this.b + " c = " + this.c;
    }
}
