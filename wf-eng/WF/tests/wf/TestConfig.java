package wf;

import wf.client.auth.Utilizator;

public class TestConfig {

    private static Utilizator user = new Utilizator("test_user",
	    "test_password");

    public static Utilizator getUser() {
	return user;
    }

}
