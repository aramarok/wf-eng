package wf.server.case_condition;

import java.io.Serializable;

public class CaseConditionPayload implements Serializable {

    private static final long serialVersionUID = 1L;

    int age;

    public CaseConditionPayload() {
    }

    public CaseConditionPayload(final int age) {
	this.age = age;
    }

    public int getAge() {
	return this.age;
    }

    public void setAge(final int age) {
	this.age = age;
    }

}
