package wf.model;

import java.io.Serializable;

public class Destination implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Node node;
	public String rule;

	public Destination(Node n, String r) {
		node = n;
		rule = r;
	}

	public void setNode(Node n) {
		node = n;
	}

	public Node getNode() {
		return node;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getRule() {
		return rule;
	}
}
