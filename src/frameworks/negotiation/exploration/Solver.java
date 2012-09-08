package frameworks.negotiation.exploration;

import java.util.Collection;

import frameworks.negotiation.NegotiationException;

public interface Solver {


	/**
	 * Compute and stock the optimal solution find so far in the solver format
	 */
	public abstract void computeBestSolution();
	
	/**
	 * 
	 * @return false if the problem has been proven to have no more solution
	 */
	public abstract boolean hasNext();

	/**
	 * add a new satifaible solution to the poll of solutions
	 */
	public abstract void addNextSolution();

	/**
	 * add every satifaible solution find within the eventual time constraint to the poll of solutions
	 */
	public abstract void addAllSolution();
	
	/**
	 * 
	 * @param millisec the time limit to compute soution in millisecond
	 */
	public abstract void setTimeLimit(int millisec);

	public abstract void setNumberOfSolutionLimit(int numberOfSolution);

	public class UnsatisfiableException extends NegotiationException{}

	public class UnsolvedException extends NegotiationException{}

	public class ExceedLimitException extends NegotiationException{}
}
