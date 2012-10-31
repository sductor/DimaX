package frameworks.negotiation.exploration;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.services.AgentModule;

import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.NegotiationException;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;


public interface ResourceAllocationSolver
<Contract extends AbstractContractTransition,
PersonalState extends AgentState> 
extends Solver, AgentModule<CompetentComponent>{

	/**
	 * Initie le solver csp
	 * @param concerned : la liste de toute les candidature :
	 * celle de l'état courant en tant que destruction et celle couramment analyser en tant que création
	 * @param currentState : l'état en cours
	 * @param socialWelfare : l'opérateur social a optimise
	 * @throws IncompleteContractException
	 */
	public abstract void setProblem(Collection<Contract> concerned);

	public abstract void setProblem(ReplicationGraph rig, Collection<AgentIdentifier> fixedVar);
	
	public abstract Collection<Contract> getBestLocalSolution() 
			throws UnsatisfiableException, ExceedLimitException;

	
	public boolean hasNext();
	
	public abstract Collection<Contract> getNextLocalSolution();
	
}