package dima.introspectionbasedagents.shells;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GenericCall {


	/**
	 * Used to call a method by introspection
	 * @param className the name of the class the method belongs to
	 * @param methodName the name of the method to call
	 * @param parametersClass the types of the methods parameters ; null if nothing
	 * @param object the object that calls the method; null if the called method is  a constructor
	 * @param methodParameters the effective parameters of the method ; null if nothing
	 * @param constructor true if the method to call is a constructor
	 * @return the result of the call
	 *
	 * Examples:
	 * callMethod("tools.xml.LogL2vC_1", "LogL2vC_1",new Class[]{String.class},null,new Object[]{"40|v"},true);
	 * callMethod("supervision.messages.ChangeLeader", "toString", null, body,null, false);
	 * callMethod("faultAndRepair.faultAndObs.costFunction."+costFunctionClassName,costFunctionClassName,new Class[]{Integer.class} , null,paramList.toArray(), true);
	 *
	 */
	public static Object callMethod(final String className,final String methodName,final Class[] parametersClass,final Object object,final Object[] methodParameters, final boolean constructor){


		Class<?> classe = null;
		Method method = null;
		Constructor<?> construc=null;
		//1° get the class
		try {
			classe = Class.forName(className);
		} catch (final ClassNotFoundException e1) {
			System.out.println("Class "+ className+ " not found");
			e1.printStackTrace();
		}

		//2° get the method
		try {
			if(constructor) {
				construc=classe.getConstructor(parametersClass);
			} else {
				method = classe.getMethod(methodName,parametersClass);
			}
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final NoSuchMethodException e) {
			System.out.println("Method"+ methodName+ "of paramsType String not found");
			e.printStackTrace();
		}
		/*
			 if(constructor){
				System.out.println("constructor found: " + construc.toString());
			}else{
				System.out.println("Public method found: " + method.toString());
			}
		 */

		//3° Call the method
		Object o=null;
		if(constructor) {
			try {
				o=construc.newInstance(methodParameters);
			} catch (final IllegalArgumentException e) {

				e.printStackTrace();
			} catch (final InstantiationException e) {

				e.printStackTrace();
			} catch (final IllegalAccessException e) {

				e.printStackTrace();
			} catch (final InvocationTargetException e) {

				e.printStackTrace();
			}
		} else {
			try {
				o= method.invoke(object,methodParameters);
			} catch (final IllegalArgumentException e) {

				e.printStackTrace();
			} catch (final IllegalAccessException e) {

				e.printStackTrace();
			} catch (final InvocationTargetException e) {

				e.printStackTrace();
			}
		}


		return o;
	}
}
