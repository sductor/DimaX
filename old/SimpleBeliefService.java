package negotiation.negotiationframework.information.belief;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.TreeSet;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.information.KnowledgeService;
import negotiation.negotiationframework.information.NoInformationAvailableException;
import negotiation.negotiationframework.information.SimpleInformationService;
import negotiation.negotiationframework.information.SimpleKnowledgeService;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;

public abstract class SimpleBeliefService<PersonalState extends AgentState>
extends SimpleKnowledgeService<PersonalState>
implements OpinionService, KnowledgeService<PersonalState>{
	private static final long serialVersionUID = -9053221849173103647L;

	//
	// Fields
	//


	//
	// Constructor
	//

	public SimpleBeliefService(
			final PersonalState s
			//			,
			//			final Agregator myPersonalOpinion,
			//			final Agregator opinionsAgg
	) {
		super(s);
		//		myAcquaintances = new OpinionHandler(myPersonalOpinion, opinionsAgg);
	}

	//
	// Accessors
	//

	@Override
	protected OpinionHandler getMyAcquaintancesHandler() {
		return (OpinionHandler) myAcquaintances;
	}

	//
	// Methods
	//

	@Override
	public InformedState getBelievedState(final AgentIdentifier id)
	throws NoInformationAvailableException {
		if (iKnowThisAgentInfomation(id))
			return getMyAcquaintancesHandler().getAgentState(id).getMyState();
		else
			return getMyAcquaintancesHandler().getRepresentativeAgent();
	}

	@Override
	public Float getAgentBelievedStateConfidence(final AgentIdentifier id)
	throws NoInformationAvailableException{
		if (!iKnowThisAgentInfomation(id))
			return new Float(1 - this.getMyAcquaintancesHandler().getStatesDeviation());
		else {
			final InformedState state = this.getBelievedState(id);
			if (state.getUptime() <=
				this.getMyAcquaintancesHandler().getMinInformationDynamicity())
				return new Float(1);
			if (state.getUptime() >=
				this.getMyAcquaintancesHandler().getMaxInformationDynamicity())
				//cas jamais utilisé pcq isKnown fait le update
				return new Float(0);
			else
				return new Float(
						(state.getUptime() -
								this.getMyAcquaintancesHandler().getMaxInformationDynamicity())/
								(this.getMyAcquaintancesHandler().getMinInformationDynamicity() -
										this.getMyAcquaintancesHandler().getMaxInformationDynamicity()));
		}
	}

	@Override
	protected boolean isStillValid(final EnrichedState<InformedState> id) {
		return id.getUptime() - new Date().getTime()<=
			this.getMyAcquaintancesHandler().getMaxInformationDynamicity();
	}


	@Override
	public boolean hasAnOpinion(){
		try {
			getMyAcquaintancesHandler().getMyOpinion();
			return true;
		} catch (final NoInformationAvailableException e) {
			return false;
		}
	}

	@Override
	public void collectInformation(){
		final int popPercent = new Random().nextInt(OpinionService.populationPercentToCollect*getKnownAgents().size())+1;
		for (int i = 0; i < popPercent; i++)
			askInformation(getKnownAgents().get(new Random().nextInt(getKnownAgents().size())));
	}

	//
	// Behavior
	//

	@MessageHandler
	@FipaACLEnvelope(
			performative=Performative.Inform,
			content=InformationProtocol.globalBeliefExchange,
			protocol=InformationProtocol.class)
	public void receiveGlobalBelief(final StateUpdateMessage<SimpleOpinion<InformedState>> m){
		getMyAcquaintancesHandler().addInformation(m.getState());
	}

	//	@StepComposant(ticker=OpinionParameters.stateExchangeFrequency)
	public void giveMyState(){
		final Random rand = new Random();
		for (int i =0; i < OpinionParameters.numberOfAgentToGiveState; i++){
			final AgentIdentifier id = getKnownAgents().get(rand.nextInt(getKnownAgents().size()));
			sendMyState(id);
		}
	}

	//	@StepComposant(ticker=OpinionParameters.opinionExchangeFrequency)
	public void giveMyOpinion(){
		try {
			final Random rand = new Random();
			for (int i =0; i < OpinionParameters.numberOfAgentToGiveOpinion; i++){
				final AgentIdentifier id = getKnownAgents().get(rand.nextInt(getKnownAgents().size()));

				sendMessage(id,
						new StateUpdateMessage<Opinion<InformedState>>(
								Performative.Inform,
								InformationProtocol.globalBeliefExchange,
								getMyAcquaintancesHandler().getMyOpinion()));
			}
		} catch (final NoInformationAvailableException e) {
			return;
		}
	}

	//
	// Subclass
	//

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	protected class SimpleOpinion<InformedState extends AgentState>
	extends SimpleAgentState implements AgentState, Opinion{
		private static final long serialVersionUID = 6616276179359237312L;


		private final int informationNumber;

		private final long minInformationDynamicity;
		private final long maxInformationDynamicity;

		private final double statesDeviation;
		private final InformedState representativeState;

		//		private final InformedState minState;
		//		private final InformedState maxState;

		/*
		 * 
		 */

		public SimpleOpinion( final AgentIdentifier id,
				final long minInformationDynamicity,
				final long maxInformationDynamicity,
				final InformedState meanState,
				final double globalInformationDeviation,
				//				final InformedState minState,
				//				final InformedState maxState,
				final int informationNumber) {
			super(new OpinionIdentifier(id));
			this.statesDeviation = globalInformationDeviation;
			this.minInformationDynamicity = minInformationDynamicity;
			this.maxInformationDynamicity = maxInformationDynamicity;
			this.representativeState = meanState;
			this.informationNumber=informationNumber;
			//			this.minState = minState;
			//			this.maxState = maxState;
		}

		//
		//
		//

		@Override
		public OpinionIdentifier getMyIdentifier() {
			return (OpinionIdentifier) getMyAgentIdentifier();
		}

		/*
		 * 
		 */


		@Override
		public Long getMinInformationDynamicity() {
			return this.minInformationDynamicity;
		}


		@Override
		public Long getMaxInformationDynamicity() {
			return this.maxInformationDynamicity;
		}

		/*
		 * 
		 */

		@Override
		public InformedState getRepresentativeAgent() {
			return this.representativeState;
		}

		@Override
		public Double getOpinionDispersion() {
			return this.statesDeviation;
		}

		@Override
		public int getOpinionWeight() {
			return this.informationNumber;
		}



		//		public InformedState getPoorestAgent() {
		//			return this.minState;
		//		}

		//		public InformedState getRichestAgent() {
		//			return this.maxState;
		//		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	class OpinionHandler extends AcquaintanceHandler
	implements Comparator<EnrichedState<InformedState>>{

		//
		// Fields
		//

		/**
		 * 
		 */
		private static final long serialVersionUID = 4367395377843931381L;
		private final SimpleOpinionAggregator<InformedState> opinions;
		private final DispersionAnalysableAggregator<InformedState> myPersonalOpinion;
		private final HeavyDoubleMinMaxAggregator systemDynamicity;

		//
		// Constructor
		//

		public OpinionHandler(
				final DispersionAnalysableAggregator<InformedState> myPersonalOpinion,
				final DispersionAnalysableAggregator<InformedState> opinionsAgg) {
			super();
			this.opinions =
				new SimpleOpinionAggregator<InformedState>(opinionsAgg);
			this.myPersonalOpinion =
				myPersonalOpinion;
			this.systemDynamicity =
				new HeavyDoubleMinMaxAggregator();
		}


		//
		// Accessors
		//

		/*
		 * Maintining time information
		 */

		public long getMaxInformationDynamicity() {
			return this.systemDynamicity.getMax().longValue();
		}

		public long getMinInformationDynamicity() {
			return this.systemDynamicity.getMin().longValue();
		}

		/*
		 * Return the personal opinion of the agent : computed using only its information
		 */

		protected Opinion<InformedState> getMyOpinion() throws NoInformationAvailableException{
			updateMyOpinion();

			final Collection<InformedState> knownStates = new ArrayList<InformedState>();
			for (final EnrichedState<InformedState> s : information.values())
				knownStates.add(s.getMyState());

			try {
				return new SimpleOpinion<InformedState>(
						getMyAgent().getIdentifier(),
						getMaxInformationDynamicity(),
						getMinInformationDynamicity(),
						this.myPersonalOpinion.getRepresentativeElement(),
						new DispersionFunctionnalAgregator<InformedState>().getVariationCoefficient(this.myPersonalOpinion, knownStates),
						this.myPersonalOpinion.getNumberOfAggregatedElement());
			} catch (final Exception e) {
				throw new NoInformationAvailableException();
			}
		}

		private void updateMyOpinion(){
			for (final AgentIdentifier id : information.keySet())
				update(id);
		}

		/*
		 * Return the aggregated opinion (pour obtenir les beliefs sur les agents inconnus) : use the personal opinion as well as the received opinions
		 */

		public double getStatesDeviation() throws NoInformationAvailableException {
			updateOpinions();
			return this.opinions.getDispersion();
		}

		public InformedState getRepresentativeAgent() throws NoInformationAvailableException {
			updateOpinions();
			return this.opinions.getRepresentativeAgent();
		}

		private void updateOpinions() throws NoInformationAvailableException{
			this.opinions.add(getMyOpinion());//MAj myOPINION dans opinions
			this.opinions.clean(2*getMaxInformationDynamicity());
		}

		//
		// Methods
		//

		/*
		 * Update the personal opinion
		 */

		@Override
		protected void addInformation(final EnrichedState<InformedState> info){
			if (information.containsKey(info.getMyAgentIdentifier()))
				removeInformation(info.getMyAgentIdentifier());

			this.systemDynamicity.add(info.getLastInfoDynamicity());
			this.myPersonalOpinion.add(info.getMyState());

			super.addInformation(info);
		}


		@Override
		protected EnrichedState<InformedState> removeInformation(final AgentIdentifier id){

			this.systemDynamicity.removeInformation(getAgentState(id).getLastInfoDynamicity());
			this.myPersonalOpinion.removeInformation(getAgentState(id));

			return super.removeInformation(id);
		}


		/*
		 * Update the received opinions
		 */

		protected void addOpinion(final Opinion<InformedState> newOp){
			this.opinions.add(newOp);
			this.systemDynamicity.add(newOp.getMaxInformationDynamicity().doubleValue());
			this.systemDynamicity.add(newOp.getMinInformationDynamicity().doubleValue());
		}

		protected void remove(final Opinion<InformedState> newOp){
			this.opinions.removeInformation(newOp);
			this.systemDynamicity.removeInformation(newOp.getMaxInformationDynamicity().doubleValue());
			this.systemDynamicity.removeInformation(newOp.getMinInformationDynamicity().doubleValue());
		}


		@Override
		public int compare(final EnrichedState<InformedState> o1,
				final EnrichedState<InformedState> o2) {
			return Double.compare(o1.getLastInfoDynamicity(), o2.getLastInfoDynamicity());
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public interface StateAggregator<InformedState extends AgentState>
	extends Aggregator<InformedState>{

		double distance(InformedState e, InformedState representativeAgent);

		double divise(double ecartType, InformedState representativeAgent);

	}

	public class SimpleOpinionAggregator<InformedState extends AgentState>
	extends TreeSet<Opinion<InformedState>>
	implements Comparator<Opinion<InformedState>>{

		/**
		 * 
		 */
		private static final long serialVersionUID = -682627964619077730L;


		//
		// Fields
		//

		private final DispersionAnalysableAggregator<InformedState> aggregatedState;
		private final Collection<InformedState> knownOpinoins;

		//
		// Constructor
		//

		public SimpleOpinionAggregator(final DispersionAnalysableAggregator<InformedState> aggregatedState) {
			super();
			this.aggregatedState = aggregatedState;
			this.knownOpinoins = new ArrayList<InformedState>();
		}

		//
		// Methods
		//

		@Override
		public boolean add(final Opinion<InformedState> value) {
			this.aggregatedState.fuseRepresentativeElement(
					value.getRepresentativeAgent(), value.getOpinionWeight());
			return super.add(value);
		}

		public boolean remove(final Opinion<InformedState> newOp) {
			for (int i = 0; i < newOp.getOpinionWeight(); i++)
				this.aggregatedState.removeInformation(newOp);
			return super.removeInformation(newOp);
		}

		@Override
		public boolean remove(final Object o){
			darx.Logger.fromDARX("erreur dans simple op agg");
			return false;
		}

		/*
		 * 
		 */

		public InformedState getRepresentativeAgent() {
			return this.aggregatedState.getRepresentativeElement();
		}


		public double getDispersion() {
			return getVariationCoefficient();
		}

		/*
		 * 
		 */

		public void clean(final long expirationTime) {
			while (last().getUptime()-new Date().getTime()>expirationTime)
				this.removeInformation(last());
		}

		@Override
		public int compare(final Opinion<InformedState> o1, final Opinion<InformedState> o2) {
			return Double.compare(o1.getUptime(), o2.getUptime());
		}

		//
		// Primitives
		//

		private double getVariationCoefficient() {
			return new DispersionFunctionnalAgregator<InformedState>().getVariationCoefficient(this.aggregatedState, this.knownOpinoins);
		}
	}



	//
	// Subclasses
	//
		
	protected class EnrichedInformationHandler extends SimpleInformationService {
		private static final long serialVersionUID = -242055400437558760L;

		//		protected final Map<AgentIdentifier, EnrichedState<InformedState>> information =
		//			new HashMap<AgentIdentifier, EnrichedState<InformedState>>();


		//		protected void update(final AgentIdentifier id){
		//			if (this.information.containsKey(id) &&
		//					SimpleStateService.this.isStillValid(this.information.get(id)))
		//				removeInformation(id);
		//		}
		//
		//		protected void addInformation(final EnrichedState<InformedState> info){
		//			addAcquaintance(info.getMyAgentIdentifier());
		//			this.information.put(info.getMyAgentIdentifier(), info);
		//		}
		//
		//		protected EnrichedState<InformedState> removeInformation(final AgentIdentifier id){
		//			return this.information.remove(id);
		//		}

		//		public boolean iKnowThisState(final AgentIdentifier id){
		//			this.update(id);
		//			return this.information.containsKey(id);
		//		}
		//
		//		public EnrichedState<InformedState> getAgentState(final AgentIdentifier id){
		//			this.update(id);
		//			if (this.information.containsKey(id))
		//				return this.information.get(id);
		//			else
		//				return null;
		//		}



	}
}


//		private void remove(Opinion<InformedState> newOp){
//			receivedOpinions.removeOpinion(newOp);
//		}

//		private AverageAgregator<InformedState> myOpinion;
//
//		final MinMaxAggregator<Double> timeDynamicity = new SimpleMinMaxAggregator();
//		final Tournevis<InformedState> statesInfo;
//
//		private final float minInformationDynamicity;
//		private final float maxInformationDynamicity;


//			if (this.information.containsKey(info.getMyAgentIdentifier())){
//				InformedState previouslyKnownState=this.information.get(info.getMyAgentIdentifier());
//				final InformedState newState=info;
//				this.timeDynamicity.add(new Float(newState.getUptime()) - new Float(previouslyKnownState.getUptime()));
//				this.statesInfo.remove(previouslyKnownState);
//				this.statesInfo.add(newState);
//			}else {
//				this.statesInfo.add(info);
//			}
//
//private void remove(Opinion<InformedState> newOp){
//	myOpinion.remove(newOp.getRepresentativeAgent());
//	//todo maj min et max et deviations
//}
//@Override
//public InformedState getRepresentativeAgent() {
//	updateExpired();
//	return myOpinion.getAverage();
//}
//
//@Override
//public double getSystemDispersion() {
//	updateExpired();
//	return this.statesDeviation;
//}
//
//@Override
//public int getInformationNumber() {
//	updateExpired();
//	return this.knownOpinions.size();
//}
//
//@Override
//public long getMinInformationDynamicity() {
//	updateExpired();
//	return this.minInformationDynamicity;
//}
//
//@Override
//public long getMaxInformationDynamicity() {
//	updateExpired();
//	return this.maxInformationDynamicity;
//}
//
////
//// Primitives
////
//
//private float getAbsoluteMaxInformationDynamicity() {
//	return timeDynamicity.getMax();
//}
//
//private float getAbsoluteMinInformationDynamicity() {
//	return timeDynamicity.getMin();
//}
//
//private InformedState getAbsoluteRepresentativeAgent() {
//	return statesInfo.getAverage();
//}
//
//private double getAbsoluteStatesDeviation() {
//	return new FunctionnalAgregator<InformedState>().getVariationCoefficient(statesInfo, information.values(), getAbsoluteRepresentativeAgent());
//}
//
//
//
//private void updateExpired(){
//
//}



//if (this.variationIsNotifiable(this.lastNotifiedOpinion, this.opinion.getGlobalBelief())){
//	this.notify(new NotificationMessage<Opinion<InformedState>>("globalInformation",this.opinion.getGlobalBelief()));
//	this.lastNotifiedOpinion = this.opinion.getGlobalBelief();
//}

//final SimpleAgregator timeDynamicity = new SimpleAgregator();
//final ExtendedAgregator<InformedState> statesInfo;
//
//public GlobalBelievesAgregator(
//		final ExtendedAgregator<InformedState> statesInfo) {
//	super();
//	this.statesInfo = statesInfo;
//}
//
//public void updateWith(final GlobalBelief<InformedState> b) {
//	this.timeDynamicity.add(b.getMinInformationDynamicity());
//	this.timeDynamicity.add(b.getMaxInformationDynamicity());
//	this.statesInfo.fuseAverage(b.getRepresentativeAgent(), b.getInformationNumber());
//	//TODO VARIANCE??
//}
//
//public GlobalBelief<InformedState> getGlobalBelief() {
//	return new GlobalBelief<InformedState>(OpinionManager.this.getMyAgent().getIdentifier(),
//		this.timeDynamicity.getMin(), this.timeDynamicity.getMax(),
//		this.statesInfo.getAverage(), this.statesInfo.getEcartType(),this.statesInfo.getNumberOfElement());
//}
//
//@Override
//protected void addGlobalBelief(final GlobalBelief<InformedState> b){
//	this.opinion.updateWith(b);
//	super.addGlobalBelief(b);
//}
//
//@Override
//protected void addInformation(final InformedState info){
//	if (this.information.containsKey(info.getMyAgentIdentifier()))
//		this.opinion.updateWith(this.information.get(info.getMyAgentIdentifier()), info);
//	else
//		this.opinion.updateWith(info);
//
//	super.addInformation(info);
//
//	if (this.variationIsNotifiable(this.lastNotifiedOpinion, this.opinion.getGlobalBelief())){
//		this.notify(new NotificationMessage<GlobalBelief<InformedState>>("globalInformation",this.opinion.getGlobalBelief()));
//		this.lastNotifiedOpinion = this.opinion.getGlobalBelief();
//	}
//}
//
//public void updateWith(final InformedState previouslyKnownState, final InformedState newState) {
//	this.timeDynamicity.add(new Float(newState.getUptime()) - new Float(previouslyKnownState.getUptime()));
//	this.statesInfo.remove(previouslyKnownState);
//	this.statesInfo.add(newState);
//}
//
//public void updateWith(final InformedState info) {
//	this.statesInfo.add(info);
//}
//
//
//public GlobalBelief<InformedState> getMyOpinion() {
//	// TODO Auto-generated method stub
//	return null;
//}







//public void setObservation(final Collection<? extends AgentIdentifier> observedAgents){
//	for (final AgentIdentifier id : observedAgents){
//		this.observe(id, "information");
//		this.observe(id, "contract");
//	}
//}



//
//public boolean variationIsNotifiable(
//		final GlobalBelief<InformedState> currentSystemState,
//		final GlobalBelief<InformedState> nouveau){
////	nouveau > +/- X% de nouveau; sur au moins une dimension
//	return true;
//}

//
//
//
//
//	@MessageHandler
//	@NotificationEnvelope
//	public void updateGlobalInfoFromState(final State s){
//		updateInfo(s);
//		updateGlobalSystemState();
//	}
//
//	public GlobalBelief<State> getGlobalBelief(){
//		return this.globalSystemState;
//	}
//
//	//
//	// Primitives
//	//
//
//
//	/*
//	 *
//	 */
//
//	private void updateInfo(final State s){
//		if (this.states.containsKey(s.getMyAgentIdentifier()))
//			//Updates the observation of the frequency of evolution of agent
//			this.timeDynamicity.add(s.getUptime() - this.states.getStateOf(s.getMyAgentIdentifier()).getUptime());
//
//		//Update the list of actual states : si equal est redefinit sur agent identifier, l'objet approprié est écrasé
//		this.states.add(s);
//	}
//
//	private void updateGlobalSystemState(){
//		//Update the golbal state and notify if it is needed
//		final GlobalBelief<State> nouveau = new GlobalBelief<State>(
//				this.timeDynamicity.getMin(), this.timeDynamicity.getMax(),
//				this.states.getMedian(), this.states.getDispersion());
//
//		if (variationIsNotifiable(this.currentSystemState, nouveau))
//			notify(new NotificationMessage<GlobalBelief<State>>("globalInformation",nouveau));
//
//		this.currentSystemState=nouveau;
//	}







//
//
//	final Agregator timeDynamicity = new Agregator();
//	final GenericAgregator<State> states=new RepresentativeState();
//
//	class  RepresentativeState extends GenericAgregator<State>{
//
//		/**
//		 *
//		 */
//		private static final long serialVersionUID = 9197112701950201720L;
//		private final Map<AgentIdentifier, State> agents = new HashMap<AgentIdentifier, State>();
//
//		@Override
//		public void add(final State a){
//			super.add(a);
//			this.agents.put(a.getMyAgentIdentifier(), a);
//		}
//
//
//		@Override
//		public boolean remove(final State a){
//			return super.remove(a) &&
//			this.agents.remove(a.getMyAgentIdentifier())!=null;
//		}
//
//		public State getStateOf(final AgentIdentifier id){
//			return this.agents.get(id);
//		}
//
//		public boolean containsKey(final AgentIdentifier myAgentIdentifier) {
//			return this.agents.containsKey(myAgentIdentifier);
//		}
//
//
//		@Override
//		public Float distance(final State a1, final State a2) {
//			return OpinionManager.this.distance(a1, a2);
//		}
//
//
//		@Override
//		public int compare(final State o1, final State o2) {
//			return OpinionManager.this.getMyPreference(o1, o2);
//		}
//	}
//GlobalBelief<State> currentSystemState;
//
//public abstract int getMyPreference(State o1, State o2);
//
//public abstract Float distance(State a1, State a2);
//
////
//// Behaviors
////
//
//
//@MessageHandler
//@NotificationEnvelope
//public void updateGlobalInfoFromNotification(final NotificationMessage<State> m){
//	updateGlobalInfoFromState(m.getNotification());
//}
//
////@MessageHandler
////@NotificationEnvelope
////public void updateGlobalInfoFromContract(final NotificationMessage<Contract> m){
////	for (final AgentIdentifier id : m.getNotification().getInvolvedAgents())
////		try {
////			updateInfo(getMyAgent().getResultingState(this.states.getStateOf(id), m.getNotification()));
////	updateGlobalSystemState();
////		} catch (final MissingInformationException ids) {
////			getMyAgent().getMyInformation().obtainInformation(ids);
////			getMyAgent().retryWhen(
////					getMyAgent().getMyInformation(),
////					"hasInformation",
////					new Object[]{ids}, new Object[]{});
////		}
////}
