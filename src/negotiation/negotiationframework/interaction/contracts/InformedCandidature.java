package negotiation.negotiationframework.interaction.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;

public class InformedCandidature<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends MatchingCandidature<ActionSpec>
implements AbstractContractTransition<ActionSpec>{	

	private Contract mainContract;
	private ReallocationContract<Contract, ActionSpec> consequentContract;
	
	public InformedCandidature(Contract c) {
		super(c.getInitiator(),c.getAgent(), c.getResource(), c.getValidityTime());
		mainContract = c;
		Collection<Contract> actions = new ArrayList<Contract>();
		actions.add(mainContract);
		this.consequentContract = 
				new ReallocationContract<Contract, ActionSpec>(
						mainContract.getInitiator(), 
						actions, 
						mainContract.getValidityTime());
	}

	/*
	 * 
	 */

	public Contract getMainContract() {
		return mainContract;
	}

	public ReallocationContract<Contract, ActionSpec> getConsequentContract() {
		return consequentContract;
	}

	public void setConsequentContract(
			ReallocationContract<Contract, ActionSpec> consequentContract) {
		this.consequentContract = consequentContract;
	}
	
	/*
	 * 
	 */

	public <State extends ActionSpec> State computeResultingState(State s) {
		return consequentContract.computeResultingState(s);
	}

	public ActionSpec computeResultingState(AgentIdentifier id) {
		return consequentContract.computeResultingState(id);
	}
	
	/*
	 * 
	 */
	


	public long getValidityTime() {
		return mainContract.getValidityTime();
	}


	public AgentIdentifier getAgent() {
		return mainContract.getAgent();
	}


	public ResourceIdentifier getResource() {
		return mainContract.getResource();
	}


	public boolean isMatchingCreation() {
		return mainContract.isMatchingCreation();
	}


	public Boolean getCreation() {
		return mainContract.getCreation();
	}


	public AgentIdentifier getInitiator() {
		return mainContract.getInitiator();
	}


	public void setCreation(Boolean creation) {
		mainContract.setCreation(creation);
	}


	public Collection<AgentIdentifier> getAllInvolved() {
		return mainContract.getAllInvolved();
	}


	public String toString() {
		return mainContract.toString();
	}


	public List<AgentIdentifier> getAllParticipants() {
		return mainContract.getAllParticipants();
	}


	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		return mainContract.getNotInitiatingParticipants();
	}


	public void setSpecification(ActionSpec s) {
		mainContract.setSpecification(s);
	}


	public ContractIdentifier getIdentifier() {
		return mainContract.getIdentifier();
	}


	public long getUptime() {
		return mainContract.getUptime();
	}


	public long getCreationTime() {
		return mainContract.getCreationTime();
	}


	public boolean hasReachedExpirationTime() {
		return mainContract.hasReachedExpirationTime();
	}


	public boolean willReachExpirationTime(long t) {
		return mainContract.willReachExpirationTime(t);
	}


	public ActionSpec getSpecificationOf(AgentIdentifier id) {
		return mainContract.getSpecificationOf(id);
	}


	public boolean equals(Object o) {
		return mainContract.equals(o);
	}


	public int hashCode() {
		return mainContract.hashCode();
	}	
}
