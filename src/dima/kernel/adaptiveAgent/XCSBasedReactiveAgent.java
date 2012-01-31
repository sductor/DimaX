package dima.kernel.adaptiveAgent;

import dima.kernel.communicatingAgent.BasicCommunicatingAgent;



public abstract class XCSBasedReactiveAgent extends BasicCommunicatingAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = 6159459777329627916L;
	public XCSForAgent cs;
	protected int attributeLength;
	protected int nbActions;
	protected int explore;

	/**
	 * XCSBasedReactiveAgent constructor comment.
	 */
	public XCSBasedReactiveAgent() {
		super();
	}
	/**
	 * XCSBasedReactiveAgent constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public XCSBasedReactiveAgent(final dima.basicagentcomponents.AgentIdentifier newId, final XCSForAgent X) {
		super(newId);
		this.cs=X;
	}


	/* This is the main method for a proactive component :
	 * what to do while in activity.
	 *
	 */
	@Override
	public void step() {
		this.cs.doOneMultiStepExperiment(this.explore);
	}

	public abstract String getPerceptions();

	//public abstract float determinReward();

	/**
	 * @param action
	 */
	public abstract double executeAction(int action);
	// TODO Auto-generated method stub


	/**
	 * @return
	 */
	public abstract float determinReward();

	/**
	 * @param action
	 */
}

