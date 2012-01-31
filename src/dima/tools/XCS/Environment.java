package dima.tools.XCS;

/**
 * This is the interface that must be implemented by all problems presented to the XCSJava implementation.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public interface Environment
{
	/**
	 * Returns if the agent has reached the end of a problem.
	 * In a classification problem such as the Multiplexer Problem, this function should return true
	 * after a classification was executed. In a multi-step problem like the reward learning in maze environments
	 * this function should return true when the animat reached a food position.
	 */
	public abstract boolean doReset();
	/**
	 * Executes an action in the environment.
	 *
	 * @param action An action can be an active action like a movement, grip...
	 * or a simple classification (good/bad, correct/incorrect, class1/class2/class3, ...).
	 */
	public abstract double executeAction(int action);
	/**
	 * Returns the length of the coded situations.
	 */
	public abstract int getConditionLength();
	/**
	 * Returns the current situation.
	 * A situation can be the current perceptual inputs, a random problem instance ...
	 */
	public abstract String getCurrentState();
	/**
	 * Returns the maximal payoff receivable in an environment.
	 */
	public abstract int getMaxPayoff();
	/**
	 * Returns the number of possible actions in the environment
	 */
	public abstract int getNrActions();
	/**
	 * Returns true if the problem is a multi-step problem.
	 * Although the doReset() function already distinguishes multi-step and single-step problems,
	 * this functions is used in order to get a better performance analysis!
	 */
	public abstract boolean isMultiStepProblem();
	/**
	 * Resets the current state to a random instance of a problem.
	 * A random instance can be a next problem in a table, a randomly generated string, a random position in
	 * a maze ...
	 */
	public abstract String resetState();
	/**
	 * Returns if this action was a good/correct action.
	 * This function is essentially necessary in single-step (classification) problems in order
	 * to evaluate the performance.
	 */
	public abstract boolean wasCorrect();
}
