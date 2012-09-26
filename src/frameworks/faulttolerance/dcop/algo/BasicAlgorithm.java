package frameworks.faulttolerance.dcop.algo;

import java.util.ArrayList;
import java.util.HashMap;

import dima.introspectionbasedagents.modules.faults.Assert;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.daj.Program;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopAbstractGraph;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.exec.Stats;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;

public abstract class BasicAlgorithm extends Program {

	protected static final int reLockInterval = 8;
	protected int lockBase;
	protected int reLockTime;
<<<<<<< HEAD
<<<<<<< HEAD
	protected DcopAbstractGraph view;
	protected AbstractVariable self;
=======
	protected DcopReplicationGraph view;
	protected ReplicationVariable self;
>>>>>>> dcopX
=======
	protected DcopReplicationGraph view;
	protected ReplicationVariable self;
>>>>>>> dcopX
	protected int lock;
	protected boolean done;

	protected HashMap<Integer, Integer> inChannelMap;
	protected HashMap<Integer, Integer> outChannelMap;

	public int preAttempt;
	public ArrayList<Stats> statList;
	public int nlockReq;
	public int preCycles;

<<<<<<< HEAD
<<<<<<< HEAD
	public BasicAlgorithm(AbstractVariable v) {
		view = ReplicationExperimentationParameters.constructDCOPGraph();
		self = new AbstractVariable(v.id, v.domain, view);
		view.varMap.put(self.id, self);
		for (AbstractConstraint c : v.neighbors) {
			AbstractVariable n = c.getNeighbor(v);
			AbstractVariable nn = new AbstractVariable(n.id, n.domain, view);
			nn.fixed = true;
			view.varMap.put(nn.id, nn);
			AbstractConstraint cc;
			if (v == c.getFirst())
				cc = new AbstractConstraint(self, nn);
			else
				cc = new AbstractConstraint(nn, self);
			for (int i = 0; i < cc.d1; i++)
				for (int j = 0; j < cc.d2; j++) {
					cc.f[i][j] = c.f[i][j];
				}
			cc.cache();
=======
=======
>>>>>>> dcopX
	public BasicAlgorithm(ReplicationVariable v) {
		view = DCOPFactory.constructDCOPGraph(v.getSocialWelfare());
		assert v.getState()!=null:v;
		self = DCOPFactory.constructVariable(v.id, v.getDomain(),v.getState(), view.getSocialWelfare());
		view.varMap.put(self.id, self);
		for (MemFreeConstraint c : v.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(v);
			ReplicationVariable nn = DCOPFactory.constructVariable(n.id, n.getDomain(),n.getState(), view.getSocialWelfare());
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
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
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
