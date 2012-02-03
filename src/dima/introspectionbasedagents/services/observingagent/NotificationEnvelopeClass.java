package dima.introspectionbasedagents.services.observingagent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dima.introspectionbasedagents.ontologies.Envelope;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.MethodHandler;

public class NotificationEnvelopeClass implements Envelope {

	//
	// Field
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -8029991545192767941L;
	private final String key;

	//
	// Constructors
	//

	public NotificationEnvelopeClass(
			final NotificationEnvelope e,
			final MethodHandler mt){
		if (!e.value().equals(""))
			this.key = e.value();
		else if (mt.getGenericClassOfFirstArgument() != null)
			this.key = mt.getGenericClassOfFirstArgument().getName();
		else{
			this.key = null;
			LogService.writeWarning(mt, "Could not get notification envelope!");
		}
	}

	public NotificationEnvelopeClass(final String key) {
		this.key = key;
	}

	//
	// Annotation
	//

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface NotificationEnvelope{
		String value() default "";
	}

	//
	// Primitive
	//

	@Override
	public boolean equals(final Object o){

		if (o instanceof NotificationEnvelopeClass){
			final NotificationEnvelopeClass that = (NotificationEnvelopeClass) o;
			//			try {// attempting key are classes
			//				Class<?> thisClass = Class.forName(this.key);
			//				Class<?> thatClass = Class.forName(that.key);
			//				return thisClass.isAssignableFrom(thatClass) || thatClass.isAssignableFrom(thisClass);
			//			} catch (ClassNotFoundException e) {
			//				//the key are not classes so simple equals of string
			return this.key.equals(((NotificationEnvelopeClass) o).key);
			//			}
		}else
			return false;
	}

	@Override
	public int hashCode(){
		//		try {// attempting key are classes
		//			Class<?> thisClass = Class.forName(this.key);
		//			while (thisClass.isAssignableFrom(Serializable.class))
		//				thisClass = thisClass.getSuperclass();
		//			return thisClass.hashCode();
		//		} catch (ClassNotFoundException e) {
		//			//the key are not classes so simple equals of string
		return this.key.hashCode();
		//		}
	}

	@Override
	public String toString(){
		return "Envelope of notification "+this.key;
	}


}
