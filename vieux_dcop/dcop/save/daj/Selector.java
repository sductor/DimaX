// ----------------------------------------------------------------------------
// $Id: Selector.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// interface for selectors of messages to be delivered
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.dcop.save.daj;

public interface Selector {

	// --------------------------------------------------------------------------
	// return next message to be delivered from non-empty queue and update queue
	// --------------------------------------------------------------------------
	public Message select(MessageQueue queue);
}
