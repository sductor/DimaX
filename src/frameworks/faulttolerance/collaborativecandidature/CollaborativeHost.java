package frameworks.faulttolerance.collaborativecandidature;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.negotiatingagent.HostCore;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationHostAllocationSolver;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.collaborative.InformedCandidature;
import frameworks.negotiation.protocoles.collaborative.InformedCandidatureRationality;
import frameworks.negotiation.protocoles.collaborative.OneDeciderCommunicationProtocol;
import frameworks.negotiation.protocoles.collaborative.ResourceInformedProposerCore;
import frameworks.negotiation.protocoles.collaborative.ResourceInformedSelectionCore;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.selection.GreedySelectionModule.GreedySelectionType;

public class CollaborativeHost extends Host{
	private static final long serialVersionUID = -8478683967125467116L;


	public CollaborativeHost(
			final ResourceIdentifier myId,
			final HostState myState,
			final SocialChoiceType socialWelfare,
			final int maxCAccepts,
			final GreedySelectionType initialSelectionType,
			final long maxComputingTime)
					throws CompetenceException {
		super(
				myId,
				myState,
				new InformedCandidatureRationality(new HostCore(socialWelfare,true,true),false),
				new ResourceInformedSelectionCore(new ReplicationHostAllocationSolver(socialWelfare),maxCAccepts,initialSelectionType,maxComputingTime){
					private static final long serialVersionUID = -1578866978817500691L;
					@Override
					protected InformedCandidature generateDestructionContract(final AgentIdentifier id) {
						return new InformedCandidature(new ReplicationCandidature(myId,id,false,false));
					}
					@Override
					protected void setSpecif(final AgentState s,
							final InformedCandidature d) {
						//						return new NoActionSpec();
					}
				},//new GreedyBasicSelectionCore(true, false),//
				new ResourceInformedProposerCore(),
				new SimpleObservationService(),
				new OneDeciderCommunicationProtocol(true) );
		this.getMyProtocol().getContracts().setMyAgent(this);
	}


	@Override
	public ReplicationCandidature generateDestructionContract(AgentIdentifier id) {
		return new ReplicationCandidature((ResourceIdentifier) getIdentifier(),id,false,false);
	}


	@Override
	public ReplicationCandidature generateCreationContract(AgentIdentifier id) {
		assert false;
		return null;
	}
}
