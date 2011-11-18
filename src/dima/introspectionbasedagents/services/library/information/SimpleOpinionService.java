package dima.introspectionbasedagents.services.library.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.FunctionalDispersionAgregator;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;
import negotiation.negotiationframework.NegotiationStaticParameters;

public class SimpleOpinionService extends
SimpleObservationService implements OpinionService {
	private static final long serialVersionUID = -3441455830192485118L;

	//
	// Fields
	//

	Map<AgentIdentifier,Class<? extends Information>> agentToRegister=
		new HashMap<AgentIdentifier, Class<? extends Information>>();
	protected HashMap<Class<? extends Information>, OpinionDataBase<?>> everyoneOpinion = 
		new HashMap<Class<? extends Information>, OpinionDataBase<?>>();

	public static final String opinionObservationKey="opinionDiffusion";

	//
	// Accessors
	//

	@Override
	public void add(Information information) {
		if (information instanceof Opinion) {
			if (!this.everyoneOpinion.containsKey(information.getClass()))
				this.everyoneOpinion.put(information.getClass(),
						new OpinionDataBase(information.getClass()));
			((OpinionDataBase<Information>) this.everyoneOpinion.get(information
					.getClass())).add((Opinion<Information>) information);
		} else {
			if (!this.infos.containsKey(information.getClass())){
				this.infos.put(information.getClass(),
						new AnalysedInformationDataBase());
				this.everyoneOpinion.put(information.getClass(),
						new OpinionDataBase(information.getClass()));
			}

			super.add(information);
		}
	}


	public String getOpinionObservationKey(Class<? extends Information> info){
		return "opiniondiffusion"+info;
	}


	//
	// Methods
	//

	@Override
	public <Info extends Information> Opinion<Info> getOpinion(
			Class<Info> informationType, 
			AgentIdentifier agentId)  throws NoInformationAvailableException{
		try {
			return (Opinion<Info>) everyoneOpinion.get(informationType).getOpinion(agentId);
		} catch (Exception e) {
			throw new NoInformationAvailableException();
		}
	}


	@Override
	public <Info extends Information> Opinion<Info> getGlobalOpinion(Class<Info> myInfoType) throws NoInformationAvailableException {
		try {
			return (Opinion<Info>) everyoneOpinion.
			get(myInfoType).
			getGlobalOpinion();
		} catch (Exception e) {
			getMyAgent().signalException("requesting "+myInfoType,e);
			throw new NoInformationAvailableException();
		}
	}

	//
	// Behavior
	//

	/*
	 * Information
	 */



	@StepComposant()
	public void broadcastMyOpinions() {
		for (OpinionDataBase<?> op : everyoneOpinion.values())
			if (op.hasSignficantChange()){
				notify(op.getGlobalOpinion(), opinionObservationKey);//+op.informationType);
				op.significantchange=false;
			}
	}

	@MessageHandler
	@NotificationEnvelope(opinionObservationKey)
	public <Info extends Information> void receiveOpinion(
			NotificationMessage<Opinion<?>> o) {
		this.add(o.getNotification());
	}

	//
	// Primitives
	//


	protected <Info extends Information> boolean isStillValid(
			final Info id) {
		long maxInfo = everyoneOpinion.get(id.getClass()).getGlobalOpinion().getMaxInformationDynamicity();
		return id.getUptime() - new Date().getTime() <= maxInfo;
	}	

	//
	// Subclasses
	//

	/*
	 * 
	 * 
	 */


	class AnalysedInformationDataBase<Info extends Information> extends
	InformationDataBase<Info> {
		private static final long serialVersionUID = 7475234188930236983L;

		//
		// Fields
		//


		Info meanInfo;
		Info minState;
		Info maxState;

		Double dispersion;
		//
		protected Long minDynamicty = Long.MAX_VALUE;
		protected Long maxDynamicity = Long.MIN_VALUE;
		//
		Map<AgentIdentifier, EnrichedInfo> sytemDynamicities = 
			new HashMap<AgentIdentifier, EnrichedInfo>();

		//
		// Constructor
		//

		public AnalysedInformationDataBase() {
			super();
		}

		//
		// Methods
		//

		@Override
		public Info add(Info o) {
			addDynamicities(o);
			Info result = super.add(o);

			meanInfo = 	(Info) o.getRepresentativeElement(values());
			dispersion = FunctionalDispersionAgregator.getEcartType(o, values());

			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Info remove(final Object i) {
			if (i instanceof AgentIdentifier) {
				Info result = super.remove(i);
				removeDynamicities(result);

				meanInfo = 	(Info) result.getRepresentativeElement(values());
				dispersion = FunctionalDispersionAgregator.getEcartType(result, values());

				return result;

			} else
				throw new RuntimeException(i+""+(i instanceof Information));

		}

		/*
		 * Maintining time information
		 */

		protected long getMaxInformationDynamicity() {
			return maxDynamicity;
		}

		protected long getMinInformationDynamicity() {
			return minDynamicty;
		}

		protected void clean() {
			for (final Information i : this.values())
				if (i.getUptime() - new Date().getTime() > NegotiationStaticParameters.maxSystemExpirationTime)
					this.remove(i.getMyAgentIdentifier());
		}

		/*
		 * Stats
		 */

		protected Info getMeanInfo() {
			return meanInfo;
		}

		protected double getGlobalInformationDeviation() {
			return dispersion;

		}

		private Info getRichestAgent() {
			return maxState;
		}

		private Info getPoorestAgent() {
			return minState;
		}

		//
		// Primitives
		//

		protected void addDynamicities(Info info){
			if (sytemDynamicities.containsKey(info.getMyAgentIdentifier())) 
				sytemDynamicities.get(info.getMyAgentIdentifier()).update(info);
			else
				sytemDynamicities.put(info.getMyAgentIdentifier(), new EnrichedInfo(info));

			updateDyn(sytemDynamicities.get(info.getMyAgentIdentifier()));
			updateMinMax(info);
		}

		protected void removeDynamicities(Info info){
			EnrichedInfo dyn = sytemDynamicities.remove(info.getMyAgentIdentifier());

			if (dyn==null)
				throw new RuntimeException("removing unknown info??");

			updateDyn(dyn);
			updateMinMax(info);
		}

		private void updateDyn(EnrichedInfo dyn) {

			if (dyn.getLastInfoDynamicity()< minDynamicty)
				minDynamicty = dyn.getLastInfoDynamicity();

			if(dyn.getLastInfoDynamicity()> maxDynamicity)
				maxDynamicity = dyn.getLastInfoDynamicity();

			if (dyn.getLastInfoDynamicity().equals(maxDynamicity) || dyn.getLastInfoDynamicity().equals(minDynamicty)){
				maxDynamicity = Long.MIN_VALUE;
				minDynamicty = Long.MAX_VALUE;
				for (EnrichedInfo e : sytemDynamicities.values()){
					if (e.getLastInfoDynamicity()<minDynamicty)
						minDynamicty = e.getLastInfoDynamicity();
					if (e.getLastInfoDynamicity()>maxDynamicity)
						maxDynamicity = e.getLastInfoDynamicity();
				}
			}
		}

		private void updateMinMax(Info o) {

			if (minState==null || o.getNumericValue(o)< o.getNumericValue(minState))
				minState = o;

			if(maxState== null || o.getNumericValue(o)> o.getNumericValue(maxState))
				maxState = o;

			if (o.getNumericValue(o).equals(o.getNumericValue(maxState)) 
					|| o.getNumericValue(o).equals(o.getNumericValue(minState))){
				Double maxStateUtil = Double.MIN_VALUE;
				Double minStateUtil = Double.MAX_VALUE;
				for (Info e : values()){
					if (o.getNumericValue(e)<minStateUtil){
						minState = e;
						minStateUtil = o.getNumericValue(minState);
					}
					if (o.getNumericValue(e)>maxStateUtil){
						maxState = e;
						maxStateUtil = o.getNumericValue(maxState);
					}
				}
			}
		}

		//
		//
		//

		private class EnrichedInfo implements Comparable<EnrichedInfo>, DimaComponentInterface{
			private static final long serialVersionUID = 6659167432850630911L;

			private Long infoDynamicity = Long.MAX_VALUE;
			private long uptime;
			private AgentIdentifier id;

			public EnrichedInfo(final Information myInfo) {
				uptime = myInfo.getUptime();
				id = myInfo.getMyAgentIdentifier();
			}

			public Long getLastInfoDynamicity() {
				return this.infoDynamicity;
			}

			public Long update(final Information s) {
				if (!s.getMyAgentIdentifier().equals(id))
					throw new RuntimeException("arghhhhh!");

				Long previous = infoDynamicity;
				this.infoDynamicity = s.getUptime() - uptime;
				uptime = s.getUptime();

				return previous;
			}

			@Override
			public int compareTo(EnrichedInfo that) {
				return this.infoDynamicity.compareTo(that.infoDynamicity);
			}

			@Override
			public boolean equals(final Object o) {
				return this.id.equals(o);
			}

			@Override
			public int hashCode() {
				return this.id.hashCode();
			}


		}
	}

	/*
	 * 
	 * 
	 */

	class OpinionDataBase<Info extends Information> extends
	InformationDataBase<Opinion<Info>> {

		boolean significantchange=false;

		private Opinion<Info> meanInfo;
		private Opinion<Info> minState;
		private Opinion<Info> maxState;
		//
		protected Long minDynamicty = Long.MAX_VALUE;
		protected Long maxDynamicity = Long.MIN_VALUE;
		//	
		Map<AgentIdentifier, Long> sytemMinDynamicities = 
			new HashMap<AgentIdentifier, Long>();
		Map<AgentIdentifier, Long> sytemMaxDynamicities = 
			new HashMap<AgentIdentifier, Long>();

		LightWeightedAverageDoubleAggregation opinionDispersion = new LightWeightedAverageDoubleAggregation();

		final Class<Info> informationType;

		//
		// Constructor
		//


		public OpinionDataBase(Class<Info> informationType) {
			this.informationType=informationType;
		}

		protected boolean hasSignficantChange() {
			return significantchange;
		}

		//
		// Methods
		//

		Opinion<Info> getOpinion(AgentIdentifier agentId){
			try {
				((AnalysedInformationDataBase) getInformation(informationType)).clean();
				return new SimpleOpinion<Info>(
						getIdentifier(), agentId,
						getInformation(informationType, agentId));
			} catch (final NoInformationAvailableException e) {
				return getGlobalOpinion();
			}
		}

		Opinion<Info> getGlobalOpinion() {
			//updating the values
			this.clean();

			//updating the personal opinion
			try {
				this.add(getPersonalOpinion(NegotiationStaticParameters.globaLAgentIdentifer));
			} catch (NoInformationAvailableException e) {
				//Do nothing
			}

			//generating opinion
			return new SimpleOpinion<Info>(
					getIdentifier(),
					NegotiationStaticParameters.globaLAgentIdentifer,
					getAgents(),
					getMinInformationDynamicity(),
					getMaxInformationDynamicity(),
					getMeanInfo(),
					getGlobalInformationDeviation(),
					getPoorestAgent(),
					getRichestAgent(),
					size());
		}


		Opinion<Info> getPersonalOpinion(
				AgentIdentifier representantdelinformation) throws NoInformationAvailableException {
			final AnalysedInformationDataBase myOp4thisInfo = (AnalysedInformationDataBase) getInformation(informationType);
			final SimpleOpinion<Info> myOwnOpinion = 
				new SimpleOpinion<Info>(
						getIdentifier(), 
						representantdelinformation,
						myOp4thisInfo.getAgents(),
						myOp4thisInfo.getMinInformationDynamicity(),
						myOp4thisInfo.getMaxInformationDynamicity(),
						(Info) myOp4thisInfo.getMeanInfo(),
						myOp4thisInfo.getGlobalInformationDeviation(),
						(Info) myOp4thisInfo.getPoorestAgent(),
						(Info) myOp4thisInfo.getRichestAgent(),
						myOp4thisInfo.size());
			return myOwnOpinion;
		}

		@Override
		public Opinion<Info> add(Opinion<Info> o) {
			addDynamicities(o);
			updateMinMax(o);
			Opinion<Info> result = super.add(o);
			meanInfo = 	(Opinion<Info>) o.fuse(values());
			opinionDispersion.add(o.getOpinionDispersion(), new Double(o.getNumberOfAggregatedElements()));
			significantchange=true;
			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Opinion<Info> remove(final Object obj) {
			if (obj instanceof AgentIdentifier){
				Opinion<Info> o = super.remove(obj);
				removeDynamicities(o);
				updateMinMax(o);
				meanInfo = (Opinion<Info>)	o.fuse(values());
				opinionDispersion.remove(o.getOpinionDispersion(), new Double(o.getNumberOfAggregatedElements()));
				significantchange=true;

				return  o;
			} else
				throw new RuntimeException();
		}

		/*
		 * Maintining time information
		 */

		protected long getMaxInformationDynamicity() {
			return maxDynamicity;
		}

		protected long getMinInformationDynamicity() {
			return minDynamicty;
		}

		protected void clean() {
			for (final Information i : this.values())
				if (i.getUptime() - new Date().getTime() > NegotiationStaticParameters.maxSystemExpirationTime)
					this.remove(i.getMyAgentIdentifier());
		}

		/*
		 * Stats
		 */

		protected Info getMeanInfo() {
			return meanInfo.getRepresentativeElement();
		}

		protected double getGlobalInformationDeviation() {
			return opinionDispersion.getRepresentativeElement();
		}

		private Info getRichestAgent() {
			return maxState.getRepresentativeElement();
		}

		private Info getPoorestAgent() {
			return minState.getRepresentativeElement();
		}

		/*
		 * 
		 */

		protected void addDynamicities(Opinion<Info> info){
			Long previousMin = sytemMinDynamicities.put(info.getCreator(),info.getMinInformationDynamicity());
			Long previousMax = sytemMaxDynamicities.put(info.getCreator(),info.getMaxInformationDynamicity());

			updateDyn(info, previousMin, previousMax);
		}
		protected void removeDynamicities(Opinion<Info> info){
			Long previousMin = sytemMinDynamicities.remove(info.getCreator());
			Long previousMax = sytemMaxDynamicities.remove(info.getCreator());

			updateDyn(info, previousMin, previousMax);
		}

		private void updateDyn(Opinion<Info> info, Long previousMin, Long previousMax) {
			if (info.getMinInformationDynamicity()< minDynamicty)
				minDynamicty = info.getMinInformationDynamicity();	
			else if (previousMin==null || previousMin.equals(minDynamicty)){
				minDynamicty = Long.MAX_VALUE;
				for (Long e : sytemMinDynamicities.values()){
					if (e<minDynamicty)
						minDynamicty = e;
				}	
			}

			if (info.getMaxInformationDynamicity()> maxDynamicity)
				maxDynamicity = info.getMaxInformationDynamicity();	
			else if (previousMax==null || previousMax.equals(maxDynamicity)){
				maxDynamicity = Long.MIN_VALUE;
				for (Long e : sytemMaxDynamicities.values()){
					if (e<maxDynamicity)
						maxDynamicity = e;
				}
			}
		}

		private void updateMinMax(Opinion<Info> o) {

			if (minState == null || o.getNumericValue(o.getRepresentativeElement())< o.getNumericValue(minState.getRepresentativeElement()))
				minState = o;

			if(maxState == null || o.getNumericValue(o.getRepresentativeElement())> o.getNumericValue(maxState.getRepresentativeElement()))
				maxState = o;

			if (o.getNumericValue(o.getRepresentativeElement()).equals(o.getNumericValue(maxState.getRepresentativeElement())) 
					|| o.getNumericValue(o.getRepresentativeElement()).equals(o.getNumericValue(minState.getRepresentativeElement()))){
				Double maxStateUtil = Double.MIN_VALUE;
				Double minStateUtil = Double.MAX_VALUE;
				for (Opinion<Info> e : values()){
					if (o.getNumericValue(e.getRepresentativeElement())<minStateUtil){
						minState = e;
						minStateUtil = o.getNumericValue(minState.getRepresentativeElement());
					}
					if (o.getNumericValue(e.getRepresentativeElement())>maxStateUtil){
						maxState = e;
						maxStateUtil = o.getNumericValue(maxState.getRepresentativeElement());
					}
				}
			}
		}

		class SimpleOpinion<InformedState extends Information> implements
		Opinion<InformedState> {
			private static final long serialVersionUID = 6616276179359237312L;

			public static final String localOpinionAboutTheSystem = "global state";

			private final AgentIdentifier id;
			private final AgentIdentifier creator;
			private final boolean isCertain;
			private final Integer informationNumber;

			private final Long creationTime;

			private final Long minInformationDynamicity;
			private final Long maxInformationDynamicity;

			private final Double statesDeviation;
			private final InformedState representativeState;

			private final Collection<AgentIdentifier> aggregatedAgents;


			private final InformedState minState;
			private final InformedState maxState;

			/*
			 * 
			 */
			public SimpleOpinion(
					final AgentIdentifier creator,
					final AgentIdentifier id, 
					final InformedState agentExactState) {
				this.id = id;
				this.isCertain = true;
				this.creator = creator;
				aggregatedAgents = new ArrayList<AgentIdentifier>();
				aggregatedAgents.add(id);
				this.representativeState = agentExactState;
				this.informationNumber = 1;
				this.statesDeviation = 0.;
				//
				this.minInformationDynamicity = getMinInformationDynamicity();
				this.maxInformationDynamicity = getMaxInformationDynamicity();
				this.minState = getMinElement();
				this.maxState = getMaxElement();
				this.creationTime = new Date().getTime();

			}

			public SimpleOpinion(
					final AgentIdentifier creator,
					final AgentIdentifier id, 
					Collection<AgentIdentifier> aggregatedAgents,
					final long minInformationDynamicity,
					final long maxInformationDynamicity,
					final InformedState meanState,
					final double globalInformationDeviation,
					final InformedState minState,
					final InformedState maxState,
					final int informationNumber) {
				this.id = id;
				this.creator = creator;
				this.aggregatedAgents = aggregatedAgents;
				this.isCertain = false;
				this.statesDeviation = globalInformationDeviation;
				this.minInformationDynamicity = minInformationDynamicity;
				this.maxInformationDynamicity = maxInformationDynamicity;
				this.representativeState = meanState;
				this.informationNumber = informationNumber;
				this.minState = minState;
				this.maxState = maxState;
				this.creationTime = new Date().getTime();
			}

			//
			//
			//

			@Override
			public AgentIdentifier getMyAgentIdentifier() {
				return this.id;
			}

			@Override
			public AgentIdentifier getCreator() {
				return this.creator;
			}

			@Override
			public InformedState getRepresentativeElement() {
				return this.representativeState;
			}

			@Override
			public boolean isCertain() {
				return this.isCertain;
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
			public Double getOpinionDispersion() {
				return this.statesDeviation;
			}

			@Override
			public int getNumberOfAggregatedElements() {
				return this.informationNumber;
			}

			@Override
			public Long getCreationTime() {
				return getCreationTime();
			}

			@Override
			public long getUptime() {
				return new Date().getTime() - this.creationTime;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Opinion)
					return ((Opinion) o).getCreator().equals(this.getCreator());
				else
					return false;
			}

			@Override
			public int hashCode() {
				return this.getCreator().hashCode();
			}

			@Override
			public Collection<AgentIdentifier> getAggregatedAgents() {
				return aggregatedAgents;
			}

			@Override
			public InformedState getMinElement() {
				return this.minState;
			}

			@Override
			public InformedState getMaxElement() {
				return this.maxState;
			}

			/*
			 * 
			 */

			@Override
			public AbstractCompensativeAggregation<Information> fuse(
					Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
				boolean fCertain=true;
				int fInfosNumber=0;
				Long fMinDyn=Long.MAX_VALUE;
				Long fMaxDyn=Long.MIN_VALUE;
				LightWeightedAverageDoubleAggregation fDisp = new LightWeightedAverageDoubleAggregation();
				Collection<AgentIdentifier> fAgentsID = new ArrayList<AgentIdentifier>();
				Map<InformedState,Double> fallRepStates = new HashMap<InformedState, Double>();
				InformedState fminState=null, fmaxState=null;

				for (AbstractCompensativeAggregation<? extends Information> av : averages)
					if (av instanceof Opinion) {
						Opinion<InformedState> a = (Opinion) av;
						fCertain = fCertain && a.isCertain();
						fInfosNumber+=a.getNumberOfAggregatedElements();
						fMinDyn = Math.min(fMinDyn, a.getMinInformationDynamicity());
						fMaxDyn = Math.max(fMaxDyn, a.getMaxInformationDynamicity());
						fDisp.add(a.getOpinionDispersion(), a.getNumberOfAggregatedElements());
						fAgentsID.addAll(a.getAggregatedAgents());
						fallRepStates.put(a.getRepresentativeElement(),new Double(a.getNumberOfAggregatedElements()));
						if (fminState==null || getNumericValue(fminState)>getNumericValue(a.getMinElement()))
							fminState= a.getMinElement();
						if (fmaxState==null || getNumericValue(fmaxState)<getNumericValue(a.getMaxElement()))
							fmaxState= a.getMaxElement();

					}

				return new SimpleOpinion(
						getCreator(), 
						getIdentifier(), 
						fAgentsID,
						fMinDyn,
						fMaxDyn, 
						getRepresentativeElement(fallRepStates), 
						fDisp.getRepresentativeElement(), fminState, fmaxState, 
						fInfosNumber);			

			}

			/*
			 * 
			 */

			@Override
			public int compareTo(Information o) {
				return getRepresentativeElement().compareTo(o);
			}

			@Override
			public Double getNumericValue(Information e) {
				return getRepresentativeElement().getNumericValue(e);
			}

			@Override
			public Information getRepresentativeElement(
					Collection<? extends Information> elems) {
				return getRepresentativeElement().getRepresentativeElement(elems);
			}

			@Override
			public Information getRepresentativeElement(
					Map<? extends Information, Double> elems) {
				return getRepresentativeElement().getRepresentativeElement(elems);
			}
			//
			// Primitives
			//

			@Override
			public String toString(){
				return "Opinion of "+creator+" about "+aggregatedAgents
				+"\n * representative state is "+getRepresentativeElement()
				+"\n * dispersion is "+getGlobalInformationDeviation();
			}

		}

	}
}
//@StepComposant()
//	public void broadcastMyState() {
//		if (getMyAgent() instanceof SimpleRationalAgent) {
//			SimpleRationalAgent ag = (SimpleRationalAgent) getMyAgent();
//		try {
//			if (everyoneOpinion.
//					get(ag.getMyCurrentState().getClass()).hasSignficantChange()){
//				notify(this.getOpinion(ag.getMyCurrentState().getClass(),getIdentifier()), "opiniondiffusion");
//			}
//			significantchange=false;
//		} catch (Exception e) {
//			getMyAgent().logException("impossible on raisonne sur son propre ��tat il doit etre au moins pr��sent!", e);
//			throw new RuntimeException();
//		}
//			
//		}
//	}
//	
//	//
//	// Behavior
//	//
//
//	/*
//	 * Information
//	 */
//
//	@StepComposant()
//	public void broadcastMyState() {
//		try {
//			if (hasSignficantChange()){
//				
//			}
//			significantchange=false;
//		} catch (Exception e) {
//			getMyAgent().logException("impossible on raisonne sur son propre ��tat il doit etre au moins pr��sent!", e);
//			throw new RuntimeException();
//		}
//	}
//
//	@MessageHandler
//	@NotificationEnvelope("personalstatediffusion")
//	public <Info extends Information> void receiveInformation(
//			NotificationMessage<Information> o) {
//		this.add(o.getNotification());
//	}
//	
//	private boolean hasSignficantChange() {
//		return significantchange;
//	}
//	
//		protected void collectInformation(Class<Information> informationType,
//				boolean isOpinion) {
//			final int popPercent = new Random()
//			.nextInt(NegotiationStaticParameters.populationPercentToCollect
//					* this.getKnownAgents().size()) + 1;
//			for (int i = 0; i < popPercent; i++) {
//				final AgentIdentifier neighbor = this.getKnownAgents().get(
//						new Random().nextInt(this.getKnownAgents().size()));
//				this.getMyAgent().sendMessage(
//						neighbor,
//						new InfoUpdateMessage(Performative.Request,
//								informationType, isOpinion));
//			}
//		}
//
//		@MessageHandler
//		@FipaACLEnvelope(performative = Performative.Request, content = InformationProtocol.informationExchange, protocol = InformationProtocol.class)
//		public void giveInformation(InfoUpdateMessage o) {
//			this.getMyAgent().sendMessage(o.getSender(),
//					new InfoUpdateMessage(
//							Performative.Inform,
//							o.isOpinion() ? this.getPersonalOpinion(
//									o.getMyInf(),
//									NegotiationStaticParameters.globaLAgentIdentifer)
//									: this.getMyInformation(o.getMyInf())));
//		}
//

//		@Override
//		public PersonalState getMyCurrentState() {
//			return this.myEnrichedState.getMyInfo();
//		}
//	
//		@Override
//		public void setNewState(final PersonalState s) {
//			if (this.myEnrichedState == null)
//				this.myEnrichedState = new EnrichedInfo<PersonalState>(s);
//			this.myEnrichedState.update(s);
//		}

//final Random rand = new Random();
//for (int i = 0; i < NegotiationStaticParameters.numberOfAgentToGiveState; i++) {
//	final AgentIdentifier id = this.getKnownAgents().get(
//			rand.nextInt(this.getKnownAgents().size()));
//	this.getMyAgent()
//	.sendMessage(
//			id,
//			new InfoUpdateMessage(
//					Performative.Inform,
//					this.getPersonalOpinion(
//							this.getMyCurrentState().getClass(),
//							NegotiationStaticParameters.globaLAgentIdentifer)));
//}
//;

//final Random rand = new Random();
//for (int i = 0; i < NegotiationStaticParameters.numberOfAgentToGiveState; i++) {
//	final AgentIdentifier id = this.getKnownAgents().get(
//			rand.nextInt(this.getKnownAgents().size()));
//	this.getMyAgent().sendMessage(
//			id,
//			);
//}
//;



//
//private final TreeSet<Long> mins = new TreeSet<Long>();
//private final TreeSet<Long> maxs = new TreeSet<Long>();
////		final HeavyAggregator<Info> myInfoAgregator;
//
////
//// Constructor
////
//
//
////
//// Methods
////
//
//protected void addOpinion(Opinion<Info> o) {
//	if (this.containsKey(o.getMyAgentIdentifier())) {
//		this.mins.remove(this.get(o.getMyAgentIdentifier())
//				.getMinInformationDynamicity());
//		this.maxs.remove(this.get(o.getMyAgentIdentifier())
//				.getMaxInformationDynamicity());
//		this.myInfoAgregator.remove(o.getInformation());
//
//		this.put(o.getMyAgentIdentifier(), o);
//
//		this.maxs.add(o.getMinInformationDynamicity());
//		this.mins.add(o.getMaxInformationDynamicity());
//		this.myInfoAgregator.put(o.getInformation(),
//				o.getOpinionWeight());
//	} else {
//		this.maxs.add(o.getMinInformationDynamicity());
//		this.mins.add(o.getMaxInformationDynamicity());
//		this.myInfoAgregator.put(o.getInformation(),
//				o.getOpinionWeight());
//
//		this.put(o.getMyAgentIdentifier(), o);
//	}
//}
//
//protected void removeOpinion(final Opinion<Info> o) {
//	this.mins.remove(this.get(o.getMyAgentIdentifier())
//			.getMinInformationDynamicity());
//	this.maxs.remove(this.get(o.getMyAgentIdentifier())
//			.getMaxInformationDynamicity());
//	this.myInfoAgregator.remove(o.getInformation());
//
//	this.remove(o.getMyAgentIdentifier());
//}
//
///*
// * Maintining time information
// */
//
//private long getMaxOpinionDynamicity() {
//	return this.maxs.last();
//}
//
//private long getMinOpinionDynamicity() {
//	return this.mins.first();
//}
//
//private void clean() {
//	for (final Opinion<Info> i : this.values())
//		if (i.getUptime() - new Date().getTime() > NegotiationStaticParameters.maxSystemExpirationTime)
//			this.removeOpinion(i);
//}
//
///*
// * Stats
// */
//
//private Info getMeanOpinion() {
//	return this.myInfoAgregator.getRepresentativeElement();
//}
//
//private double getGlobalOpinionDeviation() {
//	return this.myInfoAgregator.getEcartType();
//}


//Information result = null;
//if (this.containsKey(o.getMyAgentIdentifier())) {
//	result = (EnrichedInfo<Information>) 
//	this.get(o.getMyAgentIdentifier());
//	this.systemDynamicity.remove(((EnrichedInfo<Information>)  result).getLastInfoDynamicity());
//	this.myInfoAgregator.remove(o);
//	((EnrichedInfo<Information>) this.get(o.getMyAgentIdentifier()))
//	.update(o);
//	this.systemDynamicity
//	.add(((EnrichedInfo<Information>) this.get(o
//			.getMyAgentIdentifier()))
//			.getLastInfoDynamicity());
//	this.myInfoAgregator.add((Info) o);
//} else {
//	result = this.put(o.getMyAgentIdentifier(),
//			new EnrichedInfo<Info>((Info) o));
//	this.systemDynamicity
//	.add(((EnrichedInfo<Information>) this.get(o
//			.getMyAgentIdentifier()))
//			.getLastInfoDynamicity());
//	myInfoAgregator.add((Info) o);
//}
//return result;


//		private Info myInfo;
//
//		public Info getMyInfo() {
//			return this.myInfo;
//		}

//		@Override
//		public AgentIdentifier getMyAgentIdentifier() {
//			return this.myInfo.getMyAgentIdentifier();
//		}
//
//		@Override
//		public boolean equals(final Object o) {
//			return this.getMyAgentIdentifier().equals(o);
//		}
//
//		@Override
//		public int hashCode() {
//			return this.getMyAgentIdentifier().hashCode();
//		}

// /*
// * Opinion
// */
//
// public <Info extends Information> void collectOpinion(Class<Info>
// informationType){
// final int popPercent = new
// Random().nextInt(NegotiationStaticParameters.populationPercentToCollect*getKnownAgents().size())+1;
// for (int i = 0; i < popPercent; i++){
// AgentIdentifier neighbor = getKnownAgents().get(new
// Random().nextInt(getKnownAgents().size()));
// getMyAgent().sendMessage(
// neighbor,
// new InfoSpecMessage(Performative.Request,
// InformationProtocol.globalBeliefExchange, informationType, true));
// }
// }
//
// @MessageHandler
// @FipaACLEnvelope(
// performative=Performative.Request,
// content=InformationProtocol.globalBeliefExchange,
// protocol=InformationProtocol.class)
// public <Info extends Information> void giveOpinion(InfoSpecMessage<Info> o) {
// getMyAgent().sendMessage(o.getSender(),
// new InfoUpdateMessage<Info>(
// Performative.Inform,
// InformationProtocol.globalBeliefExchange,
// (Info) getPersonalOpinion(o.getMyInf(),
// NegotiationStaticParameters.globaLAgentIdentifer)));
// }
//
// @MessageHandler
// @FipaACLEnvelope(
// performative=Performative.Inform,
// content=InformationProtocol.globalBeliefExchange,
// protocol=InformationProtocol.class)
// public <Info extends Information> void
// receiveOpinion(InfoUpdateMessage<Opinion<Info>> o) {
// ((OpinionDataBase<Info>)
// everyoneOpinion.get(o.getClass())).addOpinion(o.getInfo());
// }
// @StepComposant(ticker=NegotiationStaticParameters.opinionExchangeFrequency)
// public void giveMyOpinion(){
// try {
// final Random rand = new Random();
// for (int i =0; i < NegotiationStaticParameters.numberOfAgentToGiveOpinion;
// i++){
// final AgentIdentifier id =
// getKnownAgents().get(rand.nextInt(getKnownAgents().size()));
//
// sendMessage(id,
// new StateUpdateMessage<Opinion<InformedState>>(
// Performative.Inform,
// InformationProtocol.globalBeliefExchange,
// getMyAcquaintancesHandler().getMyOpinion()));
// }
// } catch (final NoInformationAvailableException e) {
// return;
// }
// }