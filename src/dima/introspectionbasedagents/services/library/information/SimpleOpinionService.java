package dima.introspectionbasedagents.services.library.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import negotiation.negotiationframework.NegotiationStaticParameters;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.FunctionalDispersionAgregator;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;

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
	public void add(final Information information) {
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


	public String getOpinionObservationKey(final Class<? extends Information> info){
		return "opiniondiffusion"+info;
	}


	//
	// Methods
	//

	@Override
	public <Info extends Information> Opinion<Info> getOpinion(
			final Class<Info> informationType,
			final AgentIdentifier agentId)  throws NoInformationAvailableException{
		try {
			return (Opinion<Info>) this.everyoneOpinion.get(informationType).getOpinion(agentId);
		} catch (final Exception e) {
			throw new NoInformationAvailableException();
		}
	}


	@Override
	public <Info extends Information> Opinion<Info> getGlobalOpinion(final Class<Info> myInfoType) throws NoInformationAvailableException {
		try {
			return (Opinion<Info>) this.everyoneOpinion.
			get(myInfoType).
			getGlobalOpinion();
		} catch (final Exception e) {
			this.getMyAgent().signalException("requesting "+myInfoType,e);
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
		for (final OpinionDataBase<?> op : this.everyoneOpinion.values())
			if (op.hasSignficantChange()){
				this.notify(op.getGlobalOpinion(), SimpleOpinionService.opinionObservationKey);//+op.informationType);
				op.significantchange=false;
			}
	}

	@MessageHandler
	@NotificationEnvelope(SimpleOpinionService.opinionObservationKey)
	public <Info extends Information> void receiveOpinion(
			final NotificationMessage<Opinion<?>> o) {
		this.add(o.getNotification());
	}

	//
	// Primitives
	//


	protected <Info extends Information> boolean isStillValid(
			final Info id) {
		final long maxInfo = this.everyoneOpinion.get(id.getClass()).getGlobalOpinion().getMaxInformationDynamicity();
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
		public Info add(final Info o) {
			this.addDynamicities(o);
			final Info result = super.add(o);

			this.meanInfo = 	(Info) o.getRepresentativeElement(this.values());
			this.dispersion = FunctionalDispersionAgregator.getEcartType(o, this.values());

			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Info remove(final Object i) {
			if (i instanceof AgentIdentifier) {
				final Info result = super.remove(i);
				this.removeDynamicities(result);

				this.meanInfo = 	(Info) result.getRepresentativeElement(this.values());
				this.dispersion = FunctionalDispersionAgregator.getEcartType(result, this.values());

				return result;

			} else
				throw new RuntimeException(i+""+(i instanceof Information));

		}

		/*
		 * Maintining time information
		 */

		protected long getMaxInformationDynamicity() {
			return this.maxDynamicity;
		}

		protected long getMinInformationDynamicity() {
			return this.minDynamicty;
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
			return this.meanInfo;
		}

		protected double getGlobalInformationDeviation() {
			return this.dispersion;

		}

		private Info getRichestAgent() {
			return this.maxState;
		}

		private Info getPoorestAgent() {
			return this.minState;
		}

		//
		// Primitives
		//

		protected void addDynamicities(final Info info){
			if (this.sytemDynamicities.containsKey(info.getMyAgentIdentifier()))
				this.sytemDynamicities.get(info.getMyAgentIdentifier()).update(info);
			else
				this.sytemDynamicities.put(info.getMyAgentIdentifier(), new EnrichedInfo(info));

			this.updateDyn(this.sytemDynamicities.get(info.getMyAgentIdentifier()));
			this.updateMinMax(info);
		}

		protected void removeDynamicities(final Info info){
			final EnrichedInfo dyn = this.sytemDynamicities.remove(info.getMyAgentIdentifier());

			if (dyn==null)
				throw new RuntimeException("removing unknown info??");

			this.updateDyn(dyn);
			this.updateMinMax(info);
		}

		private void updateDyn(final EnrichedInfo dyn) {

			if (dyn.getLastInfoDynamicity()< this.minDynamicty)
				this.minDynamicty = dyn.getLastInfoDynamicity();

			if(dyn.getLastInfoDynamicity()> this.maxDynamicity)
				this.maxDynamicity = dyn.getLastInfoDynamicity();

			if (dyn.getLastInfoDynamicity().equals(this.maxDynamicity) || dyn.getLastInfoDynamicity().equals(this.minDynamicty)){
				this.maxDynamicity = Long.MIN_VALUE;
				this.minDynamicty = Long.MAX_VALUE;
				for (final EnrichedInfo e : this.sytemDynamicities.values()){
					if (e.getLastInfoDynamicity()<this.minDynamicty)
						this.minDynamicty = e.getLastInfoDynamicity();
					if (e.getLastInfoDynamicity()>this.maxDynamicity)
						this.maxDynamicity = e.getLastInfoDynamicity();
				}
			}
		}

		private void updateMinMax(final Info o) {

			if (this.minState==null || o.getNumericValue(o)< o.getNumericValue(this.minState))
				this.minState = o;

			if(this.maxState== null || o.getNumericValue(o)> o.getNumericValue(this.maxState))
				this.maxState = o;

			if (o.getNumericValue(o).equals(o.getNumericValue(this.maxState))
					|| o.getNumericValue(o).equals(o.getNumericValue(this.minState))){
				Double maxStateUtil = Double.MIN_VALUE;
				Double minStateUtil = Double.MAX_VALUE;
				for (final Info e : this.values()){
					if (o.getNumericValue(e)<minStateUtil){
						this.minState = e;
						minStateUtil = o.getNumericValue(this.minState);
					}
					if (o.getNumericValue(e)>maxStateUtil){
						this.maxState = e;
						maxStateUtil = o.getNumericValue(this.maxState);
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
			private final AgentIdentifier id;

			public EnrichedInfo(final Information myInfo) {
				this.uptime = myInfo.getUptime();
				this.id = myInfo.getMyAgentIdentifier();
			}

			public Long getLastInfoDynamicity() {
				return this.infoDynamicity;
			}

			public Long update(final Information s) {
				if (!s.getMyAgentIdentifier().equals(this.id))
					throw new RuntimeException("arghhhhh!");

				final Long previous = this.infoDynamicity;
				this.infoDynamicity = s.getUptime() - this.uptime;
				this.uptime = s.getUptime();

				return previous;
			}

			@Override
			public int compareTo(final EnrichedInfo that) {
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

		/**
		 *
		 */
		private static final long serialVersionUID = 4600199302176545678L;

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


		public OpinionDataBase(final Class<Info> informationType) {
			this.informationType=informationType;
		}

		protected boolean hasSignficantChange() {
			return this.significantchange;
		}

		//
		// Methods
		//

		Opinion<Info> getOpinion(final AgentIdentifier agentId){
			try {
				((AnalysedInformationDataBase) SimpleOpinionService.this.getInformation(this.informationType)).clean();
				return new SimpleOpinion<Info>(
						SimpleOpinionService.this.getIdentifier(), agentId,
						SimpleOpinionService.this.getInformation(this.informationType, agentId));
			} catch (final NoInformationAvailableException e) {
				return this.getGlobalOpinion();
			}
		}

		Opinion<Info> getGlobalOpinion() {
			//updating the values
			this.clean();

			//updating the personal opinion
			try {
				this.add(this.getPersonalOpinion(NegotiationStaticParameters.globaLAgentIdentifer));
			} catch (final NoInformationAvailableException e) {
				//Do nothing
			}

			//generating opinion
			return new SimpleOpinion<Info>(
					SimpleOpinionService.this.getIdentifier(),
					NegotiationStaticParameters.globaLAgentIdentifer,
					this.getAgents(),
					this.getMinInformationDynamicity(),
					this.getMaxInformationDynamicity(),
					this.getMeanInfo(),
					this.getGlobalInformationDeviation(),
					this.getPoorestAgent(),
					this.getRichestAgent(),
					this.size());
		}


		Opinion<Info> getPersonalOpinion(
				final AgentIdentifier representantdelinformation) throws NoInformationAvailableException {
			final AnalysedInformationDataBase myOp4thisInfo = (AnalysedInformationDataBase) SimpleOpinionService.this.getInformation(this.informationType);
			final SimpleOpinion<Info> myOwnOpinion =
				new SimpleOpinion<Info>(
						SimpleOpinionService.this.getIdentifier(),
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
		public Opinion<Info> add(final Opinion<Info> o) {
			this.addDynamicities(o);
			this.updateMinMax(o);
			final Opinion<Info> result = super.add(o);
			this.meanInfo = 	(Opinion<Info>) o.fuse(this.values());
			this.opinionDispersion.add(o.getOpinionDispersion(), new Double(o.getNumberOfAggregatedElements()));
			this.significantchange=true;
			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Opinion<Info> remove(final Object obj) {
			if (obj instanceof AgentIdentifier){
				final Opinion<Info> o = super.remove(obj);
				this.removeDynamicities(o);
				this.updateMinMax(o);
				this.meanInfo = (Opinion<Info>)	o.fuse(this.values());
				this.opinionDispersion.remove(o.getOpinionDispersion(), new Double(o.getNumberOfAggregatedElements()));
				this.significantchange=true;

				return  o;
			} else
				throw new RuntimeException();
		}

		/*
		 * Maintining time information
		 */

		protected long getMaxInformationDynamicity() {
			return this.maxDynamicity;
		}

		protected long getMinInformationDynamicity() {
			return this.minDynamicty;
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
			return this.meanInfo.getRepresentativeElement();
		}

		protected double getGlobalInformationDeviation() {
			return this.opinionDispersion.getRepresentativeElement();
		}

		private Info getRichestAgent() {
			return this.maxState.getRepresentativeElement();
		}

		private Info getPoorestAgent() {
			return this.minState.getRepresentativeElement();
		}

		/*
		 *
		 */

		protected void addDynamicities(final Opinion<Info> info){
			final Long previousMin = this.sytemMinDynamicities.put(info.getCreator(),info.getMinInformationDynamicity());
			final Long previousMax = this.sytemMaxDynamicities.put(info.getCreator(),info.getMaxInformationDynamicity());

			this.updateDyn(info, previousMin, previousMax);
		}
		protected void removeDynamicities(final Opinion<Info> info){
			final Long previousMin = this.sytemMinDynamicities.remove(info.getCreator());
			final Long previousMax = this.sytemMaxDynamicities.remove(info.getCreator());

			this.updateDyn(info, previousMin, previousMax);
		}

		private void updateDyn(final Opinion<Info> info, final Long previousMin, final Long previousMax) {
			if (info.getMinInformationDynamicity()< this.minDynamicty)
				this.minDynamicty = info.getMinInformationDynamicity();
			else if (previousMin==null || previousMin.equals(this.minDynamicty)){
				this.minDynamicty = Long.MAX_VALUE;
				for (final Long e : this.sytemMinDynamicities.values())
					if (e<this.minDynamicty)
						this.minDynamicty = e;
			}

			if (info.getMaxInformationDynamicity()> this.maxDynamicity)
				this.maxDynamicity = info.getMaxInformationDynamicity();
			else if (previousMax==null || previousMax.equals(this.maxDynamicity)){
				this.maxDynamicity = Long.MIN_VALUE;
				for (final Long e : this.sytemMaxDynamicities.values())
					if (e<this.maxDynamicity)
						this.maxDynamicity = e;
			}
		}

		private void updateMinMax(final Opinion<Info> o) {

			if (this.minState == null || o.getNumericValue(o.getRepresentativeElement())< o.getNumericValue(this.minState.getRepresentativeElement()))
				this.minState = o;

			if(this.maxState == null || o.getNumericValue(o.getRepresentativeElement())> o.getNumericValue(this.maxState.getRepresentativeElement()))
				this.maxState = o;

			if (o.getNumericValue(o.getRepresentativeElement()).equals(o.getNumericValue(this.maxState.getRepresentativeElement()))
					|| o.getNumericValue(o.getRepresentativeElement()).equals(o.getNumericValue(this.minState.getRepresentativeElement()))){
				Double maxStateUtil = Double.MIN_VALUE;
				Double minStateUtil = Double.MAX_VALUE;
				for (final Opinion<Info> e : this.values()){
					if (o.getNumericValue(e.getRepresentativeElement())<minStateUtil){
						this.minState = e;
						minStateUtil = o.getNumericValue(this.minState.getRepresentativeElement());
					}
					if (o.getNumericValue(e.getRepresentativeElement())>maxStateUtil){
						this.maxState = e;
						maxStateUtil = o.getNumericValue(this.maxState.getRepresentativeElement());
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
				this.aggregatedAgents = new ArrayList<AgentIdentifier>();
				this.aggregatedAgents.add(id);
				this.representativeState = agentExactState;
				this.informationNumber = 1;
				this.statesDeviation = 0.;
				//
				this.minInformationDynamicity = this.getMinInformationDynamicity();
				this.maxInformationDynamicity = this.getMaxInformationDynamicity();
				this.minState = this.getMinElement();
				this.maxState = this.getMaxElement();
				this.creationTime = new Date().getTime();

			}

			public SimpleOpinion(
					final AgentIdentifier creator,
					final AgentIdentifier id,
					final Collection<AgentIdentifier> aggregatedAgents,
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
				return this.getCreationTime();
			}

			@Override
			public long getUptime() {
				return new Date().getTime() - this.creationTime;
			}

			@Override
			public boolean equals(final Object o) {
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
				return this.aggregatedAgents;
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
					final Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
				boolean fCertain=true;
				int fInfosNumber=0;
				Long fMinDyn=Long.MAX_VALUE;
				Long fMaxDyn=Long.MIN_VALUE;
				final LightWeightedAverageDoubleAggregation fDisp = new LightWeightedAverageDoubleAggregation();
				final Collection<AgentIdentifier> fAgentsID = new ArrayList<AgentIdentifier>();
				final Map<InformedState,Double> fallRepStates = new HashMap<InformedState, Double>();
				InformedState fminState=null, fmaxState=null;

				for (final AbstractCompensativeAggregation<? extends Information> av : averages)
					if (av instanceof Opinion) {
						final Opinion<InformedState> a = (Opinion) av;
						fCertain = fCertain && a.isCertain();
						fInfosNumber+=a.getNumberOfAggregatedElements();
						fMinDyn = Math.min(fMinDyn, a.getMinInformationDynamicity());
						fMaxDyn = Math.max(fMaxDyn, a.getMaxInformationDynamicity());
						fDisp.add(a.getOpinionDispersion(), a.getNumberOfAggregatedElements());
						fAgentsID.addAll(a.getAggregatedAgents());
						fallRepStates.put(a.getRepresentativeElement(),new Double(a.getNumberOfAggregatedElements()));
						if (fminState==null || this.getNumericValue(fminState)>this.getNumericValue(a.getMinElement()))
							fminState= a.getMinElement();
						if (fmaxState==null || this.getNumericValue(fmaxState)<this.getNumericValue(a.getMaxElement()))
							fmaxState= a.getMaxElement();

					}

				return new SimpleOpinion(
						this.getCreator(),
						SimpleOpinionService.this.getIdentifier(),
						fAgentsID,
						fMinDyn,
						fMaxDyn,
						this.getRepresentativeElement(fallRepStates),
						fDisp.getRepresentativeElement(), fminState, fmaxState,
						fInfosNumber);

			}

			/*
			 *
			 */

			@Override
			public int compareTo(final Information o) {
				return this.getRepresentativeElement().compareTo(o);
			}

			@Override
			public Double getNumericValue(final Information e) {
				return this.getRepresentativeElement().getNumericValue(e);
			}

			@Override
			public Information getRepresentativeElement(
					final Collection<? extends Information> elems) {
				return this.getRepresentativeElement().getRepresentativeElement(elems);
			}

			@Override
			public Information getRepresentativeElement(
					final Map<? extends Information, Double> elems) {
				return this.getRepresentativeElement().getRepresentativeElement(elems);
			}
			//
			// Primitives
			//

			@Override
			public String toString(){
				return "Opinion of "+this.creator+" about "+this.aggregatedAgents
				+"\n * representative state is "+this.getRepresentativeElement()
				+"\n * dispersion is "+OpinionDataBase.this.getGlobalInformationDeviation();
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