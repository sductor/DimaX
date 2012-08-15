package dima.introspectionbasedagents.kernel;

import java.util.Collection;
import java.util.Date;

import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.DimaComponentInterface;

public interface IntrospectedMethodsTrunk extends DimaComponentInterface{

	public abstract Collection<? extends MethodHandler> getMethods();

	public abstract void removeMethod(MethodHandler meth);

	/**
	 * Initialise the agent by adding all the method of agent ag into the
	 * hashmaps
	 *
	 */
	public abstract void load(ActiveComponentInterface a);

	public SimpleAgentStatus getStatus() ;

	public abstract boolean executeStepMethod(MethodHandler mt, Date creation)
			throws IllegalArgumentException,  Throwable;

}