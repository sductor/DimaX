package examples.dcop.daj;

import java.util.LinkedList;

import dima.basicagentcomponents.AgentName;

public class Channel implements InChannel, OutChannel {

	private Node owner;
	private Integer neighbor;

	// set where receiver is listening in multiple receive
	private ChannelSet set = null;
	
	private final  boolean ownerSend;
	
	LinkedList<DCOPMessage> receivedMessages;


	// --------------------------------------------------------------------------
	// create channel from `sender` node to `receiver` node using `selector`
	// --------------------------------------------------------------------------
	public static void link(Node sender, Node receiver) {
		sender.outChannel(new Channel(sender, receiver.getID()));		
		receiver.inChannel(new Channel(receiver, sender.getID()));
	}
	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	public Channel(Node s, Integer r) {
		owner = s;
		neighbor = r;
		ownerSend = true;
	}

	// --------------------------------------------------------------------------
	// new channel with sender `sr` and receiver `r` using `selector`
	// --------------------------------------------------------------------------
	public Channel(Integer s, Node r) {
		owner = r;
		neighbor = s;
		ownerSend = false;
	}
	//
	// Accessors
	//
	

	// --------------------------------------------------------------------------
	// return sender node
	// --------------------------------------------------------------------------
	public Integer getSender() {
		return ownerSend?owner.getID():neighbor;
	}

	// --------------------------------------------------------------------------
	// return receiver node
	// --------------------------------------------------------------------------
	public Integer getReceiver() {
		return ownerSend?neighbor:owner.getID();
	}
	
	//
	// Methods
	//
	
	@Override
	public void send(DCOPMessage msg) {
		msg.setSender(getSender());
		owner.sendMessage(new AgentName(neighbor.toString()), msg);	
		
	}

	@Override
	public DCOPMessage receive() {
		return receivedMessages.pop();
	}

	@Override
	public DCOPMessage receive(int n) {
		//Toujours utilisé avec 1!!!!
		return receivedMessages.pop();
	}
		
	//
	// Primitive
	//

	public void addMessage(DCOPMessage msg){
		receivedMessages.add(msg);
	}
	
	// --------------------------------------------------------------------------
	// returns true iff channel is empty
	// --------------------------------------------------------------------------
	public boolean isEmpty() {
		return receivedMessages.isEmpty();
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
	public boolean registerEmpty(ChannelSet s) {
		if (isEmpty()) {
			set = s;
			return true;
		}
		else return false;
	}

	// --------------------------------------------------------------------------
	// signal that we do not wait for message any more
	// --------------------------------------------------------------------------
	public void unregister() {
		set = null;
	}
	

}


