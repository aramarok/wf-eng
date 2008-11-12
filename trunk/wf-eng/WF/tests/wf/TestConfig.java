package wf;

import wf.client.auth.User;


public class TestConfig {

  private static User user = new User( "xf_test" ,"none");

  public static User getUser() {
    return user;
  }

}
