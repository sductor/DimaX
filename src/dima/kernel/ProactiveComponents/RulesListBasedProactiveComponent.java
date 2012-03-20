package dima.kernel.ProactiveComponents;

/**
 * Insert the type's description here.
 * Creation date: (19/07/00 14:12:25)
 * @author: Zahia Guessoum
 */
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.ruleBasedSystem.SystematicRuleBase;



public class RulesListBasedProactiveComponent extends ProactiveComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = -3954550955962656713L;
	static private java.util.HashMap lists;
	public SystematicRuleBase rl;
	/**
	 * Insert the method's description here.
	 * Creation date: (25/07/00 20:04:50)
	 */
	public RulesListBasedProactiveComponent() {RulesListBasedProactiveComponent.lists = new HashMap();}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 14:14:47)
	 */
	public RulesListBasedProactiveComponent(final SystematicRuleBase r) {this.rl = r;}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 14:14:47)
	 */
	public RulesListBasedProactiveComponent(final SystematicRuleBase r, final java.lang.Object ctxt) {this.rl = r; this.rl.setContext(ctxt);}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:20:32)
	 */
	public void createActionList(final String begin) {
		final Vector v = new Vector();
		final Method m[] = this.getClass().getMethods();
		for (final Method element : m) {
			final String s = element.getName();
			if (s.startsWith(begin)) {
				v.add(new NamedAction(s,this));
			}
		}
		RulesListBasedProactiveComponent.lists.put(begin,v);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:20:32)
	 */
	public void createConditionList(final String begin) {
		final Vector v = new Vector();
		final Method m[] = this.getClass().getMethods();
		for (final Method element : m) {
			final String s = element.getName();
			if (s.startsWith(begin)) {
				v.add(new NamedCondition(s,this));
			}
		}
		RulesListBasedProactiveComponent.lists.put(begin,v);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:12:56)
	 * @return java.util.Vector
	 */
	public java.util.Vector getActionList(final String debut) {
		if (!RulesListBasedProactiveComponent.lists.containsKey(debut)) {
			this.createActionList(debut);
		}
		return (Vector)RulesListBasedProactiveComponent.lists.get(debut);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:12:56)
	 * @return java.util.Vector
	 */
	public java.util.Vector getConditionList(final String debut) {
		if (!RulesListBasedProactiveComponent.lists.containsKey(debut)) {
			this.createConditionList(debut);
		}
		return (Vector)RulesListBasedProactiveComponent.lists.get(debut);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:12:56)
	 * @return java.util.Vector
	 */
	public java.util.Vector getNewActionList(final String debut) {
		this.createActionList(debut);
		return (Vector)RulesListBasedProactiveComponent.lists.get(debut);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/06/00 13:12:56)
	 * @return java.util.Vector
	 */
	public java.util.Vector getNewConditionList(final String debut) {
		this.createConditionList(debut);
		return (Vector)RulesListBasedProactiveComponent.lists.get(debut);
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public boolean competenceIsActive() {
		return this.rl.dontstop();
	}
	/**
	 * step method comment.
	 */
	@Override
	public void step() {
		this.rl.step();
	}
}
