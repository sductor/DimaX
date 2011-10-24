package dima.tools.agentInterface;

/**
 * An abstract class for all conditions.
 * Creation date: (17/01/00 19:33:43)
 * @author: G�rard Rozsavolgyi
 * Modified by Michel Quenault (Miq)
 */


abstract public class AbstractCondition extends AgentInterfaceObject{


/**
	 *
	 */
	private static final long serialVersionUID = -2728153387180001176L;

/**
 * Insert the method's description here.
 * Creation date: (17/01/00 19:57:11)
 * @return boolean
 * @param o java.lang.Object
 */
public abstract boolean isSatisfied(Object o);
}
