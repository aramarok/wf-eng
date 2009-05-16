package wf.client.auth;

public interface Authenticator {

	public boolean authenticateUser(String userName, String password);
}
