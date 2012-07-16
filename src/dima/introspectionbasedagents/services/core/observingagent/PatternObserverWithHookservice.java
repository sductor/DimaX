package dima.introspectionbasedagents.services.core.observingagent;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;


import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.shells.BasicCompetenceShell;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.IntrospectionStaticPrimitivesLibrary;
import dima.introspectionbasedagents.shells.MethodHandler;


public class PatternObserverWithHookservice extends PatternObserverService {

	/**
	 *
	 */
	private static final long serialVersionUID = 3707890491351281107L;

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface EventHookedMethod {
		Class<? extends Serializable> value();
		//				String key() default "";


	}

	//
	// Field
	//

	public HashedHashSet<String, MethodHandler> registeredMethods;


	//
	// Accessors
	//

	public PatternObserverWithHookservice(
			final BasicCompetentAgent ag) throws UnrespectedCompetenceSyntaxException {
		super(ag);
	}

	public static void registerEventMethod(final BasicCompetentAgent ag, final PatternObserverWithHookservice me)
			throws UnrespectedCompetenceSyntaxException{

		me.registeredMethods =
				new HashedHashSet<String, MethodHandler>();
		for (final Method m : IntrospectionStaticPrimitivesLibrary.getAllMethods(ag.getClass())) {
			if (m.isAnnotationPresent(EventHookedMethod.class)) {
				if (PatternObserverWithHookservice.checkEventHookedMethodValidity(m)) {
					me.registeredMethods.add(m.getAnnotation(EventHookedMethod.class).value().getName(), new MethodHandler(ag, m));
				} else {
					throw new  UnrespectedCompetenceSyntaxException(me.toString());
				}
			}
		}
		for (final AgentCompetence<?> comp : BasicCompetenceShell.getNativeCompetences(ag)) {
			for (final Method m : IntrospectionStaticPrimitivesLibrary.getAllMethods(comp.getClass())) {
				if (m.isAnnotationPresent(EventHookedMethod.class)) {
					if (PatternObserverWithHookservice.checkEventHookedMethodValidity(m)) {
						me.registeredMethods.add(m.getAnnotation(EventHookedMethod.class).value().getName(), new MethodHandler(comp, m));
					} else {
						throw new  UnrespectedCompetenceSyntaxException(me.toString());
					}
				}
			}
		}
	}

	//
	// Methods
	//

	@Override
	public <Notification extends Serializable> Boolean notify(final Notification notification, final String key) {

		//		System.out.flush();
		if (this.registeredMethods!=null){//C'EST MOOOOOOOOOOOOOOOOOOCCCCCCCCCCCCCCCHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!
			MethodHandler msave=null ;
			try {//System.out.println(this.registeredMethods+"           "+key);
				for (final MethodHandler m : this.registeredMethods.get(key)){

					msave=m;
					m.execute(notification);
				}
			} catch (final Throwable e) {
				throw new RuntimeException("PatternObserverWithHookservice : error executing hooked method "+msave+"\n ---> NB : exception runtime suivante a ignorer seule la cause est pertinente",e);
			}
		}
		return super.notify(notification,key);
	}

	//
	// Primitives
	//

	public static boolean checkEventHookedMethodValidity(final Method mt){
		if (mt.isAnnotationPresent(EventHookedMethod.class)){
			final Class<? extends Serializable> keyclass = mt.getAnnotation(EventHookedMethod.class).value();
			if (mt.getParameterTypes().length == 1
					&& keyclass.isAssignableFrom(
							mt.getParameterTypes()[0])) {
				return true;
			} else {
				LogService.writeException(
						mt,
						"Wrong parameters type for message parser method " + mt
						+ " should be only one message class");
				return false;
			}
		} else {
			return false;
		}
	}
}
