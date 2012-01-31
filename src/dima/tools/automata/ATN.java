package dima.tools.automata;

import java.util.Enumeration;
import java.util.Vector;
/**
 * An atn, constitued by an initial state and a list of final states.
 * Creation date: (17/01/00 21:18:40)
 * @author: Gerard Rozsavolgyi
 */
public class ATN extends AutomataObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -6154942242024617928L;
	private State   initialState;   // InitialState is linked to others ..
	private java.util.Vector finalStates;
	//private final Hashtable states = new Hashtable();

	/**
	 * Insert the method's description here.
	 * Creation date: (06/07/2002 15:03:28)
	 */
	public ATN() {}
	/**
	 * ATN constructor comment.
	 */
	public ATN(final State initState, final java.util.Vector finStates) {
		super();
		this.initialize (initState, finStates);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/09/00 15:04:29)
	 * @return Gdima.tools.automata.ATN
	 */
	public void defaultAtnDelCom() {

		/*** intialize l'ATN par d�faut  */
	}
	/**
	 * return the Vector containing all atn's finalStates.
	 * Creation date: (18/01/00 17:06:01)
	 * @return java.util.Vector
	 */
	public java.util.Vector getFinalStates() {
		return this.finalStates;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:06:01)
	 * @return automata.State
	 */
	public State getInitialState() {
		return this.initialState;
	}
	public void initialize(final State initState, final java.util.Vector finStates) {
		this.initialState = initState;
		this.finalStates = finStates;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:06:01)
	 * @param newFinalStates java.util.Vector
	 */
	public void setFinalStates(final java.util.Vector newFinalStates) {
		this.finalStates = newFinalStates;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (18/01/00 17:06:01)
	 * @param newInitialState automata.State
	 */
	public void setInitialState(final State newInitialState) {
		this.initialState = newInitialState;
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		String s = new String("ATN :\n");
		final Vector statesToDo = new Vector();
		statesToDo.add(this.initialState);
		int statesDo = 0;
		while (statesToDo.size() > statesDo) {
			final State st = (State)statesToDo.get(statesDo);
			s = s.concat("	from state "+st.getStateName()+"\n");
			final Enumeration e=st.getTransitionList().elements();
			while (e.hasMoreElements()) {
				final Transition t = (Transition)e.nextElement();
				s = s.concat("		to state "+t.targetState.getStateName()+" : "+t.toString()+"\n");
				if (!statesToDo.contains(t.targetState)) statesToDo.add(t.targetState);
			}
			statesDo++;
		}
		return s;
	}

	//private final Hashtable states = new Hashtable();

	/**
	 * Insert the method's description here.
	 * Creation date: (06/07/2002 15:03:28)
	 */
	public ATN(final ATN newAtn)
	{
		this.setInitialState(newAtn.getInitialState());
		this.setFinalStates(newAtn.getFinalStates());
	}
}
