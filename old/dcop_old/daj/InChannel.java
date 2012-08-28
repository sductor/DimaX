// ----------------------------------------------------------------------------
// $Id: InChannel.java,v 1.3 1997/11/03 10:23:45 schreine Exp $
// channels to receive messages from
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop_old.daj;

public interface InChannel {

	// --------------------------------------------------------------------------
	// receive message from channel; block thread if channel is empty
	// --------------------------------------------------------------------------
	public DCOPMessage receive();

	// --------------------------------------------------------------------------
	// receive message from channel; do not block but poll at most `n` times
	// if then no message is found, return null
	// --------------------------------------------------------------------------
	public DCOPMessage receive(int n);
}
