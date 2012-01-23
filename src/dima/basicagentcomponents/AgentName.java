package dima.basicagentcomponents;

/**
 * Insert the type's description here.
 * Creation date: (27/04/00 11:58:20)
 * @author: Gerard Rozsavolgyi
 */
public class AgentName extends AgentIdentifier {
	/**
	 *
	 */
	private static final long serialVersionUID = 3087407715476845306L;
	private java.lang.String id;
	/**
	 * AgentLabel constructor comment.
	 */
	public AgentName() {
		this.id = "anAgent";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/05/00 00:17:40)
	 * @param s java.lang.String
	 */
	public AgentName(final String s) {this.id = s;}
	/**
	 * Compares two objects for equality. Returns a boolean that indicates
	 * whether this object is equivalent to the specified object. This method
	 * is used when an object is stored in a hashtable.
	 * @param obj the Object to compare with
	 * @return true if these Objects are equal; false otherwise.
	 * @see java.util.Hashtable
	 */
	public boolean equals(final AgentIdentifier agentId) {
		// Insert code to compare the receiver and obj here.
		// This implementation forwards the message to super.  You may replace or supplement this.
		// NOTE: obj might be an instance of any class
		return this.toString().equals(agentId.toString());
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
		if (obj instanceof AgentName)
			return this.toString().equals(((AgentName)obj).toString());
		else
			return false;
	}
	/**
	 * getId method comment.
	 */
	@Override
	public Object getId() {
		return this.id;
	}
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
		return this.toString().hashCode();
	}
	/**
	 * setId method comment.
	 */
	@Override
	public void setId(final Object s) {this.id=(String)s;}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return (String)this.getId();
	}
}
