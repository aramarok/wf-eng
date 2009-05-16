package wf.client.auth;

public interface Autentificator {

    public boolean authenticateUser(String userName, String password);
}
