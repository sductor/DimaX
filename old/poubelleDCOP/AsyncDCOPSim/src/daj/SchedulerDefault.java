// ----------------------------------------------------------------------------
// $Id: SchedulerDefault.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// default scheduler (round robin)
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package daj;

public class SchedulerDefault extends Scheduler {

	private int last = -1;

	// --------------------------------------------------------------------------
	// return index of next program for execution (-1, if none)
	// --------------------------------------------------------------------------
	public int nextProgram() {
		int n = getNumber();
		boolean reset = false;
		do {
			incTime();
			last++;
			if (last == n) {
				last = 0;
				if (reset) return -1;
				reset = true;
			}
		}
		while (!isReady(last));
		return last;
	}
}
