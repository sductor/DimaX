package negotiation.negotiationframework.interaction.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.candidatureprotocol.mirror.IllAnswer;
import negotiation.negotiationframework.interaction.consensualnegotiation.ContractTrunk;
import negotiation.negotiationframework.interaction.consensualnegotiation.SelectionCore;
import dima.introspectionbasedagents.competences.BasicAgentCompetence;

/**
 * Selection Cores must extenbds this class to be coherent with the roles. They
 * must implements the methods select(..) and use the primitives
 * validateContract and dismissContract to inform of the decisions
 * 
 * @author Sylvain Ductor
 * 
 * @param <PersonalState>
 * @param <Contract>
 * @param <ActionSpec>
 */
public abstract class AbstractSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements SelectionCore<ActionSpec, PersonalState, Contract> {
	private static final long serialVersionUID = -6733096733805658072L;

	private ContractTrunk<Contract> returned;
	private ContractTrunk<Contract> given;
	private final boolean fuseInitiatorNparticipant;//separate creation and destruction in mirror
	private final boolean considerOnWait;

	public AbstractSelectionCore(
			final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait) {
		super();
		this.fuseInitiatorNparticipant = fuseInitiatorNparticipant;
		this.considerOnWait = considerOnWait;
	}

	/*
	 *
	 */

	@Override
	public ContractTrunk<Contract> select(final ContractTrunk<Contract> given) {
		this.returned = 
				new ContractTrunk<Contract>(
						this.getMyAgent().getIdentifier());
		this.given = given;

		//		for (Contract c : given.getAllContracts()){
		//			if (c instanceof DestructionOrder){
		////				logMonologue("destructing! "+c+"\n my state is "+getMyAgent().getMyCurrentState());
		//				given.addAcceptation(getIdentifier(), c);
		//			}
		//		}

		this.select(given.getInitiatorConsensualContracts(),
				given.getParticipantOnWaitContracts(),
				given.getInitiatorOnWaitContracts(),
				given.getParticipantAlreadyAcceptedContracts(),
				given.getRejectedContracts());
		// if (!(this.getMyAgent().getIdentifier() instanceof
		// ResourceIdentifier) &&
		// !given.getParticipantOnWaitContracts().isEmpty())
		// this.logMonologue("AbstractSElectionCore : MyState"+this.getMyAgent().getMyCurrentState()+"\nGiven :"+given+"\n Returned : "+this.returned);
		return this.returned;
	}

	//
	// Abstract
	//

	/**
	 * Use setAnswer() method to set the results
	 * @param initiatorContractToExplore
	 * @param participantContractToExplore
	 * @param initiatorOnWaitContract
	 * @param alreadyAccepted
	 * @param rejected
	 */
	protected void select(final List<Contract> initiatorContractToExplore,
			final List<Contract> participantContractToExplore,
			final List<Contract> initiatorOnWaitContract,
			final List<Contract> participantAlreadyAccepted, 
			final List<Contract> rejected) {

		// Verification de la consistance
		if (!this.getMyAgent().respectMyRights(
				this.getMyAgent().getMyCurrentState()))
			throw new RuntimeException("what the  (1)!!!!!!"
					+ this.getMyAgent().getMyCurrentState());

		// Mis à jour de l'état si tous les agents ayant été accepter
		// confirmaient :
		final PersonalState currentState = this.getMyAgent()
				.getMyResultingState(
						this.getMyAgent().getMyCurrentState(),
						participantAlreadyAccepted);

		// Verification de la consistance
		if (!this.getMyAgent().respectMyRights(currentState))
			throw new RuntimeException("what the  (2)!!!!!!" + currentState);

		Collection<Contract> toValidate = new ArrayList<Contract>();
		final Collection<Contract> toReject = new ArrayList<Contract>();
		toReject.addAll(initiatorContractToExplore);
		toReject.addAll(participantContractToExplore);

		if (this.fuseInitiatorNparticipant) {
			final List<Contract> contractsToExplore = new ArrayList<Contract>();
			contractsToExplore.addAll(initiatorContractToExplore);
			contractsToExplore.addAll(participantContractToExplore);
			if (considerOnWait)
				contractsToExplore.addAll(initiatorOnWaitContract);

			toValidate = this.selection(currentState, contractsToExplore);

		} else {
			toValidate.addAll(this.selection(currentState,
					participantContractToExplore));

			if (considerOnWait)
				initiatorContractToExplore.addAll(initiatorOnWaitContract);
			toValidate.addAll(
					this.selection(currentState,
							initiatorContractToExplore));
		}

		toReject.removeAll(toValidate);

		this.setAnswer(toValidate, toReject);
	}

	protected abstract Collection<Contract> selection(
			PersonalState currentState,
			List<Contract> contractsToExplore);

	//
	// Methods
	//
	// private boolean lastFull=false;

	protected void setAnswer(
			final Collection<Contract> accepted,
			final Collection<Contract> rejected) {

//		this.validityVerification(accepted, rejected);

		// ACCEPTATION
		for (final Contract c : accepted) {
			this.returned.addContract(c);
			this.returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
		}

		// REFUS
		for (final Contract c : rejected) {
			this.returned.addContract(c);
			this.returned.addRejection(this.getMyAgent().getIdentifier(), c);
		}		

		this.notify(new IllAnswer<Contract>(returned));
	}

	//	private void checkImFull(final Collection<Contract> rejected) {
	//		final Collection<Contract> allContracts = new ArrayList<Contract>();
	//		allContracts.addAll(this.given.getInitiatorConsensualContracts());
	//		allContracts.addAll(this.given.getParticipantOnWaitContracts());
	//
	//		// envoie d'info
	//		if (!this.given.getParticipantOnWaitContracts().isEmpty()
	//				&& rejected.containsAll(allContracts)){
	////			logMonologue("fulll!");
	//			this.notify(new ImFull(rejected));
	//		}
	//	}

	//
	// Primitive
	//

	private void validityVerification(
			final Collection<Contract> accepted,
			final Collection<Contract> notAccepted) {
		// verification de validit�� d'appel
		final Collection<Contract> test = new ArrayList<Contract>();
		test.addAll(accepted);
		test.addAll(notAccepted);
		//		test.addAll(onWait);

		final Collection<Contract> allContracts = new ArrayList<Contract>();
		allContracts.addAll(this.given.getInitiatorConsensualContracts());
		allContracts.addAll(this.given.getParticipantOnWaitContracts());
		// allContracts.addAll(onWait);
		if (!test.containsAll(allContracts))// &&allContracts.containsAll(test)))
			throw new RuntimeException(
					"mauvaise implementation du selection core (1)");
		if (!allContracts.containsAll(accepted))
			throw new RuntimeException(
					"mauvaise implementation du selection core (2)\n"
							+ allContracts);
		for (final Contract c : notAccepted) {
			if (!allContracts.contains(c) && !this.given.getOnWaitContracts().contains(c))
				throw new RuntimeException(
						"mauvaise implementation du selection core (3)");
			if (accepted.contains(c))
				throw new RuntimeException(
						"mauvaise implementation du selection core (4)");
		}
	}
}


//protected abstract void select(List<Contract> initiatorContractToExplore,
//		List<Contract> participantContractToExplore,
//		List<Contract> initiatorOnWaitContract,
//		List<Contract> alreadyAccepted, List<Contract> rejected);
