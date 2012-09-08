package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class ReplicationSolverInterface
implements Solver, DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>{



	//
	// Solver infterface
	//

	public abstract void setProblem(ReplicationGraph rg, HashedHashSet<AgentIdentifier,AgentIdentifier> fixedVar);

	//
	// Dcop infterface
	//

	@Override
	public void initiate(DcopReplicationGraph drg) {
		Collection<ReplicaState> repSt = new ArrayList<ReplicaState>();
		for (ReplicaState r : drg.getAgentStates()){
			ReplicaState r2 = r.freeAllResources();
			repSt.add(r2);
		}

		Collection<HostState> hostSt = new ArrayList<HostState>();
		for (HostState r : drg.getHostsStates()){
			HostState r2 = r.freeAllResources();
			hostSt.add(r2);
		}
		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(drg.getSocialWelfare());
		rig.setAgents(drg.getAgentStates());
		rig.setHosts(drg.getHostsStates());
		for (AgentIdentifier id : rig.getAgentsIdentifier()){
			for (ResourceIdentifier id2 : drg.getAccessibleHosts(id))
				rig.addAcquaintance(id, id2);
		}
		
		HashedHashSet<AgentIdentifier,AgentIdentifier> fixedVar = new HashedHashSet<AgentIdentifier,AgentIdentifier>();
		for (ReplicationVariable var : drg.varMap.values())
			if (var.fixed)
				fixedVar.put(var.getAgentIdentifier(), (Set<AgentIdentifier>) var.getAllocatedRessources());
		
		setProblem(rig, fixedVar);
	}

	//
	// Nego
	//

	HostState myState;
	Collection<ReplicationCandidature> concerned;
	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {
		try {
			Collection<ReplicaState> replicasStates = new ArrayList<ReplicaState>();
			Collection<HostState> hostsStates = new ArrayList<HostState>();
			HashedHashSet<AgentIdentifier, ResourceIdentifier> accHosts = new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
			this.concerned=concerned;
			this.myState=concerned.iterator().next().getResourceInitialState();
			Iterator<ReplicationCandidature> itC = concerned.iterator();
			while (itC.hasNext()){
				ReplicationCandidature rc = itC.next();
				assert rc.getResourceInitialState().equals(rc.getResourceInitialState());
				assert rc.getAgentInitialState().getMyMemCharge().equals(
						rc.getAgentResultingState().getMyMemCharge());
				assert rc.getAgentInitialState().getMyProcCharge().equals(
						rc.getAgentResultingState().getMyProcCharge());

				if (rc.isMatchingCreation())
					replicasStates.add(rc.getAgentInitialState());
				else
					replicasStates.add(rc.getAgentResultingState());

				accHosts.add(rc.getAgent(), myState.getMyAgentIdentifier());
			}		

			ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
			rig.setAgents(replicasStates);
			rig.setHosts(hostsStates);
			for (AgentIdentifier id : rig.getAgentsIdentifier()){
				for (ResourceIdentifier id2 : accHosts.get(id))
					rig.addAcquaintance(id, id2);
			}
			setProblem(rig, new HashedHashSet<AgentIdentifier, AgentIdentifier>());
		} catch (IncompleteContractException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
