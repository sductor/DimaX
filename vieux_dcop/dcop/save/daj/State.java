// --------------------------------------------------------------------------
// $Id: State.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// global network states
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package vieux.dcop.save.daj;

public abstract class State {

	// --------------------------------------------------------------------------
	// assert global state of network constructed from
	// programs in `prog` and channels `chan`
	// --------------------------------------------------------------------------
	public abstract void test(Program prog[], Channel chan[]);
}
