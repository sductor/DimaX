package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.ProposerCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice.EventHookedMethod;
import dima.introspectionbasedagents.shells.NotReadyException;
import dimaxx.tools.HyperSetGeneration;

public abstract class ResourceManagerProposerCore<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec>extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>>>
implements
ProposerCore<
SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>,
ActionSpec,PersonalState,InformedCandidature<Contract,ActionSpec>>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2421623726781805642L;
	private final Collection<InformedCandidature<Contract, ActionSpec>> contractsToPropose =
			new HashSet<InformedCandidature<Contract, ActionSpec>>();

	@EventHookedMethod(IllAnswer.class)
	public void receiveFullNotification(final IllAnswer<PersonalState, InformedCandidature<Contract, ActionSpec>> n) {
		assert (n.getAgentState().equals(this.getMyAgent().getMyCurrentState()));
		assert n.getAnswers() instanceof InformedCandidatureContractTrunk;

		final InformedCandidatureContractTrunk<Contract, ActionSpec> myContractTrunk = (InformedCandidatureContractTrunk<Contract, ActionSpec>) n.getAnswers();
		final Collection<InformedCandidature<Contract, ActionSpec>> unacceptedContracts =
				n.getAnswers().getContractsRejectedBy(this.getIdentifier());
		final Map<AgentIdentifier,InformedCandidature<Contract, ActionSpec>> upgradingContracts =
				new HashMap<AgentIdentifier,InformedCandidature<Contract, ActionSpec>>();
		final Collection<InformedCandidature<Contract, ActionSpec>> newUpgradingContracts =
				new HashSet<InformedCandidature<Contract,ActionSpec>>();

		if (!this.getMyAgent().getMyProtocol().negotiationAsInitiatorHasStarted()
				&& !unacceptedContracts.isEmpty() && n.getAnswers().getContractsAcceptedBy(this.getIdentifier()).isEmpty()){
			//Generating new proposals

			//concerned is the set of atomic candidature that can be changed
			final Collection<InformedCandidature<Contract, ActionSpec>> concerned =
					new ArrayList<InformedCandidature<Contract, ActionSpec>>();
			concerned.addAll(unacceptedContracts);//adding allocation candidature
			for (final AgentIdentifier id : n.getAgentState().getMyResourceIdentifiers())
				//adding destruction of hosted agents
				concerned.add(this.generateDestructionContract(n.getAgentState(), id));

					//allocgen contains the set of upgrading reallocation contracts
					final Collection<Collection<InformedCandidature<Contract, ActionSpec>>> allocGen =
							new HyperSetGeneration<InformedCandidature<Contract, ActionSpec>>(concerned) {
						@Override
						public boolean toKeep(final Collection<InformedCandidature<Contract, ActionSpec>> alloc) {
							return ResourceManagerProposerCore.this.getMyAgent().Iaccept(n.getAgentState(), alloc);
						}
					}.getHyperset();


					for (final Collection<InformedCandidature<Contract, ActionSpec>> realloc : allocGen){
						//Pour tout contrat améliorant

						//MAJ du contract trunk
						for (final InformedCandidature<Contract, ActionSpec> c : realloc)
							//Pour toute action de ce contrat
							if (c.isMatchingCreation())
								myContractTrunk.removeRejection(this.getIdentifier(), c);//elle est remise en attente
								else { //si cette action est un contrat de destruction
									myContractTrunk.addContract(c);//cette action est prise en compte
									this.contractsToPropose.add(c);
								}

						//Ajout du contrat améliorant
						// --- > Création du contrat
						final ArrayList<Contract> actions = new ArrayList<Contract>();
						for (final InformedCandidature<Contract, ActionSpec> i : realloc)
							actions.add(i.getCandidature());
								final ReallocationContract<Contract, ActionSpec> upgradingContract =
										new ReallocationContract<Contract, ActionSpec>(
												this.getIdentifier(),
												actions);
								// ---> ajout
								myContractTrunk.addReallocContract(upgradingContract);
					}

		}
	}

	@Override
	public Set<? extends InformedCandidature<Contract, ActionSpec>> getNextContractsToPropose()
			throws NotReadyException {
		final Set<InformedCandidature<Contract, ActionSpec>> result =
				new HashSet<InformedCandidature<Contract, ActionSpec>>();
		result.addAll(this.contractsToPropose);
		this.contractsToPropose.clear();
		return result;
	}


	protected abstract InformedCandidature<Contract, ActionSpec> generateDestructionContract(ActionSpec state, AgentIdentifier id);
}








//Collection<InformedCandidature<Contract, ActionSpec>> newUpgradingContracts = upgradingContracts.values();
//if (!newUpgradingContracts.isEmpty())
//	logMonologue("detecting upgrading contracts "+newUpgradingContracts,
//			NegotiationProtocol.log_mirrorProto);
//this.contractsToPropose.addAll(newUpgradingContracts);
//if (upgradingContracts.containsKey(c.getAgent())){
//	if (getMyAgent().getMyAllocationPreferenceComparator(n.getAgentState()).compare(
//			upgradingContract.getAllocation(),
//			upgradingContracts.get(c).getConsequentContract().getAllocation())>0)
//		upgradingContracts.get(c.getAgent()).setConsequentContract(upgradingContract);
//} else {
//	c.setConsequentContract(upgradingContract);
//	upgradingContracts.put(c.getAgent(), c);
//}
//} else {//on traite une candidature dans un contrat améliorant
//n.getAnswers().removeRejection(getIdentifier(), c);
//}
//	public Collection<Contract> generateUpgradingContracts(
//			final SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> myAgent,
//			final PersonalState state,
//			final ContractDataBase<Contract> n) {
//
//
//
//
//	}