package frameworks.negotiation.opinion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.sound.midi.SysexMessage;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.AbstractMinMaxAggregation;
import dima.introspectionbasedagents.modules.aggregator.FunctionalDispersionAgregator;
import dima.introspectionbasedagents.modules.aggregator.HeavyDoubleAggregation;
import dima.introspectionbasedagents.modules.aggregator.HeavyParametredAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightWeightedAverageDoubleAggregation;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleInformationDataBase;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.opinion.OpinionService.Opinion;

public class OpinionDataBase<Info extends Information>
extends SimpleInformationDataBase<Info>{

	private final AgentIdentifier myAgent;
	private final OpinionHandler<Info> myOpinionHandler;

	private HashMap<AgentIdentifier,InfoDynamicity> localInfoDynamicity;

	private HashMap<AgentIdentifier, SimpleOpinion> receivedOpinions;
	private HeavyParametredAggregation<Info> globalMeanOpinions;	
	private HeavyDoubleAggregation globalOpinionDispersion;
	private TreeSet<IdentifiedInfo> globalMinMaxinfo;
	private Long globalInfoMaxDynamicity=Long.MIN_VALUE;
	private Long globalInfoMinDynamicity=Long.MAX_VALUE;

	public OpinionDataBase(AgentIdentifier myAgent,
			OpinionHandler<Info> myOpinionHandler) {
		super();
		assert myOpinionHandler!=null;
		this.myAgent = myAgent;
		this.myOpinionHandler = myOpinionHandler;

		this.localInfoDynamicity = new HashMap<AgentIdentifier,InfoDynamicity>();

		this.receivedOpinions=new HashMap<AgentIdentifier, SimpleOpinion>();
		this.globalMeanOpinions = new HeavyParametredAggregation<Info>(myOpinionHandler,new Comparator<Info>() {
			@Override
			public int compare(Info o1, Info o2) {
				return OpinionDataBase.this.myOpinionHandler.getNumericValue(o1).compareTo(
						OpinionDataBase.this.myOpinionHandler.getNumericValue(o2));
			}
		});
		this.globalOpinionDispersion = new HeavyDoubleAggregation();
		this.globalMinMaxinfo = new TreeSet<IdentifiedInfo>(new Comparator<IdentifiedInfo>() {
			@Override
			public int compare(IdentifiedInfo o1, IdentifiedInfo o2) {
				return OpinionDataBase.this.myOpinionHandler.getNumericValue(o1.getMyInfo()).compareTo(
						OpinionDataBase.this.myOpinionHandler.getNumericValue(o2.getMyInfo()));
			}
		});
	}

	public Opinion<Info> getGlobalOpinion() 
			throws NoInformationAvailableException{
		clean();
		try {
			updateMyOpinion();
		} catch (NoInformationAvailableException e){/*do nothing*/}
		Collection<AgentIdentifier> aggAgents = new ArrayList<AgentIdentifier>(getAgents());
		aggAgents.addAll(this.receivedOpinions.keySet());
		return new SimpleOpinion(
				myAgent,
				aggAgents, 
				this.getGlobalMinInfo(),
				this.getGlobalMeanInfo(),
				this.getGlobalMaxInfo(),
				this.getGlobalOpinionDispersion(),
				this.getGlobalMinInfoDynamicity(),
				this.getGlobalMaxInfoDynamicity());
	}	

	@Override
	public Info add(Info i){
		//Min & Max
		globalMinMaxinfo.remove(new IdentifiedInfo(i));
		globalMinMaxinfo.add(new IdentifiedInfo(i));
		//Local Dynamicity
		if (localInfoDynamicity.containsKey(i.getMyAgentIdentifier()))
			localInfoDynamicity.get(i.getMyAgentIdentifier()).update(i);
		else
			localInfoDynamicity.put(i.getMyAgentIdentifier(),new InfoDynamicity(i));
		assert localInfoDynamicity.get(i.getMyAgentIdentifier())!=null;
		//Update global Dynamicity
		globalInfoMaxDynamicity=Math.max(
				globalInfoMaxDynamicity, 
				localInfoDynamicity.get(i.getMyAgentIdentifier()).getLastInfoDynamicity());
		globalInfoMinDynamicity=Math.min(
				globalInfoMaxDynamicity, 
				localInfoDynamicity.get(i.getMyAgentIdentifier()).getLastInfoDynamicity());

		//Information
		return super.add(i);
	}

	public void addOpinion(SimpleOpinion o){
		receivedOpinions.put(o.getMyAgentIdentifier(), o);
		//Opinion
		globalMeanOpinions.add(o.getMeanInfo());
		//Dispersion
		globalOpinionDispersion.put(
				o.getOpinionDispersion(),
				new Double(o.getAggregatedAgents().size()));
		//Min & max
		globalMinMaxinfo.remove(new IdentifiedInfo(o.getMinInfo()));
		globalMinMaxinfo.remove(new IdentifiedInfo(o.getMaxInfo()));
		globalMinMaxinfo.add(new IdentifiedInfo(o.getMinInfo()));
		globalMinMaxinfo.add(new IdentifiedInfo(o.getMaxInfo()));
		//Dynamicity
		globalInfoMaxDynamicity=Math.max(
				globalInfoMaxDynamicity, 
				o.getMaxInformationDynamicity());
		globalInfoMinDynamicity=Math.min(
				globalInfoMaxDynamicity, 
				o.getMinInformationDynamicity());
	}

	/*
	 * 
	 */

	private void clean() {
		//Opinion
		Iterator<AgentIdentifier> opIt = receivedOpinions.keySet().iterator();
		while (opIt.hasNext()){
			AgentIdentifier i = opIt.next();
			if (this.estObsolete(receivedOpinions.get(i).getMeanInfo())){
				opIt.remove();
				globalMeanOpinions.remove(receivedOpinions.get(i));
				globalOpinionDispersion.remove(receivedOpinions.get(i).getOpinionDispersion());
				if (globalInfoMaxDynamicity.equals(receivedOpinions.get(i).getMaxInformationDynamicity()))
					globalInfoMaxDynamicity=null;
				if (globalInfoMinDynamicity.equals(receivedOpinions.get(i).getMinInformationDynamicity()))
					globalInfoMinDynamicity=null;
			}
		}
		//Min & Max
		Iterator<IdentifiedInfo> mmIt = globalMinMaxinfo.iterator();
		while (mmIt.hasNext()){
			Info i = mmIt.next().getMyInfo();
			if (this.estObsolete(i))
				mmIt.remove();
		}
		//Info & Dynami
//		for (Info i : super.get(my))//TODO
	}

	private void updateMyOpinion() throws NoInformationAvailableException {
		if (receivedOpinions.containsKey(myAgent))
			globalMeanOpinions.remove(receivedOpinions.get(myAgent));
		addOpinion(
				new SimpleOpinion(
						myAgent,
						getAgents(), 
						this.getLocalMinInfo(),
						this.getLocalMeanInfo(),
						this.getLocalMaxInfo(),
						this.getLocalOpinionDispersion(),
						this.getLocalMinInfoDynamicity(),
						this.getLocalMaxInfoDynamicity()));		
	}

	private boolean estObsolete(Info i){
		return false;//!i.getMyAgentIdentifier().equals(myAgent) && (i.getUptime()>getGlobalMaxInfoDynamicity());
	}

	/*
	 * Local information
	 */

	private Info getLocalMeanInfo() {
		return myOpinionHandler.getRepresentativeElement(this.values());
	}

	private Double getLocalOpinionDispersion() {
		return FunctionalDispersionAgregator.getVariationCoefficient(myOpinionHandler, this.values());
	}

	private Info getLocalMinInfo() throws NoInformationAvailableException {
		return getGlobalMinInfo();
	}

	private Info getLocalMaxInfo() throws NoInformationAvailableException {
		return getGlobalMaxInfo();
	}

	private Long getLocalMinInfoDynamicity() {
		return getGlobalMinInfoDynamicity();
	}

	private Long getLocalMaxInfoDynamicity() {
		return getGlobalMaxInfoDynamicity();
	}

	/*
	 * 
	 */

	private Info getGlobalMeanInfo() {
		return globalMeanOpinions.getRepresentativeElement();
	}

	private Double getGlobalOpinionDispersion() {
		return globalOpinionDispersion.getRepresentativeElement();
	}

	private Info getGlobalMinInfo() throws NoInformationAvailableException {
		if (!globalMinMaxinfo.isEmpty())
			return globalMinMaxinfo.first().getMyInfo();
		else
			throw new NoInformationAvailableException();
	}

	private Info getGlobalMaxInfo() throws NoInformationAvailableException {
		if (!globalMinMaxinfo.isEmpty())
			return globalMinMaxinfo.last().getMyInfo();
		else
			throw new NoInformationAvailableException();
	}

	private Long getGlobalMinInfoDynamicity() {
		return globalInfoMinDynamicity;
	}

	private Long getGlobalMaxInfoDynamicity() {
		if (globalInfoMaxDynamicity.equals(Long.MIN_VALUE))
			return Long.MAX_VALUE;
		else
			return Math.min(4*NegotiationParameters._timeToCollect, globalInfoMaxDynamicity);
	}

	//
	// Subclasses
	//

	public class SimpleOpinion  implements Opinion<Info>{

		private final AgentIdentifier myAgentIdentifier;

		private final Collection<AgentIdentifier> aggregatedAgents;

		private Date creationTime;

		private final Info minInfo;
		private final Info meanInfo;
		private final Info maxInfo;

		private final Double opinionDispersion;
		private final Long minInformationDynamicity;
		private final Long maxInformationDynamicity;

		private SimpleOpinion(
				AgentIdentifier myAgentIdentifier,
				Collection<AgentIdentifier> aggregatedAgents,
				Info minInfo, Info meanInfo, Info maxInfo,
				Double opinionDispersion, 
				Long minInformationDynamicity,
				Long maxInformationDynamicity) {
			super();
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
			return minInfo;
		}
		@Override
		public Info getMeanInfo() {
			return meanInfo;
		}
		@Override
		public Info getMaxInfo() {
			return maxInfo;
		}
		@Override
		public Double getOpinionDispersion() {
			return opinionDispersion;
		}
		@Override
		public Long getMinInformationDynamicity() {
			return minInformationDynamicity;
		}
		@Override
		public Long getMaxInformationDynamicity() {
			return maxInformationDynamicity;
		}
		

		/*
		 * 
		 */

		@Override
		public Collection getAggregatedAgents() {
			return aggregatedAgents;
		}
		@Override
		public boolean isCertain() {
			return aggregatedAgents.size()<=1;
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
			return new Date().getTime()-creationTime.getTime();
		}

		@Override
		public Long getCreationTime() {
			return creationTime.getTime();
		}

		@Override
		public int isNewerThan(Information that) {
			// TODO Auto-generated method stub
			throw new RuntimeException();
		}

		/*
		 * 
		 */

		public boolean equals(Object o){
			if (o instanceof OpinionDataBase.SimpleOpinion)
				return ((OpinionDataBase.SimpleOpinion) o).getMyAgentIdentifier().equals(this.getMyAgentIdentifier());
			else
				return false;
		}

		public int hashcode(){
			return getMyAgentIdentifier().hashCode();
		}

		public String toString(){
			return "Opinion of "+myAgentIdentifier+" with "+aggregatedAgents.size()+" ("+aggregatedAgents+") " +
					"\n --> min info"+minInfo +
					"\n --> mean info"+meanInfo +
					"\n --> max info"+maxInfo;
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

	public class IdentifiedInfo implements DimaComponentInterface {

		public final Info myInfo;

		public IdentifiedInfo(Info myInfo) {
			super();
			this.myInfo = myInfo;
		}

		public Info getMyInfo() {
			return myInfo;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof OpinionDataBase.IdentifiedInfo)
				return this.getMyInfo().getMyAgentIdentifier().equals(((IdentifiedInfo) o).getMyInfo().getMyAgentIdentifier());
			else
				return false;
		}

		@Override
		public int hashCode() {
			return this.myInfo.getMyAgentIdentifier().hashCode();
		}

	}
}

