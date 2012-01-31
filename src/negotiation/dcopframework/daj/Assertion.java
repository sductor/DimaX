// --------------------------------------------------------------------------
// $Id: Assertion.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// assertion handling
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package negotiation.dcopframework.daj;

public class Assertion {

	// --------------------------------------------------------------------------
	// print `s` to stderr and terminate
	// --------------------------------------------------------------------------
	public static void fail(final String s) {
		System.err.println("assertion failed: " + s);
		System.exit(-1);
	}

	// --------------------------------------------------------------------------
	// check condition `c`; if false, print `s` to stderr and terminate
	// --------------------------------------------------------------------------
	public static void test(final boolean c, final String s) {
		if (!c) Assertion.fail(s);
	}
}
