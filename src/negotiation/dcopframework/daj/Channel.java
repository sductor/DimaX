package negotiation.dcopframework.daj;

import java.util.LinkedList;

public class Channel implements InChannel, OutChannel {

	private final Node owner;
	private final Integer neighbor;

	// set where receiver is listening in multiple receive
	private ChannelSet set = null;

	private final  boolean ownerSend;

	LinkedList<Message> receivedMessages;

	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	public Channel(final Node s, final Integer r) {
		this.owner = s;
		this.neighbor = r;
		this.ownerSend = true;
	}

	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	public Channel(final Integer s, final Node r) {
		this.owner = r;
		this.neighbor = s;
		this.ownerSend = false;
	}
	//
	// Accessors
	//


	// --------------------------------------------------------------------------
	// return sender node
	// --------------------------------------------------------------------------
	public Integer getSender() {
		return this.ownerSend?this.owner.getID():this.neighbor;
	}

	// --------------------------------------------------------------------------
	// return receiver node
	// --------------------------------------------------------------------------
	public Integer getReceiver() {
		return this.ownerSend?this.neighbor:this.owner.getID();
	}

	//
	// Methods
	//

	@Override
	public void send(final Message msg) {
		msg.setSender(this.getSender());
		this.owner.getMyDarxNode().sendAsyncMessage(this.neighbor, msg);

	}

	@Override
	public Message receive() {
		return this.receivedMessages.pop();
	}

	@Override
	public Message receive(final int n) {
		//Toujours utilisé avec 1!!!!
		return this.receivedMessages.pop();
	}

	//
	// Primitive
	//

	public void addMessage(final Message msg){
		this.receivedMessages.add(msg);
	}

	// --------------------------------------------------------------------------
	// returns true iff channel is empty
	// --------------------------------------------------------------------------
	public boolean isEmpty() {
		return this.receivedMessages.isEmpty();
	}

	// --------------------------------------------------------------------------
	// signal that thread is going to be blocked on channel
	// --------------------------------------------------------------------------
	public void receiveBlock() {
		//		if (visual != null) {
		//			visual.block();
		//			NodeVisual nodeVisual = receiver.getVisual();
		//			nodeVisual.block();
		//			receiver.getNetwork().getVisualizer().setText(
		//					"Node " + nodeVisual.getLabel() + " is blocked");
		//		}
	}

	// --------------------------------------------------------------------------
	// signal that thread is not blocked any more on channel
	// --------------------------------------------------------------------------
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
	// register on channel set `s` if channel is empty
	// return true iff this was the case
	// --------------------------------------------------------------------------
	public boolean registerEmpty(final ChannelSet s) {
		if (this.isEmpty()) {
			this.set = s;
			return true;
		}
		else return false;
	}

	// --------------------------------------------------------------------------
	// signal that we do not wait for message any more
	// --------------------------------------------------------------------------
	public void unregister() {
		this.set = null;
	}


}


