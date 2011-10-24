package dima.introspectionbasedagents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Les méthodes annoter @Transient doivent retourner une valeur booléenne
 * Elles sont éxécuter tant qu'elle retourne false
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transient {}