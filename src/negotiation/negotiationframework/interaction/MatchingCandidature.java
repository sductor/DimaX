package negotiation.negotiationframework.interaction;

import dima.basicagentcomponents.AgentIdentifier;

public abstract class MatchingCandidature<
ActionSpec extends AbstractActionSpecification>
		extends ContractTransition<ActionSpec> {

	/**
	 *
	 */
	private static final long serialVersionUID = -298715657421429671L;
	protected Boolean creation = null;


	/*
	 *
	 */

	public MatchingCandidature(final AgentIdentifier intiator,
			final AgentIdentifier a, final ResourceIdentifier r,
			final long validityTime) {
		super(intiator, "matching", new AgentIdentifier[] { a, r },
				new Object[] {}, validityTime);
	}

	// public MatchingCandidature(AgentIdentifier intiator,
	// final AgentIdentifier a,final ResourceIdentifier r,
	// final boolean creation,
	// final long validityTime) {
	// super(intiator, "matching",new AgentIdentifier[]{a,r},new
	// Object[]{},validityTime);
	// this.creation = creation;
	// this.validityTime=validityTime;
	// }
	/*
	 *
	 */
	public AgentIdentifier getAgent() {
		return this.getAllParticipants().get(0);
	}

	public ResourceIdentifier getResource() {
		return (ResourceIdentifier) this.getAllParticipants().get(1);
	}

	public boolean isMatchingCreation() {
		return this.creation;
	}

	public Boolean getCreation() {
		return this.creation;
	}

	public void setCreation(final Boolean creation) {
		this.creation = creation;
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		return this.getIdentifier() + " -> ("
				+ (this.creation ? "create" : "destruct") + ")"
//		+",consensual?"+isConsensual()+"("+agentHasAccepted+","+resourceHasAccepted+")";
		+"\n  -----> agent specif : "+this.getAgent()+", "+this.getSpecificationOf(this.getAgent())
		+"\n  -----> host specif : "+this.getResource()+", "+this.getSpecificationOf(this.getResource());
	}
}

/*
 *
 */

// private Boolean agentHasAccepted=null;
// private Boolean resourceHasAccepted=null;
// if (creation){
// this.agentHasAccepted=true;
// this.resourceHasAccepted=false;
// } else {
// this.agentHasAccepted=false;
// this.resourceHasAccepted=true;
// }

// @Override
// public void setAccepted(final AgentIdentifier id) {
// if (id.equals(this.getAgent()))//&&agentHasAccepted==null
// this.agentHasAccepted=true;
// else if (id.equals(this.getResource()))//&&resourceHasAccepted==null
// this.resourceHasAccepted=true;
// else
// throw new
// RuntimeException(id+" : I should not receive it ("+id+","+agentHasAccepted+","+resourceHasAccepted+")"+this);
// }
//
// @Override
// public void setRejected(AgentIdentifier id) {
// if (id.equals(this.getAgent()))//&&agentHasAccepted==null
// this.agentHasAccepted=false;
// else if (id.equals(this.getResource()))//&&resourceHasAccepted==null
// this.resourceHasAccepted=false;
// else
// throw new RuntimeException(id+" : I should not receive it "+this);
// }
//
// @Override
// public boolean isConsensual() {
// if (this.agentHasAccepted!=null && this.resourceHasAccepted!=null)
// return this.agentHasAccepted && this.resourceHasAccepted;
// else
// return false;
// }
//
// @Override
// public boolean isAFailure() {
// if (this.agentHasAccepted!=null && this.resourceHasAccepted!=null)
// return this.agentHasAccepted==false || this.resourceHasAccepted==false;
// else
// return false;
// }

// public Boolean getHostAnswer(){
// return resourceHasAccepted;
// }
//
// public Boolean getAgentAnswer(){
// return agentHasAccepted;
// }