package dima.tools.agentInterface;

/**
 * A named condition refering to a method.
 * Creation date: (17/01/00 19:33:43)
 * @author: G�rard Rozsavolgyi
 * Modified by Michel Quenault (Miq)
 */

import dima.basiccommunicationcomponents.MessageSend2;

public class NamedCondition extends AbstractCondition{
	/**
	 *
	 */
	private static final long serialVersionUID = -5252479514533243499L;
	private final String   name;
	private final java.lang.Object defaultObject;
	private java.lang.Object[] defaultArgs;
/**
 * Insert the method's description here.
 * Creation date: (17/07/00 19:09:55)
 * @param simulationName java.lang.String
 */
public NamedCondition(final String s) {
	super();
	this.name=s;
	this.defaultObject = null;
	this.defaultArgs = null;
}
/**
 * Insert the method's description here.
 * Creation date: (17/07/00 19:09:55)
 * @param simulationName java.lang.String
 */
public NamedCondition(final String s,final Object[] args) {
	super();
	this.name=s;
	this.defaultObject = null;
	this.defaultArgs = args;
}
/**
 * Constructor for a named condition.
 * Creation date: (17/01/00 20:44:49)
 * @param s java.lang.String
 */
public NamedCondition(final String s, final Object o) {
		super();
		this.name=s;
		this.defaultObject = o;
		this.defaultArgs = null;
}
/**
 * Constructor for a named condition.
 * Creation date: (17/01/00 20:44:49)
 * @param s java.lang.String
 */
public NamedCondition(final String s, final Object o, final Object[] args) {
		super();
		this.name=s;
		this.defaultObject = o;
		this.defaultArgs = args;
}
/**
 * Insert the method's description here.
 * Creation date: (31/07/00 15:47:43)
 * @param args org.omg.CORBA.Object[]
 */
public Object[] getArgs() {
	return this.defaultArgs;
}
/**
 * Insert the method's description here.
 * Creation date: (31/07/00 16:53:57)
 * @return java.lang.String
 */
public String getName() {
	return this.name;
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 16:15:57)
 */
public boolean isSatisfied() {
	return ((Boolean)MessageSend2.invoke(this.defaultObject,this.name,this.defaultArgs)).booleanValue();
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 16:15:57)
 */
public boolean isSatisfied(final Object[] args) {
	return ((Boolean)MessageSend2.invoke(this.defaultObject,this.name,args)).booleanValue();
}
/**
 * The main method for a named condition name,  invoked in a context object o
 */
@Override
public boolean isSatisfied(final Object o) {
// System.out.println("ENTRER DANS SATISFIED NAMED CDT AVEC CTX....");
    return ((Boolean)MessageSend2.invoke(o,this.name,this.defaultArgs)).booleanValue();
}
/**
 * The main method for a named condition name,  invoked in a context object o
 */
public boolean isSatisfied(final Object o, final Object[] args) {

	return ((Boolean)MessageSend2.invoke(o,this.name,args)).booleanValue();
}
/**
 * Insert the method's description here.
 * Creation date: (31/07/00 15:47:43)
 * @param args org.omg.CORBA.Object[]
 */
public void setArgs(final Object[] args) {
	this.defaultArgs = args;
}
/**
 * Insert the method's description here.
 * Creation date: (31/01/2000 00:27:03)
 * @return java.lang.String
 */
@Override
public String toString() {
	String s = new String(this.name+"(");
	if (this.defaultArgs!=null) {
		s = s.concat(this.defaultArgs[0].toString());
		for (int i=1;i<this.defaultArgs.length;i++) s = s.concat(", "+this.defaultArgs[i].toString());
	}
	s = s.concat(")");
	return s;
}
}
