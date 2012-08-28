// --------------------------------------------------------------------------
// $Id: Channel.java,v 1.4 1997/11/03 10:23:45 schreine Exp $
// communication link between a sendser process and a receiver process
// a channel buffers any number of messages from the sender process
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package negotiation.dcopframework.daj;

import negotiation.dcopframework.daj.awt.ChannelVisual;
import negotiation.dcopframework.daj.awt.NodeVisual;
import negotiation.dcopframework.daj.awt.Visualizer;
import negotiation.dcopframework.exec.DCOPApplication;

public class Channel implements InChannel, OutChannel {

	// sender and receiver node and message selector
	private Node sender;
	private Node receiver;
	private Selector selector;
	// message queue
	private MessageQueue queue = new MessageQueue();
	// set where receiver is listening in multiple receive
	private ChannelSet set = null;
	// index of current thread
	private int index = -1;
	// associated visualizer
	private ChannelVisual visual;

	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	public Channel(Node s, Node r, Selector sel) {
		sender = s;
		receiver = r;
		selector = sel;
	}

	// --------------------------------------------------------------------------
	// create channel from `sender` node to `receiver` node with default selector
	// --------------------------------------------------------------------------
	public static void link(Node sender, Node receiver) {
		Selector sel = receiver.getNetwork().getApplication().defaultSelector;
		link(sender, receiver, sel);
	}

	// --------------------------------------------------------------------------
	// create channel from `sender` node to `receiver` node using `selector`
	// --------------------------------------------------------------------------
	public static void link(Node sender, Node receiver, Selector sel) {
		Network network = sender.getNetwork();
		if (network != receiver.getNetwork())
			Assertion.fail("nodes belong to different network");
		Channel ch = new Channel(sender, receiver, sel);
		sender.outChannel(ch);
		receiver.inChannel(ch);
		Visualizer visualizer = network.getVisualizer();
		if (visualizer == null) return;
		ch.visual = new ChannelVisual(ch);
		visualizer.getScreen().add(ch.visual);
	}

	// --------------------------------------------------------------------------
	// return sender node
	// --------------------------------------------------------------------------
	public Node getSender() {
		return sender;
	}

	// --------------------------------------------------------------------------
	// return receiver node
	// --------------------------------------------------------------------------
	public Node getReceiver() {
		return receiver;
	}

	// --------------------------------------------------------------------------
	// return content of channel messages as concatentation of substrings
	// separated by newlines
	// --------------------------------------------------------------------------
	public String getText() {
		MessageCell cell = queue.getCell();
		if (cell == null) return "(empty)";
		String string = "";
		do {
			string = string + cell.getMessage().getText() + "\n";
			cell = cell.getNext();
		}
		while (cell != null);
		return string;
	}

	// --------------------------------------------------------------------------
	// send `msg` to channel
	// --------------------------------------------------------------------------
	public void send(Message msg) {
		//
		// increase time; must take place before enqeueing message
		// in order not to invalidate network assertions
		//
		Application app = sender.getNetwork().getApplication();
		if (app instanceof DCOPApplication) {
			((DCOPApplication)app).floatingMessage(msg);
		}
		Scheduler scheduler = sender.getNetwork().getScheduler();
//		scheduler.schedule();
		ChannelSet set0 = null;
		synchronized (this) {
			if (queue.isEmpty() && visual != null) visual.fill();
			queue.enqueue(msg);
		}
		synchronized (this) {
			set0 = set;
			if (set0 == null) {
				if (index != -1) {
					int i = index;
					index = -1;
					scheduler.awake(i);
				}
				return;
			}
		}
		while (true) {
			synchronized (set0) {
				synchronized (this) {
					if (set == null) return;
					set.notify();
					Thread.yield();
				}
			}
			Thread.yield();
		}
	}
	
	public void inlineSend(Message msg) {
		//
		// increase time; must take place before enqeueing message
		// in order not to invalidate network assertions
		//
		Scheduler scheduler = sender.getNetwork().getScheduler();
//		scheduler.schedule();
		ChannelSet set0 = null;
		synchronized (this) {
			if (queue.isEmpty() && visual != null) visual.fill();
			queue.enqueue(msg);
		}
		synchronized (this) {
			set0 = set;
			if (set0 == null) {
				if (index != -1) {
					int i = index;
					index = -1;
					scheduler.awake(i);
				}
				return;
			}
		}
		synchronized (set0) {
			synchronized (this) {
				if (set == null) return;
				set.notify();
			}
		}
	}

	// --------------------------------------------------------------------------
	// signal that thread is going to be blocked on channel
	// --------------------------------------------------------------------------
	public void receiveBlock() {
		if (visual != null) {
			visual.block();
			NodeVisual nodeVisual = receiver.getVisual();
			nodeVisual.block();
			receiver.getNetwork().getVisualizer().setText(
					"Node " + nodeVisual.getLabel() + " is blocked");
		}
	}

	// --------------------------------------------------------------------------
	// signal that thread is not blocked any more on channel
	// --------------------------------------------------------------------------
	public void receiveAwake() {
		if (visual != null) {
			if (queue.isEmpty()) visual.empty();
			else visual.fill();
			NodeVisual nodeVisual = receiver.getVisual();
			nodeVisual.awake();
			receiver.getNetwork().getVisualizer().setText(
					"Node " + nodeVisual.getLabel() + " is awake");
		}
	}

	// --------------------------------------------------------------------------
	// receive message from channel; block thread if channel empty
	// --------------------------------------------------------------------------
	public Message receive() {
		boolean blocked;
		Scheduler scheduler = receiver.getNetwork().getScheduler();
		//
		// we lock the receiver before the channel in order to
		// avoid a race condition between setting index and getting blocked
		//
		synchronized (receiver) {
			synchronized (this) {
				blocked = (queue.isEmpty());
				if (blocked) {
					receiveBlock();
					index = scheduler.sleep();
				}
			}
			if (blocked) {
				try {
					receiver.wait();
				}
				catch (InterruptedException e) {
					Assertion.fail("InterruptedException");
				}
				receiveAwake();
			}
		}
		//
		// increase time; must occur before message is selected to avoid
		// inconsistencies in global assertions
		//
//		if (!blocked) scheduler.schedule();
		Message msg = selector.select(queue);
		if (queue.isEmpty() && visual != null) visual.empty();
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
	public Message receive(int n) {
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
			Scheduler scheduler = receiver.getNetwork().getScheduler();
			scheduler.schedule();
		}
		if (blocked) receiveAwake();
		return receive();
	}

	// --------------------------------------------------------------------------
	// return vector of messages in channel
	// --------------------------------------------------------------------------
	public Message[] getMessages() {
		return queue.getMessages();
	}
	
	public int getNumberOfMessage() {
		return queue.getSize();
	}
}
