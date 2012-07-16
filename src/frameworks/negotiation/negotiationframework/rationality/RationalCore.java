package frameworks.negotiation.negotiationframework.rationality;

import java.util.Collection;

import dima.introspectionbasedagents.services.AgentCompetence;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;

public interface RationalCore<
Agent extends RationalAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends AgentCompetence<Agent>
{

	/*
	 *
	 */


	//	public PersonalState getMyResultingState(PersonalState s, Contract c);
	//
	public void setMySpecif(PersonalState s, Contract c);

	public void execute(Collection<Contract> contracts);

	/*
	 *
	 */

	/**
	 *
	 * @param s etat initial de l'agent
	 * @param c1 premiere collection de contrat proposé
	 * @param c2 deuxieme collection de contrat composé
	 * @return un entier indiquant si l'état résultant de c1 a partir de s est meilleurs que celui réusltant de c2
	 * @throws IncompleteContractException
	 */
	public int getAllocationPreference(Collection<Contract> c1, Collection<Contract> c2);

	public  Double evaluatePreference(Collection<Contract> cs);


	public boolean iObserveMyRessourceChanges();

	boolean iMemorizeMyRessourceState();

}

// public boolean Iaccept(final PersonalState s1, final PersonalState s2);

// public boolean willAccept(AgentIdentifier id, final InformedState s1, final
// InformedState s2);

/*
 *
 */
// public int getMyPreference(PersonalState s1, PersonalState s2);

// public int getPreference(AgentIdentifier id, InformedState s1, InformedState
// s2);

/**
 *
 * @param a
 *            : a given state
 * @return true if the agent constraints are respected on a
 */
// public Boolean respectRights(InformedState s);

/*
 *
 */

// public InformedState getResultingState(InformedState s, Contract c);
