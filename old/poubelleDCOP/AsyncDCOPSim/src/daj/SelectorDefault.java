// ----------------------------------------------------------------------------
// $Id: SelectorDefault.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// default selector for messages to be delivered
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package daj;

public class SelectorDefault implements Selector {

	// --------------------------------------------------------------------------
	// return next message from queue
	// --------------------------------------------------------------------------
	public Message select(MessageQueue queue) {
		return queue.dequeue();
	}
}
