package wf.server.case_condition;

import java.io.Serializable;

public class CaseConditionPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	public CaseConditionPayload() {
	}

	public CaseConditionPayload(int age) {
		this.age = age;
	}

	int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}