package vieux.dcop_old.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import vieux.dcop_old.algo.topt.LockMsg;
import vieux.dcop_old.dcop.Constraint;
import vieux.dcop_old.dcop.Graph;
import vieux.dcop_old.dcop.Variable;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.support.GimaObject;

public abstract class BasicDCOPNode extends BasicCommunicatingCompetence<CommunicatingCompetentComponent> {
	
	public enum Algorithm {
		KOPTORIG, KOPTAPO, TOPTAPO
	}
	
	//
	// Fields
	//
	
	protected static final int reLockInterval = 8;
	protected int lockBase;
	protected int reLockTime;
	protected Graph view;
	protected Variable self;
	protected int lock;
	protected boolean done;
		
	public int preAttempt;

	public int nlockReq;
	public int preCycles;
	
	//
	//
	//	
	
	HashedHashSet<NodeIdentifier, DcopMessage> in;
	List<NodeIdentifier> out;	
	

	private int switches = 0; // number of context switches occurred so far
	private int numberConflicts=0;

	//
	//
	//
	
	public BasicDCOPNode(Variable v) {
		view = new Graph();
		self = new Variable(v.id, v.domain);//, view);
		view.varMap.put(self.id, self);
		for (Constraint c : v.neighbors) {
			Variable n = c.getNeighbor(v);
			Variable nn = new Variable(n.id, n.domain);//, view);
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
		reLockTime = getRandom().nextInt(reLockInterval * lockBase * 4);
		done = false;
		
		preAttempt = 0;		
		preCycles = 0;
		nlockReq = 0;

	}
	
	//
	//
	//
	
	public int getValue(){
		return self.value;
	}
	
	public int getID() {
		return self.id;
	}
	
	public boolean isStable() {
		return done;
	}

	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return switches;
	}
	
	protected void incrNumberConflicts() {
		numberConflicts++;
	}
	//
	// Behavior
	//

	@ProactivityInitialisation
	public abstract void initialisation();

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@StepComposant
	public abstract void run();

	/*
	 * 
	 */
	
	@MessageHandler
	public void receiveMessage(final DcopMessage m){
		this.in.add((NodeIdentifier) m.getSender(),m);
	}
		
	public void broadcast(DcopMessage msg) {
		sendMessage(out, msg);
	}
}
