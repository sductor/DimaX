package negotiation.negotiationframework.strategy;

import java.util.Collection;

import negotiation.agentframework.informedagent.AgentState;
import negotiation.agentframework.informedagent.BasicInformedAgent;
import negotiation.agentframework.informedagent.globalinformationhandlers.NoInformationAvailableException;
import negotiation.agentframework.negotiatingagent.BasicUtilitaristAgentCore;
import negotiation.agentframework.negotiatingagent.ProposerAgent;
import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.ContractTransition;
import negotiation.agentframework.strategicagent.BasicPossibilistStrategicComparatorModule;
import negotiation.agentframework.strategicagent.UtilitaristStrategicContractEvaluator;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionBasedAgent.competences.DuplicateCompetenceException;
import dima.introspectionBasedAgent.competences.UnInstanciableCompetenceException;

public abstract class BasicUtilitaristStrategicProposer
<PersonalState extends AgentState,
ActionIdentifier extends AgentActionSpecification,
Contract extends ContractTransition<ActionIdentifier,?>,
InformedState extends AgentState> 
extends 
BasicInformedAgent<PersonalState, Contract,InformedState>
implements 
ProposerAgent<PersonalState, Contract,InformedState>, 
UtilitaristStrategicContractEvaluator<PersonalState,Contract,InformedState>
{
	private static final long serialVersionUID = -4796141493677879028L;

	//
	// Constructors
	//
	public BasicUtilitaristStrategicProposer(final AgentIdentifier newID,
			final BasicUtilitaristAgentCore<PersonalState, Contract> myEvolution,
			final StrategicExplorator<ActionIdentifier,Contract,InformedState> myExplorator,
//			final Collection<AgentIdentifier> knownAgents,
			final Collection<ActionIdentifier> knownActions) 
	throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		super(newID, myEvolution);
		this.myExplorator=myExplorator;
//		this.knownAgents = knownAgents;
		this.knownActions = knownActions;
	}

//	public BasicUtilitaristStrategicProposer(final AgentIdentifier newID,
//			final BasicUtilitaristAgentCore<PersonalState, Contract> myEvolution,
//			final HeuristicSearch<ActionIdentifier,Action,Contract,InformedState> myExplorator) {
//		super(newID, myEvolution);
//		this.myExplorator=myExplorator;
//		this.knownAgents = new ArrayList<AgentIdentifier>();
//		this.knownActions = new ArrayList<ActionIdentifier>();
//	}
	
	//
	// Accessors
	//



	@Override
	public PersonalState getMyCurrentState() {
		return getMyInformation().getMyCurrentState();
	}
	
//	public void addKnownAgent(final AgentIdentifier id) {
//		this.knownAgents.add(id);
//	}

	public void addKnownActions(final ActionIdentifier action) {
		this.knownActions.add(action);
	}
	
//	public void removeKnownAgent(final AgentIdentifier id) {
//		this.knownAgents.remove(id);
//	}

	public void removeKnownActions(final ActionIdentifier action) {
		this.knownActions.remove(action);
	}
	
	//
	// Methods
	// 



}




//	@Override
//	public int evaluateContractsComparison(final Contract c1, final Contract c2) {
//		return getMyPreference(evaluateResultingState(getMyCurrentState(), c1), evaluateResultingState(getMyCurrentState(), c2));
//	}
//public abstract class StrategicPossibilistUtilitaristCore
//<State extends AgentState,
//Contract extends AbstractContract<?>> extends UtilitaristAgentCore<State, Contract>{
//		
//	public abstract boolean willAccept(final AgentIdentifier id, final Contract c) throws MissingInformationException;
//	
//	public abstract boolean ImRiskAdverse();
//	
//	
//
//	public Double getContractUtility(final Contract c) throws MissingInformationException{
//		return new Double(evaluatePreference(getResultingState(getMyCurrentState(), c)));
//	}
//
//	public Double evaluatePreference(final State s1) {
//		return evaluatePreference(s1);
//	}
//
//	public int compareContractUtility(final Contract c1, final Contract c2) throws MissingInformationException {
//		return getMyPreference(getResultingState(getMyCurrentState(), c1), getResultingState(getMyCurrentState(), c2));
//	}
//
//
//	public Double computeConfidenceOfInformation(final Collection<? extends AgentIdentifier> ids){
//		int nbknown=0;
//		Float confTotal=new Float(0);
//		for (final AgentIdentifier id : ids){
//			confTotal+=1 - ag.getMyInformation().getAgentBelievedStateConfidence(id);
//			if (getMyInformation().isKnown(id))
//				nbknown++;
//		}
//		return new Double(confTotal / (ids.size() * nbknown) );
//		//		new Double(nbUnknown * getMyAgent().getMyInformation().getSystemDispersion() * ageMoyen)  / (ids.size() * (ids.size() - nbUnknown));
//		
//	}
//}




//	
//	@Override
//	public int compareAcceptationConfidence(final Contract c1, final Contract c2) {
//		return this.myConfidenceEvaluator.compareAcceptationConfidence(c1, c2);
//	}
//
//	@Override
//	public int compareContractUtility(final Contract c1, final Contract c2) throws MissingInformationException {
//		return getMyPreference(getResultingState(getMyCurrentState(), c1), getResultingState(getMyCurrentState(), c2));
//	}	
//	
//	@Override
//	public int strategiclyCompare(final Contract c1, final Contract c2) throws MissingInformationException {
//		return this.myConfidenceEvaluator.strategiclyCompare(c1, c2);
//	}















/*
 * Informed Agent
 */


//
///**
// * State it is rational to the agent to propose contract c : i.e if it benefits of it.
// */
//private boolean isRational(final Contract c){
//	return myUtility.compare(predictState(getMyCurrentState(), c), getMyCurrentState())>=0;
//}


//public abstract Double evaluatePreference(PersonalState s1);

//public BasicStrategicAgent(
//		final AgentIdentifier newID,final HeuristicSearch<Action,ExecutorIdentifier,Contract> myExplorator,
//		final StrategicComparator<PersonalState, Contract> myConfidenceEvaluator) {
//	super(newID);
//	this.myExplorator=myExplorator;
//	myExplorator.setMyHeuristic(new Comparator<Contract>(){
//
//		@Override
//		public int compare(final Contract o1, final Contract o2) {
//			return BasicStrategicAgent.this.strategiclyCompare(o1, o2);
//		}
//
//	});
//	this.myConfidenceEvaluator=myConfidenceEvaluator;
//}

