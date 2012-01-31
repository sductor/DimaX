package dima.tools.agentInterface;

/**
 * A named action
 * Creation date: (22/05/00 17:52:53)
 * @author: Michel Quenault (Miq)
 */

import dima.basiccommunicationcomponents.MessageSend2;

public class NamedAction extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = -4296432771311439695L;
	private String name;
	private java.lang.Object defaultObject;
	private java.lang.Object[] defaultArgs;
	/**
	 * Insert the method's description here.
	 * Creation date: (17/07/00 19:23:03)
	 * @param s java.lang.String
	 */
	public NamedAction(final String s) {
		super();
		this.name  = s;
		this.defaultObject = null;
		this.defaultArgs = null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (17/07/00 19:23:03)
	 * @param s java.lang.String
	 */
	public NamedAction(final String s, final Object[] args) {
		super();
		this.name  = s;
		this.defaultObject = null;
		this.defaultArgs = args;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 16:12:23)
	 * @param s java.lang.String
	 * @param o java.lang.Object
	 */
	public NamedAction(final String s, final Object o) {
		super();
		this.name  = s;
		this.defaultObject = o;
		this.defaultArgs = null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 16:12:23)
	 * @param s java.lang.String
	 * @param o java.lang.Object
	 */
	public NamedAction(final String s, final Object o, final Object[] args) {
		super();
		this.name  = s;
		this.defaultObject = o;
		this.defaultArgs = args;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 16:07:51)
	 */
	public void execute() {
		MessageSend2.invoke(this.defaultObject,this.name,this.defaultArgs);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 16:07:51)
	 */
	public void execute(final Object[] args) {
		MessageSend2.invoke(this.defaultObject,this.name, args);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 09:55:45)
	 * @param o Object
	 */
	@Override
	public void execute(final Object o) {
		MessageSend2.invoke(o,this.name,this.defaultArgs);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 09:55:45)
	 * @param o Object
	 */
	public void execute(final Object o, final Object[] args) {
		MessageSend2.invoke(o,this.name, args);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/03/2003 08:54:10)
	 * @return java.lang.Object[]
	 */
	public java.lang.Object[] getArgs() {
		return this.defaultArgs;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (03/04/2003 14:41:09)
	 * @return java.lang.Object
	 */
	public java.lang.Object getContext()
	{
		return this.defaultObject;
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
	 * Creation date: (31/07/00 15:47:43)
	 * @param args org.omg.CORBA.Object[]
	 */
	public void setArgs(final Object[] args) {
		this.defaultArgs = args;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (03/04/2003 14:41:09)
	 * @param newDefaultObject java.lang.Object
	 */
	public void setContext(final Object newDefaultObject)
	{
		this.defaultObject = newDefaultObject;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/03/2003 08:55:50)
	 * @param newName java.lang.String
	 */
	public void setName(final java.lang.String newName) {
		this.name = newName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24/05/00 15:55:26)
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
