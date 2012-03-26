package examples.dcop.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import examples.dcop.api.Stats;
import examples.dcop.daj.Program;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Graph;
import examples.dcop.dcop.Variable;



public abstract class BasicAlgorithm extends Program {
	
	public static final String stabilityNotificationKey="dcop program is done!!";
	 
	protected Random random = new Random();
	protected static final int reLockInterval = 8;
	protected int lockBase;
	protected int reLockTime;
	protected Graph view;
	protected Variable self;
	protected int lock;
	private boolean done;
	
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
		reLockTime = random.nextInt(reLockInterval * lockBase * 4);
		done = false;
		
		inChannelMap = new HashMap<Integer, Integer>();
		outChannelMap = new HashMap<Integer, Integer>();
		statList = new ArrayList<Stats>();
		preAttempt = 0;		
		preCycles = 0;
		nlockReq = 0;

	}
	

	public void setDone(boolean done) {
		node.notify(done, stabilityNotificationKey);
		this.done = done;
	}

	public boolean isStable() {
		return done;
	}
	
	public int getValue(){
		return self.value;
	}
	
	public int getID() {
		return self.id;
	}
	
	
	
//	public void incrementApplicationWastedCycles(){

//		DCOPApplication app = (DCOPApplication) this.node
//				.getNetwork().getApplication();
//		app.wastedCycles++;
//	}
	
//	public void incrementApplicationNumberOfConflict(){
//		DCOPApplication app = (DCOPApplication) this.node
//				.getNetwork().getApplication();
//		app.numberConflicts++;
//	}
	
	
	
	
//	
//	@Override
//	abstract protected void main();
}
