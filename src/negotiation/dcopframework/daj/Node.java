// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package negotiation.dcopframework.daj;

import java.io.Serializable;

import negotiation.dcopframework.algo.BasicAlgorithm;
import darx.DarxCommInterface;
import darx.DarxException;
import darx.DarxTask;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;


public class Node extends Thread {

	// channels to receive messages from respectively send messages to
	private final InChannelSet in = new InChannelSet();
	private final OutChannelSet out = new OutChannelSet();
	private final Program program; // program that node executes
	private int switches; // number of context switches occurred so far

	AgentIdentifier observer;
	final DarxNode myDarxNode;

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(final Program prog, final AgentIdentifier observer) {
		this.program = prog;
		this.program.setNode(this);
		this.switches = 0;
		this.myDarxNode = new DarxNode();
		this.observer = observer;
	}

	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		this.switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return this.switches;
	}

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@Override
	synchronized public void run() {
		try {
			this.wait();
		}
		catch (final InterruptedException e) {
			Assertion.fail("InterruptedException");
		}
		// decrease priority to make sure applet thread gets updates
		this.setPriority(this.getPriority() - 1);
		this.program.main();

		this.myDarxNode.terminate();
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(final Channel channel) {
		this.in.addChannel(channel, true);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(final Channel channel) {
		this.out.addChannel(channel, false);
	}

	// --------------------------------------------------------------------------
	// return visual representation of node
	// --------------------------------------------------------------------------
	public Integer getID(){
		return ((BasicAlgorithm) this.getProgram()).getID();
	}

	public DarxNode getMyDarxNode() {
		return this.myDarxNode;
	}

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return this.program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return this.program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return this.in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return this.out;
	}

	//
	// Subclass
	//


	class DarxNode extends DarxTask{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7290036211203787743L;

		protected DarxNode() {
			super(Node.this.getID().toString());
		}

		protected  DarxCommInterface comm;

		public DarxCommInterface getComm() {
			return this.comm;
		}

		//
		// Primitive
		//

		public void sendAsyncMessage(final Integer i, final Message m) {

			final AgentIdentifier id = new AgentName(i.toString());
			RemoteTask remote = null;
			try {
				remote = this.findTask(id.toString());
			}
			catch(final DarxException e) {
				System.out.println("Getting " + id + " from nameserver failed : " + e);
				return;
			}

			if(remote != null)
				this.getComm().sendAsyncMessage(remote, (Serializable) m);
			else
				throw new RuntimeException(this+" Echec de l'envoi du message"+m);
		}

		//		public Object sendSyncMessage(Integer i, final Message m) {
		//
		//			final AgentIdentifier id = new AgentName(i.toString());
		//			RemoteTask remote = null;
		//			try {
		//				remote = findTask(id.toString());
		//			}
		//			catch(final DarxException e) {
		//				System.out.println("Getting " + id + " from nameserver failed : " + e);
		//				return null;
		//			}
		//
		//			if(remote != null)
		//				return getComm().sendSyncMessage(remote, (Serializable) m);
		//			else
		//				throw new RuntimeException(this+" Echec de l'envoi du message"+m);
		//
		//		}

		/*
		 * Message Handling
		 */

		/**
		 * Put the message
		 * received from DarX in the agent mailbox
		 *
		 * @param msg
		 *            the message, that should be cast in Message
		 * @see Message
		 */
		@Override
		public void receiveAsyncMessage(final Object msg) {
			if (msg instanceof Message)
				((Channel) Node.this.in.getChannel(((Message) msg).getSender())).addMessage((Message) msg);
			else
				LogService.writeException(this, msg+" is not a message : can not be added to mail box!");
		}

		//		/**
		//		 * UNIMPLEMENTED : Execute the task and return the results
		//		 *
		//		 * @param msg
		//		 *            the message, that should be cast in Message
		//		 * @see Message
		//		 */
		//		@Override
		//		public Serializable receiveSyncMessage(final Object msg) {
		//			this.receiveAsyncMessage(msg);
		//			return null;
		//		}
		//
	}

}