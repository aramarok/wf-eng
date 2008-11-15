package wf.client.manager;

import wf.client.WFClientConstants;
import wf.client.Admin;
import wf.model.WorkflowState;

public class ActiveProcessesWin extends javax.swing.JInternalFrame {

	private static final long serialVersionUID = 1L;
	
	Admin xFlowAdminUI;
	private int wfId = -1;

	public ActiveProcessesWin() {
		initComponents();
	}

	public ActiveProcessesWin(Admin xFlowAdminUI) {
		this();
		this.xFlowAdminUI = xFlowAdminUI;
	}

	private void initComponents() {
		jToolBar1 = new javax.swing.JToolBar();
		refreshBtn = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		activeProcTable = new javax.swing.JTable();

		setClosable(true);
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setTitle("Active Processes");
		jToolBar1.setFloatable(false);
		refreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				WFClientConstants.CLIENT_ICON_LOCATION + "refresh.gif")));
		refreshBtn.setMnemonic('r');
		refreshBtn.setText("refresh");
		refreshBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshActiveProcList(evt);
			}
		});

		jToolBar1.add(refreshBtn);

		getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

		activeProcTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(activeProcTable);

		getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

		pack();
	}

	private void refreshActiveProcList(java.awt.event.ActionEvent evt) {
		refreshList();
	}

	private void refreshList() {
		if (wfId == -1)
			return;
		try {
			WorkflowState state = xFlowAdminUI.getWorkflowState(wfId);
			this.setTitle("Workflow::" + state.getWorkflowName() + "::"
					+ state.getIsActive());
			activeProcTable.setModel(Utilities.createTableModel(state
					.getActiveProcesses()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private javax.swing.JTable activeProcTable;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JButton refreshBtn;

	public void showProcessesFoWfID(int selectedWfID) {
		this.wfId = selectedWfID;
		refreshList();
	}

}
