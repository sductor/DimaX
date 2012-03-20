package dima.kernel.adaptiveAgent;


/**
 * Insert the type's description here.
 * Creation date: (17/04/2003 18:03:07)
 * @author:
 */
public class XCSAgentEnvironment implements dima.tools.XCS.Environment {
	// length of the attribute
	protected int attributeLength;

	// number of the initial strategies
	static int nbActions=4;

	//The number of parameters perceived by the firm.
	private final int nbParameters=17;




	// the classifier system XCSFirm
	public XCSBasedReactiveAgent agent;


	/**
	 * Insert the method's description here.
	 * Creation date: (09/10/2003 10:53:59)
	 */
	public XCSAgentEnvironment() {}
	/**
	 * XCSBasedFirm constructor comment.
	 */
	public XCSAgentEnvironment(final int attlen) {
		this.attributeLength=attlen;

	}
	/**
	 * XCSBasedFirm constructor comment.
	 * @param firm aMoveco.Firm.Firm
	 */
	public XCSAgentEnvironment(final XCSBasedReactiveAgent f, final int attlen) {
		this.agent = f;
		this.attributeLength=attlen;

	}
	/**
	 * @param agent2
	 * @param nbActions2
	 * @param attributeLength2
	 */
	public XCSAgentEnvironment(final XCSBasedReactiveAgent f, final int nbActions2, final int attributeLength2) {
		this.agent = f;
		this.attributeLength=attributeLength2;
		XCSAgentEnvironment.nbActions= nbActions2;
	}
	@Override
	public boolean doReset() {
		return ! this.agent.competenceIsActive();
	}
	/**
	 * Executes an action in the environment.
	 *
	 * @param action An action can be an active action like a movement, grip...
	 * or a simple classification (good/bad, correct/incorrect, class1/class2/class3, ...).
	 */
	@Override
	public double executeAction(final int action) {
		// d�termination du reward en fonction de la strat�gie
		final float reward = this.agent.determinReward();


		return reward;
	}
	/**
	 * Returns the length of the coded situations.
	 */
	@Override
	public int getConditionLength() {
		return this.nbParameters*this.attributeLength;
	}
	/**
	 * updates the perceived parameters of the firm
	 * Creation date: (18/04/2003 10:50:28)
	 */
	@Override
	public  String getCurrentState()
	{
		return this.agent.getPerceptions();
	}
	/**
	 * Returns the maximal payoff receivable in an environment.
	 */
	@Override
	public int getMaxPayoff() {
		return 0;
	}
	/**
	 * Returns the number of possible actions in the environment
	 */
	@Override
	public int getNrActions() {
		return XCSAgentEnvironment.nbActions;
	}
	/**
	 * updates the perceived parameters of the firm
	 * Creation date: (18/04/2003 10:50:28)
	 */
	public String getPerceptions() {

		return this.agent.getPerceptions();
	}
	// shows if the problem is multi-step or not
	@Override
	public boolean isMultiStepProblem() {
		return true;
	}

	@Override
	public String resetState() {
		return this.getPerceptions();

	}
	/**
	 * Returns if this action was a good/correct action.
	 * This function is essentially necessary in single-step (classification) problems in order
	 * to evaluate the performance.
	 */
	@Override
	public boolean wasCorrect() {
		return false;
	}
}
