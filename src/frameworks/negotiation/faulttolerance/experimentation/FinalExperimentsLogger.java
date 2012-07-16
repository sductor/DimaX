package frameworks.negotiation.faulttolerance.experimentation;

import java.io.File;
import java.util.HashMap;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import frameworks.experimentation.ExperimentLogger;
import frameworks.experimentation.ObservingGlobalService;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

public class FinalExperimentsLogger implements ExperimentLogger  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7504393799165096196L;
	HashMap<String, StatisticalExperimentsLog> allExp=new HashMap<String, StatisticalExperimentsLog>();

	/* (non-Javadoc)
	 * @see negotiation.faulttolerance.experimentation.ExperimentLogger#addResults(negotiation.faulttolerance.experimentation.ReplicationObservingGlobalService)
	 */
	@Override
	public void addAndWriteResults(final ObservingGlobalService og, final File f){
		assert og instanceof ReplicationObservingGlobalService;
		final ReplicationObservingGlobalService rog = (ReplicationObservingGlobalService) og;
		final IndivdualExperiementLog iel = new IndivdualExperiementLog(rog);
		if (this.allExp.containsKey(iel.getId())){
			this.allExp.get(iel.getId()).add(iel);
		} else {
			this.allExp.put(iel.getId(), new StatisticalExperimentsLog(rog));
		}
		LogService.logOnFile(f, this.allExp.get(iel.getId()).toString(), true, false);
	}




	class StatisticalExperimentsLog extends IndivdualExperiementLog{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1586964448679790929L;
		int nbOfExperiements = 0;

		public StatisticalExperimentsLog(final ReplicationObservingGlobalService rog) {
			super(rog);
			this.nbOfExperiements++;
		}


		void add(final IndivdualExperiementLog iel){
			this.nbOfExperiements++;

			assert this.getId().equals(iel.getId());
			assert this.getNbAgent()==iel.getNbAgent();
			assert this.getNbHost()==iel.getNbHost();

			this.minState+=iel.getMinState();
			this.aveState+=iel.getAveState();
			this.maxState+=iel.getMaxState();

			this.minStab+=iel.getMinStab();
			this.aveStab+=iel.getAveStab();
			this.maxStab+=iel.getMaxStab();

			if (!this.socChoice.equals(iel.getSocChoice()))
			{
				this.socChoice=SocialChoiceType.Null;
				//			assert this.socChoice.equals(iel.getSocChoice());
			}

			this.minValue+=iel.getMinValue();
			this.nashValue+=iel.getNashValue();
			this.utilValue+=iel.getUtilValue();

			assert this.simultaneousCandidature==iel.getSimultaneousCandidature();
			assert this.simultaneousAcceptation==iel.getSimultaneousAcceptation();
			assert this.maxComputingTime==iel.getMaxComputingTime();
			assert this.time==iel.getTime();
		}

		@Override
		public double getMinState() {
			return super.getMinState()/this.nbOfExperiements;
		}

		@Override
		public double getAveState() {
			return super.getAveState()/this.nbOfExperiements;
		}

		@Override
		public double getMaxState() {
			return super.getMaxState()/this.nbOfExperiements;
		}

		@Override
		public double getMinStab() {
			return super. getMinStab()/this.nbOfExperiements;
		}

		@Override
		public double getAveStab() {
			return super.getAveStab()/this.nbOfExperiements;
		}

		@Override
		public double getMaxStab() {
			return super.getMaxStab()/this.nbOfExperiements;
		}

		@Override
		public double getMinValue() {
			return super.getMinValue()/this.nbOfExperiements;
		}

		@Override
		public double getNashValue() {
			return super.getNashValue()/this.nbOfExperiements;
		}

		@Override
		public double getUtilValue() {
			return super.getUtilValue()/this.nbOfExperiements;
		}

		//
		//
		//

		@Override
		public String entete() {
			return super.entete()+" ; nbOfExperiements";
		}

		@Override
		public String toString(){
			return super.toString()+" ; "+this.nbOfExperiements;
		}


	}
}

class IndivdualExperiementLog implements DimaComponentInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3714333911180563900L;
	//entry parameters
	protected int nbAgent;
	protected int nbHost;

	protected double hostCapacityMean;

	protected SocialChoiceType socChoice;

	protected  int simultaneousCandidature;
	protected  int simultaneousAcceptation;
	protected  long maxComputingTime;

	protected long time;

	//result parameters
	protected double minState;
	protected double aveState;
	protected double maxState;

	protected double minStab;
	protected double aveStab;
	protected double maxStab;

	protected double minValue;
	protected double nashValue;
	protected double utilValue;



	public IndivdualExperiementLog(final ReplicationObservingGlobalService rog){
		this(
				rog.getSimulationParameters().nbAgents,
				rog.getSimulationParameters().nbHosts,
				rog.getSimulationParameters().hostCapacityMean,
				rog.nbOfStateModif.getMinElement(),
				rog.nbOfStateModif.getRepresentativeElement(),
				rog.nbOfStateModif.getMaxElement(),
				rog.lastReplicationtime.getMinElement(),
				rog.lastReplicationtime.getRepresentativeElement(),
				rog.lastReplicationtime.getMaxElement(),
				rog.getSimulationParameters()._socialWelfare,
				rog.getMinWelfare(),
				rog.getNashWelfare(),
				rog.getUtilWelfare(),
				rog.getSimulationParameters().simultaneousCandidature,
				rog.getSimulationParameters().simultaneousAcceptation,
				rog.getSimulationParameters().maxComputingTime,
				rog.getSimulationParameters()._maxSimulationTime);
	}

	private IndivdualExperiementLog(final int nbAgent, final int nbHost, final double hostCapacityMean,
			final double minState, final double aveState, final double maxState,
			final double minStab, final double aveStab, final double maxStab,
			final SocialChoiceType socChoice, final double minValue, final double nashValue,
			final double utilValue, final int simultaneousCandidature,
			final int simultaneousAcceptation, final long maxComputingTime, final long time) {
		super();
		this.nbAgent = nbAgent;
		this.nbHost = nbHost;
		this.hostCapacityMean=hostCapacityMean;
		this.minState = minState;
		this.aveState = aveState;
		this.maxState = maxState;
		this.minStab = minStab;
		this.aveStab = aveStab;
		this.maxStab = maxStab;
		this.socChoice = socChoice;
		this.minValue = minValue;
		this.nashValue = nashValue;
		this.utilValue = utilValue;
		this.simultaneousCandidature = simultaneousCandidature;
		this.simultaneousAcceptation = simultaneousAcceptation;
		this.maxComputingTime = maxComputingTime;
		this.time = time;
	}

	public int getNbAgent() {
		return this.nbAgent;
	}

	public int getNbHost() {
		return this.nbHost;
	}

	public double getHostCapacityMean() {
		return this.hostCapacityMean;
	}

	public double getMinState() {
		return this.minState;
	}


	public double getAveState() {
		return this.aveState;
	}

	public double getMaxState() {
		return this.maxState;
	}

	public double getMinStab() {
		return this.minStab;
	}

	public double getAveStab() {
		return this.aveStab;
	}

	public double getMaxStab() {
		return this.maxStab;
	}

	public SocialChoiceType getSocChoice() {
		return this.socChoice;
	}

	public double getMinValue() {
		return this.minValue;
	}

	public double getNashValue() {
		return this.nashValue;
	}

	public double getUtilValue() {
		return this.utilValue;
	}

	public int getSimultaneousCandidature() {
		return this.simultaneousCandidature;
	}

	public int getSimultaneousAcceptation() {
		return this.simultaneousAcceptation;
	}

	public long getMaxComputingTime() {
		return this.maxComputingTime;
	}

	public long getTime() {
		return this.time;
	}

	/*
	 * 
	 */

	public String entete() {
		return "nbAgent ; nbHost ; instance size ; agent number	 ; hostCapacityMean ; hostCapacityPercent" +
				"minState ; aveState ; maxState ;	" +
				"minStab ; aveStab ; maxStab ; " +
				"minValue ; nashValue ; utilValue ; socChoice ; " +
				"simultaneousCandidature ; simultaneousAcceptation ; maxComputingTime ; " +
				"time ; id";
	}

	@Override
	public String toString(){
		final String result =	this.getNbAgent()+" ; "+this.getNbHost()+" ; "+ this.getNbAgent()*this.nbHost+" ; "+( this.getNbAgent()+this.getNbHost())+" ; "+
				this.getHostCapacityMean()+" ; "+this.getHostCapacityMean()/this.nbAgent+" ; "+
				this.getMinState()+" ; "+this.getAveState()+" ; "+this.getMaxState()+" ; "+
				this.getMinStab()+" ; "+this.getAveStab()+" ; "+this.getMaxStab()+" ; "+
				this.getMinValue()+" ; "+this.getNashValue()+" ; "+this.getUtilValue()+" ; "+this.getSocChoice()+" ; "+
				this.getSimultaneousCandidature()+" ; "+this.getSimultaneousAcceptation()+" ; "+this.getMaxComputingTime()+" ; "+
				+this.getTime()+" ; "+this.getId();
		//		result=result.replaceAll(".", ",");
		return result;
	}

	/*
	 * 
	 */

	public String getId() {
		return this.nbAgent+" AGENTS "+this.hostCapacityMean+" CAPACITY ";
	}

	@Override
	public int hashCode(){
		return (int) (this.nbAgent*1000+this.hostCapacityMean);
	}

	@Override
	public boolean equals(final Object o){
		if (o instanceof IndivdualExperiementLog){
			final IndivdualExperiementLog that = (IndivdualExperiementLog) o;
			return this.nbAgent==that.nbAgent && this.nbHost==that.nbHost && this.hostCapacityMean==that.hostCapacityMean;
		}else {
			return false;
		}
	}

}