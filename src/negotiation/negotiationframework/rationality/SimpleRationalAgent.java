package negotiation.negotiationframework.rationality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class SimpleRationalAgent<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicCompetentAgent {
	private static final long serialVersionUID = -6248384713199838544L;

	//
	// Fields
	//

	//numérotes les états de l'agents pour pouvoir les ordonner
	public int nextStateCounter=0;

	@Competence
	private final ObservationService myInformation;

	@Competence
	public RationalCore<ActionSpec, PersonalState, Contract> myCore;

	public Class<? extends AgentState> myStateType;
	//
	// Constructor
	//


	public SimpleRationalAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			final ObservationService myInformation)
					throws CompetenceException {
		super(id);
		this.myCore = myRationality;
		this.myCore.setMyAgent(this);
		this.myInformation = myInformation;
		this.myInformation.setMyAgent(this);
		if (myInitialState!=null) {
			this.myStateType = myInitialState.getClass();
			this.setNewState(myInitialState);
		}
	}


	//
	// Accessor
	//

	public ObservationService getMyInformation() {
		return this.myInformation;
	}

	public RationalCore<ActionSpec, PersonalState, Contract> getMyCore() {
		return this.myCore;
	}

	//
	// Services
	//

	/*
	 * State
	 */

	public PersonalState getMyCurrentState() {
		try {
			//			if(!((PersonalState) myInformation.getInformation(myStateType, getIdentifier())).equals(s))
			//				logException("arrrgggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh2222222222222222222222222"+s+"******************"+((PersonalState) myInformation.getInformation(myStateType, getIdentifier())));

			return (PersonalState) this.myInformation.getInformation(this.myStateType, this.getIdentifier());
		} catch (final NoInformationAvailableException e) {
			this.signalException("impossible!!!! ",e);//+myInformation.getInformation(myStateType),e);
			throw new RuntimeException();
		}
	}

	public Collection<ActionSpec> getMyResources(){
		final Collection<ActionSpec> myResources = new ArrayList<ActionSpec>();
		for (final AgentIdentifier id : this.getMyCurrentState().getMyResourceIdentifiers())
			try {
				myResources.add((ActionSpec) this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id));
			} catch (final NoInformationAvailableException e) {
				throw new RuntimeException("uuuuuhh impossible!!",e);
			}
		return myResources;
	}

	public void setNewState(final PersonalState s) {
		this.nextStateCounter++;
		this.logMonologue("NEW STATE !!!!!! "+s,LogService.onFile);
		this.getMyInformation().add(s);
		//		if (!getMyCurrentState().equals(s))
		//			logException("arrrgggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		this.notify(this.getMyInformation().getMyInformation(this.myStateType), SimpleObservationService.informationObservationKey);
	}


	// public Collection<AgentIdentifier> getKnownAgents() {
	// return this.myInformation.getKnownAgents();
	// }
	//
	// public void addKnownAgents(final Collection<? extends AgentIdentifier>
	// agents) {
	// this.myInformation.addAll(agents);
	// }

	/*
	 * Transition
	 */


	public ActionSpec getMySpecif(final PersonalState s, final Contract c){
		return this.myCore.getMySpecif(s, c);
	}

	public ActionSpec getMySpecif(final Contract c){
		return this.myCore.getMySpecif(this.getMyCurrentState(), c);
	}

	public PersonalState getMyResultingState(final PersonalState s, final Contract c) {
		return c.computeResultingState(s);
	}

	public PersonalState getMyResultingState(final PersonalState s, final Collection<Contract> cs) {
		PersonalState result = s;
		for (final Contract c : cs)
			result = c.computeResultingState(result);
				return result;
	}


	public PersonalState getMyResultingState(final Contract c) {
		return c.computeResultingState(this.getMyCurrentState());
	}



	/*
	 *
	 */

	public void execute(final Contract c) {
		this.myCore.execute(c);
	}

	/*
	 * Rationality
	 */

	//	public boolean Iaccept(final Contract c) {
	//		return this.Iaccept(this.getMyCommitedState(), c);//considere aussi les contrats acceptés
	//	}
	//
	//	public boolean Iaccept(final Collection<Contract> c) {
	//		return this.Iaccept(this.getMyCommitedState(), c);//considere aussi les contrats acceptés
	//	}

	public boolean Iaccept(final PersonalState s, final Contract c) {
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return this.Iaccept(s, a);
	}

	public boolean Iaccept(final PersonalState s, final Collection<? extends Contract> c) {
		return this.isAnImprovment(s, (Collection<Contract>) c)
				&& this.respectMyRights(this.getMyResultingState(s, (Collection<Contract>) c));
	}

	private boolean isAnImprovment(final PersonalState s,
			final Collection<Contract> a1) {
		final Collection<Contract> a2 = new ArrayList<Contract>();
		return this.myCore.getAllocationPreference(s, a1, a2) > 0;
	}

	/*
	 * Utility
	 */

	public Comparator<Collection<Contract>> getMyAllocationPreferenceComparator() {
		final Comparator<Collection<Contract>> myComparator = new Comparator<Collection<Contract>>() {
			@Override
			public int compare(final Collection<Contract> o1,
					final Collection<Contract> o2) {
				return SimpleRationalAgent.this.myCore.getAllocationPreference(
						SimpleRationalAgent.this.getMyCurrentState(), o1, o2);
			}
		};
		return myComparator;
	}

	public Comparator<Contract> getMyPreferenceComparator() {
		final Comparator<Contract> myComparator = new Comparator<Contract>() {
			@Override
			public int compare(final Contract o1, final Contract o2) {
				final Collection<Contract> a1 = new ArrayList<Contract>();
				a1.add(o1);
				final Collection<Contract> a2 = new ArrayList<Contract>();
				a2.add(o2);
				return SimpleRationalAgent.this.myCore.getAllocationPreference(
						SimpleRationalAgent.this.getMyCurrentState(), a1, a2);
			}
		};
		return myComparator;
	}

	/*
	 *
	 */

	public Comparator<Collection<Contract>> getMyAllocationPreferenceComparator(
			final PersonalState s) {
		final Comparator<Collection<Contract>> myComparator = new Comparator<Collection<Contract>>() {
			@Override
			public int compare(final Collection<Contract> o1,
					final Collection<Contract> o2) {
				return SimpleRationalAgent.this.myCore.getAllocationPreference(
						s, o1, o2);
			}
		};
		return myComparator;
	}

	public Comparator<Contract> getMyPreferenceComparator(final PersonalState s) {
		final Comparator<Contract> myComparator = new Comparator<Contract>() {
			@Override
			public int compare(final Contract o1, final Contract o2) {
				final Collection<Contract> a1 = new ArrayList<Contract>();
				a1.add(o1);
				final Collection<Contract> a2 = new ArrayList<Contract>();
				a2.add(o2);
				return SimpleRationalAgent.this.myCore.getAllocationPreference(
						s, a1, a2);
			}
		};
		return myComparator;
	}

	public  Double evaluatePreference(final Contract c){
		return this.evaluatePreference(this.getMyResultingState(c));
	}

	public  Double evaluatePreference(final PersonalState s1){
		return this.myCore.evaluatePreference(s1);
	}

	/*
	 * Rights
	 */

	public Boolean respectMyRights(final PersonalState s) {
		return s.isValid();
	}

	public Boolean respectMyRights(final Contract c) {
		return this.respectMyRights(this.getMyResultingState(c));
	}

	public Boolean respectMyRights(final PersonalState s, final Contract c) {
		return this.respectMyRights(this.getMyResultingState(s, c));
	}

	public Boolean respectMyRights(final PersonalState s,
			final Collection<Contract> a) {
		return this.respectMyRights(this.getMyResultingState(s, a));
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		return this.getMyCurrentState().toString();
	}
}



//public SimpleRationalAgent(
//		final AgentIdentifier id,
//		final PersonalState myInitialState,
//		final RationalCore<PersonalState, Contract, ActionSpec> myRationality)
//		throws UnInstanciedCompetenceException,
//		DuplicateCompetenceException {
//	super(id);
//	this.myCore = myRationality;
//	this.myCore.setMyAgent(this);
//	this.setMyInformation(new SimpleObservationService());
//	myState = myInitialState.getClass();
//	setNewState(myInitialState);
//}
//
//public SimpleRationalAgent(
//		final AgentIdentifier id,
//		final Date horloge,
//		final PersonalState myInitialState,
//		final RationalCore<PersonalState, Contract, ActionSpec> myRationality)
//		throws UnInstanciedCompetenceException,
//		DuplicateCompetenceException {
//	super(id, horloge);
//	this.myCore = myRationality;
//	this.myCore.setMyAgent(this);
//	this.setMyInformation(new SimpleObservationService());
//	myState = myInitialState.getClass();
//	setNewState(myInitialState);
//}

//
// @Override
// public void reset() {
// this.myState.reset();
// }

// @Override
// public Boolean respectRights(final InformedState s) {
// return myCore.respectRights(s);
// }

// @Override
// public InformedState getResultingState(final InformedState s, final Contract
// c){
// return myCore.getResultingState(s, c);
// }

// @Override
// public int getMyPreference(final PersonalState s1, final PersonalState s2) {
// return myCore.getMyPreference(s1, s2);
// }
// @Override
// public StateService<PersonalState> getMyInformation() {
// return myInformation;
// }
// @Override
// public InformedState getResultingState(final AgentIdentifier id, final
// Contract a)
// throws MissingInformationException {
// return myCore.getResultingState(getMyInformation().getAgentState(id), a);
// }

// @Ov

// @Override
// public boolean Iaccept(final PersonalState s1, final PersonalState s2) {
// return myCore.Iaccept(s1, s2);
// }

// @Override
// public boolean willAccept(final AgentIdentifier id, final InformedState s1,
// final InformedState s2) {
// return myCore.willAccept(id, s1, s2);
// }

// @Override
// public boolean IsAStrictImprovment(final Contract c) throws
// MissingInformationException{
// return this.getMyPreference(getMyCurrentState(), getMyResultingState(c))>0;
// }

// @Override
// public int getPreference(
// final AgentIdentifier id,
// final InformedState s1,
// final InformedState s2) {
// return myCore.getPreference(id, s1, s2);
// }
// @Override
// public Boolean respectRights(final AgentIdentifier id, final Contract c)
// throws MissingInformationException {
// return myCore.respectRights(getResultingState(id, c));
// }