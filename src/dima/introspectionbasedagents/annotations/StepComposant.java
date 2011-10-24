package dima.introspectionbasedagents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Les méthodes StepComposant sont exécuté à chaque pas de temps
 * Il est cependant possible de définir une valeur qui sera le temps minimum
 * entre chaque execution (en milliseconde) **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StepComposant {
	long ticker() default -1;
}