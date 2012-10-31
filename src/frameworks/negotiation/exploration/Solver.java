package frameworks.negotiation.exploration;

import java.util.Collection;

import frameworks.negotiation.NegotiationException;

public interface Solver {



	/**
	 * 
	 * @param millisec the time limit to compute soution in millisecond
	 */
	public abstract void setTimeLimit(int millisec);

	public class UnsatisfiableException extends NegotiationException{

		public UnsatisfiableException(String string) {
			super(string);
		}
		public UnsatisfiableException() {
		}
	}

	public class UnsolvedException extends NegotiationException{}

	public class ExceedLimitException extends NegotiationException{}
}
