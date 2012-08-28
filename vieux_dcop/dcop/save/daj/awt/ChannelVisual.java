// --------------------------------------------------------------------------
// $Id: ChannelVisual.java,v 1.4 1997/11/14 16:39:04 schreine Exp schreine $
// visual representation of channel
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package vieux.dcop.save.daj.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import vieux.dcop.save.daj.Application;
import vieux.dcop.save.daj.Assertion;
import vieux.dcop.save.daj.Channel;
import vieux.dcop.save.daj.Node;


public class ChannelVisual {

	// corresponding channel
	private Channel channel;
	// polygon representing channel
	private Polygon polygon;
	// like before but in virtual coordinates
	private Polygon tube;
	// point representing intersection of channel and receiver node
	private int xpoint;
	private int ypoint;
	// like before but in virtual coordinates
	private int xbullet;
	private int ybullet;
	// point representing middle of tube in virtual coordinates
	private int xmid;
	private int ymid;
	// its fill state
	private int state;
	public final static int stateEmpty = 0;
	public final static int stateWait = 1;
	public final static int stateFull = 2;

	// --------------------------------------------------------------------------
	// create channel visualizing `c`
	// --------------------------------------------------------------------------
	public ChannelVisual(Channel c) {
		channel = c;
		state = stateEmpty;
		compute();
	}

	// --------------------------------------------------------------------------
	// return actual channel
	// --------------------------------------------------------------------------
	public Channel getChannel() {
		return channel;
	}

	// --------------------------------------------------------------------------
	// compute derived values for channel representation
	// --------------------------------------------------------------------------
	private void compute() {
		// determine sender and receiver coordinates
		NodeVisual rvis = channel.getReceiver().getVisual();
		NodeVisual svis = channel.getSender().getVisual();
		int xrecv = rvis.x();
		int yrecv = rvis.y();
		int xsend = svis.x();
		int ysend = svis.y();
		// determine basic values
		int dx = xrecv - xsend;
		int dy = yrecv - ysend;
		int s = 0;
		try {
			Integer i = new Integer(dx * dx + dy * dy);
			s = (int) Math.round(Math.sqrt(i.doubleValue()));
			if (s == 0) s = 1;
		}
		catch (ArithmeticException e) {
			Assertion.fail("arithmetic exception");
		}
		// determine screen
		Screen screen = channel.getReceiver().getNetwork().getVisualizer().getScreen();
		// determine screen offsets
		int xoff = screen.getXOffset();
		int yoff = screen.getYOffset();
		// determine applicatiion parameters
		Application appl = channel.getReceiver().getNetwork().getApplication();
		int nodeRadius = appl.nodeRadius;
		int width = appl.channelWidth;
		// int radius = appl.channelRadius;
		// determine point where channel touches receiver node
		xbullet = xrecv - (dx * 87 - dy * 50) * nodeRadius / (100 * s);
		ybullet = yrecv - (dx * 50 + dy * 87) * nodeRadius / (100 * s);
		xpoint = xbullet - xoff;
		ypoint = ybullet - yoff;
		// determine point where channel touches sender node
		int xsend0 = xsend + nodeRadius * dx / s;
		int ysend0 = ysend + nodeRadius * dy / s;
		// construct channel polygon
		int a = width * dy / s;
		int b = width * dx / s;
		int x[] = new int[4];
		int y[] = new int[4];
		x[0] = xsend0 - a;
		y[0] = ysend0 + b;
		x[1] = xbullet - a;
		y[1] = ybullet + b;
		x[2] = xbullet + a;
		y[2] = ybullet - b;
		x[3] = xsend0 + a;
		y[3] = ysend0 - b;
		tube = new Polygon(x, y, 4);
		x[0] -= xoff;
		y[0] -= yoff;
		x[1] -= xoff;
		y[1] -= yoff;
		x[2] -= xoff;
		y[2] -= yoff;
		x[3] -= xoff;
		y[3] -= yoff;
		polygon = new Polygon(x, y, 4);
		// determine middle of poligon
		xmid = (x[0] + x[2]) / 2;
		ymid = (y[0] + y[2]) / 2;
	}

	// --------------------------------------------------------------------------
	// return x coordinate of bullet
	// --------------------------------------------------------------------------
	public int getXPosBullet() {
		return xbullet;
	}

	// --------------------------------------------------------------------------
	// return y coordinate of bullet
	// --------------------------------------------------------------------------
	public int getYPosBullet() {
		return ybullet;
	}

	// --------------------------------------------------------------------------
	// return true iff point `x/y` is within bullet
	// --------------------------------------------------------------------------
	public boolean insideBullet(int x, int y) {
		int xd = x - xbullet;
		int yd = y - ybullet;
		Application appl = channel.getReceiver().getNetwork().getApplication();
		int radius = appl.channelRadius;
		return (xd * xd + yd * yd <= radius * radius);
	}

	// --------------------------------------------------------------------------
	// return x coordinate of tube center
	// --------------------------------------------------------------------------
	public int getXPosTube() {
		return xmid;
	}

	// --------------------------------------------------------------------------
	// return y coordinate of tube center
	// --------------------------------------------------------------------------
	public int getYPosTube() {
		return ymid;
	}

	// --------------------------------------------------------------------------
	// return true iff point `x/y` is within tube
	// --------------------------------------------------------------------------
	public boolean insideTube(int x, int y) {
		return tube.contains(x, y);
	}

	// --------------------------------------------------------------------------
	// return the channel state
	// --------------------------------------------------------------------------
	public int getState() {
		return state;
	}

	// --------------------------------------------------------------------------
	// fill the channel
	// --------------------------------------------------------------------------
	public void fill() {
		state = stateFull;
		draw();
	}

	// --------------------------------------------------------------------------
	// let somebody wait on channel
	// --------------------------------------------------------------------------
	public void block() {
		state = stateWait;
		draw();
	}

	// --------------------------------------------------------------------------
	// empty the channel
	// --------------------------------------------------------------------------
	public void empty() {
		state = stateEmpty;
		draw();
	}

	// --------------------------------------------------------------------------
	// draw the channel depending on its state
	// --------------------------------------------------------------------------
	public void draw() {
		// determine state change
		Color chanColor = null;
		// boolean wait = false;
		switch (state) {
			case stateEmpty: {
				chanColor = Color.lightGray;
				break;
			}
			case stateWait: {
				chanColor = Color.red;
				break;
			}
			case stateFull: {
				chanColor = Color.green;
				break;
			}
			default: {
				Assertion.fail("invalid node state");
				break;
			}
		}
		// compute drawing coordinates
		compute();
		// get receiver node
		Node receiver = channel.getReceiver();
		// where to draw
		Screen screen = receiver.getNetwork().getVisualizer().getScreen();
		// determine screen graphics
		Graphics g = screen.getGraphics();
		// determine application parameter
		Application appl = receiver.getNetwork().getApplication();
		int radius = appl.channelRadius;
		// draw channel
		g.setColor(Color.lightGray);
		g.fillPolygon(polygon);
		g.setColor(chanColor);
		g.drawPolygon(polygon);
		g.fillOval(xpoint - radius, ypoint - radius, radius * 2, radius * 2);
		// draw receiver node
		receiver.getVisual().draw();
	}

	// --------------------------------------------------------------------------
	// write current status into visualizer window
	// --------------------------------------------------------------------------
	public void write() {
		String sender = channel.getSender().getVisual().getLabel();
		String receiver = channel.getReceiver().getVisual().getLabel();
		Visualizer visualizer = channel.getReceiver().getNetwork().getVisualizer();
		switch (state) {
			case stateEmpty: {
				visualizer.setText("Channel from node " + sender + " to node " + receiver
					+ " is empty");
				break;
			}
			case stateWait: {
				visualizer.setText("Node " + receiver + " is blocked on channel to node "
					+ sender);
				break;
			}
			case stateFull: {
				visualizer.setText("Channel from node " + sender + " to node " + receiver
					+ " contains messages");
				break;
			}
			default: {
				Assertion.fail("invalid node state");
				break;
			}
		}
	}
}
