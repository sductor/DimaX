// --------------------------------------------------------------------------
// $Id: Assertion.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// assertion handling
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package frameworks.dcop.daj;

public class Assertion {

	// --------------------------------------------------------------------------
	// print `s` to stderr and terminate
	// --------------------------------------------------------------------------
	public static void fail(String s) {
		System.err.println("assertion failed: " + s);
		System.exit(-1);
	}

	// --------------------------------------------------------------------------
	// check condition `c`; if false, print `s` to stderr and terminate
	// --------------------------------------------------------------------------
	public static void test(boolean c, String s) {
		if (!c) fail(s);
	}
}
