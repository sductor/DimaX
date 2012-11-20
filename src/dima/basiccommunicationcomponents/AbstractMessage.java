package dima.basiccommunicationcomponents;

import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;
import dima.introspectionbasedagents.services.loggingactivity.AbstractDebugableMessage;

/**
 * Insert the type's description here.
 * Creation date: (01/03/2000 23:42:49)
 * @author: Gerard Rozsavolgyi
 */
public abstract class AbstractMessage extends AbstractDebugableMessage implements AbstractMessageInterface {
	private static final long serialVersionUID = -5044580240872440630L;

	Object content;
	private int serial;
	static int number=-1;
	/**
	 * AbstractMessage constructor comment.
	 */
	public AbstractMessage() {
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
	 * Returns the Message's content.
	 * Creation date: (01/03/2000 23:44:32)
	 * @return java.lang.Object
	 */
	@Override
	public abstract Object getContent();
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
	 * Set Message's content.
	 * Creation date: (01/03/2000 23:44:32)
	 * @return java.lang.Object
	 */
	public abstract void setContent(Object o);
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		//	System.out.println(this.getClass());
		if (this.getContent()==null) {
			return "no content";
		} else {
			return this.getContent().toString();
		}
	}

	@Override
	public AbstractMessage clone(){
		throw new RuntimeException("clone not overrided!!");
	}
	@Override
	public int getSerial() {
		return this.serial;
	}
	@Override
	public boolean setSerial(final int serial) {
		this.serial = serial;
		return true;
	}
}
