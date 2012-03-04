package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sun.security.action.GetLongAction;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice.EventHookedMethod;
import dima.introspectionbasedagents.shells.NotReadyException;
import dimaxx.tools.HyperSetGeneration;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.communicationprotocol.AbstractCommunicationProtocol;
import negotiation.negotiationframework.communicationprotocol.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.communicationprotocol.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.selectioncores.GreedyBasicSelectionCore;
import negotiation.negotiationframework.selectioncores.GreedyRouletteWheelSelectionCore;

public abstract class ResourceInformedSelectionCore <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements SelectionCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> {

	//
	// Methods
	//

	@Override
	public ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState> select(
			final ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState> given) {

		// Verification de la consistance
		assert this.getMyAgent().getMyCurrentState().isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert given.getParticipantAlreadyAcceptedContracts().isEmpty();

		//objects
		ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState> contracts = 
				(ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState>) given;
		InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myCore = 
				(InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) getMyAgent().getMyCore();

		//contract lists
		List<InformedCandidature<Contract,ActionSpec>> allContracts = contracts.getAllContracts();		
		Collection<InformedCandidature<Contract, ActionSpec>> accepted = 
				new HashSet<InformedCandidature<Contract, ActionSpec>>();
		Collection<InformedCandidature<Contract, ActionSpec>> rejected = 
				new HashSet<InformedCandidature<Contract, ActionSpec>>();

		PersonalState currentState = this.getMyAgent().getMyCurrentState();
		rejected.addAll(allContracts);	

		if (allContracts.isEmpty())
			logMonologue("i support everyone yeah! =)", AbstractCommunicationProtocol.log_selectionStep);
		else {
			if (allCreation(allContracts)){
				assert contracts.getReallocationContracts().isEmpty();
				
				//Trying to accept simple candidatures
				//accepting as many as possible
				Collections.shuffle(allContracts);
				for (InformedCandidature<Contract,ActionSpec> c : allContracts)
					if (getMyAgent().Iaccept(currentState, c)){
						accepted.add(c);
						try {
							currentState=c.computeResultingState(currentState);
						} catch (IncompleteContractException e) {
							throw new RuntimeException();
						}
					}

				rejected.removeAll(accepted);

				if (accepted.isEmpty()){

					logMonologue("generating upgrading contracts!", AbstractCommunicationProtocol.log_selectionStep);				
					//some unaccepted contract : generating upgrading contract and adding to propose

					ResourceInformedProposerCore<Contract, ActionSpec, PersonalState> propCore =
							(ResourceInformedProposerCore<Contract, ActionSpec, PersonalState>) getMyAgent().getMyProposerCore();

					Collection<InformedCandidature<Contract, ActionSpec>> hosted = 
							new HashSet<InformedCandidature<Contract, ActionSpec>>();
					//				hosted.addAll(accepted);

					for (final ActionSpec s : getMyAgent().getMyResources()){
						//adding destruction of hosted agents
						InformedCandidature<Contract, ActionSpec> c = 
								this.generateDestructionContract(s.getMyAgentIdentifier());
						c.setSpecification(getMyAgent().getMySpecif(getMyAgent().getMyCurrentState(), c));
						c.setSpecification(s);
						hosted.add(c);
					}				

					Collection<InformedCandidature<Contract, ActionSpec>> ugradingContracts = generateUpgradingContracts(currentState, rejected, hosted, myCore, contracts);
					propCore.addContractsToPropose(ugradingContracts);
					if (!ugradingContracts.isEmpty()){
						logMonologue("upgrading contracts founds! "+ugradingContracts, AbstractCommunicationProtocol.log_selectionStep);
						logMonologue("yyeeeeaaaaahhhhhh!!!!!",LogService.onScreen);
					}
				}

			} else {
				//Trying to accept reallocating contracts	
				assert !contracts.getReallocationContracts().isEmpty();

				ReallocationContract<Contract, ActionSpec> r = 
						contracts.getBestRequestableReallocationContract(
								myCore.getReferenceAllocationComparator(getMyAgent().getMyCurrentState()));

				if (r!=null){//upgrading contract available
					logMonologue(
							"heeelllll yyeeeeaaaaahhhhhh!!!!!", AbstractCommunicationProtocol.log_selectionStep);
					logMonologue(
							"heeelllll yyeeeeaaaaahhhhhh!!!!!", LogService.onScreen);
					for (Contract c : r){
						try {
							accepted.add(contracts.getContract(c.getIdentifier()));
						} catch (UnknownContractException e) {
							e.printStackTrace();
						}
					}

					rejected.removeAll(accepted);


				} else {
					rejected.clear();
					logMonologue(
							"booooooooooooooooooooooooooouuuuuuuuuuuuuuuuuuuuuuuuhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh\n", AbstractCommunicationProtocol.log_selectionStep);
				}			
			}
		}

		/*
		 * Instanciating returned contract trunk 		
		 */

		ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState> returned = 
				new ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState>(getMyAgent());

		assert validityVerification(given, accepted, rejected);


		// ACCEPTATION
		for (final InformedCandidature<Contract, ActionSpec> c : accepted) {
			returned.addContract(c);
			returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
		}

		// REFUS
		for (final InformedCandidature<Contract, ActionSpec> c : rejected) {
			returned.addContract(c);
			returned.addRejection(this.getMyAgent().getIdentifier(), c);
		}

		return returned;
	}


	//
	// Abstract
	//


	protected abstract InformedCandidature<Contract, ActionSpec> generateDestructionContract(AgentIdentifier id);

	//
	// Primitives
	//

	private Collection<InformedCandidature<Contract, ActionSpec>>  generateUpgradingContracts(
			final PersonalState currentState, 
			Collection<InformedCandidature<Contract, ActionSpec>> unacceptedContracts,
			Collection<InformedCandidature<Contract, ActionSpec>> hosted,
			InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myAgentCore,
			final ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState> myAgentContractTrunk) {

		assert allCreation(unacceptedContracts):unacceptedContracts+" "+myAgentContractTrunk;

		//Generating new proposals

		//generating concerned : concerned is the set of atomic candidature that can be changed
		Collection<InformedCandidature<Contract, ActionSpec>> toPropose =
				new ArrayList<InformedCandidature<Contract,ActionSpec>>();
		final Collection<InformedCandidature<Contract, ActionSpec>> concerned =
				new HashSet<InformedCandidature<Contract, ActionSpec>>();

		concerned.addAll(unacceptedContracts);//adding allocation candidature
		concerned.addAll(hosted);//adding allocation candidature		

		assert allComplete(concerned):concerned+" "+myAgentContractTrunk;

		//generating allocgen : allocgen contains the set of upgrading reallocation contracts
		final Collection<Collection<InformedCandidature<Contract, ActionSpec>>> allocGen =
				new HyperSetGeneration<InformedCandidature<Contract, ActionSpec>>(concerned) {
			@Override
			public boolean toKeep(final Collection<InformedCandidature<Contract, ActionSpec>> alloc) {
				return ResourceInformedSelectionCore.this.getMyAgent().Iaccept(currentState,alloc);
			}
		}.getHyperset();

		Set<InformedCandidature<Contract, ActionSpec>> contractsToKeep = new HashSet();		
		for (final Collection<InformedCandidature<Contract, ActionSpec>> realloc : allocGen){
			for (final InformedCandidature<Contract, ActionSpec> c : realloc)
				contractsToKeep.add(c);
		}

		//MAJ du contract trunk
		for (final InformedCandidature<Contract, ActionSpec> c : contractsToKeep)
			//Pour toute action de ce contrat
			if (!c.isMatchingCreation()){//si cette action est un contrat de destruction 
				//on l'ajoute a la base de contrat
				myAgentContractTrunk.addContract(c);
				//Ajout aux propositions à faire
				toPropose.add(c);
			} else {//sinon on la laisse en attente
				assert unacceptedContracts.contains(c):unacceptedContracts;
				unacceptedContracts.remove(c);
			}

		for (final Collection<InformedCandidature<Contract, ActionSpec>> realloc : allocGen){
			//Ajout du contrat améliorant
			// --- > Création du contrat
			final ArrayList<Contract> actions = new ArrayList<Contract>();
			assert getMyAgent().Iaccept(getMyAgent().getMyCurrentState(), realloc);
			for (final InformedCandidature<Contract, ActionSpec> c : realloc){
				actions.add(c.getCandidature());				
			}
			//-->ajout
			
			myAgentContractTrunk.addReallocContract(
					new ReallocationContract<Contract, ActionSpec>(
							this.getIdentifier(),
							actions));
		}			

		for (final InformedCandidature<Contract, ActionSpec> c : toPropose){
			//Pour toute action de ce contrat
			assert (!c.isMatchingCreation());//cette action est un contrat de destruction :
			//on lui associe la meilleur réalloc et on l'ajoute au contrat à proposer
			ReallocationContract<Contract, ActionSpec> best = 
					myAgentContractTrunk.getBestReallocationContract(
							c, myAgentCore.getReferenceAllocationComparator(currentState));
			assert best!=null;
			c.getPossibleContracts().clear();
			//en ajoutant le best des realloc qui ont été généré à l'itération précédente
			c.getPossibleContracts().add(best);
		}

		return toPropose;
	}

	//
	// Validity verification
	//

	private boolean allViable(Collection<InformedCandidature<Contract, ActionSpec>> contracts) 
			throws IncompleteContractException{
		for (InformedCandidature<Contract, ActionSpec> c : contracts){
			if (!c.getCandidature().isViable())
				return false;
		}
		return true;
	}
	private boolean allCreation(Collection<InformedCandidature<Contract, ActionSpec>> contracts){
		for (InformedCandidature<Contract, ActionSpec> c : contracts){
			if (!c.getCandidature().isMatchingCreation())
				return false;
		}
		return true;
	}
	private boolean allComplete(Collection<InformedCandidature<Contract, ActionSpec>> contracts){
		for (InformedCandidature<Contract, ActionSpec> c : contracts){
			for (AgentIdentifier id : c.getCandidature().getAllParticipants())
				try {
					c.getCandidature().computeResultingState(id);
				} catch (IncompleteContractException e) {
					return false;
				}
		}
		return true;
	}

	private boolean validityVerification(
			ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> given,
			Collection<InformedCandidature<Contract, ActionSpec>> accepted,
			Collection<InformedCandidature<Contract, ActionSpec>> rejected) {

		for (InformedCandidature<Contract, ActionSpec> c : accepted){
			if (rejected.contains(c))
				return false;
		}
		for (InformedCandidature<Contract, ActionSpec> c : rejected){
			if (accepted.contains(c))
				return false;
		}


		return true;
	}

}

//	private boolean validityVerification(
//			ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> given,
//			final Collection<InformedCandidature<Contract, ActionSpec>> accepted,
//			final Collection<InformedCandidature<Contract, ActionSpec>> notAccepted) {
//		//		logMonologue("accepeted "+accepted+" refused "+notAccepted, LogService.onBoth);
//
//		// verification de validit�� d'appel
//		final Collection<InformedCandidature<Contract, ActionSpec>> test = 
//				new ArrayList<InformedCandidature<Contract, ActionSpec>>();
//		test.addAll(accepted);
//		test.addAll(notAccepted);
//		//		test.addAll(onWait);
//
//		final Collection<InformedCandidature<Contract, ActionSpec>> allContracts = 
//				new ArrayList<InformedCandidature<Contract, ActionSpec>>();
//		allContracts.addAll(given.getInitiatorRequestableContracts());
//		allContracts.addAll(given.getParticipantOnWaitContracts());
//		allContracts.addAll(given.getInitiatorOnWaitContracts());
//
//		assert (test.containsAll(allContracts) && allContracts.containsAll(test)):
//			"mauvaise implementation du selection core (1)";
//		assert (allContracts.containsAll(accepted)):"mauvaise implementation du selection core (2)\n all contracts : "
//		+ allContracts
//		+ "\n accepted : "+accepted;
//		for (final InformedCandidature<Contract, ActionSpec> c : notAccepted) {
//			assert (allContracts.contains(c) || given.getOnWaitContracts().contains(c)):
//				"mauvaise implementation du selection core (3)";
//			assert (!accepted.contains(c)):"mauvaise implementation du selection core (4)";
//		}
//		return true;
//	}
//
//@Override
//public ContractTrunk<InformedCandidature<Contract, ActionSpec>,ActionSpec,  PersonalState> select(
//		ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> cs) {
//	assert cs instanceof ResourceInformedCandidatureContractTrunk;
//	ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState> ct = 
//			(ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState>) cs;
//	InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myCore = 
//			(InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) getMyAgent().getMyCore();
//	ResourceUpgradingInformedProposerCore<Contract, ActionSpec, PersonalState> myProposerCore =
//			(ResourceUpgradingInformedProposerCore<Contract, ActionSpec, PersonalState>) getMyAgent().getMyProposerCore();
//
//	ContractTrunk<InformedCandidature<Contract, ActionSpec>,ActionSpec,  PersonalState> returned = 
//			new ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState>(getMyAgent());
//
//	Collection<InformedCandidature<Contract, ActionSpec>> accepted = new HashSet<InformedCandidature<Contract, ActionSpec>>();
//	Collection<InformedCandidature<Contract, ActionSpec>> rejected = new HashSet<InformedCandidature<Contract, ActionSpec>>() ;
//
//
//	// Verification de la consistance
//	assert (this.getMyAgent().getMyCurrentState().isValid()):
//		"what the  (1)!!!!!!"
//		+ this.getMyAgent().getMyCurrentState();
//
//	// Mis à jour de l'état si tous les agents ayant été accepter
//	// confirmaient :
//	final PersonalState currentState = this.getMyAgent()
//			.getMyResultingState(
//					this.getMyAgent().getMyCurrentState(),
//					ct.getContractsAcceptedBy(getIdentifier()));
//	// Verification de la consistance
//	assert (currentState.isValid()):
//		"what the  (2)!!!!!!" + currentState+"\n ACCEPTED \n"+ct.getContractsAcceptedBy(getIdentifier())+"\n GIVEN \n"+ct;
//
//
//	for (InformedCandidature<Contract, ActionSpec> c : ct.getAllContracts())
//		if (getMyAgent().Iaccept(getMyAgent(), c))
//
//
//
//
//
//
//
//
//
//
//
//			assert	this.validityVerification(cs, accepted, rejected);
//
//			// ACCEPTATION
//			for (final InformedCandidature<Contract, ActionSpec> c : accepted) {
//				returned.addContract(c);
//				returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
//			}
//
//			// REFUS
//			for (final InformedCandidature<Contract, ActionSpec> c : rejected) {
//				returned.addContract(c);
//				returned.addRejection(this.getMyAgent().getIdentifier(), c);
//			}
//
//
//
//			return returned;
//
//
//
//			rejected.addAll(ct.getAllInitiatorContracts());
//			rejected.addAll(ct.getParticipantOnWaitContracts());
//			rejected.addAll(ct.getParticipantAlreadyAcceptedContracts());
//
//			try {
//				ReallocationContract<Contract, ActionSpec> r = 
//						ct.getBestRequestableReallocationContracts(
//								myCore.getReferenceAllocationComparator(getMyAgent().getMyCurrentState()));
//				if (r!=null)
//					for (Contract c : r){
//						accepted.add(ct.getContract(c.getIdentifier()));
//					}
//				else {
//					assert ct.getRequestableContracts().isEmpty():
//						ct.getRequestableContracts()+" \n ------------"+ct+" \n ------------"+ct.upgradingContracts;
//				}
//			} catch (UnknownContractException e) {
//				throw new RuntimeException(e);
//			}
//
//			rejected.removeAll(accepted);
//
//
//			//		this.logMonologue("Setting my answer "+returned, CommunicationProtocol.log_selectionStep);
//			//		this.notify(new IllAnswer<PersonalState,  InformedCandidature<Contract,ActionSpec>>(returned, getMyAgent().getMyCurrentState()));
//			//		this.logMonologue("After being delaed by relevant services "+returned, CommunicationProtocol.log_selectionStep);
//
//}
//
//
