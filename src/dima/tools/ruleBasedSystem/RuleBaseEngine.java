package dima.tools.ruleBasedSystem;

/**
 * Insert the type's description here.
 * Creation date: (05/06/01 11:46:31)
 * @author:
 */
public class RuleBaseEngine extends RuleBaseObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -2905037239215641047L;
	private SystematicRuleBase rl;
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 11:48:36)
	 */
	public RuleBaseEngine() {}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 11:48:36)
	 */
	public RuleBaseEngine(final SystematicRuleBase r) {this.rl =r; }
	/**
	 * Insert the method's description here.
	 * Creation date: (05/06/01 11:48:36)
	 */
	public void initialize(final SystematicRuleBase r, final Object c) {this.rl =r; this.rl.setContext(c);}
	public void run() {

	}
}
