package frameworks.faulttolerance.dcop.algo;

import java.util.HashMap;
import java.util.HashSet;

import frameworks.faulttolerance.dcop.algo.topt.LockMsg;
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
>>>>>>> dcopX

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

<<<<<<< HEAD
	public LockingBasicAlgorithm(AbstractVariable v, boolean s, int ws) {
=======
	public LockingBasicAlgorithm(ReplicationVariable v, boolean s, int ws) {
>>>>>>> dcopX
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
