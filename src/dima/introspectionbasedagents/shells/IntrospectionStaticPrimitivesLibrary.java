package dima.introspectionbasedagents.shells;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import dima.basicinterfaces.DimaComponentInterface;

public class IntrospectionStaticPrimitivesLibrary {

	public static Collection<Field> getPrivateFields(final Class<? extends DimaComponentInterface> c){
		final Collection<Field> result = new ArrayList<Field>();

		Class<?> c2 = c;
		while ( DimaComponentInterface.class.isAssignableFrom(c2)) {
			for (final Field m : c2.getDeclaredFields()) {
				if  (Modifier.isPrivate(m.getModifiers())) {
					result.add(m);
				}
			}
			c2 = c2.getSuperclass();
		}

		AccessibleObject.setAccessible(result.toArray(new Field[0]), true);
		result.addAll(Arrays.asList(c.getFields()));
		return result;
	}

	/**
	 *
	 * @param c a composant
	 * @return all the fields (public protected and private) of this composant and its subclassess
	 */
	public static  Collection<Field> getNonPublicFields(final Class<? extends DimaComponentInterface> c){
		final Collection<Field> result = new ArrayList<Field>();

		Class<?> c2 = c;
		while ( DimaComponentInterface.class.isAssignableFrom(c2)) {
			for (final Field m : c2.getDeclaredFields()) {
				if  (Modifier.isProtected(m.getModifiers()) || Modifier.isPrivate(m.getModifiers())) {
					result.add(m);
				}
			}
			c2 = c2.getSuperclass();
		}

		AccessibleObject.setAccessible(result.toArray(new Field[0]), true);
		result.addAll(Arrays.asList(c.getFields()));
		return result;
	}
	/**
	 *
	 * @param c a composant
	 * @return all the fields (public protected and private) of this composant and its subclassess
	 */
	public static  Collection<Field> getAllFields(final Class<? extends DimaComponentInterface> c){
		final Collection<Field> result = new ArrayList<Field>();

		Class<?> c2 = c;
		while ( DimaComponentInterface.class.isAssignableFrom(c2)) {
			for (final Field m : c2.getDeclaredFields()) {
				result.add(m);
			}
			c2 = c2.getSuperclass();
		}

		AccessibleObject.setAccessible(result.toArray(new Field[0]), true);
		return result;
	}
	/*
	 *
	 */

	public static  Collection<Method> getPublicMethods(final Class<? extends DimaComponentInterface> c){
		final Collection<Method> result = new ArrayList<Method>();

		Class<?> c2 = c;
		while ( DimaComponentInterface.class.isAssignableFrom(c2)) {
			for (final Method m : c2.getDeclaredMethods()) {
				if  (Modifier.isProtected(m.getModifiers())) {
					result.add(m);
				}
			}
			c2 = c2.getSuperclass();
		}

		//Ajout des méthodes publique :
		result.addAll(Arrays.asList(c.getMethods()));

		return result;
	}

	public static Collection<Method> getAllMethods(final Class<? extends DimaComponentInterface> c){
		final Collection<Method> result = new ArrayList<Method>();

		Class<?> c2 = c;
		while ( DimaComponentInterface.class.isAssignableFrom(c2)) {
			for (final Method m : c2.getDeclaredMethods()) {
				result.add(m);
			}
			c2 = c2.getSuperclass();
		}

		//		//Ajout des méthodes publique :
		//		result.addAll(Arrays.asList(c.getMethods()));

		return result;
	}
}
