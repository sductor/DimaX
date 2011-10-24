package negotiation.interactionprotocols.socialNegotiation;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.agentframework.informedagent.globalinformationhandlers.NoInformationAvailableException;
import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.ContractTransition;
import negotiation.agentframework.rationalagent.AgentActionSpecification.ActionArguments;
import negotiation.agentframework.rationalagent.ContractTransition.UnappropriateActionException;
import negotiation.interactionprotocols.ConsensualInitiatorRole;
import negotiation.interactionprotocols.SimpleContractAnswer;
import negotiation.interactionprotocols.SimpleContractEnvellope;
import negotiation.interactionprotocols.SimpleContractEnvellope.NegotiationProtocol;
import negotiation.interactionprotocols.contracts.AbstractSendableContract;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionBasedAgent.annotations.MessageHandler;
import dima.introspectionBasedAgent.annotations.StepComposant;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;


public class SocialProposerRole< 
ActionSpec extends AgentActionSpecification,
ActionArgs extends ActionArguments,
Contract extends ContractTransition<ActionSpec, ActionArgs> & AbstractSendableContract & SocialContract<Info>,
Info extends Comparable<Info>> 
extends ConsensualInitiatorRole<Contract,ActionSpec,ActionArgs>{
	private static final long serialVersionUID = 1233902824998883464L;

	//
	// Fields
	//

	Contract myCurrentlyEvaluatedProposal=null;
	Collection<SocialAnswer<ActionSpec,ActionArgs,Info>> receivedOk;

	//
	// Constructors
	//

	public SocialProposerRole(final SocialProposerAgent<?,Contract, ?, ActionSpec,ActionArgs,Info> myAgent) {
		super(myAgent);
	}
	
	public SocialProposerAgent<?,Contract, ?, ActionSpec,ActionArgs,Info> getMyAgent(){
		return (SocialProposerAgent<?,Contract, ?, ActionSpec,ActionArgs,Info>) super.getMyAgent();
	}

	//
	// Accessors
	//


	@Override
	public boolean protocolHasStarted(){
		return this.myCurrentlyEvaluatedProposal!=null;
	}

	//
	// Protocole
	//

//	@Override
//	@StepComposant
//	public void startNegotiation(){
//		if (!protocolHasStarted())
//			try {
//				startSocialNegotiation(getMyAgent().getNextContractToPropose());
//			} catch (final NoInformationAvailableException e) {
//				getMyAgent().getMyInformation().collectInformation();
//				retryWhen(getMyAgent().getMyInformation(), "hasAnOpinion", new Object[]{}, new Object[]{});
//			}
//	}

	public void startSocialNegotiation(final Contract c){
		this.myCurrentlyEvaluatedProposal = c;
		final SimpleContractEnvellope<Contract> evaluationProposal = 
			new SimpleContractEnvellope<Contract>(
					Performative.QueryIf, 
					this.myCurrentlyEvaluatedProposal);

		//		final ContractMessage<ContractAnswer> inform = 
		//			new ContractMessage<ContractAnswer>(
		//					Performative.Inform, 
		//					new ContractAnswer(this.myCurrentlyEvaluatedProposal));

		this.receivedOk= 
			new ArrayList<SocialAnswer<ActionSpec,ActionArgs,Info>>(
					this.myCurrentlyEvaluatedProposal.
					getParticipants().size());

		this.sendMessage(this.myCurrentlyEvaluatedProposal.getParticipants(), evaluationProposal);
		this.logWarning("I'm proposing \n"+this.myCurrentlyEvaluatedProposal);			
	}


	/*
	 * 
	 */

	@MessageHandler()
	@FipaACLEnvelope(
			performative=Performative.Refuse,
			protocol=NegotiationProtocol.class)
			void receiveRefuse(final SimpleContractEnvellope<Contract>  delta){
		final AbstractSendableContract c = (AbstractSendableContract) delta.getArgs()[0];
		if (checkMessageValidity(c))	{
			final SimpleContractEnvellope<SimpleContractAnswer<ActionSpec,ActionArgs>> cancel = 
				new SimpleContractEnvellope<SimpleContractAnswer<ActionSpec,ActionArgs>>(
						Performative.Disconfirm, 
						new SimpleContractAnswer<ActionSpec,ActionArgs>(this.myCurrentlyEvaluatedProposal));

			//			sendMessage(this.myCurrentlyEvaluatedProposal.getNegotiatingAgents(), cancel);
			this.sendMessage(this.myCurrentlyEvaluatedProposal.getParticipants(), cancel);

			this.receivedOk=null;
			this.myCurrentlyEvaluatedProposal=null;	

		}
	}


	@MessageHandler()
	@FipaACLEnvelope(
			performative=Performative.Agree,
			protocol=NegotiationProtocol.class)
			void receiveAgree(final SocialAnswer<ActionSpec,ActionArgs,Info>  delta){
		if (checkMessageValidity(delta)){

			this.receivedOk.add(delta);

			if (this.isConsensual()){
				this.myCurrentlyEvaluatedProposal.attachInfo(getIdentifier(),getMyAgent().computeSocialGain(this.receivedOk));
				for (final SocialAnswer<ActionSpec,ActionArgs,Info> r : this.receivedOk)
					for (final AgentIdentifier id : r.getParticipants())
						for (ActionSpec a : r.getRequiredArgument(id).keySet()){
						try {
							this.myCurrentlyEvaluatedProposal.setRequiredArgument(id, a, r.getRequiredArgument(id).get(a));
						} catch (UnappropriateActionException e) {
							logException("arrgghhhh! imposssible!!!",e);
						}
						}

				this.receivedOk.clear();
				super.startConsensualNegotiation(this.myCurrentlyEvaluatedProposal);
			}
		}
	}

	//
	// Primitives
	//

	private boolean isConsensual(){
		return this.receivedOk.size()==this.myCurrentlyEvaluatedProposal.getParticipants().size();
	}
}
