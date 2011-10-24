package negotiation.ressourcenegotiation.faultolerance.interaction.social;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import negotiation.agentframework.SimpleUtilitaristRationalAgent;
import negotiation.agentframework.informedagent.BasicInformedAgent;
import negotiation.agentframework.informedagent.InformationService;
import negotiation.agentframework.informedagent.OpinionManager;
import negotiation.agentframework.informedagent.InformationService.MissingInformationException;
import negotiation.interactionprotocols.candidatureNegotiation.ResourceIdentifier;
import negotiation.interactionprotocols.contracts.AbstractSendableContract.ContractIdentifier;
import negotiation.interactionprotocols.socialNegotiation.SimpleSocialContract;
import negotiation.interactionprotocols.socialNegotiation.SocialExecutorRole;
import negotiation.interactionprotocols.socialNegotiation.SocialInvolvedAgent;
import negotiation.ressourcenegotiation.faultolerance.decision.AgentCharge;
import negotiation.ressourcenegotiation.faultolerance.decision.HostCore;
import negotiation.ressourcenegotiation.faultolerance.decision.HostState;
import negotiation.ressourcenegotiation.faultolerance.decision.ReplicaState;
import negotiation.ressourcenegotiation.faultolerance.decision.ReplicaStateAgregator;
import dima.introspectionBasedAgent.annotations.Competence;
import dima.introspectionBasedAgent.competences.DuplicateCompetenceException;
import dima.introspectionBasedAgent.competences.UnInstanciableCompetenceException;

public class SocialHost 
extends 
BasicInformedAgent<HostState, SimpleSocialContract<AgentCharge,Double>,ReplicaState>
implements 
SocialInvolvedAgent<HostState, ReplicaState, SimpleSocialContract<AgentCharge,Double>,ResourceIdentifier, AgentCharge, Double>{
	private static final long serialVersionUID = 9094591803306321573L;


	//
	// Fields
	//

	OptimalCandidatureSelectionModule<HostState, SimpleSocialContract<AgentCharge,Double>,Double> myExecutorCore = 
		new OptimalCandidatureSelectionModule<HostState, SimpleSocialContract<AgentCharge,Double>,Double>(this.myRationality);

	//
	// Competences
	//

	@Competence
	final SocialExecutorRole<SimpleSocialContract<AgentCharge,Double>,ResourceIdentifier,AgentCharge,Double> myExecutorRole = 
		new SocialExecutorRole<SimpleSocialContract<AgentCharge,Double>,ResourceIdentifier,AgentCharge,Double>(this);

	@Competence 
	final OpinionManager<HostState, SimpleSocialContract<AgentCharge,Double>, ReplicaState> 
	myOpinion;
	//
	// Constructor
	//

	public SocialHost(final ResourceIdentifier newId, final HostState myInitialState) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		super(newId, new HostCore<SimpleSocialContract<AgentCharge,Double>>());
		myOpinion = 
			new OpinionManager<HostState, SimpleSocialContract<AgentCharge,Double>, ReplicaState>(
					this, myInitialState, new ReplicaStateAgregator(getIdentifier()), new ReplicaStateAgregator(getIdentifier()));
	}

	public SocialHost(final ResourceIdentifier resourceIdentifier, final Double procChargeMax,
			final Double memChargeMax) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		this(resourceIdentifier, new HostState(resourceIdentifier, procChargeMax, memChargeMax));
	}
	//
	// Accessors
	//


	@Override
	public InformationService<HostState,ReplicaState> getMyInformation() {
		return this.myOpinion;
	}

	//
	// Methods
	//

	@Override
	public Collection<ContractIdentifier> select(
			final Collection<SimpleSocialContract<AgentCharge,Double>> cs) throws MissingInformationException {
		return this.myExecutorCore.select(cs);
	}

	@Override
	public Double computePersonnalGain(final SimpleSocialContract<AgentCharge,Double> c)
	throws MissingInformationException {
		return this.getResultingState(c).getMyCharge();
	}

	@Override
	public Map<ResourceIdentifier, AgentCharge> getMyArguments(
			final SimpleSocialContract<AgentCharge, Double> c) {
		return new HashMap<ResourceIdentifier, AgentCharge>();
	}

}
