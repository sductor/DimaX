package dima.introspectionbasedagents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dima.introspectionbasedagents.ontologies.Protocol;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CompetenceProtocol {

	public Class<? extends Protocol> value();
//	public Class<? extends ProtocolRole> role() default ProtocolRole.class;
}