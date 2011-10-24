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
import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;


public class Node extends Thread {

	// channels to receive messages from respectively send messages to
	private InChannelSet in = new InChannelSet();
	private OutChannelSet out = new OutChannelSet();
	private Program program; // program that node executes
	private int switches; // number of context switches occurred so far

	AgentIdentifier observer;
	final DarxNode myDarxNode;

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(Program prog, AgentIdentifier observer) {
		program = prog;
		program.setNode(this);
		switches = 0;
		myDarxNode = new DarxNode();
		this.observer = observer;
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
	@Override
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

		myDarxNode.terminate();
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
	public Integer getID(){
		return ((BasicAlgorithm) getProgram()).getID();
	}

	public DarxNode getMyDarxNode() {
		return myDarxNode;
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

	//
	// Subclass
	//


	class DarxNode extends DarxTask{

		protected DarxNode() {
			super(getID().toString());
		}

		protected  DarxCommInterface comm;

		public DarxCommInterface getComm() {
			return comm;
		}

		//
		// Primitive
		//
		
		public void sendAsyncMessage(Integer i, final Message m) {

			final AgentIdentifier id = new AgentName(i.toString());
			RemoteTask remote = null;
			try {
				remote = findTask(id.toString());
			}
			catch(final DarxException e) {
				System.out.println("Getting " + id + " from nameserver failed : " + e);
				return;
			}

			if(remote != null)
				getComm().sendAsyncMessage(remote, (Serializable) m);
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
					((Channel) in.getChannel(((Message) msg).getSender())).addMessage((Message) msg); 
				else
					LogCompetence.writeException(this, msg+" is not a message : can not be added to mail box!");
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