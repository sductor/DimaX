package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;

import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import negotiation.negotiationframework.interaction.contracts.ContractIdentifier;

public class HorizonContract extends HashSet<HorizonCandidature> implements AbstractContractTransition<HorizonSpecification> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2543486525336033954L;
	
	public HorizonContract(Collection<? extends HorizonCandidature> arg){ // TODO <? extends HorizonCandidature>
		super(arg);
	}
	
	@Override
	public <State extends HorizonSpecification> State computeResultingState(
			State s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HorizonSpecification computeResultingState(AgentIdentifier id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AgentIdentifier> getAllInvolved() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AgentIdentifier> getAllParticipants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ContractIdentifier getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentIdentifier getInitiator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HorizonSpecification getSpecificationOf(AgentIdentifier id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUptime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasReachedExpirationTime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSpecification(HorizonSpecification s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean willReachExpirationTime(long t) {
		// TODO Auto-generated method stub
		return false;
	}

}
