package wf.client;

import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import wf.client.auth.User;
import wf.client.manager.DesktopPane;
import wf.client.manager.WinListener;
import wf.exceptions.WorkFlowException;
import wf.model.WorkflowState;

public class Admin {

	public static final String USERNAME = "USER";
	public static final String PASSWORD = "PASSWORD";

	DesktopPane desk = null;
	Preferences preferences;

	private static Admin _instance;

	public static Admin getInstance() {
		return _instance;
	}

	private Admin(Preferences pref) throws IOException {
		_instance = this;
		preferences = pref;
		desk = new DesktopPane(this);
		desk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desk.setSize(preferences.getInt("MAIN_SCREEN_W", 800), preferences
				.getInt("MAIN_SCREEN_H", 600));
		desk.setTitle("XFlow Manager UI");
		desk.addWindowListener(new WinListener(preferences, "MAIN_SCREEN"));
		desk.setVisible(true);

		preferences.flush();

	}

	public User getUSER() {
		return new User(preferences.get(USERNAME, "kgi"), preferences.get(
				PASSWORD, "password"));
	}

	public void setUSER(User user) {
		preferences.put(USERNAME, user.getName());
		preferences.put(PASSWORD, user.getPassword());
	}

	public List getKnownWorkflows() throws WorkFlowException {
		List v = WorkflowManager.getWorkflowModels(getUSER());
		return v;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public List getActiveWorkflows() throws WorkFlowException {
		List v = WorkflowManager.getActiveWorkflows(getUSER());
		return v;
	}

	public WorkflowState getWorkflowState(int wfid) throws WorkFlowException {
		WorkflowState state = WorkflowManager.getWorkflowState(
				new Integer(wfid), getUSER());
		return state;
	}

	public static void main(String[] args) {
		final Admin xFlowAdminUI;
		try {
			xFlowAdminUI = new Admin(Preferences
					.getInstance("appdata.properties"));

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						System.out.println("Flushing preferences.");
						xFlowAdminUI.getPreferences().flush();
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
}
