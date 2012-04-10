// --------------------------------------------------------------------------
// $Id: Channel.java,v 1.4 1997/11/03 10:23:45 schreine Exp $
// communication link between a sendser process and a receiver process
// a channel buffers any number of messages from the sender process
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package examples.dcop.daj;

import java.util.LinkedList;

import dima.introspectionbasedagents.services.loggingactivity.LogService;


public class Channel implements InChannel, OutChannel {

	// sender and receiver node and message selector
	private Node owner;
	private NodeIdentifier neighbor;
	

	private Selector selector = new SelectorDefault();
	// message queue
	private MessageQueue queue = new MessageQueue();
	
	// set where receiver is listening in multiple receive
	private ChannelSet set = null;
	// index of current thread
	private int index = -1;

	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	private Channel(Node o, NodeIdentifier n) {
		owner = o;
		neighbor = n;
	}

	// --------------------------------------------------------------------------
	// create channel from `sender` node to `receiver` node using `selector`
	// --------------------------------------------------------------------------
	public static void link(Node n1, Node n2) {
		assert n1!=n2 && !n1.equals(n2);
		Channel n1ch = new Channel(n1, n2.getIdentifier());
		Channel n2ch = new Channel(n2, n1.getIdentifier());
		
		n1.inChannel(n1ch);
		n2.outChannel(n2ch);
		
		n1.outChannel(n1ch);
		n2.inChannel(n2ch);
	}

	// --------------------------------------------------------------------------
	// return sender node
	// --------------------------------------------------------------------------
	public Node getOwner() {
		return owner;
	}

	// --------------------------------------------------------------------------
	// return receiver node
	// --------------------------------------------------------------------------
	public NodeIdentifier getNeighbor() {
		return neighbor;
	}

	// --------------------------------------------------------------------------
	// send `msg` to channel
	// --------------------------------------------------------------------------
	public void send(DcopMessage msg) {
		owner.sendMessage(getNeighbor(), msg);
		owner.logMonologue("i've sended "+msg, LogService.onFile);
	}
	

//	// --------------------------------------------------------------------------
//	// signal that thread is going to be blocked on channel
//	// --------------------------------------------------------------------------
	public void receiveBlock() {
//		if (visual != null) {
//			visual.block();
//			NodeVisual nodeVisual = receiver.getVisual();
//			nodeVisual.block();
//			receiver.getNetwork().getVisualizer().setText(
//					"Node " + nodeVisual.getLabel() + " is blocked");
//		}
	}
//
//	// --------------------------------------------------------------------------
//	// signal that thread is not blocked any more on channel
//	// --------------------------------------------------------------------------
	public void receiveAwake() {
//		if (visual != null) {
//			if (queue.isEmpty()) visual.empty();
//			else visual.fill();
//			NodeVisual nodeVisual = receiver.getVisual();
//			nodeVisual.awake();
//			receiver.getNetwork().getVisualizer().setText(
//					"Node " + nodeVisual.getLabel() + " is awake");
//		}
	}

	// --------------------------------------------------------------------------
	// receive message from channel; block thread if channel empty
	// --------------------------------------------------------------------------
	public DcopMessage receive() {
		boolean blocked;

		//
		// we lock the receiver before the channel in order to
		// avoid a race condition between setting index and getting blocked
		//
		synchronized (neighbor) {
			synchronized (this) {
				blocked = (queue.isEmpty());
				if (blocked) {
					receiveBlock();
//					index = scheduler.sleep();
				}
			}
			if (blocked) {
//				try {
////					receiver.wait();
//				}
//				catch (InterruptedException e) {
//					Assertion.fail("InterruptedException");
//				}
				receiveAwake();
			}
		}
		//
		// increase time; must occur before message is selected to avoid
		// inconsistencies in global assertions
		//
//		if (!blocked) scheduler.schedule();
		DcopMessage msg = selector.select(queue);
		owner.logMonologue("i've received "+msg, LogService.onFile);
		return msg;
	}

	// --------------------------------------------------------------------------
	// register on channel set `s` if channel is empty
	// return true iff this was the case
	// --------------------------------------------------------------------------
	synchronized public boolean registerEmpty(ChannelSet s) {
		if (queue.isEmpty()) {
			set = s;
			return true;
		}
		else return false;
	}

	// --------------------------------------------------------------------------
	// returns true iff channel is empty
	// --------------------------------------------------------------------------
	synchronized public boolean isEmpty() {
		return queue.isEmpty();
	}

	// --------------------------------------------------------------------------
	// signal that we do not wait for message any more
	// --------------------------------------------------------------------------
	synchronized public void unregister() {
		set = null;
	}

	// --------------------------------------------------------------------------
	// receive message from channel; do not block but poll at most `n` times
	// if then no message is found, return null
	// --------------------------------------------------------------------------
	public DcopMessage receive(int n) {
		int i = n;
		boolean blocked = false;
		while (queue.isEmpty()) {
			if (i <= 0) {
				if (blocked) receiveAwake();
				return null;
			}
			if (!blocked) {
				blocked = true;
				receiveBlock();
			}
			i--;
		}
		if (blocked) receiveAwake();
		return receive();
	}

	@Override
	public void write(DcopMessage m) {
		queue.enqueue(m);
	}
}
