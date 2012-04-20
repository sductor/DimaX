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
	public void addChannel(final Channel channel) {
		assert !this.neighborChannel.containsKey(channel.getNeighbor());

		this.neighborChannel.put(channel.getNeighbor(), channel);
	}

	// --------------------------------------------------------------------------
	// return number of channels in set
	// --------------------------------------------------------------------------
	public int getSize() {
		return this.neighborChannel.size();
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	Channel channel(final int i) {
		assert this.neighborChannel.containsKey(new NodeIdentifier(i));
		return this.neighborChannel.get(new NodeIdentifier(i));
	}

	// --------------------------------------------------------------------------
	// broadcast `message` to all channels in set
	// --------------------------------------------------------------------------
	public void broadcast(final DcopMessage message) {
		for (final Channel c : this.neighborChannel.values()) {
			c.send(message);
		}
	}


	// --------------------------------------------------------------------------
	// return index of non-empty channel in set; do not block but poll at most
	// `n` times; if then no message is found, -1 is returned
	// --------------------------------------------------------------------------
	public int select(final int n) {
		assert n==1;
		Assertion.test(!this.neighborChannel.isEmpty(), "channel set is empty");
		boolean blocked = false;
		for (int j = 0; j < n; j++) {
			for (final Channel c : this.neighborChannel.values()) {
				if (!c.isEmpty()) {
					if (blocked) {
						for (final Channel c2 : this.neighborChannel.values()) {
							c2.receiveAwake();
						}
					}
					return c.getNeighbor().asInt();
				}
			}
			if (!blocked) {
				blocked = true;
				for (final Channel c : this.neighborChannel.values()) {
					c.receiveBlock();
				}
			}

		}
		if (blocked) {
			for (final Channel c : this.neighborChannel.values()) {
				c.receiveAwake();
			}
		}
		return -1;
	}


	public Collection<Channel> getChannels() {
		return this.neighborChannel.values();
	}
}
