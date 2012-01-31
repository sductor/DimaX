package dima.tools.ruleBasedSystem;

/**
 * Insert the type's description here.
 * Creation date: (16/06/00 15:30:37)
 * @author: Michel Quenault (Miq)
 */
public class SystematicRuleBase extends AbstractRuleBase {
	/**
	 *
	 */
	private static final long serialVersionUID = -5119366272801577814L;
	public java.util.Vector listFireableRules;
	private java.lang.Object context;
	/**
	 * SystematicRulesList constructor comment.
	 */
	public SystematicRuleBase() {
		super();
		this.listFireableRules = new java.util.Vector();
	}
	/**
	 * SystematicRulesList constructor comment.
	 * @param arl Gdima.tools.rulesList.AbstractRulesList
	 */
	public SystematicRuleBase(final AbstractRuleBase arl) {
		super(arl);
		this.listFireableRules = new java.util.Vector();
	}
	/**
	 * SystematicRulesList constructor comment.
	 * @param arl Gdima.tools.rulesList.AbstractRulesList
	 */
	public SystematicRuleBase(final AbstractRuleBase arl, final Object ctxt) {
		super(arl);
		this.context = ctxt;
		this.listFireableRules = new java.util.Vector();
	}
	/**
	 * SystematicRulesList constructor comment.
	 */
	public SystematicRuleBase(final Object ctxt) {
		super();
		this.context = ctxt;
		this.listFireableRules = new java.util.Vector();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 15:45:54)
	 */
	@Override
	public boolean dontstop() {
		this.updateFireableRules();
		final java.util.Enumeration e = this.getListOfRules().elements();
		return  e.hasMoreElements();
	}
	/**
	 * SystematicRulesList constructor comment.
	 */
	static public SystematicRuleBase exemple1() {
		final SystematicRuleBase a = new SystematicRuleBase ();
		a.addRule(new Rule("cond", "ba"));
		a.addRule(new Rule("cond", "bou"));
		a.addRule(new Rule("cond1", "hi"));
		return a;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 14:52:44)
	 * @return java.lang.Object
	 */
	public java.lang.Object getContext() {
		return this.context;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 14:52:44)
	 * @return java.util.Vector
	 */
	public java.util.Vector getListFireableRules() {
		return this.listFireableRules;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 15:45:54)
	 */
	@Override
	public void run() {
		while (this.dontstop())	this.step();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 14:52:44)
	 * @param newContext java.lang.Object
	 */
	public void setContext(final java.lang.Object newContext) {
		this.context = newContext;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 14:52:44)
	 * @param newListFireableRules java.util.Vector
	 */
	public void setListFireableRules(final java.util.Vector newListFireableRules) {
		this.listFireableRules = newListFireableRules;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 15:45:54)
	 */
	@Override
	public void step() {
		final java.util.Enumeration e = this.listFireableRules.elements();
		if (e.hasMoreElements()) {System.out.println("ici"); ((Rule) e.nextElement()).executeWithoutTest(this.context);};
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return super.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (16/06/00 15:45:54)
	 */
	public void updateFireableRules() {
		this.listFireableRules.clear();
		final java.util.Enumeration e = this.getListOfRules().elements();
		Rule a = new Rule();
		while (e.hasMoreElements()) { a = (Rule)e.nextElement(); if (a.isSatisfied(this.context))
			this.listFireableRules.add(a);  };
	}
}
