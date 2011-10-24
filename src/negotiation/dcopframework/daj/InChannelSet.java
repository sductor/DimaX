// ----------------------------------------------------------------------------
// $Id: InChannelSet.java,v 1.3 1997/10/31 07:46:45 schreine Exp $
// collections of input channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package negotiation.dcopframework.daj;

public class InChannelSet extends ChannelSet {

	// --------------------------------------------------------------------------
	// add `channel` to set
	// --------------------------------------------------------------------------
	public void addChannel(InChannel channel) {
		super.addChannel((Channel) channel, true);
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	public InChannel getChannel(int i) {
		return channel(i);
	}
}
