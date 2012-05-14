package negotiation.negotiationframework.rationality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class SimpleRationalAgent<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicCompetentAgent {
	private static final long serialVersionUID = -6248384713199838544L;

	public static final String stateChangement = "my state has changed!!";

	//
	// Fields
	//


	@Competence
	private final ObservationService myInformation;

	@Competence
	public RationalCore<ActionSpec, PersonalState, Contract> myCore;

	public Class<? extends AgentState> myStateType;
	public final int initialStateNumber;

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
		assert myInitialState!=null;
		this.myStateType = myInitialState.getClass();
		initialStateNumber=myInitialState.getStateCounter();
		this.setNewState(myInitialState);
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
			//				logException("arrrgggggggggggggggggggggggggggggggg"
			//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh2222222222222222222222222"
			//+s+"******************"+((PersonalState) myInformation.getInformation(myStateType, getIdentifier())));

			return (PersonalState) this.myInformation.getInformation(this.myStateType, this.getIdentifier());
		} catch (final NoInformationAvailableException e) {
			this.signalException("impossible!!!! ",e);//+myInformation.getInformation(myStateType),e);
			throw new RuntimeException();
		}
	}

	public Collection<? extends AgentState> getMyResources(){
		final Collection<AgentState> myResources = new ArrayList<AgentState>();
		for (final AgentIdentifier id : this.getMyCurrentState().getMyResourceIdentifiers()) {
			try {
				AgentState ress = (AgentState) this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
				assert ress.getMyResourceIdentifiers().contains(getIdentifier());
				myResources.add(ress);
			} catch (final NoInformationAvailableException e) {
				this.signalException("uuuuuhh impossible!! pas totalement vrai : l'info doit etre manuellement ajouté!"+this.getMyCurrentState(),e);
			}
		}
		return myResources;
	}

	public ActionSpec getResource(AgentIdentifier id) throws NoInformationAvailableException{
		return (ActionSpec) this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
	}

	public void setNewState(final PersonalState s) {
		assert this.myInformation.hasMyInformation(this.myStateType)?verifyStateValidity(s):true;
		this.logMonologue("NEW STATE !!!!!! "+s,LogService.onFile);
		this.getMyInformation().add(s);
		assert this.getMyCurrentState().equals(s):this.getMyCurrentState()+"\n"+s+"\n---------"+(s.isNewerThan(getMyCurrentState())>0);
		//		if (!getMyCurrentState().equals(s))
		//			logException("arrrgggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		this.notify(this.getMyCurrentState(), SimpleObservationService.informationObservationKey);
		this.notify(this.getMyCurrentState());
	}


	public boolean verifyStateValidity(final PersonalState s){
		assert (s.isNewerThan(getMyCurrentState())>0):this.getMyCurrentState()+"\n"+s;
		for (AgentIdentifier id : s.getMyResourceIdentifiers()){
			//			assert this.getMyInformation().hasInformation(this.getMyCurrentState().getMyResourcesClass(), id);
			AgentState ress;
			try {
				ress = (AgentState) this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
				assert ress.getMyResourceIdentifiers().contains(getIdentifier());
			} catch (NoInformationAvailableException e) {
				//				assert 1<0:e;
			}
		}
		return true;
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


	public ActionSpec computeMySpecif(final PersonalState s, final Contract c){
		return this.myCore.computeMySpecif(s, c);
	}

	public ActionSpec computeMySpecif(final Contract c){
		return this.myCore.computeMySpecif(this.getMyCurrentState(), c);
	}

	public PersonalState getMyResultingState(final PersonalState s, final Contract c) {
		try {
			return c.computeResultingState(s);
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}
	}

	public PersonalState getMyResultingState(final PersonalState s, final Collection<Contract> cs) {
		PersonalState result = s;
		for (final Contract c : cs) {
			result = this.getMyResultingState(result,c);
		}
		return result;
	}


	public PersonalState getMyResultingState(final Contract c) {
		return this.getMyResultingState(this.getMyCurrentState(),c);
	}

	public PersonalState getMyResultingState(final Collection<Contract> cs) {
		return this.getMyResultingState(this.getMyCurrentState(),cs);
	}



	/*
	 *
	 */

	public void execute(final Collection<Contract> contracts) {
		this.myCore.execute(contracts);
	}

	public void execute(final Contract... contracts) {
		this.myCore.execute(Arrays.asList(contracts));
	}

	/*
	 * Rationality
	 */

	public boolean isAnImprovment(final PersonalState s, final Contract c) {
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return this.Iaccept(s, a);
	}

	public boolean isAnImprovment(final PersonalState s, final Collection<? extends Contract> c) {
		final Collection<Contract> a2 = new ArrayList<Contract>();
		return isPersonalyValid(s, c)
				&& this.myCore.getAllocationPreference(s, (Collection<Contract>) c, a2) > 0;
	}


	public boolean Iaccept(final PersonalState s, final Contract c) {
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return this.Iaccept(s, a);
	}

	public boolean Iaccept(final PersonalState s, final Collection<? extends Contract> c) {
		final Collection<Contract> a2 = new ArrayList<Contract>();
		return isPersonalyValid(s, c)
				&&  this.myCore.getAllocationPreference(s, (Collection<Contract>) c, a2) >= 0;
	}

	/*
	 * 
	 */

	public boolean isPersonalyValid(final PersonalState s,
			final Collection<? extends Contract> c) {
		return getMyResultingState(s,(Collection<Contract>)c).isValid();
	}

	public boolean isPersonalyValid(final PersonalState s,Contract c) {
		return getMyResultingState(s,c).isValid();
	}

	public boolean isSociallyValid(final PersonalState s, final Collection<? extends Contract> cs) throws IncompleteContractException{
		return ContractTransition.respectRights((Collection<Contract>) cs,s);
	}

	public boolean isSociallyValid(final PersonalState s, Contract c) throws IncompleteContractException{
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return ContractTransition.respectRights((Collection<Contract>) a,s);
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

	public  Double evaluatePreference(final Collection<Contract> cs){
		return this.myCore.evaluatePreference(cs);
	}

	public  Double evaluatePreference(final Contract... cs){
		return this.evaluatePreference(Arrays.asList(cs));
	}
	//	public  Double evaluatePreference(final PersonalState s1){
	//		return this.myCore.evaluatePreference(s1);
	//	}

	/*
	 * Rights
	 */

	//	public Boolean respectMyRights(final PersonalState s) {
	//		return s.isValid();
	//	}
	//
	//	public Boolean respectRights(final Contract c) throws IncompleteContractException {
	//		return c.isViable();
	//	}
	//
	//	public Boolean respectRights(final PersonalState s, final Contract c) {
	//		for (AgentIdentifier id : c.getAllParticipants()){
	//			if (id.equals(getIdentifier()) && !c.computeResultingState(s).isValid())
	//				return false;
	//			else if (!c.computeResultingState(id).isValid())
	//				return false;
	//		}
	//
	//		return true;
	//	}
	//
	//	public Boolean respectRights(final Collection<Contract> cs) {
	//		ReallocationContract<Contract, ActionSpec> reall =
	//				new ReallocationContract<Contract, ActionSpec>(getIdentifier(), cs);
	//
	//		for (AgentIdentifier id : reall.getAllParticipants()){
	//			if (!reall.computeResultingState(id).isValid())
	//				return false;
	//		}
	//
	//		return true;
	//	}
	//
	//	public Boolean respectRights(final PersonalState s, final Collection<Contract> cs) {
	//		ReallocationContract<Contract, ActionSpec> reall =
	//				new ReallocationContract<Contract, ActionSpec>(getIdentifier(), cs);
	//
	//		for (AgentIdentifier id : reall.getAllParticipants()){
	//			if (id.equals(getIdentifier()) && !reall.computeResultingState(s).isValid())
	//				return false;
	//			else if (!reall.computeResultingState(id).isValid())
	//				return false;
	//		}
	//
	//		return true;
	//	}

	//	public Boolean respectMyRights(final PersonalState s, final Contract c) {
	//		return this.respectMyRights(this.getMyResultingState(s, c));
	//	}
	//
	//	public Boolean respectMyRights(final PersonalState s,
	//			final Collection<Contract> a) {
	//		return this.respectMyRights(this.getMyResultingState(s, a));
	//	}

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