package dima.basicagentcomponents;

import java.math.BigInteger;

/**
 * Provides a unique means of identify Agents.
 * 
 * @author Vincent Letard
 */
public class AgentUniqueIdentifier extends AgentIdentifier {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 4350578412067332331L;

    /**
     * The next identifier to be allocated.
     */
    private static BigInteger nextId = BigInteger.ZERO;

    /**
     * The identifier of this Agent.
     */
    private final BigInteger myId;

    /**
     * Constructs a new AgentUniqueIdentifier.
     */
    public AgentUniqueIdentifier() {
	super();
	this.myId = nextId;
	nextId = nextId.add(BigInteger.ONE);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof AgentUniqueIdentifier) {
	    return ((AgentUniqueIdentifier) obj).myId.equals(this.myId);
	} else
	    return false;
    }

    @Override
    public int hashCode() {
	return this.myId.intValue();
    }

    @Override
    public String toString() {
	return this.myId.toString();
    }

    @Override
    public BigInteger getId() {
	return this.myId;
    }

    /**
     * This method is not supported for AgentUniqueIdentifier, but could be for
     * subclasses.
     */
    @Override
    public void setId(Object id) {
	throw new UnsupportedOperationException();
    }
}
