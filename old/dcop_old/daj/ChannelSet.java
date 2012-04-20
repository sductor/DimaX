// ----------------------------------------------------------------------------
// $Id: ChannelSet.java,v 1.5 1997/11/03 10:23:45 schreine Exp $
// collections of channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop_old.daj;

import java.util.ArrayList;
import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;

import examples.dcop_old.algo.BasicAlgorithm;
import examples.dcop_old.algo.topt.ValueMsg;
import examples.dcop_old.api.DCOPLaborantin;
import examples.dcop_old.dcop.Graph;

public class ChannelSet {

	// channel set
	ArrayList<Channel> channels = new ArrayList<Channel>();
	HashMap<NodeIdentifier, Integer> neighbor2chanNum = new HashMap<NodeIdentifier, Integer>();
	Node myAgent;

	public ChannelSet(Node ag){
		myAgent=ag;
	}

	// --------------------------------------------------------------------------
	// add `channel` to set
	// if `receiver`, receiver nodes must be the same, otherwise sender nodes
	// --------------------------------------------------------------------------
	public void addChannel(Channel channel) {
		assert channel!=null;
		assert myAgent.getIdentifier().equals(channel.getOwner().getIdentifier()):myAgent+" "+channel.getOwner();
		assert !neighbor2chanNum.containsKey(channel.getNeighborIdentifier());

		channels.add(channel);
		neighbor2chanNum.put(channel.getNeighborIdentifier(), channels.size()-1);
	}

	public ArrayList<Channel> getChannels() {
		return channels;
	}
	// --------------------------------------------------------------------------
	// return number of channels in set
	// --------------------------------------------------------------------------
	public int getSize() {
		return channels.size();
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	protected Channel channel(int i) {
		return channels.get(i);
	}

	public InChannel getChannel(NodeIdentifier neighbor){
		assert neighbor2chanNum!=null;
		assert neighbor!=null;
		assert neighbor2chanNum.containsKey(neighbor):neighbor2chanNum+" "+neighbor;

		Channel result = channels.get(neighbor2chanNum.get(neighbor));

		assert result.getNeighborIdentifier().equals(neighbor);
		assert result.getOwner().equals(channels.get(0).getOwner());

		return result;
	}
	// --------------------------------------------------------------------------
	// broadcast `message` to all channels in set
	// --------------------------------------------------------------------------
	//	public void send(DCOPMessage message) {
	//		for (int i = 0; i < setNum; i++) {
	//			set[i].send(message);
	//		}
	//	}

	public void broadcast(DCOPMessage message) {
		for (Channel c : channels) {
			assert senderValidtiyVerif(c, message);
			c.send(message);
		}
		//		set[0].getSender().getNetwork().getScheduler().schedule();
	}
	
	private boolean senderValidtiyVerif(Channel c , DCOPMessage msg) {
		assert c.getOwner().getIdentifier().equals(myAgent.getIdentifier());
		assert c.getOwner().equals(myAgent);
		assert Graph.constraintExist(DCOPLaborantin.g, myAgent.getIdentifier().getAsInt(), c.getNeighbor());
		if (msg instanceof ValueMsg) {
			ValueMsg vmsg = (ValueMsg) msg;
//			assert myAgent.getIdentifier().getAsInt().equals(vmsg.getId()) || c.getNeighbor().equals(vmsg.getId()):myAgent+" "+vmsg.getId()+" "+c.getNeighbor();
//			assert Graph.constraintExist(DCOPLaborantin.g, myAgent.getIdentifier().getAsInt(), vmsg.getId())|| Graph.constraintExist(DCOPLaborantin.g, vmsg.getId(), c.getNeighbor()):c.getNeighbor()+" "+vmsg.getId();
			assert ((BasicAlgorithm)myAgent.program).view.varMap.containsKey(vmsg.getId());
		}
		return true;
	}
	//	// --------------------------------------------------------------------------
	//	// return index of non-empty channel in set
	//	// --------------------------------------------------------------------------
	//	public int select() {
	//		Assertion.test(setNum != 0, "channel set is empty");
	//		Node receiver;
	//		synchronized (this) {
	//			for (int i = 0; i < setNum; i++) {
	//				if (!set[i].registerEmpty(this)) {
	//					for (int j = 0; j < i; j++)
	//						set[j].unregister();
	//					return i;
	//				}
	//			}
	//			for (int i = 0; i < setNum; i++) {
	//				set[i].receiveBlock();
	//			}
	//			receiver = set[0].getReceiver();
	//			Scheduler scheduler = receiver.getNetwork().getScheduler();
	//			int index = scheduler.sleep();
	//			try {
	//				wait();
	//			}
	//			catch (InterruptedException e) {
	//				Assertion.fail("InterruptedException");
	//			}
	//			for (int i = 0; i < setNum; i++) {
	//				set[i].unregister();
	//				set[i].receiveAwake();
	//			}
	//			scheduler.awake(index);
	//		}
	//		//
	//		// there is a small race condition between making the thread active
	//		// and getting blocked; thus we might run into a deadlock
	//		//
	//		synchronized (receiver) {
	//			try {
	//				receiver.wait();
	//			}
	//			catch (InterruptedException e) {
	//				Assertion.fail("InterruptedException");
	//			}
	//		}
	//		// must not dequeue message before being blocked
	//		// in order to avoid inconsistencies in global network conditions
	//		for (int i = 0; i < setNum; i++) {
	//			if (!set[i].isEmpty()) return i;
	//		}
	//		Assertion.fail("no message delivered");
	//		return -1;
	//	}

	// --------------------------------------------------------------------------
	// return index of non-empty channel in set; do not block but poll at most 
	// `n` times; if then no message is found, -1 is returned
	// --------------------------------------------------------------------------
	public int select(int n) {
		assert channels.size()>0: "channel set is empty";
		for (int i = 0; i < channels.size(); i++){
			if (!channels.get(i).isEmpty())
				return i;
		}
		return -1;
	}



	//		
	//		assert setNum != 0: "channel set is empty";
	//		boolean blocked = false;
	//		for (int j = 0; j < n; j++) {
	//			for (int i = 0; i < setNum; i++) {
	//				if (!set[i].isEmpty()) {
	//					if (blocked) {
	//						for (int k = 0; k < setNum; k++)
	//							set[k].receiveAwake();
	//					}
	//					return i;
	//				}
	//			}
	//			if (!blocked) {
	//				blocked = true;
	//				for (int k = 0; k < setNum; k++)
	//					set[k].receiveBlock();
	//			}
	//			
	////			Scheduler scheduler = set[0].getReceiver().getNetwork().getScheduler();
	////			scheduler.schedule();
	//			
	//		}
	//		if (blocked) {
	//			for (int k = 0; k < setNum; k++)
	//				set[k].receiveAwake();
	//		}
	//		return -1;
	//	}
}
