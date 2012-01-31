package dima.tools.automata;


import java.io.Serializable;
import java.util.Vector;

import dima.kernel.INAF.InteractionTools.Transition2;


/**
 * A state has a name, a transition list an a type('normal', 'initial','final')
 * Creation date: (17/01/00 19:33:43)
 * @author: Gerard Rozsavolgyi
 */


public class State extends AutomataObject implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -3334109466476550883L;
	private String   stateName;
	private Vector   transitionList;
	private StateType type;

	public State(final String stateName) {
		super();
		this.initialize(stateName);
	}
	public void addTransition (final Transition t) {
		// Possible checks on 'condition' and 'action'
		this.transitionList.addElement(t);
	}
	public void beFinal() {
		this.type= StateType.finalType();
	}
	public void beInitial() {
		this.type= StateType.initialType();
	}
	public void beNormal() {
		this.type= StateType.normalType();
	}
	/**
	 * Exploring all the possible transitions in search for the first crossable transition in the list.
	 * Creation date: (18/01/00 17:07:15)
	 * @return automata.State
	 */
	public State crossTransition(final Object context) {
		Transition t;
		final java.util.Vector listeOK = new Vector();
		final java.util.Vector tlv = this.getTransitionList();
		for (int i=0; i< tlv.size(); i++) {
			t = (Transition)tlv.elementAt(i);
			if (t.isSatisfied(context)) listeOK.addElement(t);
		}
		if  (listeOK.isEmpty()) return this;
		t = (Transition)listeOK.elementAt((int)(Math.random()*listeOK.size()));
		t.performAction(context);
		return t.getTargetState();
	}
	/**
	 * An accessor for the state name.
	 * Creation date: (18/01/00 17:07:49)
	 * @return java.lang.String
	 */
	public java.lang.String getStateName() {
		return this.stateName;
	}
	/**
	 * Return the Vector containing all the possible transitions.
	 * Creation date: (18/01/00 17:07:49)
	 * @return java.util.Vector
	 */
	public java.util.Vector getTransitionList() {
		return this.transitionList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:07:50)
	 * @return automata.StateType
	 */
	public StateType getType() {
		return this.type;
	}
	public void initialize(final String stateName) {
		this.stateName  = stateName;
		this.transitionList  = new Vector();
		this.beNormal();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:07:50)
	 * @return automata.StateType
	 */
	public boolean isFinal() {
		return this.getType().isFinal ();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:07:49)
	 * @param newStateName java.lang.String
	 */
	public void setStateName(final java.lang.String newStateName) {
		this.stateName = newStateName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:07:49)
	 * @param newTransitionList java.util.Vector
	 */
	public void setTransitionList(final java.util.Vector newTransitionList) {
		this.transitionList = newTransitionList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:07:50)
	 * @param newType automata.StateType
	 */
	public void setType(final StateType newType) {
		this.type = newType;
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {

		return this.stateName;
	}

	/**
	 * Exploring all the possible transitions in search for the first crossable transition in the list.
	 * Creation date: (18/01/00 17:07:15)
	 * @return automata.State
	 */
	public State crossTransition2(final Object cxt)
	{
		Transition2 t;
		final Vector listeOK = new Vector();
		final Vector tlv = this.getTransitionList();
		// System.out.println("ENTRER DANS CROSSTRANSITION2 ....");
		for (int i=0; i<tlv.size(); i++)
		{
			t = (Transition2)tlv.elementAt(i);
			// System.out.println("ENTRER DANS SELECT TRANSITION ....");
			// System.out.println("Valeur de t"+t.toString());
			if (t.isSatisfied(cxt))
				// System.out.println("ENTRER DANS TRANSITION SELECTIONNEE....");
				listeOK.addElement(t);
		}

		if  (listeOK.isEmpty())
			return this;
		else
		{
			t = (Transition2)listeOK.elementAt((int)(Math.random()*listeOK.size()));
			// System.out.println("ENTRER POUR PERFORM ACTION .....");
			t.performActions(cxt);

			return t.getTargetState();
		}


	}
}
