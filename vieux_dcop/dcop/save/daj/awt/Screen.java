// --------------------------------------------------------------------------
// $Id: Screen.java,v 1.9 1997/11/14 16:39:04 schreine Exp schreine $
// visual representation of channel
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package vieux.dcop.save.daj.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import vieux.dcop.save.daj.Application;
import vieux.dcop.save.daj.Assertion;


@SuppressWarnings("serial")
public class Screen extends Panel implements AdjustmentListener, MouseMotionListener,
		MouseListener {

	// the visualizer
	private Visualizer visualizer;
	// coordinates of left upper corner of canvas
	private int xoffset, yoffset;
	// the canvas
	private Panel canvas;
	// the scrollbars
	private Scrollbar hbar, vbar;
	// logical size of screen
	private int xsize, ysize;
	// maximum number , current number, list of nodes
	private int nodeMax = 100;
	private int nodeNum = 0;
	private NodeVisual[] nodes = new NodeVisual[nodeMax];
	// maximum number , current number, list of channels
	private int channelMax = 100;
	private int channelNum = 0;
	private ChannelVisual[] channels = new ChannelVisual[channelMax];
	// an auxiliary window to pop up on demand and its canvas
	private Popup window;
	// true iff window is currently shown
	private boolean windowShown;
	// set to currently focused node respectively channel
	private NodeVisual currentNode;
	private ChannelVisual currentChannel;
	// set to currently selected node for movement
	private NodeVisual selectedNode;
	// current network time
	private int time = 0;
	/* current number of messages */
	public int lastNumMsgs, lastNumBaseMsgs = 0;

	// --------------------------------------------------------------------------
	// create screen for visualizer `visual` of
	// `nodeNum` nodes and `chanNum` channels of size `x`/`y`
	// --------------------------------------------------------------------------
	public Screen(Visualizer visual, int x, int y) {
		// set visualizer
		visualizer = visual;
		// create components
		canvas = new Panel();
		hbar = new Scrollbar(Scrollbar.HORIZONTAL);
		vbar = new Scrollbar(Scrollbar.VERTICAL);
		// set layout and add components
		setLayout(new BorderLayout(0, 0));
		add("Center", canvas);
		add("South", hbar);
		add("West", vbar);
		// set background color of canvas
		canvas.setBackground(Color.white);
		// set size of screen
		xsize = x;
		ysize = y;
		// set coordinates and size of canvas
		xoffset = 0;
		yoffset = 0;
		// Dimension dim = getSize();
		// create window for screen information
		newWindow();
		// register event handlers
		hbar.addAdjustmentListener(this);
		vbar.addAdjustmentListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
	}

	// --------------------------------------------------------------------------
	// create window for additional screen information
	// --------------------------------------------------------------------------
	public void newWindow() {
		window = new Popup(visualizer);
		windowShown = false;
	}

	// --------------------------------------------------------------------------
	// return preferred size for layout manager
	// --------------------------------------------------------------------------
	public Dimension getPreferredSize() {
		// results are initially 0, thus not useable
		return new Dimension(xsize, // +vbar.getSize().width,
				ysize); // +hbar.getSize().height);
	}

	// --------------------------------------------------------------------------
	// return minimum size for layout manager
	// --------------------------------------------------------------------------
	public Dimension getMinimumSize() {
		// results are initially 0, thus not useable
		return new Dimension(xsize, // +vbar.getSize().width,
				ysize); // +hbar.getSize().height);
	}

	// --------------------------------------------------------------------------
	// add node `n` to screen
	// --------------------------------------------------------------------------
	public void add(NodeVisual n) {
		if (nodeNum == nodeMax) {
			NodeVisual oldNodes[] = nodes;
			nodeMax *= 2;
			nodes = new NodeVisual[nodeMax];
			for (int i = 0; i < nodeNum; i++)
				nodes[i] = oldNodes[i];
		}
		nodes[nodeNum] = n;
		nodeNum++;
	}

	// --------------------------------------------------------------------------
	// add channel `c` to screen
	// --------------------------------------------------------------------------
	public void add(ChannelVisual c) {
		if (channelNum == channelMax) {
			ChannelVisual oldChannels[] = channels;
			channelMax *= 2;
			channels = new ChannelVisual[channelMax];
			for (int i = 0; i < channelNum; i++)
				channels[i] = oldChannels[i];
		}
		channels[channelNum] = c;
		channelNum++;
	}

	// --------------------------------------------------------------------------
	// return graphics to paint on
	// --------------------------------------------------------------------------
	public Graphics getGraphics() {
		return canvas.getGraphics();
	}

	// --------------------------------------------------------------------------
	// called when window size is changed, update scrollbars accordingly
	// --------------------------------------------------------------------------
	public synchronized void setBounds(int x, int y, int width, int height) {
		// avoid warnings, can be ignored
		if (width == 0 && height == 0) return;
		// reset offset such that drawing is not clipped on resize
		xoffset = 0;
		yoffset = 0;
		// update screen size, (cannot adjust to scrollbar, see
		// getPreferredSize)
		int xlen = width; // -vbar.getSize().width;
		int ylen = height; // -hbar.getSize().height;
		// update scrollbar values
		hbar.setValues(0, xlen, 0, xsize);
		vbar.setValues(0, ylen, 0, ysize);
		hbar.setBlockIncrement(xlen);
		vbar.setBlockIncrement(ylen);
		// reshape window
		super.setBounds(x, y, width, height);
	}

	// --------------------------------------------------------------------------
	// close popup window
	// --------------------------------------------------------------------------
	public void close() {
		if (windowShown) {
			window.setVisible(false);
			redraw();
			windowShown = false;
		}
	}

	// --------------------------------------------------------------------------
	// called if scrolling event `e` was triggered
	// --------------------------------------------------------------------------
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int offset = e.getValue();
		Object target = e.getSource();
		if (target == hbar) xoffset = offset;
		else {
			Assertion.test(target == vbar, "unknown target");
			yoffset = offset;
		}
		clear();
		redraw();
	}

	// --------------------------------------------------------------------------
	// called if mouse release event `e` was triggered
	// --------------------------------------------------------------------------
	public void mouseReleased(MouseEvent e) {
		Assertion.test(e.getSource() == canvas, "unknown target");
		selectedNode = null;
	}

	// --------------------------------------------------------------------------
	// called if mouse pressing event `e` was triggered
	// --------------------------------------------------------------------------
	public void mousePressed(MouseEvent e) {
		Assertion.test(e.getSource() == canvas, "unknown target");
		selectNode(e.getX(), e.getY());
	}

	// --------------------------------------------------------------------------
	// called if mouse dragging event `e` was triggered
	// --------------------------------------------------------------------------
	public void mouseDragged(MouseEvent e) {
		Assertion.test(e.getSource() == canvas, "unknown target");
		if (selectedNode == null) selectNode(e.getX(), e.getY());
		else moveNode(e.getX(), e.getY());
	}

	// --------------------------------------------------------------------------
	// called if mouse move event `e` was triggered
	// --------------------------------------------------------------------------
	public void mouseMoved(MouseEvent e) {
		Assertion.test(e.getSource() == canvas, "unknown target");
		int x = e.getX();
		int y = e.getY();
		focus(x, y);
		redraw();
	}

	// --------------------------------------------------------------------------
	// other mouse events
	// --------------------------------------------------------------------------
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	// --------------------------------------------------------------------------
	// clears the screen
	// --------------------------------------------------------------------------
	public void clear() {
		getGraphics().clearRect(0, 0, xsize, ysize);
	}

	// --------------------------------------------------------------------------
	// redraws the screen
	// --------------------------------------------------------------------------
	public void redraw() {
		paint(getGraphics());
	}

	// --------------------------------------------------------------------------
	// draws time `t` on screen
	// --------------------------------------------------------------------------
	public void drawTime(int t) {
		Graphics g = getGraphics();
		Application appl = visualizer.getApplication();
		Font font = appl.nodeSmallFont;
		g.setFont(font);
		g.setColor(Color.white);
		int height = getFontMetrics(font).getHeight();
		g.drawString("Global Time: " + String.valueOf(time), 1, height);
		g.setColor(Color.black);
		g.drawString("Global Time: " + String.valueOf(t), 1, height);
		time = t;
	}

	/**
	 * draws the number of non-algorithm messages
	 */
	public void drawNumberOfBaseMessages(int numMsgs) {
		Graphics g = getGraphics();
		Font font = visualizer.getApplication().nodeSmallFont;
		g.setFont(font);
		g.setColor(Color.white);
		int height = getFontMetrics(font).getHeight();
		g.drawString("#BaseMsg: " + String.valueOf(lastNumBaseMsgs), 1, height * 2);
		lastNumBaseMsgs = numMsgs;
		g.setColor(Color.black);
		g.drawString("#BaseMsg: " + String.valueOf(numMsgs), 1, height * 2);
	}

	/**
	 * draws the number of algorithm messages
	 */
	public void drawNumberOfAlgorithmMessages(int numMsgs) {
		Graphics g = getGraphics();
		Font font = visualizer.getApplication().nodeSmallFont;
		g.setFont(font);
		g.setColor(Color.white);
		int height = getFontMetrics(font).getHeight();
		g.drawString("#AlgMsg: " + String.valueOf(lastNumMsgs), 1, height * 3);
		lastNumMsgs = numMsgs;
		g.setColor(Color.black);
		g.drawString("#AlgMsg: " + String.valueOf(numMsgs), 1, height * 3);
	}

	// --------------------------------------------------------------------------
	// draws the screen
	// --------------------------------------------------------------------------
	public void paint(Graphics g) {
		drawTime(time);
		for (int i = 0; i < channelNum; i++) {
			ChannelVisual ch = channels[i];
			ch.draw();
		}
		for (int i = 0; i < nodeNum; i++) {
			nodes[i].draw();
		}
	}

	// --------------------------------------------------------------------------
	// select node at canvas coordinates `x` and `y` for movement
	// --------------------------------------------------------------------------
	private void selectNode(int x, int y) {
		// logical coordinates of focus
		int xpos = x + xoffset;
		int ypos = y + yoffset;
		// select node for movement
		selectedNode = null;
		for (int i = 0; i < nodeNum; i++) {
			NodeVisual node = nodes[i];
			if (node.inside(xpos, ypos)) {
				selectedNode = node;
				return;
			}
		}
	}

	// --------------------------------------------------------------------------
	// move selected node to canvas coordinates `x` and `y` and redraw screen
	// --------------------------------------------------------------------------
	private void moveNode(int x, int y) {
		Assertion.test(selectedNode != null, "no node selected");
		// logical coordinates of focus
		int xpos = x + xoffset;
		int ypos = y + yoffset;
		// update position and redraw
		selectedNode.setPosition(xpos, ypos);
		clear();
		redraw();
	}

	// --------------------------------------------------------------------------
	// determine mouse focus on canvas coordinates `x` and `y`
	// --------------------------------------------------------------------------
	private void focus(int x, int y) {
		// base coordinates of screen
		Point cloc = canvas.getLocationOnScreen();
		int xbase = cloc.x;
		int ybase = cloc.y;
		// logical coordinates of focus
		int xpos = x + xoffset;
		int ypos = y + yoffset;
		// try to display node status
		for (int i = 0; i < nodeNum; i++) {
			NodeVisual node = nodes[i];
			if (node.inside(xpos, ypos)) {
				if (!windowShown || currentNode != node) {
					if (windowShown) {
						window.setVisible(false);
						redraw();
						windowShown = false;
					}
					node.write();
					int nodeRadius = visualizer.getApplication().nodeRadius;
					popup(xbase - xoffset + node.x() + nodeRadius, ybase - yoffset
						+ node.y() + nodeRadius, node.getNode().getText());
					currentNode = node;
					currentChannel = null;
				}
				return;
			}
		}
		// try to display channel contents based on channel bullet
		for (int i = 0; i < channelNum; i++) {
			ChannelVisual channel = channels[i];
			if (channel.insideBullet(xpos, ypos)) {
				if (!windowShown || currentChannel != channel) {
					if (windowShown) {
						window.setVisible(false);
						redraw();
						windowShown = false;
					}
					channel.write();
					// int chRadius = visualizer.getApplication().channelRadius;
					popup(xbase - xoffset + channel.getXPosTube(), ybase - yoffset
						+ channel.getYPosTube(), channel.getChannel().getText());
					currentChannel = channel;
					currentNode = null;
				}
				return;
			}
		}
		// try to display channel contents based on channel tube
		for (int i = 0; i < channelNum; i++) {
			ChannelVisual channel = channels[i];
			if (channel.insideTube(xpos, ypos)) {
				if (!windowShown || currentChannel != channel) {
					if (windowShown) {
						window.setVisible(false);
						redraw();
						windowShown = false;
					}
					channel.write();
					popup(xbase - xoffset + channel.getXPosTube(), ybase - yoffset
						+ channel.getYPosTube(), channel.getChannel().getText());
					currentChannel = channel;
					currentNode = null;
				}
				return;
			}
		}
		// no status to be displayed
		if (windowShown) {
			window.setVisible(false);
			redraw();
			windowShown = false;
			currentNode = null;
			currentChannel = null;
			visualizer.setText("");
		}
	}

	// --------------------------------------------------------------------------
	// write `text` separated by newlines into window positioned at `xpos/ypos`
	// --------------------------------------------------------------------------
	private void popup(int xpos, int ypos, String text) {
		// this is necessary to overcome an apparent AWT applet window resize
		// bug
		boolean isApplet = visualizer.getApplication().isApplet();
		if (isApplet) newWindow();
		// let window popup
		window.popup(xpos, ypos, text);
		// window is shown now
		windowShown = true;
	}

	// --------------------------------------------------------------------------
	// return offset
	// --------------------------------------------------------------------------
	public int getXOffset() {
		return xoffset;
	}

	// --------------------------------------------------------------------------
	// return offset
	// --------------------------------------------------------------------------
	public int getYOffset() {
		return yoffset;
	}
}
