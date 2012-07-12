package negotiation.faulttolerance.candidaturewithstatus;

import negotiation.faulttolerance.experimentation.Host;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.protocoles.InactiveProposerCore;
import negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import negotiation.negotiationframework.protocoles.status.StatusObservationCompetence;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;

public class StatusHost extends Host {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1891276000545412915L;
	@Competence
	public StatusObservationCompetence soc;

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final AgentIdentifier myLaborantin) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,true);
		this.soc=new StatusObservationCompetence(myLaborantin,false, ReplicaState.class);
		this.soc.setActive(false);
	}

	public StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final int numberToContact) throws CompetenceException {
		this(id, myState, participantCore, _socialWelfare,false);
		this.soc=new StatusObservationCompetence(numberToContact, false, ReplicaState.class);
	}


	private StatusHost(
			final ResourceIdentifier id,
			final HostState myState,
			final SelectionCore participantCore,
			final SocialChoiceType _socialWelfare,
			final boolean centralised) throws UnrespectedCompetenceSyntaxException, CompetenceException{
		super(id,
				myState,
				new HostCore(_socialWelfare,true,true),
				participantCore,
				new InactiveProposerCore(),
				centralised?new SimpleObservationService():new SimpleOpinionService(),
						new ReverseCFPProtocol());
	}
}
