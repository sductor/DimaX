package frameworks.negotiation.exploration;

import frameworks.negotiation.NegotiationException;

public interface Solver {



	/**
	 * 
	 * @param millisec the time limit to compute soution in millisecond
	 */
	public abstract void setTimeLimit(int millisec);

	public class UnsatisfiableException extends NegotiationException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -1556722149681744457L;
		public UnsatisfiableException(final String string) {
			super(string);
		}
		public UnsatisfiableException() {
		}
	}

	public class UnsolvedException extends NegotiationException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 892999519405216489L;}

	public class ExceedLimitException extends NegotiationException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6677775370022039495L;}
}
