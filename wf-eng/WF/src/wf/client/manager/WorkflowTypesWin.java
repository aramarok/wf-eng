package wf.client.manager;

import wf.cfg.Constante;
import wf.client.ManagerWorkflow;
import wf.client.Admin;
import wf.client.manager.Utilities;
import javax.swing.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class WorkflowTypesWin extends javax.swing.JInternalFrame {

	private static final long serialVersionUID = 1L;
	
	Admin wfAdminUI;

	public WorkflowTypesWin() {
		initComponents();
	}

	public WorkflowTypesWin(Admin wfAdminUI) {
		this();
		this.wfAdminUI = wfAdminUI;
	}

	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		wfTypesTable = new javax.swing.JTable();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		uploadWfMI = new javax.swing.JMenuItem();
		removeWfMI = new javax.swing.JMenuItem();
		refreshListMI = new javax.swing.JMenuItem();
		jMenu2 = new javax.swing.JMenu();

		setClosable(true);
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setTitle("Workflow Types");
		wfTypesTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(wfTypesTable);

		getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jMenu1.setMnemonic('a');
		jMenu1.setText("Actions");
		uploadWfMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				Constante.CLIENT_ICON_LOCATION + "open.gif")));
		uploadWfMI.setMnemonic('u');
		uploadWfMI.setText("Upload WF definition");
		uploadWfMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				uploadWfDefinition(evt);
			}
		});

		jMenu1.add(uploadWfMI);

		removeWfMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				Constante.CLIENT_ICON_LOCATION + "delete.gif")));
		removeWfMI.setText("Remove Definition");
		removeWfMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteSelectedWfType(evt);
			}
		});

		jMenu1.add(removeWfMI);

		refreshListMI.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				Constante.CLIENT_ICON_LOCATION + "refresh.gif")));
		refreshListMI.setMnemonic('r');
		refreshListMI.setText("Refresh List");
		refreshListMI.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshListMIActionPerformed(evt);
			}
		});

		jMenu1.add(refreshListMI);

		jMenuBar1.add(jMenu1);

		jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				Constante.CLIENT_ICON_LOCATION + "refresh.gif")));
		jMenu2.setMnemonic('r');
		jMenu2.setText("Refresh");
		jMenu2.setFocusable(false);
		jMenu2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshList(evt);
			}
		});
		jMenu2.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				refreshMenuClicked(evt);
			}
		});

		jMenuBar1.add(jMenu2);

		setJMenuBar(jMenuBar1);

		pack();
	}

	private void deleteSelectedWfType(java.awt.event.ActionEvent evt) {
		deleteSelectedWorkflowType();
	}

	private void deleteSelectedWorkflowType() {
		String wfName = getSelectedWfName();
		if (wfName == null) {
			JOptionPane.showMessageDialog(this, "No WF type selected", "Error",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int ver = getSelectedWfTytpeVersion();
		if (ver == -1)
			return;
		JOptionPane.showMessageDialog(this, "Not Implemented", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	private int getSelectedWfTytpeVersion() {
		int row = wfTypesTable.getSelectedRow();
		if (row == -1) {
			return -1;
		}
		String wfVer = (String) Utilities.getColumnValue(wfTypesTable,
				"workflowVersion", row);
		return Integer.parseInt(wfVer);
	}

	private String getSelectedWfName() {
		int row = wfTypesTable.getSelectedRow();
		if (row == -1) {
			return null;
		}
		String wfName = (String) Utilities.getColumnValue(wfTypesTable, "name",
				row);
		return wfName;
	}

	private void refreshList(java.awt.event.ActionEvent evt) {
		refreshList();
	}

	private void refreshMenuClicked(java.awt.event.MouseEvent evt) {
		refreshList();
	}

	private void uploadWfDefinition(java.awt.event.ActionEvent evt) {
		JFileChooser fc = Utilities.getFileChooser();
		fc.setCurrentDirectory(new File(wfAdminUI.getPreferences().get(
				"OPEN_DIRECTORY", ".")));
		if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this)) {
			File f = fc.getSelectedFile();
			wfAdminUI.getPreferences().put("OPEN_DIRECTORY",
					f.getParentFile().getAbsolutePath());
			try {

				BufferedReader in = new BufferedReader(new FileReader(f));
				StringBuffer xml = new StringBuffer();
				String str;
				while ((str = in.readLine()) != null) {
					xml.append(str);
				}
				ManagerWorkflow.incarcaModel(xml.toString(), "WF", Admin
						.getInstance().getUSER());
				refreshList();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void refreshListMIActionPerformed(java.awt.event.ActionEvent evt) {
		refreshList();

	}

	private void refreshList() {
		try {
			wfTypesTable.setModel(Utilities.createTableModel(wfAdminUI
					.getKnownWorkflows()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JMenuItem refreshListMI;
	private javax.swing.JMenuItem removeWfMI;
	private javax.swing.JMenuItem uploadWfMI;
	private javax.swing.JTable wfTypesTable;

}
