package wf.test;

import java.io.Serializable;

public class ABC implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Integer a;
	String b;
	Double c;

	public ABC(int i, String s, double d) {
		a = new Integer(i);
		b = new String(s);
		c = new Double(d);
	}

	public String toString() {
		return "a = " + a + " b = " + b + " c = " + c;
	}
}
