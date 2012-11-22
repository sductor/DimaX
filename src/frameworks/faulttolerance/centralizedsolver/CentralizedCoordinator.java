package frameworks.faulttolerance.centralizedsolver;

import java.util.Collection;
import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.JMetalRessAllocProblem;
import frameworks.faulttolerance.solver.RessourceAllocationProblem;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.core.SolutionSortedSet;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.InactiveCommunicationProtocole;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.selection.InactiveSelectionCore;

public class CentralizedCoordinator extends Host {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7349678256844655400L;
	final SolutionSortedSet parallelParents;
	Solution currentSol;

	HashMap<AgentIdentifier,ReplicaState> agents;
	HashMap<ResourceIdentifier,HostState> hosts;
	RessourceAllocationProblem<Solution> p;



	public CentralizedCoordinator(
			final ResourceIdentifier id, final HostState myState,
			final RessourceAllocationProblem<Solution> p,
			Double collectiveSeed) throws CompetenceException {
		super(
				id,
				myState,
				new HostCore(null, false, false),
				new InactiveSelectionCore(),
				new InactiveProposerCore(),
				new SimpleObservationService(),
				new InactiveCommunicationProtocole(),collectiveSeed);
		this.agents=new HashMap<AgentIdentifier, ReplicaState>();
		this.hosts=new HashMap<ResourceIdentifier, HostState>();
		for (final ReplicaState s :p.rig.getAgentStates()){
			this.agents.put(s.getMyAgentIdentifier(), s);
		}
		for (final HostState s :p.rig.getHostsStates()){
			this.hosts.put(s.getMyAgentIdentifier(), s);
		}
		this.p = p;
		final JMetalRessAllocProblem jp = new JMetalRessAllocProblem(p);
		this.currentSol = jp.getUnallocatedSolution();
		this.parallelParents = new SolutionSortedSet(jp.mu,jp.getComparator());
		this.parallelParents.add(this.currentSol);
	}

	public void register(final CentralizedHost h){
		h.solver.setParallelParents(this.parallelParents);
	}

	@StepComposant
	public void updatesStates(){
		if (!this.parallelParents.best().equals(this.currentSol)){
			this.currentSol=this.parallelParents.best();
			for (final AgentIdentifier id : this.agents.keySet()){
				final ReplicaState oldS = this.agents.get(id);
				ReplicaState newState = this.agents.get(id);
				final Collection<AgentIdentifier> newAlloc = this.p.getRessources(this.currentSol, id);

				for (final ResourceIdentifier h : oldS.getMyResourceIdentifiers()){
					if (!newAlloc.contains(h)){
						newState=newState.allocate(this.p.rig.getHostState(h), false);
					}
				}

				for (final AgentIdentifier h : newAlloc){
					if (!oldS.getMyResourceIdentifiers().contains(h)){
						newState=newState.allocate(this.p.rig.getHostState((ResourceIdentifier)h), true);
					}
				}

				if (!newState.equals(oldS)){
					this.sendMessage(id, new StateUpdate(newState));
					this.agents.put(id, newState);
				}
			}
			for (final ResourceIdentifier h : this.hosts.keySet()){
				final HostState oldS = this.hosts.get(h);
				HostState newState = this.hosts.get(h);
				final Collection<AgentIdentifier> newAlloc = this.p.getRessources(this.currentSol, h);

				for (final AgentIdentifier ag : oldS.getMyResourceIdentifiers()){
					if (!newAlloc.contains(ag)){
						newState=newState.allocate(this.p.rig.getAgentState(ag), false);
					}
				}

				for (final AgentIdentifier ag : newAlloc){
					if (!oldS.getMyResourceIdentifiers().contains(ag)){
						newState=newState.allocate(this.p.rig.getAgentState(ag), true);
					}
				}

				if (!newState.equals(oldS)){
					this.sendMessage(h, new StateUpdate(newState));
					this.hosts.put(h, newState);
				}
			}
		}
	}


	//
	// Message
	//

	public class StateUpdate extends Message{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2643834137531125907L;
		private final AgentState newState;

		public StateUpdate(final AgentState newState) {
			super();
			this.newState = newState;
		}

		public AgentState getNewState() {
			return this.newState;
		}
	}
	@MessageHandler
	public void updateMyState(final StateUpdate m){
		this.logMonologue("state update",LogService.onBoth);
		this.setNewState((HostState) m.getNewState());
	}
}
