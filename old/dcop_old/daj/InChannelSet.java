// ----------------------------------------------------------------------------
// $Id: InChannelSet.java,v 1.3 1997/10/31 07:46:45 schreine Exp $
// collections of input channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop_old.daj;

import java.util.HashMap;

public class InChannelSet extends ChannelSet {

	
	public InChannelSet(Node id) {
		super(id);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set
	// --------------------------------------------------------------------------
	public void addChannel(InChannel channel) {
		super.addChannel((Channel) channel);
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	public InChannel getChannel(int i) {
		return channel(i);
	}

}
