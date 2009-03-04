package com.uvt.wf.designer.gui.editor;

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;

// A Custom Model that does not allow Self-References
public class MyModel extends DefaultGraphModel {
	// Override Superclass Method
	public boolean acceptsSource(Object edge, Object port) {
		// Source only Valid if not Equal Target
		return (((Edge) edge).getTarget() != port);
	}

	// Override Superclass Method
	public boolean acceptsTarget(Object edge, Object port) {
		// Target only Valid if not Equal Source
		return (((Edge) edge).getSource() != port);
	}
}