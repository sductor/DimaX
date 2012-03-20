package negotiation.dcopframework.algo;

import java.util.ArrayList;
import java.util.HashMap;

import negotiation.dcopframework.daj.Program;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;
import negotiation.dcopframework.dcop.Variable;
import negotiation.dcopframework.exec.Stats;

public abstract class BasicAlgorithm extends Program {

	protected static final int reLockInterval = 8;
	protected int lockBase;
	protected int reLockTime;
	protected Graph view;
	protected Variable self;
	protected int lock;
	protected boolean done;

	protected HashMap<Integer, Integer> inChannelMap;
	protected HashMap<Integer, Integer> outChannelMap;

	public int preAttempt;
	public ArrayList<Stats> statList;
	public int nlockReq;
	public int preCycles;

	public BasicAlgorithm(final Variable v) {
		this.view = new Graph();
		this.self = new Variable(v.id, v.domain, this.view);
		this.view.varMap.put(this.self.id, this.self);
		for (final Constraint c : v.neighbors) {
			final Variable n = c.getNeighbor(v);
			final Variable nn = new Variable(n.id, n.domain, this.view);
			nn.fixed = true;
			this.view.varMap.put(nn.id, nn);
			Constraint cc;
			if (v == c.first) {
				cc = new Constraint(this.self, nn);
			} else {
				cc = new Constraint(nn, this.self);
			}
			for (int i = 0; i < cc.d1; i++) {
				for (int j = 0; j < cc.d2; j++) {
					cc.f[i][j] = c.f[i][j];
				}
			}
			cc.cache();
			this.view.conList.add(cc);
		}

		this.lock = -1;
		this.lockBase = 1;
		this.reLockTime = Helper.random.nextInt(BasicAlgorithm.reLockInterval * this.lockBase * 4);
		this.done = false;

		this.inChannelMap = new HashMap<Integer, Integer>();
		this.outChannelMap = new HashMap<Integer, Integer>();
		this.statList = new ArrayList<Stats>();
		this.preAttempt = 0;
		this.preCycles = 0;
		this.nlockReq = 0;

	}

	public int getValue(){
		return this.self.value;
	}

	public int getID() {
		return this.self.id;
	}

	public boolean isStable() {
		return this.done;
	}


	public void incrementApplicationWastedCycles(){

		//		DCOPApplication app = (DCOPApplication) this.node
		//				.getNetwork().getApplication();
		//		app.wastedCycles++;
	}

	public void incrementApplicationNumberOfConflict(){
		//		DCOPApplication app = (DCOPApplication) this.node
		//				.getNetwork().getApplication();
		//		app.numberConflicts++;
	}





	@Override
	abstract protected void main();
}
