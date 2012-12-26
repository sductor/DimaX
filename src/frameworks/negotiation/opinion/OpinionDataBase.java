package frameworks.negotiation.opinion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.modules.aggregator.FunctionalDispersionAgregator;
import dima.introspectionbasedagents.modules.aggregator.HeavyDoubleAggregation;
import dima.introspectionbasedagents.modules.aggregator.HeavyParametredAggregation;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.information.SimpleInformationDataBase;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.opinion.OpinionService.Opinion;

public class OpinionDataBase<Info extends Information>
extends SimpleInformationDataBase<Info>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4445640243087103650L;
	private final AgentIdentifier myAgent;
	private final OpinionHandler<Info> myOpinionHandler;


	private HashMap<AgentIdentifier, SimpleOpinion> receivedOpinions;
	private HeavyParametredAggregation<Info> globalMeanOpinions;
	private TreeSet<IdentifiedInfo> globalMinMaxinfo;
	
	
	private HashMap<AgentIdentifier,InfoDynamicity> localInfoDynamicity;
	
	private HeavyDoubleAggregation globalOpinionDispersion;
	private Long globalInfoMaxDynamicity=Long.MIN_VALUE;
	private Long globalInfoMinDynamicity=Long.MAX_VALUE;

	public OpinionDataBase(final AgentIdentifier myAgent,
			final OpinionHandler<Info> myOpinionHandler) {
		super();
		assert myOpinionHandler!=null;
		this.myAgent = myAgent;
		this.myOpinionHandler = myOpinionHandler;

		this.localInfoDynamicity = new HashMap<AgentIdentifier,InfoDynamicity>();

		this.receivedOpinions=new HashMap<AgentIdentifier, SimpleOpinion>();
		this.globalMeanOpinions = new HeavyParametredAggregation<Info>(myOpinionHandler,new Comparator<Info>() {
			@Override
			public int compare(final Info o1, final Info o2) {
				return OpinionDataBase.this.myOpinionHandler.getNumericValue(o1).compareTo(
						OpinionDataBase.this.myOpinionHandler.getNumericValue(o2));
			}
		});
		this.globalOpinionDispersion = new HeavyDoubleAggregation();
		this.globalMinMaxinfo = new TreeSet<IdentifiedInfo>(new Comparator<IdentifiedInfo>() {
			@Override
			public int compare(final IdentifiedInfo o1, final IdentifiedInfo o2) {
				return OpinionDataBase.this.myOpinionHandler.getNumericValue(o1.getMyInfo()).compareTo(
						OpinionDataBase.this.myOpinionHandler.getNumericValue(o2.getMyInfo()));
			}
		});
	}

	public Opinion<Info> getGlobalOpinion()
			throws NoInformationAvailableException{
		this.clean();
		try {
			this.updateMyOpinion();
		} catch (final NoInformationAvailableException e){/*do nothing*/}
		final Collection<AgentIdentifier> aggAgents = new ArrayList<AgentIdentifier>(this.getAgents());
		aggAgents.addAll(this.receivedOpinions.keySet());
		return new SimpleOpinion(
				this.myAgent,
				aggAgents,
				this.getGlobalMinInfo(),
				this.getGlobalMeanInfo(),
				this.getGlobalMaxInfo(),
				this.getGlobalOpinionDispersion(),
				this.getGlobalMinInfoDynamicity(),
				this.getGlobalMaxInfoDynamicity());
	}

	@Override
	public Info add(final Info i){
		//Min & Max
		this.globalMinMaxinfo.remove(new IdentifiedInfo(i));
		this.globalMinMaxinfo.add(new IdentifiedInfo(i));

		assert verifyStructure():i+"\n ----> "+myAgent;
		//Local Dynamicity
		if (this.localInfoDynamicity.containsKey(i.getMyAgentIdentifier())) {
			this.localInfoDynamicity.get(i.getMyAgentIdentifier()).update(i);
		} else {
			this.localInfoDynamicity.put(i.getMyAgentIdentifier(),new InfoDynamicity(i));
		}
		assert this.localInfoDynamicity.get(i.getMyAgentIdentifier())!=null;
		//Update global Dynamicity
		this.globalInfoMaxDynamicity=Math.max(
				this.globalInfoMaxDynamicity,
				this.localInfoDynamicity.get(i.getMyAgentIdentifier()).getLastInfoDynamicity());
		this.globalInfoMinDynamicity=Math.min(
				this.globalInfoMaxDynamicity,
				this.localInfoDynamicity.get(i.getMyAgentIdentifier()).getLastInfoDynamicity());

		//Information
		return super.add(i);
	}

	public void addOpinion(final SimpleOpinion o){
		this.receivedOpinions.put(o.getMyAgentIdentifier(), o);
		//Opinion
		assert o.getMeanInfo()!=null;
		this.globalMeanOpinions.add(o.getMeanInfo());
		//Dispersion
		this.globalOpinionDispersion.put(
				o.getOpinionDispersion(),
				new Double(o.getAggregatedAgents().size()));
		//Min & max
		this.globalMinMaxinfo.remove(new IdentifiedInfo(o.getMinInfo()));
		this.globalMinMaxinfo.remove(new IdentifiedInfo(o.getMaxInfo()));
		this.globalMinMaxinfo.add(new IdentifiedInfo(o.getMinInfo()));
		this.globalMinMaxinfo.add(new IdentifiedInfo(o.getMaxInfo()));
		//Dynamicity
		this.globalInfoMaxDynamicity=Math.max(
				this.globalInfoMaxDynamicity,
				o.getMaxInformationDynamicity());
		this.globalInfoMinDynamicity=Math.min(
				this.globalInfoMaxDynamicity,
				o.getMinInformationDynamicity());
	}

	/*
	 * 
	 */

	private void clean() {
		//Opinion
		final Iterator<AgentIdentifier> opIt = this.receivedOpinions.keySet().iterator();
		while (opIt.hasNext()){
			final AgentIdentifier i = opIt.next();
			if (this.estObsolete(this.receivedOpinions.get(i).getMeanInfo())){
				this.globalMeanOpinions.remove(this.receivedOpinions.get(i));
				this.globalOpinionDispersion.remove(this.receivedOpinions.get(i).getOpinionDispersion());
				if (this.globalInfoMaxDynamicity.equals(this.receivedOpinions.get(i).getMaxInformationDynamicity())) {
					this.globalInfoMaxDynamicity=null;
				}
				if (this.globalInfoMinDynamicity.equals(this.receivedOpinions.get(i).getMinInformationDynamicity())) {
					this.globalInfoMinDynamicity=null;
				}
				opIt.remove();
			}
		}
		//Min & Max
		 
//		assert verifyStructure();
		final Iterator<IdentifiedInfo> mmIt = this.globalMinMaxinfo.iterator();
		while (mmIt.hasNext()){
			final Info i = mmIt.next().getMyInfo();
			if (this.estObsolete(i)) {
				assert !i.getMyAgentIdentifier().equals(myAgent);
				mmIt.remove();
			}
		}

//		assert verifyStructure();
		//Info & Dynami
		//		for (Info i : super.get(my))//TODO
	}

	private void updateMyOpinion() throws NoInformationAvailableException {
		if (this.receivedOpinions.containsKey(this.myAgent)) {
			this.globalMeanOpinions.remove(this.receivedOpinions.get(this.myAgent));
		}
		this.addOpinion(
				new SimpleOpinion(
						this.myAgent,
						this.getAgents(),
						this.getLocalMinInfo(),
						this.getLocalMeanInfo(),
						this.getLocalMaxInfo(),
						this.getLocalOpinionDispersion(),
						this.getLocalMinInfoDynamicity(),
						this.getLocalMaxInfoDynamicity()));
	}

	private boolean estObsolete(final Info i){
		return !i.getMyAgentIdentifier().equals(myAgent) && (i.getUptime()>getGlobalMaxInfoDynamicity());//false;//
	}

	/*
	 * Local information
	 */

	private Info getLocalMeanInfo() {
		return this.myOpinionHandler.getRepresentativeElement(this.values());
	}

	private Double getLocalOpinionDispersion() {
		return FunctionalDispersionAgregator.getVariationCoefficient(this.myOpinionHandler, this.values());
	}

	private Info getLocalMinInfo() throws NoInformationAvailableException {
		return this.getGlobalMinInfo();
	}

	private Info getLocalMaxInfo() throws NoInformationAvailableException {
		return this.getGlobalMaxInfo();
	}

	private Long getLocalMinInfoDynamicity() {
		return this.getGlobalMinInfoDynamicity();
	}

	private Long getLocalMaxInfoDynamicity() {
		return this.getGlobalMaxInfoDynamicity();
	}

	/*
	 * 
	 */

	private Info getGlobalMeanInfo() {
		return this.globalMeanOpinions.getRepresentativeElement();
	}

	private Double getGlobalOpinionDispersion() {
		return this.globalOpinionDispersion.getRepresentativeElement();
	}

	private Info getGlobalMinInfo() throws NoInformationAvailableException {
		if (!this.globalMinMaxinfo.isEmpty()) {
			return this.globalMinMaxinfo.first().getMyInfo();
		} else {
			throw new NoInformationAvailableException();
		}
	}

	private Info getGlobalMaxInfo() throws NoInformationAvailableException {
		if (!this.globalMinMaxinfo.isEmpty()) {
			return this.globalMinMaxinfo.last().getMyInfo();
		} else {
			throw new NoInformationAvailableException();
		}
	}

	private Long getGlobalMinInfoDynamicity() {
		return this.globalInfoMinDynamicity;
	}

	private Long getGlobalMaxInfoDynamicity() {
//		if (this.globalInfoMaxDynamicity.equals(Long.MIN_VALUE)) {
//			return Long.MAX_VALUE;
//		} else {
			return Math.max(NegotiationParameters.ammortissementDynamiciteOpinion, this.globalInfoMaxDynamicity);
//		}
	}

	private boolean verifyStructure(){
		if (ReplicationInstanceGraph.isRessource(myAgent))
			return true;
		
		final Iterator<IdentifiedInfo> mmIt = this.globalMinMaxinfo.iterator();
		assert !this.globalMinMaxinfo.isEmpty();
		while (mmIt.hasNext()){
			if (mmIt.next().getMyInfo().getMyAgentIdentifier().equals(myAgent)){
				return true;
			}
		}		
		return false;
	}
	
	//
	// Subclasses
	//

	public class SimpleOpinion  implements Opinion<Info>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7189805009370149904L;

		private final AgentIdentifier myAgentIdentifier;

		private final Collection<AgentIdentifier> aggregatedAgents;

		private final Date creationTime;

		private final Info minInfo;
		private final Info meanInfo;
		private final Info maxInfo;

		private final Double opinionDispersion;
		private final Long minInformationDynamicity;
		private final Long maxInformationDynamicity;

		private SimpleOpinion(
				final AgentIdentifier myAgentIdentifier,
				final Collection<AgentIdentifier> aggregatedAgents,
				final Info minInfo, final Info meanInfo, final Info maxInfo,
				final Double opinionDispersion,
				final Long minInformationDynamicity,
				final Long maxInformationDynamicity) {
			super();
			assert minInfo!=null;
			assert meanInfo!=null;
			assert maxInfo!=null;
			assert opinionDispersion!=null;
			assert minInformationDynamicity!=null;
			assert minInformationDynamicity!=null;
			assert aggregatedAgents!=null;
			this.myAgentIdentifier = myAgentIdentifier;
			this.aggregatedAgents = aggregatedAgents;
			this.creationTime = new Date();
			this.minInfo = minInfo;
			this.meanInfo = meanInfo;
			this.maxInfo = maxInfo;
			this.opinionDispersion = opinionDispersion;
			this.minInformationDynamicity = minInformationDynamicity;
			this.maxInformationDynamicity = maxInformationDynamicity;
		}

		@Override
		public Info getMinInfo() {
			return this.minInfo;
		}
		@Override
		public Info getMeanInfo() {
			return this.meanInfo;
		}
		@Override
		public Info getMaxInfo() {
			return this.maxInfo;
		}
		@Override
		public Double getOpinionDispersion() {
			return this.opinionDispersion;
		}
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
		public Collection getAggregatedAgents() {
			return this.aggregatedAgents;
		}
		@Override
		public boolean isCertain() {
			return this.aggregatedAgents.size()<=1;
		}

		/*
		 *
		 */

		@Override
		public AgentIdentifier getMyAgentIdentifier() {
			return null;
		}

		@Override
		public long getUptime() {
			return new Date().getTime()-this.creationTime.getTime();
		}

		@Override
		public Long getCreationTime() {
			return this.creationTime.getTime();
		}

		@Override
		public int isNewerThan(final Information that) {
			// TODO Auto-generated method stub
			throw new RuntimeException();
		}

		/*
		 * 
		 */

		@Override
		public boolean equals(final Object o){
			if (o instanceof OpinionDataBase.SimpleOpinion) {
				return ((OpinionDataBase.SimpleOpinion) o).getMyAgentIdentifier().equals(this.getMyAgentIdentifier());
			} else {
				return false;
			}
		}

		public int hashcode(){
			return this.getMyAgentIdentifier().hashCode();
		}

		@Override
		public String toString(){
			return "Opinion of "+this.myAgentIdentifier+" with "+this.aggregatedAgents.size()+" ("+this.aggregatedAgents+") " +
					"\n --> min info"+this.minInfo +
					"\n --> mean info"+this.meanInfo +
					"\n --> max info"+this.maxInfo;
		}
	}

	private class InfoDynamicity implements Comparable<InfoDynamicity>, DimaComponentInterface{
		private static final long serialVersionUID = 6659167432850630911L;

		private Long infoDynamicity = Long.MAX_VALUE;
		private long uptime;
		private final AgentIdentifier id;

		public InfoDynamicity(final Information myInfo) {
			this.uptime = myInfo.getUptime();
			this.id = myInfo.getMyAgentIdentifier();
		}

		public Long getLastInfoDynamicity() {
			return this.infoDynamicity;
		}

		public Long update(final Information s) {
			if (!s.getMyAgentIdentifier().equals(this.id)) {
				throw new RuntimeException("arghhhhh!");
			}

			final Long previous = this.infoDynamicity;
			this.infoDynamicity = s.getUptime() - this.uptime;
			this.uptime = s.getUptime();

			return previous;
		}

		@Override
		public int compareTo(final InfoDynamicity that) {
			return this.infoDynamicity.compareTo(that.infoDynamicity);
		}

		@Override
		public boolean equals(final Object o) {
			throw new RuntimeException();
			//			return this.id.equals(o);

		}

		@Override
		public int hashCode() {
			return this.id.hashCode();
		}
	}

	//Permet de surcharger equals et hashcode
	public class IdentifiedInfo implements DimaComponentInterface {

		/**
		 * 
		 */
		private static final long serialVersionUID = -57750939693336413L;
		public final Info myInfo;

		public IdentifiedInfo(final Info myInfo) {
			super();
			this.myInfo = myInfo;
		}

		public Info getMyInfo() {
			return this.myInfo;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof OpinionDataBase.IdentifiedInfo) {
				return this.getMyInfo().getMyAgentIdentifier().equals(((IdentifiedInfo) o).getMyInfo().getMyAgentIdentifier());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.myInfo.getMyAgentIdentifier().hashCode();
		}

		@Override
		public String toString(){
			return myInfo.toString();
		}
	}
}

