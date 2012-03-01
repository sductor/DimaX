package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.ProposerCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice.EventHookedMethod;
import dima.introspectionbasedagents.shells.NotReadyException;
import dimaxx.tools.HyperSetGeneration;

public abstract class ResourceUpgradingInformedProposerCore<
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
		assert getMyAgent().getMyProtocol().getContracts() instanceof ResourceInformedCandidatureContractTrunk:""+n.getAnswers().getClass();

		final ResourceInformedCandidatureContractTrunk<Contract, ActionSpec> myContractTrunk = 
				(ResourceInformedCandidatureContractTrunk<Contract, ActionSpec>) getMyAgent().getMyProtocol().getContracts();
		final Collection<InformedCandidature<Contract, ActionSpec>> unacceptedContracts =
				n.getAnswers().getContractsRejectedBy(this.getIdentifier());
		final HashSet<ReallocationContract<Contract, ActionSpec>> upgradingcontracts = 
				new HashSet<ReallocationContract<Contract,ActionSpec>>();

		if (!this.getMyAgent().getMyProtocol().negotiationAsInitiatorHasStarted()
				&& !unacceptedContracts.isEmpty() 
				&& n.getAnswers().getContractsAcceptedBy(this.getIdentifier()).isEmpty()){
			//Generating new proposals

			//generating concerned : concerned is the set of atomic candidature that can be changed
			final Collection<InformedCandidature<Contract, ActionSpec>> concerned =
					new HashSet<InformedCandidature<Contract, ActionSpec>>();
			concerned.addAll(unacceptedContracts);//adding allocation candidature
			for (final AgentIdentifier id : n.getAgentState().getMyResourceIdentifiers()){
				//adding destruction of hosted agents
				InformedCandidature<Contract, ActionSpec> c = 
						this.generateDestructionContract(id);
				c.setSpecification(getMyAgent().getMySpecif(n.getAgentState(), c));
				c.getPossibleContracts().clear();

				try {
					ActionSpec agentSpe = 
							(ActionSpec) getMyAgent().getMyInformation()
							.getInformation(getMyAgent().getMyCurrentState().getMyResourcesClass(), id);
					c.setSpecification(agentSpe);
				} catch (NoInformationAvailableException e) {
					throw new RuntimeException(e);
				}

				concerned.add(c);
			}

			assert concernedValidity(concerned);
			
			//generating allocgen : allocgen contains the set of upgrading reallocation contracts
			final Collection<Collection<InformedCandidature<Contract, ActionSpec>>> allocGen =
					new HyperSetGeneration<InformedCandidature<Contract, ActionSpec>>(concerned) {
				@Override
				public boolean toKeep(final Collection<InformedCandidature<Contract, ActionSpec>> alloc) {
					return ResourceUpgradingInformedProposerCore.this.getMyAgent().Iaccept(
							ResourceUpgradingInformedProposerCore.this.getMyAgent().getMyCurrentState(),alloc);
				}
			}.getHyperset();


			for (final Collection<InformedCandidature<Contract, ActionSpec>> realloc : allocGen){
				//Pour tout contrat améliorant

				//MAJ du contract trunk
				for (final InformedCandidature<Contract, ActionSpec> c : realloc)
					//Pour toute action de ce contrat
					if (c.isMatchingCreation()){
						n.getAnswers().removeRejection(this.getIdentifier(), c);//elle est remise en attente
					}	else { //si cette action est un contrat de destruction
						myContractTrunk.addContract(c);//cette action est prise en compte
						this.contractsToPropose.add(c);

					}

				//Ajout du contrat améliorant
				// --- > Création du contrat
				final ArrayList<Contract> actions = new ArrayList<Contract>();
				for (final InformedCandidature<Contract, ActionSpec> i : realloc){
					actions.add(i.getCandidature());
				}
				upgradingcontracts.add(
						new ReallocationContract<Contract, ActionSpec>(
								this.getIdentifier(),
								actions));
			}

			// ---> ajout
			for ( ReallocationContract<Contract, ActionSpec> upgradingContract : upgradingcontracts)
				myContractTrunk.addReallocContract(upgradingContract);
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


	protected abstract InformedCandidature<Contract, ActionSpec> generateDestructionContract(AgentIdentifier id);


	private boolean concernedValidity(Collection<InformedCandidature<Contract, ActionSpec>> concerned){
		for (InformedCandidature<Contract, ActionSpec> c : concerned){
			for (AgentIdentifier id : c.getCandidature().getAllParticipants())
				c.getCandidature().computeResultingState(id);
		}
		return true;
	}

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