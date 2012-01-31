package dima.basicagentcomponents;

/**
 * Insert the type's description here.
 * Creation date: (27/04/00 11:31:04)
 * @author: Zahia Guessoum
 */
import dima.support.GimaObject;
public abstract class AbstractAgentAddress extends GimaObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -7652705640251230387L;
	/**
	 * AbstractAgent constructor comment.
	 */
	public AbstractAgentAddress() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:37:10)
	 * @return Gdima.competences.communication.AgentIdentifier
	 */
	public abstract AgentIdentifier getId();
}
