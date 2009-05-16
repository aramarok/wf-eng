package wf.client.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import wf.cfg.Constante;
import wf.client.Admin;

public class ActiveWorkflowWin extends javax.swing.JInternalFrame {

    private static final long serialVersionUID = 1L;

    private javax.swing.JTable activeWfTable;
    DesktopPane desktopPane;

    private javax.swing.JScrollPane jScrollPane1;

    private javax.swing.JToolBar jToolBar1;

    private javax.swing.JButton refreshBtn;

    Admin wfAdminUI;

    public ActiveWorkflowWin() {
	this.initComponents();
	this.activeWfTable.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() > 1) {
		    ActiveWorkflowWin.this.desktopPane
			    .showActiveProcessesWin(ActiveWorkflowWin.this
				    .getSelectedWfID());
		}
	    }
	});

	final JPopupMenu popMenu = new JPopupMenu("Actions");
	JMenuItem mi = new JMenuItem("show active processes");
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(final ActionEvent e) {
		ActiveWorkflowWin.this.desktopPane
			.showActiveProcessesWin(ActiveWorkflowWin.this
				.getSelectedWfID());
	    }
	});
	popMenu.add(mi);

	MouseAdapter popupAdapter = new MouseAdapter() {
	    private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
		    popMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	    }

	    @Override
	    public void mousePressed(MouseEvent e) {
		this.maybeShowPopup(e);
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		this.maybeShowPopup(e);
	    }
	};

	this.activeWfTable.addMouseListener(popupAdapter);
    }

    public ActiveWorkflowWin(final Admin wfAdminUI, final DesktopPane desk) {
	this();
	this.wfAdminUI = wfAdminUI;
	this.desktopPane = desk;
    }

    private int getSelectedWfID() {
	int row = this.activeWfTable.getSelectedRow();
	String id = (String) Utilities.getColumnValue(this.activeWfTable,
		"workflowId", row);
	return Integer.parseInt(id);
    }

    private void initComponents() {
	this.jToolBar1 = new javax.swing.JToolBar();
	this.refreshBtn = new javax.swing.JButton();
	this.jScrollPane1 = new javax.swing.JScrollPane();
	this.activeWfTable = new javax.swing.JTable();

	this.setClosable(true);
	this
		.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	this.setIconifiable(true);
	this.setMaximizable(true);
	this.setResizable(true);
	this.setTitle("Active Workflows");
	this.jToolBar1.setFloatable(false);
	this.refreshBtn.setIcon(new javax.swing.ImageIcon(this.getClass()
		.getResource(Constante.CLIENT_ICON_LOCATION + "refresh.gif")));
	this.refreshBtn.setMnemonic('r');
	this.refreshBtn.setText("refresh");
	this.refreshBtn.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(final java.awt.event.ActionEvent evt) {
		ActiveWorkflowWin.this.refreshBtnActionPerformed(evt);
	    }
	});

	this.jToolBar1.add(this.refreshBtn);

	this.getContentPane().add(this.jToolBar1, java.awt.BorderLayout.NORTH);

	this.activeWfTable.setModel(new javax.swing.table.DefaultTableModel(
		new Object[][] { { null, null, null, null },
			{ null, null, null, null }, { null, null, null, null },
			{ null, null, null, null } }, new String[] { "Title 1",
			"Title 2", "Title 3", "Title 4" }));
	this.jScrollPane1.setViewportView(this.activeWfTable);

	this.getContentPane().add(this.jScrollPane1,
		java.awt.BorderLayout.CENTER);

	this.pack();
    }

    private void refreshBtnActionPerformed(final java.awt.event.ActionEvent evt) {
	try {
	    this.activeWfTable.setModel(Utilities
		    .createTableModel(this.wfAdminUI.getActiveWorkflows()));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
