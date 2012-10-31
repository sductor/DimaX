package frameworks.faulttolerance.centralizedsolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.solver.JMetalElitistES;
import frameworks.faulttolerance.solver.JMetalRessAllocProblem;
import frameworks.faulttolerance.solver.RessourceAllocationProblem;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSortedSet;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedCoordinator extends Host {

	final SolutionSortedSet parallelParents;
	Solution currentSol;

	HashMap<AgentIdentifier,ReplicaState> agents;
	HashMap<ResourceIdentifier,HostState> hosts;
	RessourceAllocationProblem<Solution> p;



	public CentralizedCoordinator(
			ResourceIdentifier id, HostState myState,
			RessourceAllocationProblem<Solution> p) throws CompetenceException {
		super(
				id, 
				myState, 
				new HostCore(null, false, false),
				new InactiveSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new InactiveCommunicationProtocole());
		agents=new HashMap<AgentIdentifier, ReplicaState>();
		hosts=new HashMap<ResourceIdentifier, HostState>();
		for (ReplicaState s :p.rig.getAgentStates()){
			agents.put(s.getMyAgentIdentifier(), s);
		}
		for (HostState s :p.rig.getHostsStates()){
			hosts.put(s.getMyAgentIdentifier(), s);
		}
		this.p = p;
		JMetalRessAllocProblem jp = new JMetalRessAllocProblem(p);
		currentSol = jp.getUnallocatedSolution();
		parallelParents = new SolutionSortedSet(jp.mu,jp.getComparator());
		parallelParents.add(currentSol);
	}

	public void register(CentralizedHost h){
		h.solver.setParallelParents(parallelParents);
	}

	@StepComposant
	public void updatesStates(){
		if (!parallelParents.best().equals(currentSol)){
			currentSol=parallelParents.best();
			for (AgentIdentifier id : agents.keySet()){
				ReplicaState oldS = agents.get(id);
				ReplicaState newState = agents.get(id);
				Collection<AgentIdentifier> newAlloc = p.getRessources(currentSol, id);

				for (ResourceIdentifier h : oldS.getMyResourceIdentifiers()){
					if (!newAlloc.contains(h)){
						newState=newState.allocate(p.rig.getHostState(h), false);
					}
				}	

				for (AgentIdentifier h : newAlloc){
					if (!oldS.getMyResourceIdentifiers().contains(h)){
						newState=newState.allocate(p.rig.getHostState((ResourceIdentifier)h), true);
					}
				}

				if (!newState.equals(oldS)){
					sendMessage(id, new StateUpdate(newState));
					agents.put(id, newState);
				}
			}
			for (ResourceIdentifier h : hosts.keySet()){
				HostState oldS = hosts.get(h);
				HostState newState = hosts.get(h);
				Collection<AgentIdentifier> newAlloc = p.getRessources(currentSol, h);

				for (AgentIdentifier ag : oldS.getMyResourceIdentifiers()){
					if (!newAlloc.contains(ag)){
						newState=newState.allocate(p.rig.getAgentState(ag), false);
					}
				}	

				for (AgentIdentifier ag : newAlloc){
					if (!oldS.getMyResourceIdentifiers().contains(ag)){
						newState=newState.allocate(p.rig.getAgentState(ag), true);
					}
				}

				if (!newState.equals(oldS)){
					sendMessage(h, new StateUpdate(newState));
					hosts.put(h, newState);
				}
			}
		}
	}


	//
	// Message
	//

	public class StateUpdate extends Message{
		private final AgentState newState;

		public StateUpdate(AgentState newState) {
			super();
			this.newState = newState;
		}

		public AgentState getNewState() {
			return newState;
		}
	}
	@MessageHandler
	public void updateMyState(StateUpdate m){
		logMonologue("state update",LogService.onBoth);
		setNewState((HostState) m.getNewState());
	}
}
