package dima.tools.ruleBasedSystem;

/**
 * Implementation of a classifier
 * Creation date: (22/05/00 17:03:06)
 * @author: Michel Quenault (Miq)
 */
public abstract class AbstractRuleBase extends RuleBaseObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 3082393392831420298L;
	protected java.util.Vector listOfRules; // list of classifiers rules
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:19:53)
 */
public AbstractRuleBase() {
	super();
	this.listOfRules = new java.util.Vector();
}
/**
 * Insert the method's description here.
 * Creation date: (04/06/00 16:17:42)
 * @param cla Gdima.tools.classifier.Classifier
 */
public AbstractRuleBase(final AbstractRuleBase arl) {
	super();
	this.listOfRules = new java.util.Vector();
	this.addRules(arl.listOfRules);
}
/**
 * add one rule to the list of rules
 * Creation date: (22/05/00 17:26:46)
 * @param mr Gdima.tools.classifier.MarkedRule
 */
public void addRule(final Rule r) {
	this.listOfRules.add(new Rule(r));
}
/**
 * add a vector of rules to the list of rules
 * Creation date: (22/05/00 17:21:59)
 * @param lor java.util.Vector
 */
public void addRules(final java.util.Vector lomr) {
	final java.util.Enumeration e = lomr.elements();
	while (e.hasMoreElements()) this.addRule((Rule)e.nextElement());
}
/**
 * Insert the method's description here.
 * Creation date: (05/06/01 14:59:56)
 * @return java.lang.Boolean
 */
public abstract  boolean dontstop() ;
/**
 * Insert the method's description here.
 * Creation date: (06/06/01 19:39:15)
 * @return java.util.Vector
 */
public java.util.Vector getListOfRules() {
	return this.listOfRules;
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:45:36)
 */
public abstract void run();
/**
 * Insert the method's description here.
 * Creation date: (06/06/01 19:39:15)
 * @param newListOfRules java.util.Vector
 */
public void setListOfRules(final java.util.Vector newListOfRules) {
	this.listOfRules = newListOfRules;
}
/**
 * Insert the method's description here.
 * Creation date: (05/06/01 14:59:56)
 * @return java.lang.Boolean
 */
public abstract void step() ;
/**
 * Insert the method's description here.
 * Creation date: (04/06/00 16:32:46)
 * @return java.lang.String
 */
@Override
public String toString() {
	String s = new String("Classifier :\n	Rules :\n");
	final java.util.Enumeration e = this.listOfRules.elements();
	while (e.hasMoreElements())
		s+=new String("		"+((Rule)e.nextElement()).toString()+"\n");
	return s;
}
}
