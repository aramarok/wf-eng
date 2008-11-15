package wf.client.auth;

public interface Authenticator {

	public boolean authenticate(String userName, String password);
}
