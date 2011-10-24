package dima.introspectionbasedagents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method annotated with @MessageCollectionHandler
 * must return a boolean value and take exactly a collection of message as argument.
 * Each time a message of the same envellope is received, the shell will add it
 * in the current collection of such received message and try to execute the method with it
 * When the method return true the current collection is cleaned
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageCollectionHandler {}