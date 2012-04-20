package examples.dcop_old.daj;

import java.util.ArrayList;
import java.util.LinkedList;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import examples.dcop_old.algo.BasicAlgorithm;
import examples.dcop_old.algo.topt.ValueMsg;
import examples.dcop_old.api.DCOPLaborantin;
import examples.dcop_old.dcop.Graph;

public class Channel implements InChannel, OutChannel {

	private Node owner;
	private NodeIdentifier neighbor;
		
	LinkedList<DCOPMessage> receivedMessages = new LinkedList<DCOPMessage>();


	// --------------------------------------------------------------------------
	// create channel from `sender` node to `receiver` node using `selector`
	// --------------------------------------------------------------------------
	public static void link2ways(Node node1, Node node2) {
		Channel node1chan = new Channel(node1, node2.getIdentifier());
		node1.outChannel(node1chan);
		node1.inChannel(node1chan);	

		Channel node2chan = new Channel(node2, node1.getIdentifier());
		node2.outChannel(node2chan);
		node2.inChannel(node2chan);
	}
	// --------------------------------------------------------------------------
	// new channel with owner (as sender and receiver) and neighbor :	
	// --------------------------------------------------------------------------
	public Channel(Node owner, NodeIdentifier neighbor) {
		assert !owner.getIdentifier().equals(neighbor);
		assert owner!=null;
		assert neighbor!=null;
		assert Graph.constraintExist(DCOPLaborantin.g, owner.getIdentifier().getAsInt(), neighbor.getAsInt());
		assert ((BasicAlgorithm)owner.program).view.varMap.containsKey(neighbor.getAsInt());
		this.owner = owner;
		this.neighbor = neighbor;
	}
//
//	// Accessors
//	//
//	
//
//	// --------------------------------------------------------------------------
//	// return sender node
//	// --------------------------------------------------------------------------
//	public Integer getSender() {
//		return ownerSend?owner.getID():neighbor;
//	}
//
//	// --------------------------------------------------------------------------
//	// return receiver node
//	// --------------------------------------------------------------------------
//	public Integer getReceiver() {
//		return ownerSend?neighbor:owner.getID();
//	}
//	
	public Node getOwner() {
		return owner;
	}
	public Integer getNeighbor() {
		return neighbor.getAsInt();
	}

	public NodeIdentifier getNeighborIdentifier() {
		return neighbor;
	}
	//
	// Methods
	//
	
	@Override
	public void send(DCOPMessage msg) {		
		msg.setSender(owner.getIdentifier());
		msg.setReceiver(neighbor);

		assert senderValidtiyVerif(msg);
		
		owner.sendMessage(neighbor, msg);		

		assert senderValidtiyVerif(msg);
		
	}
	private boolean senderValidtiyVerif(DCOPMessage msg) {
		assert msg.getSender().equals(owner.getIdentifier());
		if (msg instanceof ValueMsg) {
			ValueMsg vmsg = (ValueMsg) msg;
//			assert 
//			owner.getIdentifier().getAsInt().equals(vmsg.getId()) 
//			|| getNeighbor().equals(vmsg.getId())
//			:owner.getIdentifier()+" "+vmsg.getId();
//			assert 
//			Graph.constraintExist(DCOPLaborantin.g, vmsg.getId(), owner.getIdentifier().getAsInt()) 
//			|| Graph.constraintExist(DCOPLaborantin.g, vmsg.getId(), getNeighbor())
//			:getNeighbor()+" "+vmsg.getId();
			assert ((BasicAlgorithm)owner.program).view.varMap.containsKey(vmsg.getId());
//			assert ((BasicAlgorithm)DCOPLaborantin.nodeMap.get(getNeighbor()).program).view.varMap.containsKey(vmsg.getId());
		}
		return true;
	}

	@Override
	public DCOPMessage receive() {
		return receivedMessages.pop();
	}

	@Override
	public DCOPMessage receive(int n) {
		assert n==1;
		return receivedMessages.pop();
	}
		
	//
	// Primitive
	//

	public void addMessage(DCOPMessage msg){
		
		assert Graph.constraintExist(DCOPLaborantin.g, owner.getIdentifier().getAsInt(), msg.getSender().getAsInt());
		assert ((BasicAlgorithm)owner.program).view.varMap.containsKey(msg.getSender().getAsInt());
		
		assert msg.getSender().equals(neighbor);	
		
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
//	public void receiveBlock() {
//		if (visual != null) {
//			visual.block();
//			NodeVisual nodeVisual = receiver.getVisual();
//			nodeVisual.block();
//			receiver.getNetwork().getVisualizer().setText(
//					"Node " + nodeVisual.getLabel() + " is blocked");
//		}
//	}

	// --------------------------------------------------------------------------
	// signal that thread is not blocked any more on channel
	// --------------------------------------------------------------------------
//	public void receiveAwake() {
//		if (visual != null) {
//			if (queue.isEmpty()) visual.empty();
//			else visual.fill();
//			NodeVisual nodeVisual = receiver.getVisual();
//			nodeVisual.awake();
//			receiver.getNetwork().getVisualizer().setText(
//					"Node " + nodeVisual.getLabel() + " is awake");
//		}
//	}
	
//	// --------------------------------------------------------------------------
//	// register on channel set `s` if channel is empty
//	// return true iff this was the case
//	// --------------------------------------------------------------------------
//	public boolean registerEmpty(ChannelSet s) {
//		if (isEmpty()) {
//			set = s;
//			return true;
//		}
//		else return false;
//	}
//
//	// --------------------------------------------------------------------------
//	// signal that we do not wait for message any more
//	// --------------------------------------------------------------------------
//	public void unregister() {
//		set = null;
//	}
	

}


