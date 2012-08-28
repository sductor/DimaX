package negotiation.dcopframework.daj;


import java.util.HashMap;
import java.util.LinkedList;

import negotiation.dcopframework.algo.BasicAlgorithm;

public class ChanneledMailBox {

	//
	// Fields
	//
	
	private final Node myNode;
	private final HashMap<Integer, InChannel> inchannels = new HashMap<Integer, InChannel>();
	private final HashMap<Integer, OutChannel> outchannels = new HashMap<Integer, OutChannel>();
	
	//
	// Constructor
	//
	
	public ChanneledMailBox(Node myNode) {
		super();
		this.myNode = myNode;
	}

	//
	// Accessors
	//

	public void addChannel(Integer i ){
		inchannels.put(i, new InChannelMailBox(i));
		outchannels.put(i, new OutChannelMailBox(((BasicAlgorithm) myNode.getProgram()).getID()));
	}
	
	public InChannel getInChannel(Integer i) {
		return inchannels.get(i);
	}

	public OutChannel getOutChannel(Integer i) {
		return outchannels.get(i);
	}
	//
	// Methods
	//
	
	public void broadcast(Message message) {
		for (OutChannel o : outchannels.values()) {
			o.send(message);
		}
	}
	
	//
	// Subclasses
	//
	
	public class InChannelMailBox extends LinkedList<Message> implements InChannel {

		private final Integer senderID;
		
		
		public InChannelMailBox(Integer senderID) {
			super();
			this.senderID = senderID;
		}

		public Integer getSender() {
			return senderID;
		}

		@Override
		public Message receive() {
			return this.pop();
		}

		@Override
		public Message receive(int n) {
			//Toujours utilisé avec 1!!!!
			return this.pop();
		}		
	}
	
	public class OutChannelMailBox implements OutChannel {

		private final Integer progID;
		
		public OutChannelMailBox(Integer progID) {
			super();
			this.progID = progID;
		}

		@Override
		public void send(Message msg) {
			myNode.sendAsyncMessage(progID, msg);			
		}
		
	}
}
