// --------------------------------------------------------------------------
// $Id: NodeVisual.java,v 1.3 1997/11/14 16:39:04 schreine Exp schreine $
// visual representation of channel
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package frameworks.dcop.daj.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import frameworks.dcop.daj.Application;
import frameworks.dcop.daj.Assertion;
import frameworks.dcop.daj.Node;



public class NodeVisual {

	// associated node
	private Node node;
	// position and text label 
	private int xpos;
	private int ypos;
	private String label;
	// its state
	private int state;
	private final static int stateInit = 0;
	private final static int stateRun = 1;
	private final static int stateBlocked = 2;
	private final static int stateTerminated = 3;

	// --------------------------------------------------------------------------
	// create node visualizing `n` to be drawn with label `l` and center `x`/`y`
	// --------------------------------------------------------------------------
	public NodeVisual(Node n, String l, int x, int y) {
		node = n;
		label = l;
		xpos = x;
		ypos = y;
		state = stateInit;
	}

	// --------------------------------------------------------------------------
	// return `x` position of node
	// --------------------------------------------------------------------------
	public int x() {
		return xpos;
	}

	// --------------------------------------------------------------------------
	// return `y` position of node
	// --------------------------------------------------------------------------
	public int y() {
		return ypos;
	}

	// --------------------------------------------------------------------------
	// set position of node to `x/y`
	// --------------------------------------------------------------------------
	public void setPosition(int x, int y) {
		xpos = x;
		ypos = y;
	}

	// --------------------------------------------------------------------------
	// return actual node
	// --------------------------------------------------------------------------
	public Node getNode() {
		return node;
	}

	// -------------------------------------------------------------------------
	// block the node
	// --------------------------------------------------------------------------
	public void block() {
		state = stateBlocked;
		draw();
	}

	// --------------------------------------------------------------------------
	// awake the node
	// --------------------------------------------------------------------------
	public void awake() {
		state = stateRun;
		draw();
	}

	// --------------------------------------------------------------------------
	// terminate the node
	// --------------------------------------------------------------------------
	public void terminate() {
		state = stateTerminated;
		draw();
		node.getNetwork().print("Node " + label + " is terminated");
	}

	// --------------------------------------------------------------------------
	// draw the node depending on its state
	// --------------------------------------------------------------------------
	public void draw() {
		// determine state change
		Color color = null;
		switch (state) {
		case stateInit:
		case stateRun: {
			color = Color.green;
			break;
		}
		case stateBlocked: {
			color = Color.red;
			break;
		}
		case stateTerminated: {
			color = Color.blue;
			break;
		}
		default: {
			Assertion.fail("invalid node state");
			break;
		}
		}
		// where to draw
		Screen screen = node.getNetwork().getVisualizer().getScreen();
		// determine drawing parameters
		Application appl = node.getNetwork().getApplication();
		Font font = appl.nodeNormalFont;
		Font sub = appl.nodeSmallFont;
		int radius = appl.nodeRadius;
		// determine screen values
		Graphics g = screen.getGraphics();
		int xoff = screen.getXOffset();
		int yoff = screen.getYOffset();
		// draw node
		g.setColor(Color.lightGray);
		g.fillOval(xpos - radius - xoff, ypos - radius - yoff, radius * 2, radius * 2);
		g.setColor(color);
		g.drawOval(xpos - radius - xoff, ypos - radius - yoff, radius * 2, radius * 2);
		g.drawOval(xpos - radius + 1 - xoff, ypos - radius + 1 - yoff, radius * 2 - 2,
				radius * 2 - 2);
		g.setColor(Color.black);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(label);
		int height = metrics.getHeight();
		int descent = metrics.getDescent();
		g.drawString(label, xpos - width / 2 - xoff, ypos + height / 2 - descent - yoff);
		g.setFont(sub);
		g.drawString(String.valueOf(node.getSwitches()), xpos - xoff + width / 2, ypos
			- yoff + height - descent);
		g.setFont(font);
	}

	// --------------------------------------------------------------------------
	// write current status into visualizer window
	// --------------------------------------------------------------------------
	public void write() {
		Visualizer visualizer = node.getNetwork().getVisualizer();
		switch (state) {
		case stateInit: {
			visualizer.setText("Node " + label + " is in initial state.");
			break;
		}
		case stateRun: {
			visualizer.setText("Node " + label + " is running.");
			break;
		}
		case stateBlocked: {
			visualizer.setText("Node " + label + " is blocked.");
			break;
		}
		case stateTerminated: {
			visualizer.setText("Node " + label + " is terminated.");
			break;
		}
		default: {
			Assertion.fail("invalid node state");
			break;
		}
		}
	}

	// --------------------------------------------------------------------------
	// returns true iff coordinates `x/y` are within node
	// --------------------------------------------------------------------------
	public boolean inside(int x, int y) {
		int x0 = x - xpos;
		int y0 = y - ypos;
		Application appl = node.getNetwork().getApplication();
		int radius = appl.nodeRadius;
		return (x0 * x0 + y0 * y0 <= radius * radius);
	}

	// --------------------------------------------------------------------------
	// returns label of node
	// --------------------------------------------------------------------------
	public String getLabel() {
		return label;
	}
}
