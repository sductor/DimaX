package vieux.dcopAmeliorer.algo;

import java.util.HashMap;
import java.util.HashSet;

import vieux.dcopAmeliorer.algo.topt.LockMsg;
import vieux.dcopAmeliorer.dcop.Variable;


public abstract class LockingBasicAlgorithm extends BasicAlgorithm {

	public HashMap<Integer, LockMsg> lockMap;
	public HashMap<Integer, Integer> lockSet;
	public HashSet<Integer> acceptSet;
	public boolean subsetlocking;
	public int windowsize;
	public boolean waiting;
	public boolean changed;
	public int lockVal;
	public int lockMsgTimer;
	public int attempt;

	public LockingBasicAlgorithm(final Variable v, final boolean s, final int ws) {
		super(v);
		this.subsetlocking = s;
		this.windowsize = ws;
		this.lockSet = new HashMap<Integer, Integer>();
		this.lockMap = new HashMap<Integer, LockMsg>();
		this.acceptSet = new HashSet<Integer>();
		this.lockVal = -1;
		this.waiting = false;
		this.changed = false;
		this.lockMsgTimer = 0;
		this.attempt = 0;
	}
	public void removeLock(final int id) {
		this.lockMap.remove(id);
		this.lockSet.remove(id);
		if (this.lockSet.isEmpty()) {
			this.lockVal = -1;
		}
	}
}
