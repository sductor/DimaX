package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.algo.Algorithm;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class DcopBranchAndBoundSolver implements DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>{

	private double _maxReward;
	private HashMap<Integer, Integer> _bestSolution;

	
	protected final SocialChoiceType socialWelfare;	


	public DcopBranchAndBoundSolver(SocialChoiceType socialChoice) {
		super();
		this.socialWelfare = socialChoice;
	}


	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg){
		startTime=new Date().getTime();
		_bestSolution=ResourceAllocationProblem.getFailedSolution(drg);
		_maxReward=Double.NEGATIVE_INFINITY;
		return branchBoundSolve(drg);
	}


	private HashMap<Integer, Integer> branchBoundSolve(DcopReplicationGraph drg) {
		ArrayList<ReplicationVariable> list = new ArrayList<ReplicationVariable>();
		for (ReplicationVariable v : drg.varMap.values()){
			list.add(v);
		}
		Collections.sort(list, new Comparator<ReplicationVariable>() {
			public int compare(ReplicationVariable v0, ReplicationVariable v1) {
				if (v0.getDegree() >= v1.getDegree())
					return -1;
				else
					return 1;
			}
		});
		int[] queue = new int[list.size()];
		int idx = 0;
		for (ReplicationVariable v : list) {
			queue[idx] = v.id;
			idx++;
		}

		drg.backup();

		for (ReplicationVariable v : drg.varMap.values())
			if (!v.fixed && v.getValue() == -1)
				v.setValue(0);

				_maxReward = drg.evaluate();
				_bestSolution = drg.getSolution();

				// int r = _maxReward;

				for (ReplicationVariable v : drg.varMap.values()) {
					if (!v.fixed)
						v.setValue(-1);
				}

				_bbEnumerate(drg, queue, 0);

				// int rr = _maxReward;

				drg.recover();

				return _bestSolution;

	}

	private void _bbEnumerate(DcopReplicationGraph drg, int[] queue, int idx) {
		if (hasExpired())
			return;
		if (idx == queue.length) {
			double val = drg.evaluate();
			if (val > _maxReward) {
				_maxReward = val;
				_bestSolution = drg.getSolution();
			}
			return;
		}
		ReplicationVariable v = drg.varMap.get(queue[idx]);
		if (v.fixed)
			_bbEnumerate(drg,queue, idx + 1);
		else {
			for (int i = 0; i < v.getDomain(); i++) {
				v.setValue(i);
				double val = drg.evaluate();
				if (val <= _maxReward)
					continue;
				_bbEnumerate(drg,queue, idx + 1);
			}
			v.setValue(-1);
		}
	}

	/*
	 * 
	 */
	

	long startTime;
	int timeLimit=Integer.MAX_VALUE;

	@Override
	public void setTimeLimit(int millisec) {
		timeLimit=millisec;
	}
	public boolean hasExpired(){
		return new Date().getTime()-startTime>timeLimit;
	}
	
	/*
	 * Negotiation
	 */


	DcopReplicationGraph drg;
	Collection<ReplicationCandidature> concerned;
	boolean hasNext=true;

	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {
		drg = ResourceAllocationProblem.toDrg(concerned, socialWelfare);
		this.concerned=concerned;
	}


	@Override
	public Collection<ReplicationCandidature> getBestLocalSolution()
			throws UnsatisfiableException, ExceedLimitException {
		hasNext=false;
		return ResourceAllocationProblem.getContractSolution(drg, concerned, solve(drg));
	}


	@Override
	public boolean hasNext() {
		return hasNext;
	}


	@Override
	public Collection<ReplicationCandidature> getNextLocalSolution() {
		hasNext=false;
		return ResourceAllocationProblem.getContractSolution(drg, concerned, solve(drg));
	}


}
