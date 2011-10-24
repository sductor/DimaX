package negotiation.interactionprotocols.socialNegotiation;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.agentframework.informedagent.InformationService.MissingInformationException;
import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.ContractTransition;
import negotiation.agentframework.rationalagent.AgentActionSpecification.ActionArguments;
import negotiation.interactionprotocols.ConsensualParticipantRole;
import negotiation.interactionprotocols.SimpleContractAnswer;
import negotiation.interactionprotocols.SimpleContractEnvellope;
import negotiation.interactionprotocols.SimpleContractEnvellope.NegotiationProtocol;
import negotiation.interactionprotocols.contracts.AbstractSendableContract;
import negotiation.interactionprotocols.contracts.AbstractSendableContract.ContractIdentifier;
import dima.introspectionBasedAgent.annotations.MessageHandler;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;

public class SocialExecutorRole<
Contract extends AbstractSendableContract & ContractTransition<ActionSpec,ActionArgs> & SocialContract<Info>,
ActionSpec extends AgentActionSpecification,
ActionArgs extends ActionArguments,
Info extends Comparable<Info>> 
extends ConsensualParticipantRole<Contract,ActionSpec,ActionArgs>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3435043657616727326L;
	Collection<ContractIdentifier> inProgressContracts = new ArrayList<ContractIdentifier>();
	
	public SocialExecutorRole(final SocialInvolvedAgent<?,?, Contract,ActionSpec,ActionArgs,Info> ag) {
		super(ag);
	}

	public SocialInvolvedAgent<?,?, Contract,ActionSpec,ActionArgs,Info> getMyAgent(){
		return (SocialInvolvedAgent<?,?, Contract,ActionSpec,ActionArgs,Info>) super.getMyAgent();
	}

	//
	// Protocol
	//
	
	@MessageHandler()
	@FipaACLEnvelope(
			performative=Performative.QueryIf,
			protocol=NegotiationProtocol.class)
			public void evaluate(final SimpleContractEnvellope<Contract> delta){
		final Contract c = delta.getMyContract();
		try {
			if (getMyAgent().Iaccept(c))
				//			this.receivedRequest.add(c.getIdentifier());
				this.sendMessage(c.getInitiator(),  this.agreeAndAttach(c));
			else
				this.sendMessage(c.getInitiator(), this.refuse(c));
		} catch (final MissingInformationException ids) {
			getMyAgent().getMyInformation().obtainInformation(ids);
			retryWhen(getMyAgent().getMyInformation(), "hasInformation", new Object[]{ids}, new Object[]{delta});
		}

	}

//	@MessageHandler()
//	@FipaACLEnvelope(
//			performative=Performative.Disconfirm,
//			protocol=NegotiationProtocol.class)
//			public void receiveCancel(final ContractMessage<SendableContract> delta){
////		this.receivedRequest.remove(delta.getMyContract());
//	}
	
	//
	// Primitives
	//
	
	private SimpleContractEnvellope<SocialAnswer<ActionSpec,ActionArgs,Info>> agreeAndAttach(final Contract c) throws MissingInformationException{
		final SocialAnswer<ActionSpec,ActionArgs,Info> answer = new SocialAnswer<ActionSpec,ActionArgs,Info>(c);
		answer.attachInfo(getIdentifier(),getMyAgent().computePersonnalGain(c));
		answer.setRequiredArgument(getIdentifier(), getMyAgent().getMyArguments(c));
		final SimpleContractEnvellope<SocialAnswer<ActionSpec,ActionArgs,Info>> m = 
			new SimpleContractEnvellope<SocialAnswer<ActionSpec,ActionArgs,Info>>(
				Performative.Agree, answer);	
		return m;
	}
	
	private SimpleContractEnvellope<SimpleContractAnswer<ActionSpec,ActionArgs>> refuse(final Contract c){
		final SimpleContractEnvellope<SimpleContractAnswer<ActionSpec,ActionArgs>> m = 
			new SimpleContractEnvellope<SimpleContractAnswer<ActionSpec,ActionArgs>>(
					Performative.Refuse, 
					new SimpleContractAnswer<ActionSpec,ActionArgs>(c));
		return m;
	}
	
//	@MessageHandler()
//	@FipaACLEnvelope(
//			performative=Performative.Inform,
//			protocol=NegotiationProtocol.class)
//			void receiveInform(final ContractMessage<SendableContract> delta){
//		if (!this.inProgressContracts.add(delta.getMyContract().getIdentifier()))
//			logException("contrat déjà connus");
//	}	

	@MessageHandler()
	@FipaACLEnvelope(
			performative=Performative.Disconfirm,
			protocol=NegotiationProtocol.class)
			public void receiveDisconfirm(final SimpleContractEnvellope<AbstractSendableContract> delta){
		if (!this.inProgressContracts.remove(delta.getMyContract()))
			this.logException("annulation d'une requete inconnu");
	}

	@Override
	protected boolean shouldGiveAnAnswer() {
		return this.inProgressContracts.isEmpty();
	}
	
	//Vérifie les signatures
	@Override
	protected boolean requestIsValid(final SimpleContractAnswer<ActionSpec,ActionArgs> c) {
		if (!this.inProgressContracts.remove(c)){
			this.logException("reception d'une requete inconnu");
			return false;
		}
		return true;
	}
	

}
