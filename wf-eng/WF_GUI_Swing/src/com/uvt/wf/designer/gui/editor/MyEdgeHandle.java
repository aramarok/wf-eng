package com.uvt.wf.designer.gui.editor;

import java.awt.event.MouseEvent;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphContext;

//
// Custom Edge Handle
//

// Defines a EdgeHandle that uses the Shift-Button (Instead of the Right
// Mouse Button, which is Default) to add/remove point to/from an edge.
public class MyEdgeHandle extends EdgeView.EdgeHandle {

	/**
	 * @param edge
	 * @param ctx
	 */
	public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
		super(edge, ctx);
	}

	// Override Superclass Method
	public boolean isAddPointEvent(MouseEvent event) {
		// Points are Added using Shift-Click
		return event.isShiftDown();
	}

	// Override Superclass Method
	public boolean isRemovePointEvent(MouseEvent event) {
		// Points are Removed using Shift-Click
		return event.isShiftDown();
	}

}
