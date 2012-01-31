package dima.basicagentcomponents;

import dima.basiccommunicationcomponents.CommunicationObject;

/**
 * Insert the type's description here.
 * Creation date: (02/03/2000 00:01:25)
 * @author: Gerard Rozsavolgyi
 */
//import Gdima.basiccommunicationcomponents.CommunicationObject;

public abstract class AgentIdentifier extends CommunicationObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1729969312196491814L;
	/**
	 * CommunicationAgentIdentifier constructor comment.
	 */
	public AgentIdentifier() {
		super();
	}
	/**
	 * Compares two objects for equality. Returns a boolean that indicates
	 * whether this object is equivalent to the specified object. This method
	 * is used when an object is stored in a hashtable.
	 * @param obj the Object to compare with
	 * @return true if these Objects are equal; false otherwise.
	 * @see java.util.Hashtable
	 */
	@Override
	public boolean equals(final Object obj) {
		// Insert code to compare the receiver and obj here.
		// This implementation forwards the message to super.  You may replace or supplement this.
		// NOTE: obj might be an instance of any class
		return super.equals(obj);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:56:20)
	 * @return java.lang.Object
	 */
	public abstract Object getId();
	/**
	 * Generates a hash code for the receiver.
	 * This method is supported primarily for
	 * hash tables, such as those provided in java.util.
	 * @return an integer hash code for the receiver
	 * @see java.util.Hashtable
	 */
	@Override
	public int hashCode() {
		// Insert code to generate a hash code for the receiver here.
		// This implementation forwards the message to super.  You may replace or supplement this.
		// NOTE: if two objects are equal (equals(Object) returns true) they must have the same hash code
		return super.hashCode();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:56:20)
	 * @return java.lang.Object
	 */
	public abstract void setId(Object id);
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return super.toString();
	}
}
