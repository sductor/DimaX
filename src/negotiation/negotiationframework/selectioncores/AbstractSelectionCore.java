package negotiation.negotiationframework.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.NegotiationProtocol;
import negotiation.negotiationframework.SelectionCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.proposercores.collaborative.IllAnswer;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

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
	private final boolean considerOnWait;//cette variable n'a pas de sens puisque elle amene l'agent a accepter des contrat en imaginant que les siens ont été accepté!!

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

	protected abstract Collection<Contract> selection(
			PersonalState currentState,
			List<Contract> contractsToExplore);

	//
	// Primitive
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
			throw new RuntimeException("what the  (2)!!!!!!" + currentState+"\n ACCEPTED \n"+participantAlreadyAccepted+"\n GIVEN \n"+given);

		Collection<Contract> toValidate = new ArrayList<Contract>();
		final Collection<Contract> toReject = new ArrayList<Contract>();
		toReject.addAll(initiatorContractToExplore);
		toReject.addAll(participantContractToExplore);

		if (this.fuseInitiatorNparticipant) {
			
			final List<Contract> contractsToExplore = new ArrayList<Contract>();
			contractsToExplore.addAll(initiatorContractToExplore);
			contractsToExplore.addAll(participantContractToExplore);
			if (this.considerOnWait)
				contractsToExplore.addAll(initiatorOnWaitContract);

			toValidate.addAll(this.selection(currentState, contractsToExplore));

		} else {
			
			toValidate.addAll(this.selection(currentState,
					participantContractToExplore));

			if (this.considerOnWait)
				initiatorContractToExplore.addAll(initiatorOnWaitContract);			
			toValidate.addAll(
					this.selection(currentState,
							initiatorContractToExplore));
		}

		toReject.removeAll(toValidate);

		this.setAnswer(currentState,toValidate, toReject);
	}

	// private boolean lastFull=false;

	protected void setAnswer(PersonalState currentState,
			final Collection<Contract> accepted,
			final Collection<Contract> rejected) {

//		assert	this.validityVerification(accepted, rejected);

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

		this.logMonologue("Setting my answer "+this.returned, NegotiationProtocol.log_selectionStep);
		this.notify(new IllAnswer<PersonalState, Contract>(this.returned, currentState));
		this.logMonologue("After being delaed by relevant services "+this.returned, NegotiationProtocol.log_selectionStep);
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

	private boolean validityVerification(
			final Collection<Contract> accepted,
			final Collection<Contract> notAccepted) {
//		logMonologue("accepeted "+accepted+" refused "+notAccepted, LogService.onBoth);
		
		// verification de validit�� d'appel
		final Collection<Contract> test = new ArrayList<Contract>();
		test.addAll(accepted);
		test.addAll(notAccepted);
		//		test.addAll(onWait);

		final Collection<Contract> allContracts = new ArrayList<Contract>();
		allContracts.addAll(this.given.getInitiatorConsensualContracts());
		allContracts.addAll(this.given.getParticipantOnWaitContracts());
		 allContracts.addAll(this.given.getInitiatorOnWaitContracts());
		if (!test.containsAll(allContracts))// &&allContracts.containsAll(test)))
			throw new RuntimeException(
					"mauvaise implementation du selection core (1)");
		if (!allContracts.containsAll(accepted))
			throw new RuntimeException(
					"mauvaise implementation du selection core (2)\n all contracts : "
							+ allContracts
							+ "\n accepted : "+accepted);
		for (final Contract c : notAccepted) {
			if (!allContracts.contains(c) && !this.given.getOnWaitContracts().contains(c))
				throw new RuntimeException(
						"mauvaise implementation du selection core (3)");
			if (accepted.contains(c))
				throw new RuntimeException(
						"mauvaise implementation du selection core (4)");
		}
		return true;
	}
}


//protected abstract void select(List<Contract> initiatorContractToExplore,
//		List<Contract> participantContractToExplore,
//		List<Contract> initiatorOnWaitContract,
//		List<Contract> alreadyAccepted, List<Contract> rejected);
