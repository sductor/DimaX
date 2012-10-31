// ----------------------------------------------------------------------------
// $Id: GlobalAssertion.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// global network assertions
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package frameworks.faulttolerance.olddcop.daj;

public abstract class GlobalAssertion {

	// --------------------------------------------------------------------------
	// returns true iff assertion is fulfilled 
	// `program[i]` contains the program of node with index `i` in network
	//
	// for any program `p`
	// `p.getTime()` return the local time of the corresponding node
	// `p.in()` and `p.out()` yield the set of all input/output channels
	// `p.in(i)` and `p.out(i)` yield particular input/output channels
	// --------------------------------------------------------------------------
	public abstract boolean test(Program program[]);

	// --------------------------------------------------------------------------
	// returns string printed if assertion is violated
	// --------------------------------------------------------------------------
	public String getText() {
		return "(no information)";
	}

	// --------------------------------------------------------------------------
	// return vector of messages currently in `c`
	// sorted in in the (ascending) order in which they were sent
	// --------------------------------------------------------------------------
	static protected Message[] getMessages(InChannel c) {
		return ((Channel) c).getMessages();
	}

	// --------------------------------------------------------------------------
	// return vector of messages currently in `c`
	// sorted in in the (ascending) order in which they were sent
	// --------------------------------------------------------------------------
	static protected Message[] getMessages(OutChannel c) {
		return ((Channel) c).getMessages();
	}
}
