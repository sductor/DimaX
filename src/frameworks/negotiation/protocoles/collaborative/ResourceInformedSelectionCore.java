package frameworks.negotiation.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.experimentation.SearchTimeNotif;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ReallocationContract;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.selection.SelectionModule;

public abstract class ResourceInformedSelectionCore <
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends
BasicAgentCompetence<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>>
implements SelectionCore<
NegotiatingAgent<PersonalState, InformedCandidature<Contract>>,
PersonalState,
InformedCandidature<Contract>> {
	private static final long serialVersionUID = 5994721006483536151L;

	final ResourceAllocationSolver<Contract, PersonalState> solver;
	final int kMax;
	final long maxComputingTime;
	final SelectionModule gsm;

	Random rand = new Random();

	//
	// Methods
	//

	public ResourceInformedSelectionCore(
			final ResourceAllocationSolver<Contract, PersonalState> solver,
			final int kMax,
			final SelectionModule<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>,PersonalState, InformedCandidature<Contract>> initialSelectionType,
			final long maxComputingTime)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.solver = solver;
		this.kMax=kMax;
		this.gsm=initialSelectionType;
		this.maxComputingTime=maxComputingTime;
	}


	@Override
	public void select(
			final ContractTrunk<InformedCandidature<Contract>> given,
			PersonalState currentState,
			final Collection<InformedCandidature<Contract>> accepted,
			final Collection<InformedCandidature<Contract>> rejected,
			final Collection<InformedCandidature<Contract>> onWait)  {

		assert accepted.isEmpty();
		assert rejected.isEmpty();
		assert onWait.isEmpty();

		// Verification de la consistance
		assert currentState.isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert given.getParticipantAlreadyAcceptedContracts().isEmpty();

		//objects
		final ResourceInformedCandidatureContractTrunk<Contract> contracts =
				(ResourceInformedCandidatureContractTrunk<Contract>) given;
		final InformedCandidatureRationality<PersonalState, Contract> myCore =
				(InformedCandidatureRationality<PersonalState, Contract>) this.getMyAgent().getMyCore();

		//contract lists
		final Collection<InformedCandidature<Contract>> allContracts = contracts.getAllContracts();
		assert ContractTransition.allComplete(allContracts):allContracts;

		rejected.addAll(allContracts);

		//Beginninng//

		if (allContracts.isEmpty()) {
			this.logMonologue("i support everyone yeah! =)", AbstractCommunicationProtocol.log_selectionStep);
		} else //There is new agent who want to be allocated!!!
			if (MatchingCandidature.areAllCreation(allContracts)){//I'm not currently negotiating reallocations
				assert !contracts.hasReallocationContracts():contracts.getReallocationContracts()+"\n\n -------------------> \n"+allContracts;

				//Trying to accept simple candidatures
				//accepting as many as possible*
				//				final RooletteWheel<ActionSpec, PersonalState, InformedCandidature<Contract>> r =
				//						new RooletteWheel<ActionSpec, PersonalState, InformedCandidature<Contract>>(
				//								this.getMyAgent(), allContracts);
				this.logMonologue("trying to accept simple nego",
						AbstractCommunicationProtocol.log_selectionStep);
				final boolean useSelectionModule=true;//for debugging
				if (!useSelectionModule){
					final Iterator<InformedCandidature<Contract>>r = allContracts.iterator();
					while (r.hasNext()){
						final InformedCandidature<Contract> c = r.next();//r.popNextContract();
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
				} else {
					//					final GreedySelectionModule<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>,PersonalState, InformedCandidature<Contract>> gsm =
					//							new GreedySelectionModule<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>,PersonalState, InformedCandidature<Contract>>(this.initialSelectionType);
					this.gsm.setMyAgent(this.getMyAgent());
					accepted.addAll(this.gsm.selection(currentState, allContracts));
					try {
						currentState=ReallocationContract.computeResultingState(currentState, accepted);
					} catch (final IncompleteContractException e) {
						this.signalException("impossible", e);
					}
					this.logMonologue("i accept "+accepted+" (my state is "+currentState+")",
							AbstractCommunicationProtocol.log_selectionStep);
					rejected.removeAll(accepted);
				}

				if (accepted.isEmpty()){// I accepted noone, trying to find realloc

					assert currentState.equals(this.getMyAgent().getMyCurrentState());
					this.logMonologue("no contracts accepted : searching upgrading contracts! : \nhosted :"
							+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()
							+"\n required "+rejected,
							AbstractCommunicationProtocol.log_selectionStep);
					//some unaccepted contract : generating upgrading contract and adding to propose

					final ResourceInformedProposerCore<Contract, PersonalState> propCore =
							(ResourceInformedProposerCore<Contract, PersonalState>) this.getMyAgent().getMyProposerCore();

					//					final Collection<InformedCandidature<Contract>> hosted =
					//							new HashSet<InformedCandidature<Contract>>();



					assert MatchingCandidature.assertAllCreation(contracts.getAllContracts());
					assert AbstractCommunicationProtocol.partitioning(contracts.getAllContracts(), accepted, rejected, onWait);
					final Collection<InformedCandidature<Contract>> ugradingContracts =
							this.generateUpgradingContracts(rejected, onWait, myCore, contracts);
					propCore.addContractsToPropose(ugradingContracts);

					assert AbstractCommunicationProtocol.partitioning(given.getAllContracts(), accepted, rejected, onWait);

					if (!ugradingContracts.isEmpty()){
						//						this.logMonologue("upgrading contracts founds! yyeeeeaaaaahhhhhh!!!!!"+contracts.getReallocationContracts(),
						//								AbstractCommunicationProtocol.log_selectionStep);
						this.logMonologue("upgrading contracts founds! yyeeeeaaaaahhhhhh!!!!!",
								LogService.onBoth);
					} else {
						this.logMonologue("NO upgrading contracts founds!",
								AbstractCommunicationProtocol.log_selectionStep);
					}
				}

			} else {//I'm currently  negotiating reallocations
				//Trying to accept reallocating contracts
				assert contracts.hasReallocationContracts() || !contracts.getContractToCancel().isEmpty();


				if (contracts.hasReallocationContracts()){
					final ReallocationContract<Contract> r =
							contracts.getBestRequestableReallocationContract();


					if (r!=null){//upgrading contract available
						this.logMonologue(
								"upgrading contracts applied! heeelllll yyeeeeaaaaahhhhhh!!!!!", AbstractCommunicationProtocol.log_selectionStep);
						this.logMonologue(
								"heeelllll yyeeeeaaaaahhhhhh!!!!!", LogService.onBoth);
						for (final Contract c : r) {
							try {
								rejected.remove(contracts.getContract(c.getContractIdentifier()));
								accepted.add(contracts.getContract(c.getContractIdentifier()));
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
			for (final InformedCandidature<Contract> i : accepted) {
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


	protected abstract InformedCandidature<Contract> generateDestructionContract(
			AgentIdentifier id);


	protected abstract  void setSpecif(
			AgentState s,
			InformedCandidature<Contract> d);

	//
	// Primitives
	//

	private Collection<InformedCandidature<Contract>>  generateUpgradingContracts(
			//			final PersonalState currentState,
			final Collection<InformedCandidature<Contract>> unacceptedContracts,
			final Collection<InformedCandidature<Contract>> onWait,
			final InformedCandidatureRationality<PersonalState, Contract> myAgentCore,
			final ResourceInformedCandidatureContractTrunk<Contract> myAgentContractTrunk) {

		assert MatchingCandidature.assertAllCreation(unacceptedContracts):unacceptedContracts+" "+myAgentContractTrunk;
		assert onWait.isEmpty();

		//Generating new proposals

		//generating concerned : concerned is the set of atomic candidature that can be changed
		final Collection<InformedCandidature<Contract>> toPropose =
				new ArrayList<InformedCandidature<Contract>>();
		final Map<Contract,InformedCandidature<Contract>> concerned =
				new HashMap<Contract,InformedCandidature<Contract>>();

		final List<Contract> dealloc = new ArrayList<Contract>();
		for (final InformedCandidature<Contract> c : unacceptedContracts){
			assert c.isMatchingCreation();
			concerned.put(c.getCandidature(),c);//adding allocation candidature
			dealloc.add(c.getCandidature());
		}

		final List<Contract> alloc = new ArrayList<Contract>();
		for (final AgentState s : this.getMyAgent().getMyResources()){
			assert s.getMyResourceIdentifiers().contains(this.getIdentifier());
			assert this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().contains(s.getMyAgentIdentifier());
			final InformedCandidature<Contract> d =
					this.generateDestructionContract(s.getMyAgentIdentifier());
			//setting my paramaters
			this.getMyAgent().setMySpecif(this.getMyAgent().getMyCurrentState(), d);
			d.setInitialState(this.getMyAgent().getMyCurrentState());
			//setting my ressource parameters
			d.setInitialState(s);
			this.setSpecif(s,d);
			//
			alloc.add(d.getCandidature());

			concerned.put(d.getCandidature(),d);//adding destruction candidature
		}
		//		for (final ActionSpec s : this.getMyAgent().getMyResources()){
		//			//adding destruction of hosted agents
		//			final InformedCandidature<Contract> c =
		//					this.generateDestructionContract(s.getMyAgentIdentifier());
		//			c.setSpecification(this.getMyAgent().getMySpecif(this.getMyAgent().getMyCurrentState(), c));
		//			c.setSpecification(s);
		//			concerned.put(c.getCandidature(),c);//adding destruction candidature
		//		}
		//		for (final ActionSpec s : this.getMyAgent().getMyResources()){
		//			final InformedCandidature<Contract> d =
		//					this.generateDestructionContract(s.getMyAgentIdentifier());
		//			d.setSpecification(this.getMyAgent().getMySpecif(currentState, d));
		//			d.setSpecification(s);
		//			concerned.put(d.getCandidature(),d);//adding destruction candidature
		//		}

		assert ContractTransition.allComplete(concerned.values()):concerned+" "+myAgentContractTrunk;

		final Set<Contract> kConcerned = new HashSet<Contract>();
		int nextInt;
		//		List<Contract> allConcerned = new ArrayList<Contract>(concerned.keySet());

		//Adding one alloc
		if (!alloc.isEmpty()){
			nextInt = this.rand.nextInt(alloc.size());
			kConcerned.add(alloc.get(nextInt));
			alloc.remove(alloc.get(nextInt));
		}

		//Adding one dealloc
		if (!dealloc.isEmpty()){
			nextInt = this.rand.nextInt(dealloc.size());
			kConcerned.add(dealloc.get(nextInt));
			dealloc.remove(dealloc.get(nextInt));
		}

		while (kConcerned.size()<this.kMax){// && !allConcerned.isEmpty()){
			if (dealloc.isEmpty() && alloc.isEmpty()){
				break;
			} else if (dealloc.isEmpty() && !alloc.isEmpty()){
				nextInt = this.rand.nextInt(alloc.size());
				kConcerned.add(alloc.get(nextInt));
				alloc.remove(nextInt);
			} else if (!dealloc.isEmpty() && alloc.isEmpty()){
				nextInt = this.rand.nextInt(dealloc.size());
				kConcerned.add(dealloc.get(nextInt));
				dealloc.remove(nextInt);
			} else {
				if (this.rand.nextBoolean()){
					nextInt = this.rand.nextInt(alloc.size());
					kConcerned.add(alloc.get(nextInt));
					alloc.remove(nextInt);
				} else {
					nextInt = this.rand.nextInt(dealloc.size());
					kConcerned.add(dealloc.get(nextInt));
					dealloc.remove(nextInt);
				}
			}
		}


		//generating allocgen : allocgen contains the set of upgrading reallocation contracts
		try {
			assert kConcerned!=null;
			this.solver.setProblem(kConcerned);
			this.solver.setTimeLimit((int) this.maxComputingTime);
			final Set<InformedCandidature<Contract>> alreadyDone =
					new HashSet<InformedCandidature<Contract>>();
			final Date startingExploringTime = new Date();
			//			logWarning("beginning exploration");
			while (this.solver.hasNext() && new Date().getTime() - startingExploringTime.getTime()<this.maxComputingTime){
				final Collection<Contract> realloc = this.solver.getNextLocalSolution();
				if (!realloc.isEmpty()){
					final Set<InformedCandidature<Contract>> contractsToKeep =
							new HashSet<InformedCandidature<Contract>>();
					for (final Contract c : realloc) {
						contractsToKeep.add(concerned.get(c));
					}
					//					assert isImprovment(contractsToKeep);
					if (this.getMyAgent().Iaccept(contractsToKeep)){
						//MAJ du contract trunk
						for (final InformedCandidature<Contract> c : contractsToKeep) {
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
								assert !alreadyDone.add(c)||unacceptedContracts.contains(c):unacceptedContracts;
								unacceptedContracts.remove(c);
								onWait.add(c);
							}
						}

						//Ajout du contrat améliorant
						// --- > Création du contrat
						myAgentContractTrunk.addReallocContract(new ReallocationContract<Contract>(
								this.getIdentifier(),
								realloc));
					}
				}
			}
			//			logWarning("ending exploration, time : "+(new Date().getTime() - startingExploringTime.getTime()));
			this.notify(new SearchTimeNotif(new Double(new Date().getTime() - startingExploringTime.getTime())));
			for (final InformedCandidature<Contract> c : toPropose){
				//Pour toute action de ce contrat
				assert !c.isMatchingCreation();//cette action est un contrat de destruction :
				//on lui associe la meilleur réalloc et on l'ajoute au contrat à proposer
				final ReallocationContract<Contract> best =
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

		}catch (final Throwable e){
			this.signalException("solver failed",e);
		}
		return toPropose;
	}



	private boolean isImprovment(
			final Set<InformedCandidature<Contract>> contractsToKeep) {
		assert this.getMyAgent().isPersonalyValid(this.getMyAgent().getMyCurrentState(), contractsToKeep);
		//		assert getMyAgent().evaluatePreference(new ArrayList<InformedCandidature<Contract>>())<getMyAgent().evaluatePreference(contractsToKeep):getMyAgent().evaluatePreference()+" "+getMyAgent().evaluatePreference(contractsToKeep);
		assert this.getMyAgent().Iaccept(contractsToKeep):
			this.getMyAgent().getMyCurrentState()+" \n"+contractsToKeep+"\n donne -------> "
			+this.getMyAgent().getMyResultingState(this.getMyAgent().getMyCurrentState(), contractsToKeep)
			+"\n-------------->"+

			this.getMyAgent().getMyCore().getAllocationPreference(contractsToKeep,
					new ArrayList<InformedCandidature<Contract>>());

		return true;
	}
}

//	private boolean validityVerification(
//			ContractTrunk<InformedCandidature<Contract>, PersonalState> given,
//			final Collection<InformedCandidature<Contract>> accepted,
//			final Collection<InformedCandidature<Contract>> notAccepted) {
//		//		logMonologue("accepeted "+accepted+" refused "+notAccepted, LogService.onBoth);
//
//		// verification de validit�� d'appel
//		final Collection<InformedCandidature<Contract>> test =
//				new ArrayList<InformedCandidature<Contract>>();
//		test.addAll(accepted);
//		test.addAll(notAccepted);
//		//		test.addAll(onWait);
//
//		final Collection<InformedCandidature<Contract>> allContracts =
//				new ArrayList<InformedCandidature<Contract>>();
//		allContracts.addAll(given.getInitiatorRequestableContracts());
//		allContracts.addAll(given.getParticipantOnWaitContracts());
//		allContracts.addAll(given.getInitiatorOnWaitContracts());
//
//		assert (test.containsAll(allContracts) && allContracts.containsAll(test)):
//			"mauvaise implementation du selection core (1)";
//		assert (allContracts.containsAll(accepted)):"mauvaise implementation du selection core (2)\n all contracts : "
//		+ allContracts
//		+ "\n accepted : "+accepted;
//		for (final InformedCandidature<Contract> c : notAccepted) {
//			assert (allContracts.contains(c) || given.getOnWaitContracts().contains(c)):
//				"mauvaise implementation du selection core (3)";
//			assert (!accepted.contains(c)):"mauvaise implementation du selection core (4)";
//		}
//		return true;
//	}
//
//@Override
//public ContractTrunk<InformedCandidature<Contract>,  PersonalState> select(
//		ContractTrunk<InformedCandidature<Contract>, PersonalState> cs) {
//	assert cs instanceof ResourceInformedCandidatureContractTrunk;
//	ResourceInformedCandidatureContractTrunk<Contract, PersonalState> ct =
//			(ResourceInformedCandidatureContractTrunk<Contract, PersonalState>) cs;
//	InformedCandidatureRationality<ActionSpec, PersonalState, Contract> myCore =
//			(InformedCandidatureRationality<ActionSpec, PersonalState, Contract>) getMyAgent().getMyCore();
//	ResourceUpgradingInformedProposerCore<Contract, PersonalState> myProposerCore =
//			(ResourceUpgradingInformedProposerCore<Contract, PersonalState>) getMyAgent().getMyProposerCore();
//
//	ContractTrunk<InformedCandidature<Contract>,  PersonalState> returned =
//			new ContractTrunk<InformedCandidature<Contract>, PersonalState>(getMyAgent());
//
//	Collection<InformedCandidature<Contract>> accepted = new HashSet<InformedCandidature<Contract>>();
//	Collection<InformedCandidature<Contract>> rejected = new HashSet<InformedCandidature<Contract>>() ;
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
//	for (InformedCandidature<Contract> c : ct.getAllContracts())
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
//			for (final InformedCandidature<Contract> c : accepted) {
//				returned.addContract(c);
//				returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
//			}
//
//			// REFUS
//			for (final InformedCandidature<Contract> c : rejected) {
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
//				ReallocationContract<Contract> r =
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
//			//		this.notify(new IllAnswer<PersonalState,  InformedCandidature<Contract>>(returned, getMyAgent().getMyCurrentState()));
//			//		this.logMonologue("After being delaed by relevant services "+returned, CommunicationProtocol.log_selectionStep);
//
//}
//
//
