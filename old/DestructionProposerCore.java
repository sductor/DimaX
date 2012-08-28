package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.competences.BasicAgentCompetence;
import dima.introspectionbasedagents.coreservices.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.coreservices.observingagent.NotificationMessage;
import negotiation.faulttolerance.candidaturenegotiation.mirrordestruction.ReplicationCandidatureWithMinInfo;
import negotiation.faulttolerance.negotiatingagent.NegotiatingHost;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;

public abstract class DestructionProposerCore
<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentCompetence<Agent>
implements AbstractProposerCore<Agent, ActionSpec, PersonalState, Contract> {

	private Collection<Contract> unacceptedContracts = new ArrayList();

	//
	// Accessors
	//
	
	public boolean imFull() {
		return !this.unacceptedContracts.isEmpty();
	}
	
	//
	// Communication
	//

	@ProactivityInitialisation
	public boolean activateObservation() {
		this.getMyAgent().autoObserve(ImFull.class);
		return true;
	}

	@NotificationEnvelope
	@MessageHandler
	public void setiMFull(final NotificationMessage<ImFull> n) {
		this.unacceptedContracts.addAll(n.getNotification().getUnacceptedContracts());
	}
	
	//
	// Methods
	//
	@Override
	public Collection<Contract> getNextContractsToPropose()
			throws NotReadyException {
		if (this.imFull()){
			Contract minRefused =  
					Collections.min(
							this.unacceptedContracts, 
							this.getMyAgent().getMyPreferenceComparator());
			Iterator<AgentIdentifier> itAg = //obliger de caster pour atteindre les ressources alloué :
					(((NegotiatingHost) this.getMyAgent()).
							getMyCurrentState().getMyAgents());
			while (itAg.hasNext()){
				AgentIdentifier id = itAg.next();
				if (((NegotiatingHost) this.getMyAgent()).
						getReliability(id) > minCandidatedReliability)
					result.add(new ReplicationCandidatureWithMinInfo(
							(ResourceIdentifier) this.getMyAgent()
							.getIdentifier(), id, false,
							minCandidatedReliability));
			}
		}
	}
	
	//
	// Abstract
	//
	
	public abstract Contra getMin(){
		
	}
	
	
}
