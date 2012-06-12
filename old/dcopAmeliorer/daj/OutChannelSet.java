// ----------------------------------------------------------------------------
// $Id: OutChannelSet.java,v 1.3 1997/10/31 07:46:45 schreine Exp $
// collections of output channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcopAmeliorer.daj;

public class OutChannelSet extends ChannelSet {

	// --------------------------------------------------------------------------
	// add `channel` to set
	// --------------------------------------------------------------------------
	public void addChannel(final OutChannel channel) {
		super.addChannel((Channel) channel);
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	public OutChannel getChannel(final int i) {
		return this.channel(i);
	}

}
