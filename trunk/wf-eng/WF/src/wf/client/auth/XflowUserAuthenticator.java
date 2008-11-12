

package wf.client.auth;

import java.security.*;

public class XflowUserAuthenticator implements Authenticator {

    public boolean authenticate (String userName, String password) {
        return true;
    }
}
