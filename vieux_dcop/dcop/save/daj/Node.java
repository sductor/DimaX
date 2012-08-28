// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.dcop.save.daj;

import vieux.dcop.save.daj.awt.NodeVisual;
import vieux.dcop.save.daj.awt.Visualizer;

public class Node extends Thread {

	// channels to receive messages from respectively send messages to
	private InChannelSet in = new InChannelSet();
	private OutChannelSet out = new OutChannelSet();
	private Network network; // network of which node is part of
	private Program program; // program that node executes
	private NodeVisual visual; // visual representation of node
	private int switches; // number of context switches occurred so far

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(Network net, Program prog) {
		Visualizer visualizer = net.getVisualizer();
		Assertion.test(visualizer == null, "missing node visualization info");
		init(net, prog);
	}

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// and add it to visualizer giving it `label` and position `x/y`
	// --------------------------------------------------------------------------
	public Node(Network net, Program prog, String label, int x, int y) {
		Visualizer visualizer = net.getVisualizer();
		if (visualizer != null) {
			Assertion.test(visual == null, "conflicting node visualization");
			visual = new NodeVisual(this, label, x, y);
			visualizer.getScreen().add(visual);
		}
		init(net, prog);
	}

	// --------------------------------------------------------------------------
	// initialize node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	private void init(Network net, Program prog) {
		network = net;
		network.add(this);
		program = prog;
		program.setNode(this);
		switches = 0;
	}

	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return switches;
	}

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	synchronized public void run() {
		try {
			wait();
		}
		catch (InterruptedException e) {
			Assertion.fail("InterruptedException");
		}
		// decrease priority to make sure applet thread gets updates
		setPriority(getPriority() - 1);
		program.main();
		if (visual != null) visual.terminate();
		network.terminate(this);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(Channel channel) {
		in.addChannel(channel, true);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(Channel channel) {
		out.addChannel(channel, false);
	}

	// --------------------------------------------------------------------------
	// return visual representation of node
	// --------------------------------------------------------------------------
	public NodeVisual getVisual() {
		return visual;
	}

	// --------------------------------------------------------------------------
	// return network of node
	// --------------------------------------------------------------------------
	public Network getNetwork() {
		return network;
	}

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return out;
	}
}
