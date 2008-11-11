package xflow;

import xflow.security.User;

/**
 * User: kosta
 * Date: Jun 28, 2004
 * Time: 9:31:59 PM
 */
public class TestConfig {

  private static User user = new User( "xf_test" ,"none");

  public static User getUser() {
    return user;
  }

}
