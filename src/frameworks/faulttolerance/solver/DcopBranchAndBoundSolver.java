package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.faults.Assert;

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

public class DcopBranchAndBoundSolver extends ResourceAllocationInterface<HashMap<Integer, Integer>>
implements DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>{


	private double _maxReward;
	private HashMap<Integer, Integer> _bestSolution;
	private LinkedList<HashMap<Integer, Integer>> foundSolution=null;

	long startTime;
	int timeLimit=Integer.MAX_VALUE;


	public DcopBranchAndBoundSolver(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}



	@Override
	protected HashMap<Integer, Integer> solveProb(boolean opt)
			throws UnsatisfiableException {
		if (opt || foundSolution==null){
			startTime=new Date().getTime();
			if (isLocal()){
				_bestSolution=intialSolution;
				_maxReward=getSocWelfare(_bestSolution);			
			} else {
				_bestSolution=getFailedSolution(drg);
				_maxReward=Double.NEGATIVE_INFINITY;
			}
			return branchBoundSolve(drg);
		} else {
			assert !foundSolution.isEmpty();
			return foundSolution.pop();
		}
	}


	@Override
	protected void initiateSolver() throws UnsatisfiableException {
		if (drg==null)
			drg=new DcopReplicationGraph(rig);
	}



	@Override
	protected HashMap<Integer, Integer> getInitialAllocAsSolution(double[] intialAlloc) {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		Collection<AgentIdentifier> allocated = new ArrayList<AgentIdentifier>();
		allocated.add(myState.getMyAgentIdentifier());
		Collection<AgentIdentifier> unAllocated=new ArrayList<AgentIdentifier>();

		for (int i = 0; i < n; i++){
			ReplicationVariable var = drg.getVariable(getAgentIdentifier(i));
			result.put(var.id, var.getValue(intialAlloc[i]==0?unAllocated:allocated));
		}
		return result;
	}

	@Override
	protected double read(HashMap<Integer, Integer> var, int agent, int host) {
		AgentIdentifier agId = drg.getVariable(getAgentIdentifier(agent)).getAgentIdentifier();
		AgentIdentifier hostId = drg.getVariable(getAgentIdentifier(host)).getAgentIdentifier();
		int agAlloc = var.get( drg.getVariable(getAgentIdentifier(agent)).id);
		int hostAlloc = var.get( drg.getVariable(getAgentIdentifier(host)).id);
		if (rig.getAccessibleHosts(agId).contains(hostId)){
			assert Assert.IIF(drg.getVariable(agId).hasAllocatedRessource(hostId, agAlloc),drg.getVariable(hostId).hasAllocatedRessource(agId, hostAlloc));
			return drg.getVariable(agId).hasAllocatedRessource(hostId, agAlloc)?1.0:0.0;
		} else {
			return 0.;
		}
	}


	@Override
	public boolean hasNext() {
		if (foundSolution==null){
			foundSolution=new LinkedList<HashMap<Integer,Integer>>();
			return true;
		} else if (foundSolution.isEmpty()){
			foundSolution=null;
			return false;
		} else
			return true;
	}


	@Override
	public void setTimeLimit(int millisec) {
		timeLimit=millisec;
	}

	//
	// Primitives
	//

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
			if (!v.fixed && v.getValue() == -1){
				v.setValue(0);
			}

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
		HashMap<Integer, Integer> solution = drg.getSolution();
		if (isViable(solution))
			foundSolution.add(solution);

		if (idx == queue.length) {
			double val = drg.evaluate();
			if (val > _maxReward) {
				_maxReward = val;
				_bestSolution = solution;
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

	private boolean hasExpired(){
		return new Date().getTime()-startTime>timeLimit;
	}
}
