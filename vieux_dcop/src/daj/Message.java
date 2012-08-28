// ----------------------------------------------------------------------------
// $Id: Message.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// objects exchanged between processes
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.src.daj;

public class Message {

	// --------------------------------------------------------------------------
	// return string denoting message content
	// --------------------------------------------------------------------------
	public String getText() {
		return ("(no information)");
	}
	
	public int getSize() {
		return 0;
	}
}
