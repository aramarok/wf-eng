

package wf.client.auth;

import java.security.*;

public interface Authenticator {

    public boolean authenticate (String userName, String password);
}
