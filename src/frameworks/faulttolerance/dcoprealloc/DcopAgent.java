package frameworks.faulttolerance.dcoprealloc;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.dcopProtocol.DCOPLeaderProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopAgentProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopAgentSelectionCore;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class DcopAgent extends Replica<ReplicationCandidature>  {

	public DcopAgent(
			AgentIdentifier id, 
			ReplicaState myState,
			int k,
			boolean dynamicCriticity)
					throws CompetenceException {
		super(id, myState,new ReplicaCore(false,false), 
				new DcopAgentSelectionCore(), 
				new InactiveProposerCore(), 
				new SimpleObservationService(),
				new DcopAgentProtocol(k), 
				dynamicCriticity);
	}
}
