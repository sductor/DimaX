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
import negotiation.negotiationframework.CommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
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