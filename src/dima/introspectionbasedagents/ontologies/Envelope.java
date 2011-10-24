package dima.introspectionbasedagents.ontologies;

import java.io.Serializable;

import dima.introspectionbasedagents.shells.BasicCommunicatingShell;

/**
 * This class should be implemented by any class which aims at being used as an envelope.
 * @see BasicCommunicatingShell
 *
 * In order to define a new  envelope :
 * 1. Create a class that implements this interface
 * 2. Create the annotation used for the method as a subclass of this class
 * * * The annotation declaration must be annotated with :
 * * * * * * * * * * * * * * * @Documented
 * * * * * * * * * * * * * * * @Retention(RetentionPolicy.RUNTIME)
 * * * * * * * * * * * * * * * @Target(ElementType.METHOD)
 * 3. Create a constructor of this class which take exactly two arguments:
 * * * * * * * * * * * * * * * 1) An enclosed instance of the annotation,
 * * * * * * * * * * * * * * * 2) The class of the argument of the method (Class<AbstractMessage> argClass).
 * 4. You might want to override equals() and hashCode()
 * 5. Messages associated to this envelope must implements MessageInEnvelope interface,
 *  otherwise the default class envelope is used
 *
 * @author Ductor Sylvain
 */
public interface Envelope extends Serializable {

}
