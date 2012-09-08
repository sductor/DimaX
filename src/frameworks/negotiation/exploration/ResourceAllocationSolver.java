package frameworks.negotiation.exploration;

import java.util.Collection;

import frameworks.negotiation.NegotiationException;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.AgentState;


public interface ResourceAllocationSolver
<Contract extends MatchingCandidature,
PersonalState extends AgentState> 
extends Solver{

	/**
	 * Initie le solver csp
	 * @param concerned : la liste de toute les candidature :
	 * celle de l'état courant en tant que destruction et celle couramment analyser en tant que création
	 * @param currentState : l'état en cours
	 * @param socialWelfare : l'opérateur social a optimise
	 * @throws IncompleteContractException
	 */
	public abstract void initiate(Collection<Contract> concerned);

	public abstract Collection<Contract> computeBestLocalSolution() 
			throws UnsatisfiableException, ExceedLimitException;
	
	public abstract Collection<Collection<Contract>> computeAllLocalSolutions();

	public abstract Collection<Contract> getNextSolution();
	
}