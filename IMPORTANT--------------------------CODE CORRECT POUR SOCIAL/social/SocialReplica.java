package negotiation.ressourcenegotiation.faultolerance.interaction.social;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import negotiation.agentframework.informedagent.OpinionManager;
import negotiation.agentframework.informedagent.InformationService.MissingInformationException;
import negotiation.agentframework.informedagent.globalinformationhandlers.NoInformationAvailableException;
import negotiation.agentframework.rationalagent.BasicRationalityCore;
import negotiation.agentframework.rationalagent.RationalCore;
import negotiation.agentframework.strategicagent.strategiccontractexploration.BasicUtilitaristStrategicProposer;
import negotiation.interactionprotocols.candidatureNegotiation.OptimalCandidatureSelectionModule;
import negotiation.interactionprotocols.candidatureNegotiation.ResourceIdentifier;
import negotiation.interactionprotocols.contracts.AbstractSendableContract.ContractIdentifier;
import negotiation.interactionprotocols.socialNegotiation.SimpleSocialContract;
import negotiation.interactionprotocols.socialNegotiation.SocialAnswer;
import negotiation.interactionprotocols.socialNegotiation.SocialExecutorRole;
import negotiation.interactionprotocols.socialNegotiation.SocialInvolvedAgent;
import negotiation.interactionprotocols.socialNegotiation.SocialProposerAgent;
import negotiation.interactionprotocols.socialNegotiation.SocialProposerRole;
import negotiation.ressourcenegotiation.PossibilistSocialGradientSearch;
import negotiation.ressourcenegotiation.faultolerance.decision.AgentCharge;
import negotiation.ressourcenegotiation.faultolerance.decision.ReplicaCore;
import negotiation.ressourcenegotiation.faultolerance.decision.ReplicaState;
import negotiation.ressourcenegotiation.faultolerance.decision.ReplicaStateAgregator;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionBasedAgent.annotations.Competence;
import dima.introspectionBasedAgent.annotations.MessageHandler;
import dima.introspectionBasedAgent.competences.DuplicateCompetenceException;
import dima.introspectionBasedAgent.competences.UnInstanciableCompetenceException;
import dima.introspectionBasedAgent.libraries.observingagent.NotificationMessage;
import dima.introspectionBasedAgent.libraries.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import examples.introspectiveExample.mas.SimpleMessage;

public class SocialReplica 
extends 
BasicUtilitaristStrategicProposer<ReplicaState, ResourceIdentifier, SimpleSocialContract<AgentCharge,Double>,ReplicaState>
implements 
SocialProposerAgent<ReplicaState, SimpleSocialContract<AgentCharge,Double>, ReplicaState,ResourceIdentifier,AgentCharge,Double>, 
SocialInvolvedAgent<ReplicaState,ReplicaState, SimpleSocialContract<AgentCharge,Double>, ResourceIdentifier,AgentCharge, Double>{
	private static final long serialVersionUID = -8177323752800620776L;

	//
	// Fields
	//

	RationalCore<ReplicaState, SimpleSocialContract<AgentCharge,Double>> myCore = 
		new BasicRationalityCore<ReplicaState, SimpleSocialContract<AgentCharge,Double>>(this.myNegotiatingModel);	
	OptimalCandidatureSelectionModule<ReplicaState, SimpleSocialContract<AgentCharge,Double>,Double> myExecutorCore = 
		new OptimalCandidatureSelectionModule<ReplicaState, SimpleSocialContract<AgentCharge,Double>,Double>(this.myCore);


	//
	// Competences
	//

	@Competence
	protected final SocialProposerRole<ResourceIdentifier,AgentCharge,SimpleSocialContract<AgentCharge,Double>, Double> myProposerRole =
		new SocialProposerRole<ResourceIdentifier,AgentCharge,SimpleSocialContract<AgentCharge,Double>, Double>(this);

	@Competence
	protected final SocialExecutorRole<SimpleSocialContract<AgentCharge,Double>,ResourceIdentifier,AgentCharge, Double> myInvolvedRole =
		new SocialExecutorRole<SimpleSocialContract<AgentCharge,Double>,ResourceIdentifier,AgentCharge, Double>(this);

	@Competence
	protected final OpinionManager<ReplicaState, SimpleSocialContract<AgentCharge,Double>, ReplicaState> myInformation;
	
	//
	// Constructor
	//

	public SocialReplica(
			final AgentIdentifier newID,
			final ReplicaState repState,
			final Collection<ResourceIdentifier> knownActions) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		super(newID,
				new ReplicaCore<SimpleSocialContract<AgentCharge,Double>>(), 
				new PossibilistSocialGradientSearch<AgentCharge,Double>(),
				knownActions);
		myInformation = 
			new OpinionManager<ReplicaState, SimpleSocialContract<AgentCharge,Double>, ReplicaState>(
					this,repState,  new ReplicaStateAgregator(getIdentifier()),new ReplicaStateAgregator(getIdentifier()));
	}


	//
	// Accessors
	//


	@Override
	public OpinionManager<ReplicaState, SimpleSocialContract<AgentCharge,Double>, ReplicaState> getMyInformation() {
		return this.myInformation;
	}

	//
	// Methods
	//

	@Override
	public Double computeSocialGain(final Collection<SocialAnswer<ResourceIdentifier,AgentCharge,Double>> individualGain) {
		Double agent =new Double(0);
		Double maxHost =new Double(0);
		Double minHost=new Double(Float.MAX_VALUE);
		for (final SocialAnswer<ResourceIdentifier,AgentCharge,Double> s : individualGain)
			if (s.getInfoOwner() instanceof ResourceIdentifier) {
				maxHost = Math.max(maxHost, s.getInfo());
				minHost = Math.min(minHost, s.getInfo());
			} else
				agent += s.getInfo();
		return agent;	//TODO  : ordre intervalle
	}

	@Override
	public Double computePersonnalGain(final SimpleSocialContract<AgentCharge,Double> c) throws MissingInformationException {
		final ReplicaState s1 = getMyCurrentState();
		final ReplicaState s2 = this.getResultingState(s1, c);
		return s1.getMyCriticity() * (s2.getMyDisponibility() - s1.getMyDisponibility());
	}

	/*
	 * 
	 */

	@Override
	public boolean iMRiskAdverse() {
		return getMyCurrentState().getMyDisponibility()<0.3;
	}

	@Override
	public boolean iThinkItwillAccept(final AgentIdentifier id, final SimpleSocialContract<AgentCharge,Double> c) throws NoInformationAvailableException{
		return this.Iaccept(getMyInformation().getBelievedState(id), evaluateResultingState(getMyInformation().getBelievedState(id), c));
	}

	@Override
	public ReplicaState evaluateResultingState(final ReplicaState s,
			final SimpleSocialContract<AgentCharge,Double> a) {
		return ((ReplicaCore<SimpleSocialContract<AgentCharge,Double>>)this.myNegotiatingModel).getResultingState(s, a);
	}

	@Override
	public Double evaluateContractUtility(final AgentIdentifier id,
			final SimpleSocialContract<AgentCharge,Double> c)  throws NoInformationAvailableException{
		if (id instanceof ResourceIdentifier){//Pas optimal : ce calcul est refait pour chaque host!!!!!!!! =/
			Double somme =new Double(0);
			for (final AgentIdentifier h : c.getParticipants())
				somme += evaluatePersonnalGain(h, c);
			return somme;	
		} else {
			final ReplicaState s1 = getMyInformation().getBelievedState(id);
			final ReplicaState s2 = evaluateResultingState(s1, c);
			return s1.getMyCriticity() * (s2.getMyDisponibility() - s1.getMyDisponibility());			 
		}
	}

	//
	// Delegation pour héritage multiple
	//
	
	@Override
	public boolean Iaccept(final ReplicaState s1, final ReplicaState s2) {
		return this.myCore.Iaccept(s1, s2);
	}
	
	
	@Override
	public boolean IsAStrictImprovment(final SimpleSocialContract<AgentCharge,Double> c)
	throws MissingInformationException {
		return IsAStrictImprovment(c);
	}

	//
	// Primitives
	//

	private Double evaluatePersonnalGain(final AgentIdentifier id, final SimpleSocialContract<AgentCharge,Double> c) throws NoInformationAvailableException {
		final ReplicaState s1 = getMyInformation().getBelievedState(id);
		final ReplicaState s2 = evaluateResultingState(s1, c);
		return s1.getMyCriticity() * (s2.getMyDisponibility() - s1.getMyDisponibility());	
	}

	@Override
	public Collection<ContractIdentifier> select(
			final Collection<SimpleSocialContract<AgentCharge,Double>> cs)
			throws MissingInformationException {
		return this.myExecutorCore.select(cs);
	}


	@Override
	public Map<ResourceIdentifier, AgentCharge> getMyArguments(
			final SimpleSocialContract<AgentCharge, Double> c) {
		final Map<ResourceIdentifier, AgentCharge> result = new HashMap<ResourceIdentifier, AgentCharge>();
		result.put(null, getMyCurrentState().getMyCharge());
		return null;
	}


	@Override
	public boolean Iaccept(final SimpleSocialContract<AgentCharge, Double> c)
			throws MissingInformationException {
		return this.myCore.Iaccept(getMyCurrentState(), getResultingState(c));//false;
	}
}





//
//@Override
//public boolean Iaccept(final ReplicaState s1, final ReplicaState s2) {
//	return this.myCore.Iaccept(s1, s2);
//}
//
//
//@Override
//public boolean IsAStrictImprovment(final SocialAllocationContract<AgentCharge,Float> c)
//throws MissingInformationException {
//	return this.IsAStrictImprovment(c);
//}
