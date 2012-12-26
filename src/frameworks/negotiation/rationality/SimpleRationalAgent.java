package frameworks.negotiation.rationality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractTransition;

public class SimpleRationalAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends BasicCompetentAgent implements RationalAgent<PersonalState, Contract> {
	private static final long serialVersionUID = -6248384713199838544L;


	//
	// Fields
	//

	@Competence
	private final ObservationService myInformation;

	@Competence
	public RationalCore<? extends SimpleRationalAgent,PersonalState, Contract> myCore;

	public Class<? extends AgentState> myStateType;
	public final int initialStateNumber;

	public Collection<AgentIdentifier> knownResources;

	public static final String stateChangementObservation="my state has changed!!";

	//
	// Constructor
	//


	public SimpleRationalAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<? extends SimpleRationalAgent,PersonalState, Contract> myRationality,
			final ObservationService myInformation,
			Double collectiveSeed)
					throws CompetenceException {
		super(id,collectiveSeed);
		this.myCore = myRationality;
		((RationalCore<SimpleRationalAgent<PersonalState, Contract>,PersonalState, Contract>)this.myCore).setMyAgent(this);
		this.myInformation = myInformation;
		this.myInformation.setMyAgent(this);
		assert myInitialState!=null;
		this.myStateType = myInitialState.getClass();
		this.initialStateNumber=myInitialState.getStateCounter();
		this.setNewState(myInitialState);
	}


	//
	// Accessor
	//

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyInformation()
	 */
	@Override
	public ObservationService<RationalAgent<PersonalState, Contract>> getMyInformation() {
		return this.myInformation;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyCore()
	 */
	@Override
	public RationalCore<? extends SimpleRationalAgent,PersonalState, Contract> getMyCore() {
		return this.myCore;
	}

	//
	// Services
	//

	/*
	 * State
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyCurrentState()
	 */
	@Override
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

	@Override
	public Class<? extends AgentState> getMyStateType(){
		return this.myStateType;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyResources()
	 */
	@Override
	public Collection<? extends AgentState> getMyResources(){
		final Collection<AgentState> myResources = new ArrayList<AgentState>();
		for (final AgentIdentifier id : this.getMyCurrentState().getMyResourceIdentifiers()) {
			try {
				final AgentState ress = this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
				assert ress.getMyResourceIdentifiers().contains(this.getIdentifier());
				myResources.add(ress);
			} catch (final NoInformationAvailableException e) {
				this.signalException("uuuuuhh impossible!! pas totalement vrai : l'info doit etre manuellement ajouté!"+this.getMyCurrentState(),e);
			}
		}
		return myResources;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getResource(dima.basicagentcomponents.AgentIdentifier)
	 */
	@Override
	public AgentState getResource(final AgentIdentifier id) throws NoInformationAvailableException{
		return this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#setNewState(PersonalState)
	 */
	@Override
	public void setNewState(final PersonalState s) {
		
		//		assert this.myInformation.hasMyInformation(this.myStateType)?this.verifyStateValidity(s):true;
		//		assert Assert.Imply(this.myInformation.hasMyInformation(this.myStateType),!s.equals(getMyCurrentState()));
		//		assert Assert.Imply(this.myInformation.hasMyInformation(this.myStateType),this.verifyStateValidity(s));
		assert s.isValid():s;
		this.logMonologue("NEW STATE !!!!!! "+s,LogService.onFile);
		this.getMyInformation().add(s);
		assert this.getMyCurrentState().equals(s):this.getMyCurrentState()+"\n"+s+"\n---------"+(s.isNewerThan(this.getMyCurrentState())>0);
		//		if (!getMyCurrentState().equals(s))
		//			logException("arrrgggggggggggggggggggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		this.notify(this.getMyCurrentState());

	}



	public boolean verifyStateValidity(final PersonalState s){
		assert s.isNewerThan(this.getMyCurrentState())>0:this.getMyCurrentState()+"\n"+s;
		for (final AgentIdentifier id : s.getMyResourceIdentifiers()){
			//			assert this.getMyInformation().hasInformation(this.getMyCurrentState().getMyResourcesClass(), id);
			AgentState ress;
			try {
				ress = this.getMyInformation().getInformation(this.getMyCurrentState().getMyResourcesClass(), id);
				assert ress.getMyResourceIdentifiers().contains(this.getIdentifier()):ress+"\n---\n"+this.getMyCurrentState()+"\n---\n"+s;
			} catch (final NoInformationAvailableException e) {
				//								assert false:e;
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

	@Override
	public Collection<AgentIdentifier> getKnownResources(){
		return Collections.unmodifiableCollection(this.knownResources);
	}

	public void setKnownResources(final Collection<AgentIdentifier> knownResources) {
		this.knownResources = knownResources;
	}
	/*
	 * Transition
	 */




	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#setMySpecif(PersonalState, Contract)
	 */
	@Override
	public void setMySpecif(final PersonalState s, final Contract c){
		this.myCore.setMySpecif(s, c);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#setMySpecif(Contract)
	 */
	@Override
	public  void setMySpecif(final Contract c){
		this.myCore.setMySpecif(this.getMyCurrentState(), c);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyResultingState(PersonalState, Contract)
	 */
	@Override
	public PersonalState getMyResultingState(final PersonalState s, final Contract c) {
		try {
			return c.computeResultingState(s);
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyResultingState(PersonalState, java.util.Collection)
	 */
	@Override
	public PersonalState getMyResultingState(final PersonalState s, final Collection<Contract> cs) {
		PersonalState result = s;
		for (final Contract c : cs) {
			result = this.getMyResultingState(result,c);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyResultingState(Contract)
	 */
	@Override
	public PersonalState getMyResultingState(final Contract c) {
		return this.getMyResultingState(this.getMyCurrentState(),c);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyResultingState(java.util.Collection)
	 */
	@Override
	public PersonalState getMyResultingState(final Collection<Contract> cs) {
		return this.getMyResultingState(this.getMyCurrentState(),cs);
	}



	/*
	 *
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#execute(java.util.Collection)
	 */
	@Override
	public void execute(final Collection<Contract> contracts) {
		this.myCore.execute(contracts);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#execute(Contract)
	 */
	@Override
	public void execute(final Contract... contracts) {
		this.myCore.execute(Arrays.asList(contracts));
	}

	/*
	 * Rationality
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(PersonalState, Contract)
	 */
	@Override
	public boolean Iaccept(final PersonalState s, final Contract c) {
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return this.Iaccept(s, a);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(PersonalState, java.util.Collection)
	 */
	@Override
	public boolean Iaccept(final PersonalState s, final Collection<? extends Contract> c) {
		final Collection<Contract> a2 = new ArrayList<Contract>();
		return this.isPersonalyValid(s, c)
				&& this.myCore.getAllocationPreference((Collection<Contract>) c, a2) > 0;
	}


	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(Contract)
	 */
	@Override
	public boolean Iaccept(final Contract c) {
		return this.Iaccept(this.getMyCurrentState(), c);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(java.util.Collection)
	 */
	@Override
	public boolean Iaccept(final Collection<? extends Contract> c) {
		return this.Iaccept(this.getMyCurrentState(),c);
	}

	/***********************
	 * 
	 */
	
	@Override
	public boolean IdontCare(final PersonalState s, final Contract c) {
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return this.IdontCare(s, a);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(PersonalState, java.util.Collection)
	 */
	@Override
	public boolean IdontCare(final PersonalState s, final Collection<? extends Contract> c) {
		final Collection<Contract> a2 = new ArrayList<Contract>();
		return this.isPersonalyValid(s, c)
				&& this.myCore.getAllocationPreference((Collection<Contract>) c, a2) >= 0;
	}


	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(Contract)
	 */
	@Override
	public boolean IdontCare(final Contract c) {
		return this.IdontCare(this.getMyCurrentState(), c);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#Iaccept(java.util.Collection)
	 */
	@Override
	public boolean IdontCare(final Collection<? extends Contract> c) {
		return this.IdontCare(this.getMyCurrentState(),c);
	}	
	
	/*
	 * 
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#isPersonalyValid(PersonalState, java.util.Collection)
	 */
	@Override
	public boolean isPersonalyValid(final PersonalState s,
			final Collection<? extends Contract> c) {
		return this.getMyResultingState(s,(Collection<Contract>)c).isValid();
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#isPersonalyValid(PersonalState, Contract)
	 */
	@Override
	public boolean isPersonalyValid(final PersonalState s,final Contract c) {
		return this.getMyResultingState(s,c).isValid();
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#isSociallyValid(PersonalState, java.util.Collection)
	 */
	@Override
	public boolean isSociallyValid(final PersonalState s, final Collection<? extends Contract> cs) throws IncompleteContractException{
		return ContractTransition.respectRights((Collection<Contract>) cs,s);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#isSociallyValid(PersonalState, Contract)
	 */
	@Override
	public boolean isSociallyValid(final PersonalState s, final Contract c) throws IncompleteContractException{
		final Collection<Contract> a = new ArrayList<Contract>();
		a.add(c);
		return ContractTransition.respectRights(a,s);
	}

	/*
	 * Utility
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyAllocationPreferenceComparator()
	 */
	@Override
	public Comparator<Collection<Contract>> getMyAllocationPreferenceComparator() {
		final Comparator<Collection<Contract>> myComparator = new Comparator<Collection<Contract>>() {
			@Override
			public int compare(final Collection<Contract> o1,
					final Collection<Contract> o2) {
				return SimpleRationalAgent.this.myCore.getAllocationPreference(o1, o2);
			}
		};
		return myComparator;
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#getMyPreferenceComparator()
	 */
	@Override
	public Comparator<Contract> getMyPreferenceComparator() {
		final Comparator<Contract> myComparator = new Comparator<Contract>() {
			@Override
			public int compare(final Contract o1, final Contract o2) {
				final Collection<Contract> a1 = new ArrayList<Contract>();
				a1.add(o1);
				final Collection<Contract> a2 = new ArrayList<Contract>();
				a2.add(o2);
				return SimpleRationalAgent.this.myCore.getAllocationPreference(a1, a2);
			}
		};
		return myComparator;
	}

	/*
	 *
	 */

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#evaluatePreference(java.util.Collection)
	 */
	@Override
	public  Double evaluatePreference(final Collection<Contract> cs){
		return this.myCore.evaluatePreference(cs);
	}

	/* (non-Javadoc)
	 * @see negotiation.negotiationframework.rationality.RationalAgent#evaluatePreference(Contract)
	 */
	@Override
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