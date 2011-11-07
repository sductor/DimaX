package dima.introspectionbasedagents.services.core.observingagent;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetenceShell;
import dima.introspectionbasedagents.shells.IntrospectionStaticPrimitivesLibrary;
import dima.introspectionbasedagents.shells.MethodHandler;
import dimaxx.tools.mappedcollections.HashedHashSet;


public class PatternObserverWithHookservice extends PatternObserverService {

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface EventHookedMethod {
		Class<? extends Serializable> value();
		//		String key() default "";


	}

	//
	// Field
	//

	public HashedHashSet<String, MethodHandler> registeredMethods;


	//
	// Accessors
	//

	public PatternObserverWithHookservice(
			BasicCompetentAgent ag) throws UnrespectedCompetenceSyntaxException {
		super(ag);
	}

	public static void registerEventMethod(BasicCompetentAgent ag, PatternObserverWithHookservice me) 
			throws UnrespectedCompetenceSyntaxException{

		me.registeredMethods =
				new HashedHashSet<String, MethodHandler>();
		for (Method m : IntrospectionStaticPrimitivesLibrary.getAllMethods(ag.getClass())){
			if (m.isAnnotationPresent(EventHookedMethod.class)){
				if (checkEventHookedMethodValidity(m)){
					me.registeredMethods.add(m.getAnnotation(EventHookedMethod.class).value().getName(), new MethodHandler(ag, m));
				}else
					throw new  UnrespectedCompetenceSyntaxException(me.toString());
			}
		}
		for (AgentCompetence<?> comp : BasicCompetenceShell.getNativeCompetences(ag)){
			for (Method m : IntrospectionStaticPrimitivesLibrary.getAllMethods(comp.getClass())){
				if (m.isAnnotationPresent(EventHookedMethod.class)){
					if (checkEventHookedMethodValidity(m)){
						me.registeredMethods.add(m.getAnnotation(EventHookedMethod.class).value().getName(), new MethodHandler(comp, m));
					}else
						throw new  UnrespectedCompetenceSyntaxException(me.toString());
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
		if (registeredMethods!=null){//C'EST MOOOOOOOOOOOOOOOOOOCCCCCCCCCCCCCCCHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEEEEE!!!!!!!!!!!!!!	
			MethodHandler msave=null ;
			try {
				for (MethodHandler m : registeredMethods.get(key)){
					msave=m;
					m.execute(notification);
				}
			} catch (Throwable e) {
				throw new RuntimeException("error executing "+msave+"\n ---> NB : exception runtime suivante a ignorer seule la cause est pertinente",e);
			}
		}
		return super.notify(notification,key);
	}

	//
	// Primitives
	//

	public static boolean checkEventHookedMethodValidity(Method mt){
		if (mt.isAnnotationPresent(EventHookedMethod.class)){
			Class<? extends Serializable> keyclass = mt.getAnnotation(EventHookedMethod.class).value();
			if ((mt.getParameterTypes().length == 1
					&& keyclass.isAssignableFrom(
							mt.getParameterTypes()[0]))) {
				return true;
			} else {
				LogService.writeException(
						mt,
						"Wrong parameters type for message parser method " + mt
						+ " should be only one message class");
				return false;
			}
		} else
			return false;
	}
}
