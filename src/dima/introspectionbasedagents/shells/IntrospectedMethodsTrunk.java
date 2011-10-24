package dima.introspectionbasedagents.shells;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.tools.SimpleAgentStatus;

public interface IntrospectedMethodsTrunk extends DimaComponentInterface{

	public abstract Collection<? extends MethodHandler> getMethods();

	public abstract void removeMethod(MethodHandler meth);

	/**
	 * Initialise the agent by adding all the method of agent ag into the
	 * hashmaps
	 *
	 */
	public abstract void load(DimaComponentInterface a);

	public SimpleAgentStatus getStatus() ;
	
	public abstract boolean executeStepMethod(MethodHandler mt)
			throws IllegalArgumentException, InvocationTargetException;

}