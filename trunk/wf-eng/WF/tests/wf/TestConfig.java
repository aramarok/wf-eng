package wf;

import wf.client.auth.User;

public class TestConfig {

	private static User user = new User("test_user", "test_password");

	public static User getUser() {
		return user;
	}

}
