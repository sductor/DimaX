package dima.introspectionbasedagents.shells;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.LinkedList;

import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.support.GimaObject;


public class MethodHandler extends SimpleMethodHandler {

	/**
	 *
	 */
	private static final long serialVersionUID = -8867529827033886947L;
	ActiveComponentInterface caller;
	Object[] args=null;
	boolean active=true;


	//
	// Constructor
	//


	public MethodHandler(final ActiveComponentInterface caller, final Method mt)//, final Object[] args)
			throws SecurityException, IllegalArgumentException{
		super(mt);
		this.caller = caller;
	}


	public MethodHandler(final ActiveComponentInterface caller, final String methodName, final Class<?>[] signature, final Object[] args)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException{
		super(caller.getClass().getMethod(methodName, signature==null?SimpleMethodHandler.getSignature(args):signature));
		this.caller = caller;
		this.args = args;
		if (!SimpleMethodHandler.checkSignature(this.getParameterTypes(), args)) {
			throw new IllegalArgumentException();
		}
	}

	//
	// Accessors
	//

	public boolean isActive() {
		return active && caller.isActive();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public DimaComponentInterface getMyComponent() {
		return this.caller;
	}

	//
	// Methods
	//

	/**
	 * Execute the method mt of agent ag with arguments args Update the
	 * currentlyExecutedAgent and currentlyExecutedMethod variables
	 *
	 * @param ag
	 * @param mt
	 * @param args
	 * @return the result of the method
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Object execute(final Object... args) throws Throwable {
		try {
			return this.execute(this.caller, args);
		} catch (final IllegalAccessException e) {
			LogService.writeException(this, "Impossible");
			return null;
		} catch (final IllegalArgumentException e) {
			LogService.writeException(this, "wrong arg!! "+args,e);
			return null;
		}  catch (final InvocationTargetException e) {
			// The method has not been invocated
			//				LogService.writeException(caller,
			//						"Couldn't invokate method "+
			//						this,
			//						e.getCause());
			throw e.getCause();
		}
	}

	public Object execute() throws Throwable {
		try {
			return this.execute(this.caller, this.args);
		} catch (final IllegalAccessException e) {
			LogService.writeException(this, "Impossible");
			return null;
		} catch (final IllegalArgumentException e) {
			LogService.writeException(this, "Impossible");
			return null;
		}  catch (final InvocationTargetException e) {
			// The method has not been invocated
			//				LogService.writeException(caller,
			//						"Couldn't invokate method "+
			//						this,
			//						e.getCause());
			throw e.getCause();
		}
	}

	//
	// Primitive
	//

	private Object execute(final Object myComponent, final Object[] args) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException {
		if (active){	
			final Method m = this.getMethod();
			if (!m.isAccessible()) {
				m.setAccessible(true);
			}
			return m.invoke(myComponent, args);
		} else {
			return null;
		}
	}
}


/**
 * This class allow to stock methods as serializable object. (Method class
 * is not serializable)
 *
 * It allows to execute the method and to obtain it annotations
 *
 * @author Sylvain Ductor
 *
 */
class SimpleMethodHandler extends GimaObject {

	private static final long serialVersionUID = 6179083297892397923L;

	private final Class<?> caller;
	private final String methodName;
	private final Class<?>[] parameterTypes;

	/**
	 *
	 * @param mt
	 *            the method to be stoken
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public SimpleMethodHandler(final Method mt) {
		super();
		this.caller=mt.getDeclaringClass();
		this.methodName = mt.getName();
		this.parameterTypes = mt.getParameterTypes();
	}

	//
	// Accessor
	//

	public Method getMethod(){
		try {
			final Method m = this.caller.getDeclaredMethod(this.getMethodName(),
					this.getParameterTypes());
			return m;
		} catch (final Exception e) {
			// The method has not been invocated
			LogService.writeException(
					"Couldn't invocate method "+this.methodName,
					e);
			return null;
		}

	}

	/*
	 *
	 */

	public String getMethodName() {
		return this.methodName;
	}

	public Class<?>[] getParameterTypes() {
		return this.parameterTypes;
	}

	public Object getReturnType() {
		return this.getMethod().getReturnType();
	}

	/*
	 *
	 */

	public Annotation[] getAnnotations() {
		return	this.getMethod().getAnnotations();
	}

	public boolean isAnnotationPresent(final Class<? extends Annotation> c) {
		return this.getMethod().isAnnotationPresent(c);
	}

	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)  {
		return this.getMethod().getAnnotation(annotationClass);
	}

	/**
	 *
	 * @param numberofArg
	 * @param numberOfGernericType
	 * @return The class of the numberOfGernericType th of the numberofArg th arguments
	 * or null if one of those not exist
	 */
	public Class<?> getGenericClassOfArgument(final int numberofArg, final int numberOfGernericType){
		try {
			final Method mt = this.caller.getMethod(this.getMethodName(), this.getParameterTypes());
			return
					(Class<?>)
					((ParameterizedType) mt.getGenericParameterTypes()[numberofArg]).
					getActualTypeArguments()[numberOfGernericType];
		} catch (final ClassCastException e){
			return null;
		} catch (final ArrayIndexOutOfBoundsException e){
			return null;
		} catch (final Exception e) {
			// The method has not been invocated
			LogService.writeException(this,
					"Couldn't invocate method "+
							this.getMethodName(),
							e);
			return null;
		}
	}

	public Class<?> getGenericClassOfFirstArgument(){
		return this.getGenericClassOfArgument(0,0);
	}


	//
	// Routines static
	//

	public static  String getCurrentlyExecutedMethod(final int level) {
		try {
			throw new Exception();
		} catch (final Exception e) {
			return e.getStackTrace()[level].getMethodName();
		}
	}
	public static  Class<?> getExecutingObjectClass(final int level) {
		try {
			throw new Exception();
		} catch (final Exception e) {
			try {
				return Class.forName(e.getStackTrace()[level].getClassName());
			} catch (final ClassNotFoundException e1) {
				LogService.writeException("Impossible de pas trouver la classe!!");
				return null;
			}
		}
	}

	public static Class<?>[] getSignature(final Object[] args){
		if (args==null) {
			return null;
		} else {
			final LinkedList<Class<?>> r = new LinkedList<Class<?>>();
			for (final Object arg : args) {
				r.addLast(arg.getClass());
			}
			return r.toArray(new Class<?>[0]);
		}
	}


	public static  boolean checkSignature(
			final Class<?>[] attachementSignature, final Object[] attachement) {
		if (attachementSignature.length == attachement.length) {
			if (attachementSignature.length==0) {
				return true;
			} else {
				int cpt = 0;
				while (attachementSignature[cpt].isAssignableFrom(attachement[cpt]
						.getClass())
						&& cpt < attachement.length - 1) {
					cpt++;
				}
				if (cpt != attachement.length - 1) {
					LogService.writeException("unappropriate message ("
							+ cpt + ") :\n" + Arrays.asList(attachement) + ","
							+ Arrays.asList(attachementSignature));
					return false;
				} else {
					return true;
				}
			}
		} else {
			LogService.writeException("unappropriate message (different length) :"
					+ Arrays.asList(attachement) + ","
					+ Arrays.asList(attachementSignature));
			return false;
		}


	}

	//
	// Methods
	//

	@Override
	public String toString() {
		return
				this.getMethodName()
				+ "\n      --> param :"+ (this.parameterTypes == null ? "null" : Arrays.asList(this.parameterTypes))
				+ "\n      --> callerClass: '" + this.caller	+ "'";
	}
}

//
//public Object getReturnType() {
//	try {
//		return this.caller.getMethod(this.getMethodName(),
//				this.getParameterTypes()).getReturnType();
//	} catch (final Exception e) {
//		// The method has not been invocated
//		LoggerManager.writeException(
//				"Couldn't invocate method "+this.methodName,
//				e);
//		return null;
//	}
//}

///**
// *
// * @param caller
// *            the object from wich the method is taken
// * @param name
// *            the name of the method
// * @param parameterTypes
// *            the class arrays taht represent the type of the method
// *            parameters
// * @throws NoSuchMethodException
// * @throws SecurityException
// */
//public MethodHandler(final Object caller, final String name,
//		final Class<?>[] parameterTypes) throws SecurityException, NoSuchMethodException {
//	super();
//	myComponent=caller;
//	this.methodName = name;
//	this.parameterTypes = parameterTypes;
//
//	//TEST:
//	this.myComponent.getClass().getMethod(this.getMethodName(), this.parameterTypes);
//}

