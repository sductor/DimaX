package frameworks.faulttolerance.olddcop.algo;

import java.util.HashMap;
import java.util.HashSet;

import frameworks.faulttolerance.olddcop.algo.topt.LockMsg;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;

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

	public LockingBasicAlgorithm(ReplicationVariable v, boolean s, int ws) {
		super(v);		
		subsetlocking = s;
		windowsize = ws;
		lockSet = new HashMap<Integer, Integer>();
		lockMap = new HashMap<Integer, LockMsg>();
		acceptSet = new HashSet<Integer>();
		lockVal = -1;		
		waiting = false;
		changed = false;
		lockMsgTimer = 0;
		attempt = 0;
	}
	public void removeLock(int id) {
		lockMap.remove(id);
		lockSet.remove(id);
		if (lockSet.isEmpty())
			lockVal = -1;
	}
}
