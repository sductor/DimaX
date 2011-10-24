package dima.introspectionbasedagents.ontologies;

/**
 * Message associated to an envellope.
 * If a message that does not implements this interface is received by a message handler, it will be handled with a class envelope
 * @author ductors
 *
 */
public interface MessageInEnvelope {

	public Envelope getMyEnvelope();
}
