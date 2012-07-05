package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import negotiation.faulttolerance.experimentation.SearchTimeNotif;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import negotiation.negotiationframework.exploration.AllocationSolver;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public abstract class ResourceInformedSelectionCore <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements SelectionCore<
SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>,
ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> {
	private static final long serialVersionUID = 5994721006483536151L;

	final AllocationSolver<Contract, ActionSpec, PersonalState> solver;
	final int kMax;
	final long maxComputingTime;

	Random rand = new Random();

	//
	// Methods
	//

	public ResourceInformedSelectionCore(
			final AllocationSolver<Contract, ActionSpec, PersonalState> solver,
			final int kMax,
			long maxComputingTime)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.solver = solver;
		this.kMax=kMax;
		this.maxComputingTime=maxComputingTime;
	}


	@Override
	public void select(
			final ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState> given,
			final Collection<InformedCandidature<Contract,ActionSpec>> accepted,
			final Collection<InformedCandidature<Contract,ActionSpec>> rejected,
			final Collection<InformedCandidature<Contract,ActionSpec>> onWait)  {

		assert accepted.isEmpty();
		assert rejected.isEmpty();
		assert onWait.isEmpty();

		// Verification de la consistance
		assert this.getMyAgent().getMyCurrentState().isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert given.getParticipantAlreadyAcceptedContracts().isEmpty();

		//objects
		final ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState> contracts =
				(ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState>) given;
		final InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myCore =
				(InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) this.getMyAgent().getMyCore();

		//contract lists
		final List<InformedCandidature<Contract,ActionSpec>> allContracts = contracts.getAllContracts();
		PersonalState currentState = this.getMyAgent().getMyCurrentState();
		assert ContractTransition.allComplete(allContracts):allContracts;

		rejected.addAll(allContracts);

		//Beginninng//


		if (allContracts.isEmpty()) {
			this.logMonologue("i support everyone yeah! =)", AbstractCommunicationProtocol.log_selectionStep);
		} else //There is new agent who want to be allocated!!!
			if (MatchingCandidature.areAllCreation(allContracts)){//I'm not currently negotiating reallocations
				assert !contracts.hasReallocationContracts():contracts.getReallocationContracts()+"\n ---> "+allContracts;

				//Trying to accept simple candidatures
				//accepting as many as possible*
				//				final RooletteWheel<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> r =
				//						new RooletteWheel<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>(
				//								this.getMyAgent(), allContracts);
				this.logMonologue("trying to accept simple nego",
						AbstractCommunicationProtocol.log_selectionStep);
				final Iterator<InformedCandidature<Contract, ActionSpec>>r = allContracts.iterator();
				while (r.hasNext()){
					final InformedCandidature<Contract, ActionSpec> c = r.next();//r.popNextContract();
					if (this.getMyAgent().Iaccept(currentState, c)){
						this.logMonologue("i accept "+c+" (my state is "+currentState+")",
								AbstractCommunicationProtocol.log_selectionStep);
						rejected.remove(c);
						accepted.add(c);
						try {
							currentState=c.computeResultingState(currentState);
						} catch (final IncompleteContractException e) {
							this.signalException("impossible", e);
						}
					} else {
						this.logMonologue("i refuse "+c+" (my state is "+currentState+")",
								AbstractCommunicationProtocol.log_selectionStep);
					}
				}

				if (accepted.isEmpty()){// I accepted noone, trying to find realloc

					assert currentState.equals(getMyAgent().getMyCurrentState());
					this.logMonologue("no contracts accepted : searching upgrading contracts! : \nhosted :"
							+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()
							+"\n required "+rejected,
							AbstractCommunicationProtocol.log_selectionStep);
					//some unaccepted contract : generating upgrading contract and adding to propose

					final ResourceInformedProposerCore<Contract, ActionSpec, PersonalState> propCore =
							(ResourceInformedProposerCore<Contract, ActionSpec, PersonalState>) this.getMyAgent().getMyProposerCore();

					//					final Collection<InformedCandidature<Contract, ActionSpec>> hosted =
					//							new HashSet<InformedCandidature<Contract, ActionSpec>>();



					assert MatchingCandidature.assertAllCreation(contracts.getAllContracts());
					assert AbstractCommunicationProtocol.partitioning(contracts.getAllContracts(), accepted, rejected, onWait);
					final Collection<InformedCandidature<Contract, ActionSpec>> ugradingContracts =
							this.generateUpgradingContracts(rejected, onWait, myCore, contracts);
					propCore.addContractsToPropose(ugradingContracts);

					assert AbstractCommunicationProtocol.partitioning(given.getAllContracts(), accepted, rejected, onWait);

					if (!ugradingContracts.isEmpty()){
						//						this.logMonologue("upgrading contracts founds! yyeeeeaaaaahhhhhh!!!!!"+contracts.getReallocationContracts(),
						//								AbstractCommunicationProtocol.log_selectionStep);
						this.logMonologue("upgrading contracts founds! yyeeeeaaaaahhhhhh!!!!!",
								LogService.onScreen);
					} else {
						this.logMonologue("NO upgrading contracts founds!",
								AbstractCommunicationProtocol.log_selectionStep);
					}
				}

			} else {//I'm currently  negotiating reallocations
				//Trying to accept reallocating contracts
				assert !contracts.getReallocationContracts().isEmpty() || !contracts.getContractToCancel().isEmpty();

				if (contracts.hasReallocationContracts()){
					final ReallocationContract<Contract, ActionSpec> r =
							contracts.getBestRequestableReallocationContract();

					if (r!=null){//upgrading contract available
						this.logMonologue(
								"upgrading contracts applied! heeelllll yyeeeeaaaaahhhhhh!!!!!", AbstractCommunicationProtocol.log_selectionStep);
						this.logMonologue(
								"heeelllll yyeeeeaaaaahhhhhh!!!!!", LogService.onScreen);
						for (final Contract c : r) {
							try {
								rejected.remove(contracts.getContract(c.getIdentifier()));
								accepted.add(contracts.getContract(c.getIdentifier()));
							} catch (final UnknownContractException e) {
								e.printStackTrace();
							}
						}
					} else {
						onWait.addAll(rejected);
						rejected.clear();
						this.logMonologue(
								"booooooooooooooooooooooooooouuuuuuuuuuuuuuuuuuuuuuuuhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh\n",
								AbstractCommunicationProtocol.log_selectionStep);
						//					this.logMonologue(
						//							"booooooooooooooooooooooooooouuuuuuuuuuuuuuuuuuuuuuuuhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh\n",
						//							LogService.onScreen);
					}
				}

				//				rejected.addAll(contracts.getContractToCancel());
				//				contracts.getContractToCancel().clear();
			}

		try {
			for (final InformedCandidature<Contract, ActionSpec> i : accepted) {
				if (i.isMatchingCreation()){
					this.getMyAgent().getMyInformation().add(
							i.computeResultingState(i.getAgent()));
					//					this.observe(i.getAgent(),
					//							SimpleObservationService.informationObservationKey);
				}else{
					this.getMyAgent().getMyInformation().remove(
							i.computeResultingState(i.getAgent()));
					//					this.stopObservation(i.getAgent(),
					//							SimpleObservationService.informationObservationKey);
				}
			}
		} catch (final IncompleteContractException e) {
			this.signalException("solver failed!!!!!!!!!!!!!!!!!!!!", e);
		}
	}


	//
	// Abstract
	//


	protected abstract InformedCandidature<Contract, ActionSpec> generateDestructionContract(AgentIdentifier id);


	//
	// Primitives
	//

	private Collection<InformedCandidature<Contract, ActionSpec>>  generateUpgradingContracts(
			//			final PersonalState currentState,
			final Collection<InformedCandidature<Contract, ActionSpec>> unacceptedContracts,
			final Collection<InformedCandidature<Contract,ActionSpec>> onWait,
			final InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myAgentCore,
			final ResourceInformedCandidatureContractTrunk<Contract, ActionSpec, PersonalState> myAgentContractTrunk) {

		assert MatchingCandidature.assertAllCreation(unacceptedContracts):unacceptedContracts+" "+myAgentContractTrunk;
		assert onWait.isEmpty();

		//Generating new proposals

		//generating concerned : concerned is the set of atomic candidature that can be changed
		final Collection<InformedCandidature<Contract, ActionSpec>> toPropose =
				new ArrayList<InformedCandidature<Contract,ActionSpec>>();
		final Map<Contract,InformedCandidature<Contract,ActionSpec>> concerned =
				new HashMap<Contract,InformedCandidature<Contract,ActionSpec>>();

		List<Contract> dealloc = new ArrayList<Contract>();
		for (final InformedCandidature<Contract,ActionSpec> c : unacceptedContracts){
			assert c.isMatchingCreation();
			concerned.put(c.getCandidature(),c);//adding allocation candidature
			dealloc.add(c.getCandidature());
		}

		List<Contract> alloc = new ArrayList<Contract>();
		for (final ActionSpec s : getMyAgent().getMyResources()){
			assert s.getMyResourceIdentifiers().contains(getIdentifier());
			assert getMyAgent().getMyCurrentState().getMyResourceIdentifiers().contains(s.getMyAgentIdentifier());
			final InformedCandidature<Contract, ActionSpec> d =
					this.generateDestructionContract(s.getMyAgentIdentifier());
			d.setSpecification(this.getMyAgent().getMySpecif(getMyAgent().getMyCurrentState(), d));
			d.setSpecification(s);
			alloc.add(d.getCandidature());
			concerned.put(d.getCandidature(),d);//adding destruction candidature
		}
		//		for (final ActionSpec s : this.getMyAgent().getMyResources()){
		//			//adding destruction of hosted agents
		//			final InformedCandidature<Contract, ActionSpec> c =
		//					this.generateDestructionContract(s.getMyAgentIdentifier());
		//			c.setSpecification(this.getMyAgent().getMySpecif(this.getMyAgent().getMyCurrentState(), c));
		//			c.setSpecification(s);
		//			concerned.put(c.getCandidature(),c);//adding destruction candidature
		//		}
		//		for (final ActionSpec s : this.getMyAgent().getMyResources()){
		//			final InformedCandidature<Contract, ActionSpec> d =
		//					this.generateDestructionContract(s.getMyAgentIdentifier());
		//			d.setSpecification(this.getMyAgent().getMySpecif(currentState, d));
		//			d.setSpecification(s);
		//			concerned.put(d.getCandidature(),d);//adding destruction candidature
		//		}

		assert ContractTransition.allComplete(concerned.values()):concerned+" "+myAgentContractTrunk;

		Set<Contract> kConcerned = new HashSet<Contract>();
		int nextInt;
		//		List<Contract> allConcerned = new ArrayList<Contract>(concerned.keySet());

		//Adding one alloc
		if (!alloc.isEmpty()){
			nextInt = rand.nextInt(alloc.size());
			kConcerned.add(alloc.get(nextInt));
			alloc.remove(alloc.get(nextInt));
		}

		//Adding one dealloc
		if (!dealloc.isEmpty()){
			nextInt = rand.nextInt(dealloc.size());
			kConcerned.add(dealloc.get(nextInt));
			dealloc.remove(dealloc.get(nextInt));
		}

		while (kConcerned.size()<kMax){// && !allConcerned.isEmpty()){
			if (dealloc.isEmpty() && alloc.isEmpty()){
				break;
			} else if (dealloc.isEmpty() && !alloc.isEmpty()){
				nextInt = rand.nextInt(alloc.size());
				kConcerned.add(alloc.get(nextInt));
				alloc.remove(nextInt);
			} else if (!dealloc.isEmpty() && alloc.isEmpty()){
				nextInt = rand.nextInt(dealloc.size());
				kConcerned.add(dealloc.get(nextInt));
				dealloc.remove(nextInt);
			} else {
				if (rand.nextBoolean()){
					nextInt = rand.nextInt(alloc.size());
					kConcerned.add(alloc.get(nextInt));
					alloc.remove(nextInt);					
				} else {
					nextInt = rand.nextInt(dealloc.size());
					kConcerned.add(dealloc.get(nextInt));
					dealloc.remove(nextInt);					
				}
			}			
		}


		//generating allocgen : allocgen contains the set of upgrading reallocation contracts
		try {

			this.solver.initiate(kConcerned);
			solver.setTimeLimit((int) (maxComputingTime));
			Set<InformedCandidature<Contract, ActionSpec>> alreadyDone =
					new HashSet<InformedCandidature<Contract,ActionSpec>>();
			Date startingExploringTime = new Date();
//			logWarning("beginning exploration");
			while (this.solver.hasNext() && (new Date().getTime() - startingExploringTime.getTime()<maxComputingTime)){
				final Collection<Contract> realloc = this.solver.getNextSolution();
				if (!realloc.isEmpty()){
					final Set<InformedCandidature<Contract, ActionSpec>> contractsToKeep =
							new HashSet<InformedCandidature<Contract, ActionSpec>>();
					for (final Contract c : realloc) {
						contractsToKeep.add(concerned.get(c));
					}
					//					assert isImprovment(contractsToKeep);
					if (this.getMyAgent().isAnImprovment(getMyAgent().getMyCurrentState(), contractsToKeep)){
						//MAJ du contract trunk
						for (final InformedCandidature<Contract, ActionSpec> c : contractsToKeep) {
							//Pour toute action de ce contrat
							if (!c.isMatchingCreation()){//si cette action est un contrat de destruction
								//on l'ajoute a la base de contrat
								myAgentContractTrunk.addContract(c);
								onWait.add(c);
								//Ajout aux propositions à faire
								//						try { NON CAR L'HOTE PEUT ENVOYER DES DEMANDES DE TUAGE, DES FOIS QUE L4AGENT POURRAIT SE RETOURNER!!
								//							assert c.isViable();
								//						} catch (final IncompleteContractException e) {
								//							this.getMyAgent().signalException("impossible");
								//						}
								toPropose.add(c);
							} else {//sinon on la laisse en attente
								assert (!alreadyDone.add(c)||unacceptedContracts.contains(c)):unacceptedContracts;
								unacceptedContracts.remove(c);
								onWait.add(c);
							}
						}

						//Ajout du contrat améliorant
						// --- > Création du contrat
						myAgentContractTrunk.addReallocContract(new ReallocationContract<Contract, ActionSpec>(
								this.getIdentifier(),
								realloc));
					}
				}
			}
//			logWarning("ending exploration, time : "+(new Date().getTime() - startingExploringTime.getTime()));
			notify(new SearchTimeNotif(new Double(new Date().getTime() - startingExploringTime.getTime())));
			for (final InformedCandidature<Contract, ActionSpec> c : toPropose){
				//Pour toute action de ce contrat
				assert !c.isMatchingCreation();//cette action est un contrat de destruction :
				//on lui associe la meilleur réalloc et on l'ajoute au contrat à proposer
				final ReallocationContract<Contract, ActionSpec> best =
						myAgentContractTrunk.getBestReallocationContract(c);
				//				final ReallocationContract<Contract, ActionSpec> best =
				//						myAgentContractTrunk.getBestReallocationContract(
				//								c, myAgentCore.getReferenceAllocationComparator(getMyAgent().getMyCurrentState()));
				assert best!=null;
				c.getPossibleContracts().clear();
				//en ajoutant le best des realloc qui ont été généré à l'itération précédente
				c.getPossibleContracts().add(best);
			}
//			logWarning("ending exploration 2, time : "+(new Date().getTime() - startingExploringTime.getTime()));

		}catch (Throwable e){
			signalException("solver failed",e); 
		}
		return toPropose;
	}


	private boolean isImprovment(
			Set<InformedCandidature<Contract, ActionSpec>> contractsToKeep) {
		assert getMyAgent().isPersonalyValid(getMyAgent().getMyCurrentState(), contractsToKeep);
		//		assert getMyAgent().evaluatePreference(new ArrayList<InformedCandidature<Contract,ActionSpec>>())<getMyAgent().evaluatePreference(contractsToKeep):getMyAgent().evaluatePreference()+" "+getMyAgent().evaluatePreference(contractsToKeep);
		assert this.getMyAgent().isAnImprovment(getMyAgent().getMyCurrentState(), contractsToKeep):
			getMyAgent().getMyCurrentState()+" \n"+contractsToKeep+"\n donne -------> "
			+getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), contractsToKeep)
			+"\n-------------->"+
			(getMyAgent().getMyCore().getAllocationPreference(contractsToKeep, 
					new ArrayList<InformedCandidature<Contract, ActionSpec>>()));
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
