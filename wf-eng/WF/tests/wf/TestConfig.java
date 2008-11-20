package wf;

import wf.client.auth.User;

public class TestConfig {

	private static User user = new User("user", "password");

	public static User getUser() {
		return user;
	}

}
