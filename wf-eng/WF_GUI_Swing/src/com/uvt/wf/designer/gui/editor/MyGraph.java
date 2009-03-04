package com.uvt.wf.designer.gui.editor;

import java.awt.Color;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

//
// Custom Graph
//

// Defines a Graph that uses the Shift-Button (Instead of the Right
// Mouse Button, which is Default) to add/remove point to/from an edge.
public class MyGraph extends JGraph {

	// Construct the Graph using the Model as its Data Source
	public MyGraph(GraphModel model) {
		this(model, null);
	}

	// Construct the Graph using the Model as its Data Source
	public MyGraph(GraphModel model, GraphLayoutCache cache) {
		super(model, cache);
		// Make Ports Visible by Default
		setPortsVisible(true);
		// Use the Grid
		setGridEnabled(true);
		setGridVisible(true);
		setGridColor(Color.BLUE);
		// Set the Grid Size to 10 Pixel
		setGridSize(6);
		// Set the Tolerance to 2 Pixel
		setTolerance(2);
		// Accept edits if click on background
		setInvokesStopCellEditing(true);
		// Allows control-drag
		setCloneable(true);
		// Jump to default port on connect
		setJumpToDefaultPort(true);
	}
}