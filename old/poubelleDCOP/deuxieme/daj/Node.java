// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package negotiation.dcopframework.daj;

import darx.DarxCommInterface;
import darx.DarxException;
import darx.DarxTask;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;

public class Node extends Thread {
	
	// channels to receive messages from respectively send messages to
//	private InChannelSet in = new InChannelSet();
//	private OutChannelSet out = new OutChannelSet();
//	private Network network; // network of which node is part of
	private Program program; // program that node executes
//	private NodeVisual visual; // visual representation of node
	private int switches; // number of context switches occurred so far
	ChanneledMailBox mailBox;
	protected  DarxCommInterface comm;
	protected DarxTask task;

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(Program prog) {
		super(prog.toString());
//		Visualizer visualizer = net.getVisualizer();
//		Assertion.test(visualizer == null, "missing node visualization info");
		init(prog);
	}

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// and add it to visualizer giving it `label` and position `x/y`
	// --------------------------------------------------------------------------
	public Node(Program prog, String label, int x, int y) {
		super(prog.toString());
//		Visualizer visualizer = net.getVisualizer();
//		if (visualizer != null) {
//			Assertion.test(visual == null, "conflicting node visualization");
//			visual = new NodeVisual(this, label, x, y);
//			visualizer.getScreen().add(visual);
//		}
		init(prog);
	}

	// --------------------------------------------------------------------------
	// initialize node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	private void init(Program prog) {
//		network = net;
//		network.add(this);
		program = prog;
		program.setNode(this);
		switches = 0;
		mailBox = new ChanneledMailBox(this);
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
//		if (visual != null) visual.terminate();
//		network.terminate(this);
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

	

	public void sendAsyncMessage(Integer i, final Message m) {

		final AgentIdentifier id = new AgentName(i.toString());
		RemoteTask remote = null;
		try {
			remote = task.findTask(id.toString());
		}
		catch(final DarxException e) {
			System.out.println("Getting " + id + " from nameserver failed : " + e);
			return;
		}

		if(remote != null)
			this.comm.sendAsyncMessage(remote, m);
		else
			throw new RuntimeException(this+" Echec de l'envoi du message"+m);

	}
	

	public Object sendASyncMessage(Integer i, final Message m) {

		final AgentIdentifier id = new AgentName(i.toString());
		RemoteTask remote = null;
		try {
			remote = task.findTask(id.toString());
		}
		catch(final DarxException e) {
			System.out.println("Getting " + id + " from nameserver failed : " + e);
			return null;
		}

		if(remote != null)
			return this.comm.sendSyncMessage(remote, m);
		else
			throw new RuntimeException(this+" Echec de l'envoi du message"+m);

	}

	public ChanneledMailBox getMailBox() {
		return mailBox;
	}
}
//class DiCOPmaXNodeTask extends DarXAdvancedTask {
//
//	
//	private Node myNode;
//	
//	public DiCOPmaXNodeTask(String name) {
//		super(name);
//	}
//
//	public void setNode(Node diCOPmaXNode) {
//		this.myNode = diCOPmaXNode;
//		
//	}
//	
//	/**
//	 * Put the message
//	 * received from DarX in the agent mailbox
//	 *
//	 * @param msg
//	 *            the message, that should be cast in Message
//	 * @see Message
//	 */
//	@Override
//	public void receiveAsyncMessage(final Object msg) {
//			if (msg instanceof Message)
//				myNode.getIn().getChannel((Message) msg.);
//			else
//				LoggerManager.writeException(this, msg+" is not a message : can not be added to mail box!");
//
//	}
//}