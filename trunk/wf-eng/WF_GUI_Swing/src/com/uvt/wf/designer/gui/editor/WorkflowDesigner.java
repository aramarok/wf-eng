package com.uvt.wf.designer.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphCellEditor;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import com.uvt.wf.designer.nodes.AndNode;
import com.uvt.wf.designer.nodes.EndNode;
import com.uvt.wf.designer.nodes.OrNode;
import com.uvt.wf.designer.nodes.ProcessNode;
import com.uvt.wf.designer.nodes.StartNode;

/**
 * @author Flavius
 * 
 */
public class WorkflowDesigner extends JApplet implements
		GraphSelectionListener, KeyListener {

	private static final String VALID = "Workflow is Valid";
	private static final String XML = "xml";
	private static final String WF = "wf";

	// JGraph instance
	protected JGraph graph;

	// Undo Manager
	protected GraphUndoManager undoManager;

	// Actions which Change State
	protected Action undo, redo, remove, group, ungroup, tofront, toback, cut,
			copy, paste;

	protected String workFlowName = "Workflow1";

	// Status Bar
	protected StatusBarGraphListener statusBar;

	private static JFileChooser fcOpenSave = new JFileChooser(".");
	private static JFileChooser fcExport = new JFileChooser(".");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("sun.java2d.d3d", "false");
		// Construct Frame
		JFrame frame = new JFrame("Workflow Designer");
		// Set Close Operation to Exit
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Add an Editor Panel
		frame.getContentPane().add(new WorkflowDesigner());
		// Fetch URL to Icon Resource
		URL jgraphUrl = WorkflowDesigner.class.getClassLoader().getResource(
				ToolbarIcons.JGRAPH);
		// If Valid URL
		if (jgraphUrl != null) {
			// Load Icon
			ImageIcon jgraphIcon = new ImageIcon(jgraphUrl);
			// Use in Window
			frame.setIconImage(jgraphIcon.getImage());
		}

		setFiltersfoFileChoosers();
		// Set Default Size
		frame.setSize(800, 600);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeApplication();
			}
		});
		// Show Frame
		frame.setVisible(true);
	}

	private static void setFiltersfoFileChoosers() {
		FileFilter wf = new ExtensionFileFilter("Workflow", new String[] { WF });
		FileFilter xml = new ExtensionFileFilter("XML File",
				new String[] { XML });
		fcOpenSave.setFileFilter(wf);
		fcExport.setFileFilter(xml);

	}

	/**
	 * Construct an editor panel
	 */
	public WorkflowDesigner() {
		// Construct the Graph
		graph = createGraph();
		// Use a Custom Marquee Handler
		graph.setMarqueeHandler(createMarqueeHandler());
		// Construct Command History
		undoManager = new GraphUndoManager() {
			// Override Superclass
			public void undoableEditHappened(UndoableEditEvent e) {
				// First Invoke Superclass
				super.undoableEditHappened(e);
				// Then Update Undo/Redo Buttons
				updateHistoryButtons();
			}
		};
		populateContentPane();

		installListeners(graph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		super.destroy();
		PortView.renderer = new PortRenderer();
		EdgeView.renderer = new EdgeRenderer();
		AbstractCellView.cellEditor = new DefaultGraphCellEditor();
		VertexView.renderer = new VertexRenderer();
	}

	/**
	 */
	protected void populateContentPane() {
		// Use Border Layout
		getContentPane().setLayout(new BorderLayout());
		// Add a ToolBar
		getContentPane().add(createToolBar(), BorderLayout.NORTH);
		// Add the Graph as Center Component
		getContentPane().add(new JScrollPane(graph), BorderLayout.CENTER);
		statusBar = createStatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	protected JGraph createGraph() {
		JGraph graph = new MyGraph(new MyModel());
		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {

			// Override Superclass Method to Return Custom EdgeView
			protected EdgeView createEdgeView(Object cell) {

				// Return Custom EdgeView
				return new EdgeView(cell) {

					/**
					 * Returns a cell handle for the view.
					 */
					public CellHandle getHandle(GraphContext context) {
						return new MyEdgeHandle(this, context);
					}

				};
			}
		});
		return graph;
	}

	/**
	 * @param graph
	 */
	protected void installListeners(JGraph graph) {
		// Add Listeners to Graph

		// Register UndoManager with the Model
		graph.getModel().addUndoableEditListener(undoManager);
		// Update ToolBar based on Selection Changes
		graph.getSelectionModel().addGraphSelectionListener(this);
		// Listen for Delete Keystroke when the Graph has Focus
		graph.addKeyListener(this);
		graph.getModel().addGraphModelListener(statusBar);
	}

	protected void uninstallListeners(JGraph graph) {
		graph.getModel().removeUndoableEditListener(undoManager);
		graph.getSelectionModel().removeGraphSelectionListener(this);
		graph.removeKeyListener(this);
		graph.getModel().removeGraphModelListener(statusBar);
	}

	protected BasicMarqueeHandler createMarqueeHandler() {
		return new MyMarqueeHandler();
	}

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * @return
	 */
	private Point2D getRandomPoint() {
		double maxx = this.getContentPane().getWidth() / 1.5;
		double maxy = this.getContentPane().getHeight() / 1.5;
		int x = (int) (maxx * Math.random());
		int y = (int) (maxy * Math.random());
		return new Point(x, y);
	}

	/**
	 * @param point
	 */
	public void insertStartNode(Point2D point) {
		DefaultGraphCell vertex = createStartNode();
		vertex.getAttributes().applyMap(
				createCellAttributes(point, Color.GREEN, Color.BLACK,
						Color.YELLOW));
		graph.getGraphLayoutCache().insert(vertex);
	}

	/**
	 * @param point
	 */
	public void insertEndNode(Point2D point) {
		DefaultGraphCell vertex = createEndNode();
		vertex.getAttributes().applyMap(
				createCellAttributes(point, Color.RED, Color.BLACK,
						Color.ORANGE));
		graph.getGraphLayoutCache().insert(vertex);
	}

	/**
	 * @param point
	 */
	public void insertAndNode(Point2D point) {
		DefaultGraphCell vertex = createAndNode();
		vertex.getAttributes().applyMap(
				createCellAttributes(point, Color.GRAY, Color.BLACK,
						Color.LIGHT_GRAY));
		graph.getGraphLayoutCache().insert(vertex);
	}

	/**
	 * @param point
	 */
	public void insertOrNode(Point2D point) {
		DefaultGraphCell vertex = createOrNode();
		vertex.getAttributes().applyMap(
				createCellAttributes(point, Color.LIGHT_GRAY, Color.BLACK,
						Color.WHITE));
		graph.getGraphLayoutCache().insert(vertex);
	}

	// Insert a new Vertex at point
	public void insertDefaultNode(Point2D point) {
		// Construct Vertex with no Label
		DefaultGraphCell vertex = createDefaultNode();
		// Create a Map that holds the attributes for the Vertex
		vertex.getAttributes()
				.applyMap(
						createCellAttributes(point, Color.BLUE, Color.BLACK,
								Color.CYAN));
		// Insert the Vertex (including child port and attributes)
		graph.getGraphLayoutCache().insert(vertex);
	}

	public Map createCellAttributes(Point2D point, Color c1, Color c2, Color c3) {
		Map map = new Hashtable();
		// Snap the Point to the Grid
		if (graph != null) {
			point = graph.snap((Point2D) point.clone());
		} else {
			point = (Point2D) point.clone();
		}
		GraphConstants.setFont(map, new Font("sansserif", Font.BOLD, 18));
		// Add a Bounds Attribute to the Map
		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 0, 0));
		// Make sure the cell is resized on insert
		GraphConstants.setResize(map, true);
		// Add a nice looking gradient background
		GraphConstants.setGradientColor(map, c1);
		// Add a Border Color Attribute to the Map
		GraphConstants.setBorderColor(map, c1);
		// Add a White Background
		GraphConstants.setBackground(map, c3);
		// Make Vertex Opaque
		GraphConstants.setOpaque(map, true);
		return map;
	}

	protected DefaultGraphCell createDefaultNode() {
		DefaultGraphCell cell = new ProcessNode("Proc");
		// Add one Floating Port
		cell.addPort();
		return cell;
	}

	protected DefaultGraphCell createStartNode() {
		DefaultGraphCell cell = new StartNode("Start");
		// Add one Floating Port
		cell.addPort();
		return cell;
	}

	protected DefaultGraphCell createEndNode() {
		DefaultGraphCell cell = new EndNode("End");
		// Add one Floating Port
		cell.addPort();
		return cell;
	}

	protected DefaultGraphCell createAndNode() {
		DefaultGraphCell cell = new AndNode("And");
		// Add one Floating Port
		cell.addPort();
		return cell;
	}

	protected DefaultGraphCell createOrNode() {
		DefaultGraphCell cell = new OrNode("Or");
		// Add one Floating Port
		cell.addPort();
		return cell;
	}

	// Insert a new Edge between source and target
	public void connect(Port source, Port target) {
		// Construct Edge with no label
		DefaultEdge edge = createDefaultEdge();
		if (graph.getModel().acceptsSource(edge, source)
				&& graph.getModel().acceptsTarget(edge, target)) {
			// Create a Map thath holds the attributes for the edge
			edge.getAttributes().applyMap(createEdgeAttributes());
			// Insert the Edge and its Attributes
			graph.getGraphLayoutCache().insertEdge(edge, source, target);
		}
	}

	protected DefaultEdge createDefaultEdge() {
		return new DefaultEdge();
	}

	public Map createEdgeAttributes() {
		Map map = new Hashtable();
		// Add a Line End Attribute
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
		// Add a label along edge attribute
		GraphConstants.setLabelAlongEdge(map, true);
		return map;
	}

	public void group(Object[] cells) {
		// Order Cells by Model Layering
		cells = graph.order(cells);
		// If Any Cells in View
		if (cells != null && cells.length > 0) {
			DefaultGraphCell group = createGroupCell();
			// Insert into model
			graph.getGraphLayoutCache().insertGroup(group, cells);
		}
	}

	protected DefaultGraphCell createGroupCell() {
		return new DefaultGraphCell();
	}

	protected int getCellCount(JGraph graph) {
		Object[] cells = graph.getDescendants(graph.getRoots());
		return cells.length;
	}

	public void ungroup(Object[] cells) {
		graph.getGraphLayoutCache().ungroup(cells);
	}

	public boolean isGroup(Object cell) {
		// Map the Cell to its View
		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		if (view != null)
			return !view.isLeaf();
		return false;
	}

	public void toFront(Object[] c) {
		graph.getGraphLayoutCache().toFront(c);
	}

	public void toBack(Object[] c) {
		graph.getGraphLayoutCache().toBack(c);
	}

	public void undo() {
		try {
			undoManager.undo(graph.getGraphLayoutCache());
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
		}
	}

	public void redo() {
		try {
			undoManager.redo(graph.getGraphLayoutCache());
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
		}
	}

	protected void updateHistoryButtons() {
		// The View Argument Defines the Context
		undo.setEnabled(undoManager.canUndo(graph.getGraphLayoutCache()));
		redo.setEnabled(undoManager.canRedo(graph.getGraphLayoutCache()));
	}

	// Listeners

	// From GraphSelectionListener Interface
	public void valueChanged(GraphSelectionEvent e) {
		// Group Button only Enabled if more than One Cell Selected
		group.setEnabled(graph.getSelectionCount() > 1);
		// Update Button States based on Current Selection
		boolean enabled = !graph.isSelectionEmpty();
		remove.setEnabled(enabled);
		ungroup.setEnabled(enabled);
		tofront.setEnabled(enabled);
		toback.setEnabled(enabled);
		copy.setEnabled(enabled);
		cut.setEnabled(enabled);
	}

	// KeyListener for Delete KeyStroke
	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// Listen for Delete Key Press
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
			// Execute Remove Action on Delete Key Press
			remove.actionPerformed(null);
	}

	// MarqueeHandler that Connects Vertices and Displays PopupMenus
	public class MyMarqueeHandler extends BasicMarqueeHandler {

		// Holds the Start and the Current Point
		protected Point2D start, current;

		// Holds the First and the Current Port
		protected PortView port, firstPort;

		/**
		 * Component that is used for highlighting cells if the graph does not
		 * allow XOR painting.
		 */
		protected JComponent highlight = new JPanel();

		public MyMarqueeHandler() {
			// Configures the panel for highlighting ports
			highlight = createHighlight();
		}

		/**
		 * Creates the component that is used for highlighting cells if the
		 * graph does not allow XOR painting.
		 */
		protected JComponent createHighlight() {
			JPanel panel = new JPanel();
			panel
					.setBorder(BorderFactory
							.createBevelBorder(BevelBorder.RAISED));
			panel.setVisible(false);
			panel.setOpaque(false);

			return panel;
		}

		// Override to Gain Control (for PopupMenu and ConnectMode)
		public boolean isForceMarqueeEvent(MouseEvent e) {
			if (e.isShiftDown())
				return false;
			// If Right Mouse Button we want to Display the PopupMenu
			if (SwingUtilities.isRightMouseButton(e))
				// Return Immediately
				return true;
			// Find and Remember Port
			port = getSourcePortAt(e.getPoint());
			// If Port Found and in ConnectMode (=Ports Visible)
			if (port != null && graph.isPortsVisible())
				return true;
			// Else Call Superclass
			return super.isForceMarqueeEvent(e);
		}

		// Display PopupMenu or Remember Start Location and First Port
		public void mousePressed(final MouseEvent e) {
			// If Right Mouse Button
			if (SwingUtilities.isRightMouseButton(e)) {
				// Find Cell in Model Coordinates
				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
				// Create PopupMenu for the Cell
				JPopupMenu menu = createPopupMenu(e.getPoint(), cell);
				// Display PopupMenu
				menu.show(graph, e.getX(), e.getY());
				// Else if in ConnectMode and Remembered Port is Valid
			} else if (port != null && graph.isPortsVisible()) {
				// Remember Start Location
				start = graph.toScreen(port.getLocation());
				// Remember First Port
				firstPort = port;
			} else {
				// Call Superclass
				super.mousePressed(e);
			}
		}

		// Find Port under Mouse and Repaint Connector
		public void mouseDragged(MouseEvent e) {
			// If remembered Start Point is Valid
			if (start != null) {
				// Fetch Graphics from Graph
				Graphics g = graph.getGraphics();
				// Reset Remembered Port
				PortView newPort = getTargetPortAt(e.getPoint());
				// Do not flicker (repaint only on real changes)
				if (newPort == null || newPort != port) {
					// Xor-Paint the old Connector (Hide old Connector)
					paintConnector(Color.black, graph.getBackground(), g);
					// If Port was found then Point to Port Location
					port = newPort;
					if (port != null)
						current = graph.toScreen(port.getLocation());
					// Else If no Port was found then Point to Mouse Location
					else
						current = graph.snap(e.getPoint());
					// Xor-Paint the new Connector
					paintConnector(graph.getBackground(), Color.black, g);
				}
			}
			// Call Superclass
			super.mouseDragged(e);
		}

		public PortView getSourcePortAt(Point2D point) {
			// Disable jumping
			graph.setJumpToDefaultPort(false);
			PortView result;
			try {
				// Find a Port View in Model Coordinates and Remember
				result = graph.getPortViewAt(point.getX(), point.getY());
			} finally {
				graph.setJumpToDefaultPort(true);
			}
			return result;
		}

		// Find a Cell at point and Return its first Port as a PortView
		protected PortView getTargetPortAt(Point2D point) {
			// Find a Port View in Model Coordinates and Remember
			return graph.getPortViewAt(point.getX(), point.getY());
		}

		// Connect the First Port and the Current Port in the Graph or Repaint
		public void mouseReleased(MouseEvent e) {
			highlight(graph, null);

			// If Valid Event, Current and First Port
			if (e != null && port != null && firstPort != null
					&& firstPort != port) {
				// Then Establish Connection
				connect((Port) firstPort.getCell(), (Port) port.getCell());
				e.consume();
				// Else Repaint the Graph
			} else
				graph.repaint();
			// Reset Global Vars
			firstPort = port = null;
			start = current = null;
			// Call Superclass
			super.mouseReleased(e);
		}

		// Show Special Cursor if Over Port
		public void mouseMoved(MouseEvent e) {
			// Check Mode and Find Port
			if (e != null && getSourcePortAt(e.getPoint()) != null
					&& graph.isPortsVisible()) {
				// Set Cusor on Graph (Automatically Reset)
				graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				// Consume Event
				// Note: This is to signal the BasicGraphUI's
				// MouseHandle to stop further event processing.
				e.consume();
			} else
				// Call Superclass
				super.mouseMoved(e);
		}

		// Use Xor-Mode on Graphics to Paint Connector
		protected void paintConnector(Color fg, Color bg, Graphics g) {
			if (graph.isXorEnabled()) {
				// Set Foreground
				g.setColor(fg);
				// Set Xor-Mode Color
				g.setXORMode(bg);
				// Highlight the Current Port
				paintPort(graph.getGraphics());

				drawConnectorLine(g);
			} else {
				Rectangle dirty = new Rectangle((int) start.getX(), (int) start
						.getY(), 1, 1);

				if (current != null) {
					dirty.add(current);
				}

				dirty.grow(1, 1);

				graph.repaint(dirty);
				highlight(graph, port);
			}
		}

		// Overrides parent method to paint connector if
		// XOR painting is disabled in the graph
		public void paint(JGraph graph, Graphics g) {
			super.paint(graph, g);

			if (!graph.isXorEnabled()) {
				g.setColor(Color.black);
				drawConnectorLine(g);
			}
		}

		protected void drawConnectorLine(Graphics g) {
			if (firstPort != null && start != null && current != null) {
				// Then Draw A Line From Start to Current Point
				g.drawLine((int) start.getX(), (int) start.getY(),
						(int) current.getX(), (int) current.getY());
			}
		}

		// Use the Preview Flag to Draw a Highlighted Port
		protected void paintPort(Graphics g) {
			// If Current Port is Valid
			if (port != null) {
				// If Not Floating Port...
				boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
				// ...Then use Parent's Bounds
				Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
						.getBounds();
				// Scale from Model to Screen
				r = graph.toScreen((Rectangle2D) r.clone());
				// Add Space For the Highlight Border
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
						.getHeight() + 6);
				// Paint Port in Preview (=Highlight) Mode
				graph.getUI().paintCell(g, port, r, true);
			}
		}

		/**
		 * Highlights the given cell view or removes the highlight if no cell
		 * view is specified.
		 * 
		 * @param graph
		 * @param cellView
		 */
		protected void highlight(JGraph graph, CellView cellView) {
			if (cellView != null) {
				highlight.setBounds(getHighlightBounds(graph, cellView));

				if (highlight.getParent() == null) {
					graph.add(highlight);
					highlight.setVisible(true);
				}
			} else {
				if (highlight.getParent() != null) {
					highlight.setVisible(false);
					highlight.getParent().remove(highlight);
				}
			}
		}

		/**
		 * Returns the bounds to be used to highlight the given cell view.
		 * 
		 * @param graph
		 * @param cellView
		 * @return
		 */
		protected Rectangle getHighlightBounds(JGraph graph, CellView cellView) {
			boolean offset = (GraphConstants.getOffset(cellView
					.getAllAttributes()) != null);
			Rectangle2D r = (offset) ? cellView.getBounds() : cellView
					.getParentView().getBounds();
			r = graph.toScreen((Rectangle2D) r.clone());
			int s = 3;

			return new Rectangle((int) (r.getX() - s), (int) (r.getY() - s),
					(int) (r.getWidth() + 2 * s), (int) (r.getHeight() + 2 * s));
		}

	}

	// PopupMenu
	public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();
		if (cell != null) {
			// Edit
			menu.add(new AbstractAction("Edit Node Name") {
				public void actionPerformed(ActionEvent e) {
					graph.startEditingAtCell(cell);
				}
			});
		}
		// Remove
		if (!graph.isSelectionEmpty()) {
			menu.addSeparator();
			menu.add(new AbstractAction("Remove Node") {
				public void actionPerformed(ActionEvent e) {
					remove.actionPerformed(e);
				}
			});
		}
		menu.addSeparator();
		// Insert Process
		menu.add(new AbstractAction("Insert Process Node") {
			public void actionPerformed(ActionEvent ev) {
				insertDefaultNode(pt);
			}
		});
		menu.addSeparator();
		// Insert Start
		menu.add(new AbstractAction("Insert Start Node") {
			public void actionPerformed(ActionEvent ev) {
				insertStartNode(pt);
			}
		});
		// Insert End
		menu.add(new AbstractAction("Insert End Node") {
			public void actionPerformed(ActionEvent ev) {
				insertEndNode(pt);
			}
		});
		menu.addSeparator();
		// Insert And
		menu.add(new AbstractAction("Insert And Node") {
			public void actionPerformed(ActionEvent ev) {
				insertAndNode(pt);
			}
		});
		// Insert Or
		menu.add(new AbstractAction("Insert Or Node") {
			public void actionPerformed(ActionEvent ev) {
				insertOrNode(pt);
			}
		});
		return menu;
	}

	// ToolBar
	public JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		// New
		File newURL = new File(ToolbarIcons.NEW);
		ImageIcon newIcon = new ImageIcon(newURL.getAbsolutePath());
		JButton newButton = new JButton(new AbstractAction("", newIcon) {
			public void actionPerformed(ActionEvent e) {
				createNewWorkflow();
			}
		});
		newButton.setToolTipText("New Workflow");
		toolbar.add(newButton);

		// Open
		File openURL = new File(ToolbarIcons.OPEN);
		ImageIcon openIcon = new ImageIcon(openURL.getAbsolutePath());
		JButton openButton = new JButton(new AbstractAction("", openIcon) {
			public void actionPerformed(ActionEvent e) {
				openFromFileAsk();
			}
		});
		openButton.setToolTipText("Open Workflow");
		toolbar.add(openButton);

		// Save
		File saveURL = new File(ToolbarIcons.SAVE);
		ImageIcon saveIcon = new ImageIcon(saveURL.getAbsolutePath());
		JButton saveButton = new JButton(new AbstractAction("", saveIcon) {
			public void actionPerformed(ActionEvent e) {
				saveToFile();
			}
		});
		saveButton.setToolTipText("Save Workflow");
		toolbar.add(saveButton);
		toolbar.addSeparator();

		// Export
		File exportUrl = new File(ToolbarIcons.EXPORT);
		ImageIcon exportIcon = new ImageIcon(exportUrl.getAbsolutePath());
		JButton exportButton = new JButton(new AbstractAction("", exportIcon) {
			public void actionPerformed(ActionEvent e) {
				// exportGraph2XML();
				exportGraph2XML();
			}
		});
		exportButton.setToolTipText("Export Workflow to XML");
		toolbar.add(exportButton);
		toolbar.addSeparator();

		// Insert Start
		File insertStartUrl = new File(ToolbarIcons.START);
		ImageIcon insertStartIcon = new ImageIcon(insertStartUrl
				.getAbsolutePath());
		JButton insertStartButton = new JButton(new AbstractAction("",
				insertStartIcon) {
			public void actionPerformed(ActionEvent e) {
				insertStartNode(getRandomPoint());
			}
		});
		insertStartButton.setToolTipText("Insert Start Node");
		toolbar.add(insertStartButton);

		// Insert End
		File insertEndUrl = new File(ToolbarIcons.END);
		ImageIcon insertEndIcon = new ImageIcon(insertEndUrl.getAbsolutePath());
		JButton insertEndButton = new JButton(new AbstractAction("",
				insertEndIcon) {
			public void actionPerformed(ActionEvent e) {
				insertEndNode(getRandomPoint());
			}
		});
		insertEndButton.setToolTipText("Insert End Node");
		toolbar.add(insertEndButton);

		// Insert And
		File insertAndUrl = new File(ToolbarIcons.AND);
		ImageIcon insertAndIcon = new ImageIcon(insertAndUrl.getAbsolutePath());
		JButton insertAndButton = new JButton(new AbstractAction("",
				insertAndIcon) {
			public void actionPerformed(ActionEvent e) {
				insertAndNode(getRandomPoint());
			}
		});
		insertAndButton.setToolTipText("Insert And Node");
		toolbar.add(insertAndButton);

		// Insert Or
		File insertOrUrl = new File(ToolbarIcons.OR);
		ImageIcon insertOrIcon = new ImageIcon(insertOrUrl.getAbsolutePath());
		JButton insertOrButton = new JButton(new AbstractAction("",
				insertOrIcon) {
			public void actionPerformed(ActionEvent e) {
				insertOrNode(getRandomPoint());
			}
		});
		insertOrButton.setToolTipText("Insert Or Node");
		toolbar.add(insertOrButton);

		// Insert
		File insertUrl = new File(ToolbarIcons.PROCESS);
		ImageIcon insertIcon = new ImageIcon(insertUrl.getAbsolutePath());
		JButton insertProcessButton = new JButton(new AbstractAction("",
				insertIcon) {
			public void actionPerformed(ActionEvent e) {
				insertDefaultNode(getRandomPoint());
			}
		});
		insertProcessButton.setToolTipText("Insert Process Node");
		toolbar.add(insertProcessButton);

		// Toggle Connect Mode
		File connectUrl = new File(ToolbarIcons.CONNECTION);
		ImageIcon connectIcon = new ImageIcon(connectUrl.getAbsolutePath());
		JButton connectButton = new JButton(
				new AbstractAction("", connectIcon) {
					public void actionPerformed(ActionEvent e) {
						graph.setPortsVisible(!graph.isPortsVisible());
						File connectUrl;
						if (graph.isPortsVisible())
							connectUrl = new File(ToolbarIcons.CONNECTION);
						else
							connectUrl = new File(ToolbarIcons.CONNECTIONOFF);
						ImageIcon connectIcon = new ImageIcon(connectUrl
								.getAbsolutePath());
						putValue(SMALL_ICON, connectIcon);
					}
				});
		connectButton.setToolTipText("Toggle Transition Mode");
		toolbar.add(connectButton);

		// Undo
		toolbar.addSeparator();
		File undoUrl = new File(ToolbarIcons.UNDO);
		ImageIcon undoIcon = new ImageIcon(undoUrl.getAbsolutePath());
		undo = new AbstractAction("", undoIcon) {
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		};
		undo.setEnabled(false);
		JButton undoButton = new JButton(undo);
		undoButton.setToolTipText("Undo");
		toolbar.add(undoButton);

		// Redo
		File redoUrl = new File(ToolbarIcons.REDO);
		ImageIcon redoIcon = new ImageIcon(redoUrl.getAbsolutePath());
		redo = new AbstractAction("", redoIcon) {
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		};
		redo.setEnabled(false);
		JButton redoButton = new JButton(redo);
		redoButton.setToolTipText("Redo");
		toolbar.add(redoButton);

		toolbar.addSeparator();
		Action action;
		File url;

		// Copy
		action = javax.swing.TransferHandler.getCopyAction();
		url = new File(ToolbarIcons.COPY);
		JButton copyButton = new JButton(copy = new EventRedirector(action,
				new ImageIcon(url.getAbsolutePath())));
		copyButton.setToolTipText("Copy");
		toolbar.add(copyButton);

		// Paste
		action = javax.swing.TransferHandler.getPasteAction();
		url = new File(ToolbarIcons.PASTE);
		JButton pasteButton = new JButton(paste = new EventRedirector(action,
				new ImageIcon(url.getAbsolutePath())));
		pasteButton.setToolTipText("Paste");
		toolbar.add(pasteButton);

		// Cut
		action = javax.swing.TransferHandler.getCutAction();
		url = new File(ToolbarIcons.CUT);
		JButton cutButton = new JButton(cut = new EventRedirector(action,
				new ImageIcon(url.getAbsolutePath())));
		cutButton.setToolTipText("Cut");
		toolbar.add(cutButton);

		// Remove
		File removeUrl = new File(ToolbarIcons.DELETE);
		ImageIcon removeIcon = new ImageIcon(removeUrl.getAbsolutePath());
		remove = new AbstractAction("", removeIcon) {
			public void actionPerformed(ActionEvent e) {
				if (!graph.isSelectionEmpty()) {
					Object[] cells = graph.getSelectionCells();
					cells = graph.getDescendants(cells);
					graph.getModel().remove(cells);
				}
			}
		};
		remove.setEnabled(false);
		JButton removeButton = new JButton(remove);
		removeButton.setToolTipText("Delete");
		toolbar.add(removeButton);

		// To Front
		toolbar.addSeparator();
		File toFrontUrl = new File(ToolbarIcons.TOFRONT);
		ImageIcon toFrontIcon = new ImageIcon(toFrontUrl.getAbsolutePath());
		tofront = new AbstractAction("", toFrontIcon) {
			public void actionPerformed(ActionEvent e) {
				if (!graph.isSelectionEmpty())
					toFront(graph.getSelectionCells());
			}
		};
		tofront.setEnabled(false);
		JButton tofrontButton = new JButton(tofront);
		tofrontButton.setToolTipText("Bring to Front");
		toolbar.add(tofrontButton);

		// To Back
		File toBackUrl = new File(ToolbarIcons.TOBACK);
		ImageIcon toBackIcon = new ImageIcon(toBackUrl.getAbsolutePath());
		toback = new AbstractAction("", toBackIcon) {
			public void actionPerformed(ActionEvent e) {
				if (!graph.isSelectionEmpty())
					toBack(graph.getSelectionCells());
			}
		};
		toback.setEnabled(false);
		JButton tobackButton = new JButton(toback);
		tobackButton.setToolTipText("Send to Back");
		toolbar.add(tobackButton);

		// Zoom Std
		toolbar.addSeparator();
		File zoomUrl = new File(ToolbarIcons.ZOOM);
		ImageIcon zoomIcon = new ImageIcon(zoomUrl.getAbsolutePath());
		JButton zoomstButton = new JButton(new AbstractAction("", zoomIcon) {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(1.0);
			}
		});
		zoomstButton.setToolTipText("Zoom 1:1");
		toolbar.add(zoomstButton);

		// Zoom In
		File zoomInUrl = new File(ToolbarIcons.ZOOMIN);
		ImageIcon zoomInIcon = new ImageIcon(zoomInUrl.getAbsolutePath());
		JButton zoomInButton = new JButton(new AbstractAction("", zoomInIcon) {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(2 * graph.getScale());
			}
		});
		zoomInButton.setToolTipText("Zoom In");
		toolbar.add(zoomInButton);

		// Zoom Out
		File zoomOutUrl = new File(ToolbarIcons.ZOOMOUT);
		ImageIcon zoomOutIcon = new ImageIcon(zoomOutUrl.getAbsolutePath());
		JButton zoomOutButton = new JButton(
				new AbstractAction("", zoomOutIcon) {
					public void actionPerformed(ActionEvent e) {
						graph.setScale(graph.getScale() / 2);
					}
				});
		zoomOutButton.setToolTipText("Zoom Out");
		toolbar.add(zoomOutButton);

		// Group
		toolbar.addSeparator();
		File groupUrl = new File(ToolbarIcons.GROUP);
		ImageIcon groupIcon = new ImageIcon(groupUrl.getAbsolutePath());
		group = new AbstractAction("", groupIcon) {
			public void actionPerformed(ActionEvent e) {
				group(graph.getSelectionCells());
			}
		};
		group.setEnabled(false);
		JButton groupButton = new JButton(group);
		groupButton.setToolTipText("Group");
		toolbar.add(groupButton);

		// Ungroup
		File ungroupUrl = new File(ToolbarIcons.UNGROUP);
		ImageIcon ungroupIcon = new ImageIcon(ungroupUrl.getAbsolutePath());
		ungroup = new AbstractAction("", ungroupIcon) {
			public void actionPerformed(ActionEvent e) {
				ungroup(graph.getSelectionCells());
			}
		};
		ungroup.setEnabled(false);
		JButton ungroupButton = new JButton(ungroup);
		ungroupButton.setToolTipText("Ungroup");
		toolbar.add(ungroupButton);

		return toolbar;
	}

	/**
	 * Creates New and Empty Workflow
	 */
	protected void createNewWorkflow() {
		if (getCellCount(graph) > 0) {
			int r = JOptionPane.showConfirmDialog(this.getContentPane(),
					"Are you sure that you want to create a new workflow?",
					"New Workflow", JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				// graph = createGraph();
				resetGraph();
			}
		}
	}

	private void resetGraph() {
		GraphLayoutCache glc = graph.getGraphLayoutCache();
		Object[] all = glc.getCells(true, true, true, true);
		graph.getGraphLayoutCache().remove(all);
		undoManager.discardAllEdits();
		updateHistoryButtons();
	}

	/**
	 * Closes application
	 */
	protected static void closeApplication() {
		int r = JOptionPane.showConfirmDialog(null,
				"Are you sure that you want to close the application?",
				"New Workflow", JOptionPane.YES_NO_OPTION);
		if (r == JOptionPane.OK_OPTION) {
			System.exit(0);
		}
	}

	protected void saveToFile() {
		int returnVal = fcOpenSave.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fcOpenSave.getSelectedFile();
			String filename = file.getAbsolutePath();
			if (!filename.endsWith("." + WF))
				filename += "." + WF;
			try {
				FileOutputStream fout = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				// Save graph cells
				GraphLayoutCache glc = graph.getGraphLayoutCache();
				Object[] cells = glc.getCells(true, true, true, true);
				oos.writeObject(cells);
				oos = new ObjectOutputStream(fout);
				oos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void openFromFileAsk() {
		if (getCellCount(graph) > 0) {
			int r = JOptionPane.showConfirmDialog(null,
					"Are you sure that you want to open a workflow from file?",
					"Open Workflow", JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				openFromFile();
			}
		} else
			openFromFile();
	}

	protected void openFromFile() {
		int returnVal = fcOpenSave.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fcOpenSave.getSelectedFile();
			String filename = file.getAbsolutePath();
			try {
				FileInputStream fin = new FileInputStream(filename);
				ObjectInputStream ois = new ObjectInputStream(fin);
				// Read graph cells
				Object[] cells = (Object[]) ois.readObject();
				// Reset Graph
				resetGraph();
				// insert cells in Graph Cache
				for (Object cell : cells) {
					graph.getGraphLayoutCache().insert(cell);
				}
				ois.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Exports the graph layout to an XML file
	 */
	protected void exportGraph2XML() {
		String workflowStatus = validateWorkflow();
		if (!workflowStatus.equals(VALID))
			showMessage("Workflow is not valid!\n" + workflowStatus);
		else {
			String newWorkflowName = JOptionPane.showInputDialog(null,
					"Enter Workflow Name", workFlowName);
			workFlowName = newWorkflowName;
			int returnVal = fcExport.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fcExport.getSelectedFile();
				String Filename = file.getAbsolutePath();
				if (!Filename.endsWith("." + XML))
					Filename += "." + XML;
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							Filename));
					bw.write(generateXMLContentsFromGraph(graph));
					bw.close();
				} catch (IOException exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	private String getNodeType(DefaultGraphCell currentNode) {
		String nodeType = "";
		if (currentNode instanceof StartNode) {
			nodeType = NodeType.START;
		} else if (currentNode instanceof EndNode) {
			nodeType = NodeType.END;
		} else if (currentNode instanceof AndNode) {
			nodeType = NodeType.AND;
		} else if (currentNode instanceof OrNode) {
			nodeType = NodeType.OR;
		} else if (currentNode instanceof ProcessNode) {
			nodeType = NodeType.PROCESS;
		}
		return nodeType;
	}

	private String validateWorkflow() {
		String isValidWorkflow = VALID;
		int startNodes = 0;
		int endNodes = 0;

		GraphLayoutCache glc = graph.getGraphLayoutCache();

		Object[] nodes = glc.getCells(false, true, false, false);
		for (int i = 0; i < nodes.length; i++) {
			DefaultGraphCell currentNode = (DefaultGraphCell) nodes[i];
			String nodeType = getNodeType(currentNode);

			List<DefaultGraphCell> transOut = (List<DefaultGraphCell>) glc
					.getNeighbours(currentNode, null, true, true);
			List<DefaultGraphCell> transAll = (List<DefaultGraphCell>) glc
					.getNeighbours(currentNode, null, false, true);
			List<DefaultGraphCell> transIn = new ArrayList<DefaultGraphCell>();
			for (DefaultGraphCell transition : transAll) {
				if (!transOut.contains(transition))
					transIn.add(transition);
			}

			// Start Nodes
			if (nodeType.equals(NodeType.START)) {
				startNodes++;
				// Only one start node
				if (startNodes > 1)
					return "Workflow cannot have more than 1 start node!";
				// No transitions going in the Start Node
				if (transIn.size() > 0)
					return "Workflow cannot have transitions going in the start node!";
				// Must have transitions going out
				if (transOut.size() == 0)
					return "Workflow must have transitions going out of the start node!";
				else
					// All transitions must go into Process Nodes
					for (DefaultGraphCell transition : transOut) {
						String destNodeType = getNodeType(transition);
						if (!destNodeType.equals(NodeType.PROCESS))
							return "Every transition going out of the start node must go into a process node!";
					}
			}

			// End Node
			if (nodeType.equals(NodeType.END)) {
				endNodes++;
				// Only one end node
				if (endNodes > 1)
					return "Workflow cannot have more than 1 end node!";
				// No transitions going out of the End Node
				if (transOut.size() > 0)
					return "Workflow cannot have transitions going out of the end node!";
				// Must have transitions going in
				if (transIn.size() == 0)
					return "Workflow must have transitions going in the end node!";
			}

			// And Node
			if (nodeType.equals(NodeType.AND)) {
				// 2 or more transitions in
				if (transIn.size() < 2)
					return "An And node must have two or more transitions going into it!";
				// at least 1 transition out
				if (transOut.size() == 0)
					return "An And node must have at least one transition going out of it!";
			}
			// Or Node
			if (nodeType.equals(NodeType.OR)) {
				// 2 or more transitions in
				if (transIn.size() < 2)
					return "An Or node must have two or more transitions going into it!";
				// at least 1 transition out
				if (transOut.size() == 0)
					return "An Or node must have at least one transition going out of it!";
			}
			// Process Node
			if (nodeType.equals(NodeType.PROCESS)) {
				// at least 1 transition out
				if (transOut.size() == 0)
					return "A Process node must have at least one transition going out of it!";
			}
		}

		if (startNodes == 0)
			return "No start node present!";
		if (endNodes == 0)
			return "No end node present!";
		return isValidWorkflow;
	}

	/**
	 * Generates XML Content from the Graph Layout Cache
	 * 
	 * @param graph
	 * @return
	 */
	private String generateXMLContentsFromGraph(JGraph graph) {
		StringBuilder sb_main = new StringBuilder();
		StringBuilder sb_nodes = new StringBuilder();
		StringBuilder sb_transitions = new StringBuilder();

		// variable
		sb_main.append("<wf name=\"" + workFlowName + "\">\n");
		sb_nodes.append("\t<nodes>\n");
		sb_transitions.append("\t<transitions>\n");

		GraphLayoutCache glc = graph.getGraphLayoutCache();

		// extract only the vertices, without edges
		Object[] vertices = glc.getCells(false, true, false, false);
		for (int i = 0; i < vertices.length; i++) {
			DefaultGraphCell currentNode = (DefaultGraphCell) vertices[i];
			String currentNodeName = (String) currentNode.getUserObject();
			String currentNodeType = "";
			if (currentNode instanceof StartNode) {
				currentNodeType = NodeType.START;
			} else if (currentNode instanceof EndNode) {
				currentNodeType = NodeType.END;
			} else if (currentNode instanceof AndNode) {
				currentNodeType = NodeType.AND;
			} else if (currentNode instanceof OrNode) {
				currentNodeType = NodeType.OR;
			} else if (currentNode instanceof ProcessNode) {
				currentNodeType = NodeType.PROCESS;
			}

			sb_nodes.append("\t\t<node id=\"" + currentNodeName + "\" type=\""
					+ currentNodeType + "\" />\n");

			List<DefaultGraphCell> neighbours = (List<DefaultGraphCell>) glc
					.getNeighbours(currentNode, null, true, true);
			for (DefaultGraphCell neighbour : neighbours) {
				String neighbourNodeName = (String) neighbour.getUserObject();
				sb_transitions.append("\t\t<transition from=\"" + currentNode
						+ "\" to=\"" + neighbourNodeName + "\" />\n");
			}
		}

		sb_transitions.append("\t</transitions>\n");
		sb_nodes.append("\t</nodes>\n");
		sb_main.append(sb_nodes);
		sb_main.append(sb_transitions);
		sb_main.append("</wf>");

		return sb_main.toString();
	}

	/**
	 * @return Returns the graph.
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 *            The graph to set.
	 */
	public void setGraph(JGraph graph) {
		this.graph = graph;
	}

	// This will change the source of the actionevent to graph.
	public class EventRedirector extends AbstractAction {

		protected Action action;

		// Construct the "Wrapper" Action
		public EventRedirector(Action a, ImageIcon icon) {
			super("", icon);
			this.action = a;
		}

		// Redirect the Actionevent
		public void actionPerformed(ActionEvent e) {
			e = new ActionEvent(graph, e.getID(), e.getActionCommand(), e
					.getModifiers());
			action.actionPerformed(e);
		}
	}

	/**
	 * Create a status bar
	 */
	protected StatusBarGraphListener createStatusBar() {
		return new EdStatusBar();
	}

	/**
	 * 
	 * @return a String representing the version of this application
	 */
	protected String getVersion() {
		return JGraph.VERSION;
	}

	/**
	 * @return Returns the redo.
	 */
	public Action getRedo() {
		return redo;
	}

	/**
	 * @param redo
	 *            The redo to set.
	 */
	public void setRedo(Action redo) {
		this.redo = redo;
	}

	/**
	 * @return Returns the undo.
	 */
	public Action getUndo() {
		return undo;
	}

	/**
	 * @param undo
	 *            The undo to set.
	 */
	public void setUndo(Action undo) {
		this.undo = undo;
	}

	public class StatusBarGraphListener extends JPanel implements
			GraphModelListener {

		/**
		 * Graph Model change event
		 */
		public void graphChanged(GraphModelEvent e) {
			updateStatusBar();
		}

		protected void updateStatusBar() {

		}
	}

	public class EdStatusBar extends StatusBarGraphListener {
		/**
		 * 
		 */
		protected JLabel leftSideStatus;

		/**
		 * contains the scale for the current graph
		 */
		protected JLabel rightSideStatus;

		/**
		 * Constructor for GPStatusBar.
		 * 
		 */
		public EdStatusBar() {
			super();
			// Add this as graph model change listener
			setLayout(new BorderLayout());
			leftSideStatus = new JLabel("Zoom Level:"
					+ String.valueOf(graph.getScale()));
			rightSideStatus = new JLabel("Memory Usage: 0/0 MB");
			leftSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
			rightSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
			add(leftSideStatus, BorderLayout.CENTER);
			add(rightSideStatus, BorderLayout.EAST);
		}

		protected void updateStatusBar() {
			leftSideStatus.setText("Zoom Level:"
					+ String.valueOf(graph.getScale()));
			Runtime runtime = Runtime.getRuntime();
			int freeMemory = (int) (runtime.freeMemory() / 1024);
			int totalMemory = (int) (runtime.totalMemory() / 1024);
			int usedMemory = (totalMemory - freeMemory);
			String str = ("Memory Usage: " + usedMemory / 1024) + "/"
					+ (totalMemory / 1024) + " MB";
			rightSideStatus.setText(str);
		}

		/**
		 * @return Returns the leftSideStatus.
		 */
		public JLabel getLeftSideStatus() {
			return leftSideStatus;
		}

		/**
		 * @param leftSideStatus
		 *            The leftSideStatus to set.
		 */
		public void setLeftSideStatus(JLabel leftSideStatus) {
			this.leftSideStatus = leftSideStatus;
		}

		/**
		 * @return Returns the rightSideStatus.
		 */
		public JLabel getRightSideStatus() {
			return rightSideStatus;
		}

		/**
		 * @param rightSideStatus
		 *            The rightSideStatus to set.
		 */
		public void setRightSideStatus(JLabel rightSideStatus) {
			this.rightSideStatus = rightSideStatus;
		}
	}

	/**
	 * @return Returns the copy.
	 */
	public Action getCopy() {
		return copy;
	}

	/**
	 * @param copy
	 *            The copy to set.
	 */
	public void setCopy(Action copy) {
		this.copy = copy;
	}

	/**
	 * @return Returns the cut.
	 */
	public Action getCut() {
		return cut;
	}

	/**
	 * @param cut
	 *            The cut to set.
	 */
	public void setCut(Action cut) {
		this.cut = cut;
	}

	/**
	 * @return Returns the paste.
	 */
	public Action getPaste() {
		return paste;
	}

	/**
	 * @param paste
	 *            The paste to set.
	 */
	public void setPaste(Action paste) {
		this.paste = paste;
	}

	/**
	 * @return Returns the toback.
	 */
	public Action getToback() {
		return toback;
	}

	/**
	 * @param toback
	 *            The toback to set.
	 */
	public void setToback(Action toback) {
		this.toback = toback;
	}

	/**
	 * @return Returns the tofront.
	 */
	public Action getTofront() {
		return tofront;
	}

	/**
	 * @param tofront
	 *            The tofront to set.
	 */
	public void setTofront(Action tofront) {
		this.tofront = tofront;
	}

	/**
	 * @return Returns the remove.
	 */
	public Action getRemove() {
		return remove;
	}

	/**
	 * @param remove
	 *            The remove to set.
	 */
	public void setRemove(Action remove) {
		this.remove = remove;
	}
}