package frameworks.faulttolerance.dcop.algo;

import java.util.ArrayList;
import java.util.HashMap;

import dima.introspectionbasedagents.modules.faults.Assert;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.daj.Program;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.dcop.exec.Stats;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;

public abstract class BasicAlgorithm extends Program {

	protected static final int reLockInterval = 8;
	protected int lockBase;
	protected int reLockTime;
	protected DcopReplicationGraph view;
	protected ReplicationVariable self;
	protected int lock;
	protected boolean done;

	protected HashMap<Integer, Integer> inChannelMap;
	protected HashMap<Integer, Integer> outChannelMap;

	public int preAttempt;
	public ArrayList<Stats> statList;
	public int nlockReq;
	public int preCycles;

	public BasicAlgorithm(ReplicationVariable v) {
		view = DCOPFactory.constructDCOPGraph(v.getSocialWelfare());
		assert v.getState()!=null:v;
		self = DCOPFactory.constructVariable(v.id, v.getDomain(),v.getState(), view);
		view.varMap.put(self.id, self);
		for (MemFreeConstraint c : v.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(v);
			ReplicationVariable nn = DCOPFactory.constructVariable(n.id, n.getDomain(),n.getState(), view);
			nn.fixed = true;
			view.varMap.put(nn.id, nn);
			MemFreeConstraint cc;
			if (v == c.first)
				cc = DCOPFactory.constructConstraint(self, nn);
			else
				cc = DCOPFactory.constructConstraint(nn, self);
			if (DCOPFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					for (int j = 0; j < cc.d2; j++) {
						((CPUFreeConstraint)cc).f[i][j] = ((CPUFreeConstraint)c).f[i][j];
					}
				((CPUFreeConstraint)cc).cache();
			}
			view.conList.add(cc);
		}

		lock = -1;
		lockBase = 1;
		reLockTime = Helper.random.nextInt(reLockInterval * lockBase * 4);
		done = false;

		inChannelMap = new HashMap<Integer, Integer>();
		outChannelMap = new HashMap<Integer, Integer>();
		statList = new ArrayList<Stats>();
		preAttempt = 0;		
		preCycles = 0;
		nlockReq = 0;

	}

	public int getValue(){
		return self.getValue();
	}

	public int getID() {
		return self.id;
	}

	public boolean isStable() {
		return done;
	}

	protected boolean checkStable() {
		Program[] prog = this.node.getNetwork().getPrograms();
		for (int i = 0; i < prog.length; i++) {
			if (!(prog[i] instanceof BasicAlgorithm))
				continue;
			BasicAlgorithm p = (BasicAlgorithm) prog[i];
			if (!p.done)
				return false;
		}
		return true;
	}
	@Override
	abstract protected void main();
}
