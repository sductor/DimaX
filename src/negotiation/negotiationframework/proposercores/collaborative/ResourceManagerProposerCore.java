package negotiation.negotiationframework.proposercores.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice.EventHookedMethod;
import dima.introspectionbasedagents.shells.NotReadyException;
import dimaxx.tools.HyperSetGeneration;

import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.NegotiationProtocol;
import negotiation.negotiationframework.ProposerCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;

public abstract class ResourceManagerProposerCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec>extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, MatchingCandidature<ActionSpec>>>
implements
ProposerCore<
SimpleNegotiatingAgent<ActionSpec, PersonalState, MatchingCandidature<ActionSpec>>,
ActionSpec,PersonalState,MatchingCandidature<ActionSpec>>  { 

	private final Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> contractsToPropose = 
			new HashSet<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>>();

	@EventHookedMethod(IllAnswer.class)
	public void receiveFullNotification(final IllAnswer<PersonalState, InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> n) {
		assert (n.getAgentState().equals(getMyAgent().getMyCurrentState()));
		final Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> unacceptedContracts = 
				n.getAnswers().getContractsRejectedBy(getIdentifier());
		final Map<AgentIdentifier,InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> upgradingContracts =
				new HashMap<AgentIdentifier,InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>>();

		if (!getMyAgent().getMyProtocol().negotiationAsInitiatorHasStarted()
				&& !unacceptedContracts.isEmpty() && n.getAnswers().getContractsAcceptedBy(getIdentifier()).isEmpty()){
			Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> concerned = 
					new ArrayList<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>>();
			concerned.addAll(unacceptedContracts);
			for (AgentIdentifier id : n.getAgentState().getMyResourceIdentifiers()){
				concerned.add(generateDestructionContract(n.getAgentState(), id));
			}

			HyperSetGeneration<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> allocGen = 
					new HyperSetGeneration<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>>(concerned) {
				@Override
				public boolean toKeep(Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> alloc) {
					return getMyAgent().Iaccept(n.getAgentState(), alloc);
				}
			};

			for (Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> realloc : allocGen.getHyperset()){//Pour tout contrat améliorant
				ArrayList<MatchingCandidature<ActionSpec>> actions = new ArrayList<MatchingCandidature<ActionSpec>>();
				for (InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec> i : realloc)
					actions.add(i.getMainContract());
				ReallocationContract<MatchingCandidature<ActionSpec>, ActionSpec> upgradingContract = 
						new ReallocationContract<MatchingCandidature<ActionSpec>, ActionSpec>(
								getIdentifier(),
								actions, ExperimentationProtocol._contractExpirationTime);
				for (InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec> c : realloc)//Pour toute action de ce contrat
					if (!c.isMatchingCreation()){//On traite un contrat de destruction dans un contrat améliorant
						if (upgradingContracts.containsKey(c.getAgent())){
							if (getMyAgent().getMyAllocationPreferenceComparator(n.getAgentState()).compare(
									upgradingContract.getAllocation(),
									upgradingContracts.get(c).getConsequentContract().getAllocation())>0)
								upgradingContracts.get(c.getAgent()).setConsequentContract(upgradingContract);
						} else {
							c.setConsequentContract(upgradingContract);
							upgradingContracts.put(c.getAgent(), c);
						}
					} else {//on traite une candidature dans un contrat améliorant
						n.getAnswers().removeRejection(getIdentifier(), c);
					}
			}


			Collection<InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec>> newUpgradingContracts = upgradingContracts.values();
			if (!newUpgradingContracts.isEmpty())
				logMonologue("detecting upgrading contracts "+newUpgradingContracts, 
						NegotiationProtocol.log_mirrorProto);
			this.contractsToPropose.addAll(newUpgradingContracts);

		}
	}

	@Override
	public Set<? extends MatchingCandidature<ActionSpec>> getNextContractsToPropose()
			throws NotReadyException {
		final Set<MatchingCandidature<ActionSpec>> result = 
				new HashSet<MatchingCandidature<ActionSpec>>();
		result.addAll(this.contractsToPropose);
		this.contractsToPropose.clear();
		return result;
	}


	protected abstract InformedCandidature<MatchingCandidature<ActionSpec>, ActionSpec> generateDestructionContract(ActionSpec state, AgentIdentifier id);
}


//	public Collection<Contract> generateUpgradingContracts(
//			final SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> myAgent,
//			final PersonalState state,
//			final ContractDataBase<Contract> n) { 
//	
//	
//	
//	
//	}