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

	public BasicAlgorithm(Variable v) {
		view = new Graph();
		self = new Variable(v.id, v.domain, view);
		view.varMap.put(self.id, self);
		for (Constraint c : v.neighbors) {
			Variable n = c.getNeighbor(v);
			Variable nn = new Variable(n.id, n.domain, view);
			nn.fixed = true;
			view.varMap.put(nn.id, nn);
			Constraint cc;
			if (v == c.first)
				cc = new Constraint(self, nn);
			else
				cc = new Constraint(nn, self);
			for (int i = 0; i < cc.d1; i++)
				for (int j = 0; j < cc.d2; j++) {
					cc.f[i][j] = c.f[i][j];
				}
			cc.cache();
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
		return self.value;
	}
	
	public int getID() {
		return self.id;
	}
	
	public boolean isStable() {
		return done;
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
