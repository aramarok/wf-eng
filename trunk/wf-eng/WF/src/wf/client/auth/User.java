

package wf.client.auth;

import java.security.*;
import java.io.*;

public class User implements Principal, Serializable {

    private String userName;
    private String password;

    public User() {}

    public User (String un, String pw) {
        userName = un;
        password = pw;
    }

    public boolean equals (Object u) {
        User user = (User)u;
        return userName.equals(user.getName());
    }

    public String getName () {
        return userName;
    }

    public void setName (String n) {
        userName = n;
    }

    public String getPassword () {
        return password;
    }
 
    public void setPassword (String p) {
        password = p;
    }    

    public int hashCode() {
        return userName.hashCode();
    }

    public String toString() {
        return "username: " + userName + " password: " + password;
    }

}
