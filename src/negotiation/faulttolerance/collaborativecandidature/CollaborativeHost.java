package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.experimentation.Host;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationHostAllocationSolver;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AtMostCContractSelectioner;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedProposerCore;
import negotiation.negotiationframework.protocoles.collaborative.ResourceInformedSelectionCore;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;

public class CollaborativeHost extends Host{
	private static final long serialVersionUID = -8478683967125467116L;


	public CollaborativeHost(
			final ResourceIdentifier myId,
			final HostState myState,
			final SocialChoiceType socialWelfare,
			int maxCAccepts)
					throws CompetenceException {
		super(
				myId,
				myState,
				new InformedCandidatureRationality(new HostCore(socialWelfare),false),
				new ResourceInformedSelectionCore(new ReplicationHostAllocationSolver(socialWelfare),maxCAccepts){
					private static final long serialVersionUID = -1578866978817500691L;
					@Override
					protected InformedCandidature generateDestructionContract(final AgentIdentifier id) {
						return new InformedCandidature(new ReplicationCandidature(myId,id,false,false));
					}
				},//new GreedyBasicSelectionCore(true, false),//
				new ResourceInformedProposerCore(),
				new SimpleObservationService(),
				new OneDeciderCommunicationProtocol(true) );
		this.getMyProtocol().getContracts().setMyAgent(this);
	}
}
