// ----------------------------------------------------------------------------
// $Id: ChannelSet.java,v 1.5 1997/11/03 10:23:45 schreine Exp $
// collections of channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop.daj;

import java.util.Collection;
import java.util.HashMap;

public class ChannelSet {

	public HashMap<NodeIdentifier, Channel> neighborChannel =
			new HashMap<NodeIdentifier, Channel>();
	
	// --------------------------------------------------------------------------
	// add `channel` to set
	// if `receiver`, receiver nodes must be the same, otherwise sender nodes
	// --------------------------------------------------------------------------
	public void addChannel(Channel channel) {
		assert !neighborChannel.containsKey(channel.getNeighbor());
		
		neighborChannel.put(channel.getNeighbor(), channel);
	}

	// --------------------------------------------------------------------------
	// return number of channels in set
	// --------------------------------------------------------------------------
	public int getSize() {
		return neighborChannel.size();
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	Channel channel(int i) {
		assert neighborChannel.containsKey(new NodeIdentifier(i));
		return neighborChannel.get(new NodeIdentifier(i));
	}

	// --------------------------------------------------------------------------
	// broadcast `message` to all channels in set
	// --------------------------------------------------------------------------
	public void broadcast(DcopMessage message) {
		for (Channel c : neighborChannel.values()) {
			c.send(message);
		}
	}


	// --------------------------------------------------------------------------
	// return index of non-empty channel in set; do not block but poll at most 
	// `n` times; if then no message is found, -1 is returned
	// --------------------------------------------------------------------------
	public int select(int n) {
		assert n==1;
		Assertion.test(!neighborChannel.isEmpty(), "channel set is empty");
		boolean blocked = false;
		for (int j = 0; j < n; j++) {
			for (Channel c : neighborChannel.values()) {
				if (!c.isEmpty()) {
					if (blocked) {
						for (Channel c2 : neighborChannel.values())
							c2.receiveAwake();
					}
					return c.getNeighbor().asInt();
				}
			}
			if (!blocked) {
				blocked = true;
				for (Channel c : neighborChannel.values())
					c.receiveBlock();
			}
						
		}
		if (blocked) {
			for (Channel c : neighborChannel.values())
				c.receiveAwake();
		}
		return -1;
	}
	

	public Collection<Channel> getChannels() {
		return neighborChannel.values();
	}
}
