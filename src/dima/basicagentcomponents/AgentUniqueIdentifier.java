package dima.basicagentcomponents;

import java.math.BigInteger;

import dima.basiccommunicationcomponents.CommunicationObject;

/**
 * Provides a unique mean of identify Agents.
 * 
 * @author Vincent Letard
 */
public class AgentUniqueIdentifier extends CommunicationObject {

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
	}
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
}
