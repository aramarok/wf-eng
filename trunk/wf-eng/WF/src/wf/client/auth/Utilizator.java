package wf.client.auth;

import java.io.Serializable;
import java.security.Principal;

public class Utilizator implements Principal, Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
    private String userName;

    public Utilizator() {
    }

    public Utilizator(final String un, final String pw) {
	this.userName = un;
	this.password = pw;
    }

    @Override
    public boolean equals(final Object u) {
	Utilizator user = (Utilizator) u;
	return this.userName.equals(user.getName());
    }

    public String getName() {
	return this.userName;
    }

    public String getPassword() {
	return this.password;
    }

    @Override
    public int hashCode() {
	return this.userName.hashCode();
    }

    public void setName(final String n) {
	this.userName = n;
    }

    public void setPassword(final String p) {
	this.password = p;
    }

    @Override
    public String toString() {
	return "username: " + this.userName + " password: " + this.password;
    }

}
