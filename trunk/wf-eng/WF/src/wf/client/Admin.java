package wf.client;

import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import wf.client.auth.Utilizator;
import wf.client.manager.DesktopPane;
import wf.client.manager.WinListener;
import wf.exceptions.ExceptieWF;
import wf.model.StareWF;

public class Admin {

    private static Admin _instance;
    public static final String PASSWORD = "PASSWORD";

    public static final String USERNAME = "USER";

    public static Admin getInstance() {
	return _instance;
    }

    public static void main(final String[] args) {
	final Admin wfAdminUI;
	try {
	    wfAdminUI = new Admin(Preferinte.getInstance("appdata.properties"));

	    Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
		    try {
			System.out.println("Flushing preferences.");
			wfAdminUI.getPreferences().flush();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    });

	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }

    DesktopPane desk = null;

    Preferinte preferences;

    private Admin(final Preferinte pref) throws IOException {
	_instance = this;
	this.preferences = pref;
	this.desk = new DesktopPane(this);
	this.desk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.desk.setSize(this.preferences.getInt("MAIN_SCREEN_W", 800),
		this.preferences.getInt("MAIN_SCREEN_H", 600));
	this.desk.setTitle("WF Manager UI");
	this.desk.addWindowListener(new WinListener(this.preferences,
		"MAIN_SCREEN"));
	this.desk.setVisible(true);

	this.preferences.flush();

    }

    @SuppressWarnings("unchecked")
    public List getActiveWorkflows() throws ExceptieWF {
	List v = ManagerWorkflow.getInstanteActiveWorkflow(this.getUSER());
	return v;
    }

    @SuppressWarnings("unchecked")
    public List getKnownWorkflows() throws ExceptieWF {
	List v = ManagerWorkflow.getModeleWorkflow(this.getUSER());
	return v;
    }

    public Preferinte getPreferences() {
	return this.preferences;
    }

    public Utilizator getUSER() {
	return new Utilizator(this.preferences.get(USERNAME, "kgi"),
		this.preferences.get(PASSWORD, "password"));
    }

    public StareWF getWorkflowState(final int wfid) throws ExceptieWF {
	StareWF state = ManagerWorkflow.getStareWorkflow(new Integer(wfid),
		this.getUSER());
	return state;
    }

    public void setPreferences(final Preferinte preferences) {
	this.preferences = preferences;
    }

    public void setUSER(final Utilizator user) {
	this.preferences.put(USERNAME, user.getName());
	this.preferences.put(PASSWORD, user.getPassword());
    }
}
