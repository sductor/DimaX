package frameworks.negotiation.rationality;

import java.util.Collection;
import java.util.Comparator;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.kernel.LaunchableCompetentComponent;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.launch.LaunchableComponent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;

public interface RationalAgent<PersonalState extends AgentState, Contract extends AbstractContractTransition> extends CommunicatingCompetentComponent, LaunchableCompetentComponent {

	public abstract ObservationService getMyInformation();

	public abstract RationalCore<? extends SimpleRationalAgent, PersonalState, Contract> getMyCore();

	public abstract PersonalState getMyCurrentState();

	public Class<? extends AgentState> getMyStateType();
	
	public abstract Collection<? extends AgentState> getMyResources();

	public abstract AgentState getResource(AgentIdentifier id)
			throws NoInformationAvailableException;

	public abstract void setNewState(final PersonalState s);

//	public abstract boolean verifyStateValidity(final PersonalState s);

	// public Collection<AgentIdentifier> getKnownAgents() {
	// return this.myInformation.getKnownAgents();
	// }
	//
	// public void addKnownAgents(final Collection<? extends AgentIdentifier>
	// agents) {
	// this.myInformation.addAll(agents);
	// }

	public abstract void setMySpecif(final PersonalState s, final Contract c);

	public abstract void setMySpecif(final Contract c);

	public abstract PersonalState getMyResultingState(final PersonalState s,
			final Contract c);

	public abstract PersonalState getMyResultingState(final PersonalState s,
			final Collection<Contract> cs);

	public abstract PersonalState getMyResultingState(final Contract c);

	public abstract PersonalState getMyResultingState(
			final Collection<Contract> cs);

	public abstract void execute(final Collection<Contract> contracts);

	public abstract void execute(final Contract... contracts);

	public abstract boolean Iaccept(final PersonalState s, final Contract c);

	public abstract boolean Iaccept(final PersonalState s,
			final Collection<? extends Contract> c);

	public abstract boolean Iaccept(final Contract c);

	public abstract boolean Iaccept(final Collection<? extends Contract> c);

	public abstract boolean isPersonalyValid(final PersonalState s,
			final Collection<? extends Contract> c);

	public abstract boolean isPersonalyValid(final PersonalState s, Contract c);

	public abstract boolean isSociallyValid(final PersonalState s,
			final Collection<? extends Contract> cs)
					throws IncompleteContractException;

	public abstract boolean isSociallyValid(final PersonalState s, Contract c)
			throws IncompleteContractException;

	public abstract Comparator<Collection<Contract>> getMyAllocationPreferenceComparator();

	public abstract Comparator<Contract> getMyPreferenceComparator();

	public abstract Double evaluatePreference(final Collection<Contract> cs);

	public abstract Double evaluatePreference(final Contract... cs);
	//	public  Double evaluatePreference(final PersonalState s1){
	//		return this.myCore.evaluatePreference(s1);
	//	}


}