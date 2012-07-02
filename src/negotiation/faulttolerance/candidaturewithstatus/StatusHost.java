package negotiation.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.OpinionService;
import negotiation.faulttolerance.experimentation.Host;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;

public class StatusHost extends Host {

	final boolean centralised;
	public StatusObservationCompetence soc;

	public StatusHost(ResourceIdentifier id, HostState myState,
			RationalCore myRationality, SelectionCore participantCore,
			ProposerCore proposerCore, ObservationService myInformation,
			AbstractCommunicationProtocol protocol,
			AgentIdentifier myLaborantin) throws CompetenceException {
		super(id, 
				myState, myRationality, 
				participantCore, proposerCore, 
				myInformation,
				protocol);
		this.centralised=true;
		soc=new StatusObservationCompetence(this,myLaborantin);
	}

	public StatusHost(ResourceIdentifier id, HostState myState,
			RationalCore myRationality, SelectionCore participantCore,
			ProposerCore proposerCore, ObservationService myInformation,
			AbstractCommunicationProtocol protocol,
			int numberToContact) throws CompetenceException {
		super(id, 
				myState, myRationality, 
				participantCore, proposerCore, 
				myInformation,
				protocol);
		this.centralised=true;
		soc=new StatusObservationCompetence(this,numberToContact);
	}
	@StepComposant(ticker = NegotiationParameters._statusObservationFrequency)
	public void notifyOpinion4Status() {
		if (!centralised){
			// logMonologue("relia send to "+observer.getObserver(ReplicationExperimentationProtocol.reliabilityObservationKey));
			try {
			soc.diffuse(((OpinionService) getMyInformation()).getGlobalOpinion(ReplicaState.class));
			} catch (NoInformationAvailableException e) {
				//do nothing
			}
		}
	}

}
