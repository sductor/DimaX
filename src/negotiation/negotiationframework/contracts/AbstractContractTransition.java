package negotiation.negotiationframework.contracts;

import java.util.Collection;

import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractContractTransition
extends DimaComponentInterface {

	public ContractIdentifier getIdentifier();

	public AgentIdentifier getInitiator();

	public Collection<AgentIdentifier> getNotInitiatingParticipants();

	public Collection<AgentIdentifier> getAllParticipants();

	public Collection<AgentIdentifier> getAllInvolved();

	public <ActionSpec extends AbstractActionSpecif> void setSpecification(ActionSpec spec);
	
	public <State extends AgentState> void setInitialState(final State state);

	AbstractActionSpecif getSpecificationOf(AgentIdentifier id) throws IncompleteContractException;

	public AgentState getInitialState(AgentIdentifier id)  throws IncompleteContractException;
	
	//Attention retourne l'état tel quel si l'agent n'est pas concerné
	public <State extends AgentState> State computeResultingState(AgentIdentifier id)
			throws IncompleteContractException;

	public <State extends AgentState> State computeResultingState(final State s)
			throws IncompleteContractException;

	//	public <State extends ActionSpec> State computeResultingState(AgentIdentifier id, Collection<State> initialStates)
	//			throws IncompleteContractException;

	//	public ActionSpec computeResultingState(AgentIdentifier id) throws IncompleteContractException;
	//doit associer comme time a l'action spec le time du contract
	//
	// public void setAccepted(AgentIdentifier id);
	//
	// public void setRejected(AgentIdentifier id);
	//
	// public boolean isConsensual();
	//
	// public boolean isAFailure();

	public long getUptime();

	public boolean hasReachedExpirationTime();

	public boolean willReachExpirationTime(long t);

	public long getCreationTime();

	//	public boolean isViable()	throws IncompleteContractException;

	public boolean isInitiallyValid()	throws IncompleteContractException;

	public <State extends AgentState> boolean isViable(State... initialStates)	throws IncompleteContractException;

	public <State extends AgentState> boolean isViable(Collection<State> initialStates)	throws IncompleteContractException;

	public class IncompleteContractException extends Exception{
		private static final long serialVersionUID = 7759487818635127561L;
		public IncompleteContractException(String message) {
			super(message);
		}	
		public IncompleteContractException() {}
	};
}

//
// public class UnappropriateActionException extends Exception{
// private static final long serialVersionUID = 6086764754472358004L;};
//

// public String toString() {
// String result = "";
// for (final AgentIdentifier id : getInvolvedAgents())
// if (!getAssociatedActions((ExecutorIdentifier) id).isEmpty())
// result +=
// "\nAgent "+id+" executes : "+getAssociatedActions((ExecutorIdentifier) id);
// result+="\nAgents to contact : " +getInvolvedAgents();
// return result;
// }

// public Collection<ActionIdentifier> getAssociatedActions(AgentIdentifier id);

//
//
//
//
// public ActionSpec getSpecification(AgentIdentifier ag, ActionIdentifier id)
// throws UnappropriateActionException;
//
// public boolean setSpecificationt(AgentIdentifier ag, ActionIdentifier id,
// ActionSpec c)
// throws UnappropriateActionException;
//
// //
// //
// //
//
// /**
// * return : false si l'action était déja présente
// */
// boolean addAction(AgentIdentifier ag, ActionName n);
//
// /**
// * return : false si l'action n'était pas présente
// */
// public boolean removeAction(ActionIdentifier a);

// private static final long serialVersionUID = -4231201541403292015L;

//
// Constructor
//

// public AbstractContract(final Performative performative) {
// super(performative, NegotiationProtocol.class);
// }
//
// public AbstractContract(final Performative performative, final String
// content) {
// super(performative, content, NegotiationProtocol.class);
// }

//
// Interface
//
// public abstract ContractIdentifier getIdentifier();
//
// public abstract ExecutorIdentifier getProposer();
//
// public abstract ExecutorIdentifier getManager();
//
// public abstract Collection<? extends AgentIdentifier> getInvolvedAgents();

// public void addAction(final ContractMove<Action,ExecutorIdentifier> c);
// // {
// // addAction(c.getAgent(), c.getAction());
// // }
//
// public void removeAction(final ContractMove<Action,ExecutorIdentifier> c);
// // {
// removeAction(c.getAgent(), c.getAction());
// }

// public String description() {
// @Override
// public String toString() {
// String result = "";
// for (final AgentIdentifier id : getInvolvedAgents())
// if (!getAssociatedActions((ExecutorIdentifier) id).isEmpty())
// result +=
// "\nAgent "+id+" executes : "+getAssociatedActions((ExecutorIdentifier) id);
// result+="\nAgents to contact : " +getInvolvedAgents();
// return result;
// }

//
// SubClass
//

