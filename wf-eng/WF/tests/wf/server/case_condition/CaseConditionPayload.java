package wf.server.case_condition;

import java.io.Serializable;

/**
 * User: kosta
 * Date: Jul 20, 2004
 * Time: 9:06:13 PM
 */
public class CaseConditionPayload implements Serializable{

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
