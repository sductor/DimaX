package negotiation.negotiationframework.exploration;

import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;

public interface AllocationSolver
<Contract extends MatchingCandidature<ActionSpec>, 
ActionSpec extends AbstractActionSpecification, 
PersonalState extends ActionSpec> {

	/**
	 * Initie le solver csp
	 * @param concerned : la liste de toute les candidature : 
	 * celle de l'état courant en tant que destruction et celle couramment analyser en tant que création
	 * @param currentState : l'état en cours
	 * @param socialWelfare : l'opérateur social a optimise
	 * @throws IncompleteContractException
	 */
	public abstract void initiate(
			Collection<Contract> concerned,
			PersonalState currentState);

	public abstract Collection<Contract> getBestSolution();

	public abstract boolean hasNext();

	public abstract Collection<Contract> getNextSolution();
	
	public abstract void setTimeLimit(int millisec);

}