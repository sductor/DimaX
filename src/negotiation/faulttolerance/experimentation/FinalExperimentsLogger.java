package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentLogger;
import dimaxx.experimentation.ObservingGlobalService;

import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

public class FinalExperimentsLogger implements ExperimentLogger  {

	HashMap<String, StatisticalExperimentsLog> allExp=new HashMap<String, StatisticalExperimentsLog>();

	/* (non-Javadoc)
	 * @see negotiation.faulttolerance.experimentation.ExperimentLogger#addResults(negotiation.faulttolerance.experimentation.ReplicationObservingGlobalService)
	 */
	@Override
	public void addResults(ObservingGlobalService og){
		assert og instanceof ReplicationObservingGlobalService;
		ReplicationObservingGlobalService rog = (ReplicationObservingGlobalService) og;
		IndivdualExperiementLog iel = new IndivdualExperiementLog(rog);
		if (allExp.containsKey(iel.getId())){
			allExp.get(iel.getId()).add(iel);
		} else {
			allExp.put(iel.getId(), new StatisticalExperimentsLog(rog));
		}
	}


	/* (non-Javadoc)
	 * @see negotiation.faulttolerance.experimentation.ExperimentLogger#write(java.io.File)
	 */
	@Override
	public void write(File f){
		List<StatisticalExperimentsLog> all = new ArrayList<StatisticalExperimentsLog>(allExp.values());
		Collections.sort(all);
		String result = all.get(0).entete();
		for (StatisticalExperimentsLog sel : all){
			result+="\n"+sel.toString();
		}
		LogService.logOnFile(f, result, true, false);
	}



	class StatisticalExperimentsLog extends IndivdualExperiementLog{

		int nbOfExperiements = 0;

		public StatisticalExperimentsLog(ReplicationObservingGlobalService rog) {
			super(rog);
			nbOfExperiements++;
		}


		void add(IndivdualExperiementLog iel){
			nbOfExperiements++;

			assert this.getId().equals(iel.getId());
			assert this.getNbAgent()==iel.getNbAgent();
			assert this.getNbHost()==iel.getNbHost();

			minState+=iel.getMinState();
			aveState+=iel.getAveState();
			maxState+=iel.getMaxState();

			minStab+=iel.getMinStab();
			aveStab+=iel.getAveStab();
			maxStab+=iel.getMaxStab();


			assert this.socChoice.equals(iel.getSocChoice());

			minValue+=iel.getMinValue();
			nashValue+=iel.getNashValue();
			utilValue+=iel.getUtilValue();

			assert this.simultaneousCandidature==iel.getSimultaneousCandidature();
			assert this.simultaneousAcceptation==iel.getSimultaneousAcceptation();
			assert this.maxComputingTime==iel.getMaxComputingTime();
			assert this.time==iel.getTime();
		}

		@Override
		public double getMinState() {
			return super.getMinState()/nbOfExperiements;
		}

		@Override
		public double getAveState() {
			return super.getAveState()/nbOfExperiements;
		}

		@Override
		public double getMaxState() {
			return super.getMaxState()/nbOfExperiements;
		}

		@Override
		public double getMinStab() {
			return super. getMinStab()/nbOfExperiements;
		}

		@Override
		public double getAveStab() {
			return super.getAveStab()/nbOfExperiements;
		}

		@Override
		public double getMaxStab() {
			return super.getMaxStab()/nbOfExperiements;
		}

		@Override
		public double getMinValue() {
			return super.getMinValue()/nbOfExperiements;
		}

		@Override
		public double getNashValue() {
			return super.getNashValue()/nbOfExperiements;
		}

		@Override
		public double getUtilValue() {
			return super.getUtilValue()/nbOfExperiements;
		}	

		//
		//
		//

		@Override
		public String entete() {
			return super.entete()+" ; nbOfExperiements";
		}

		public String toString(){
			return super.toString()+" ; "+nbOfExperiements;
		}


	}	
}

class IndivdualExperiementLog implements Comparable<IndivdualExperiementLog>{

	protected String id;

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



	public IndivdualExperiementLog(ReplicationObservingGlobalService rog){
		this(
				rog.getSimulationParameters().getSimulationName(),
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

	private IndivdualExperiementLog(String id, int nbAgent, int nbHost, double hostCapacityMean,
			double minState, double aveState, double maxState,
			double minStab, double aveStab, double maxStab,
			SocialChoiceType socChoice, double minValue, double nashValue,
			double utilValue, int simultaneousCandidature,
			int simultaneousAcceptation, long maxComputingTime, long time) {
		super();
		this.id = id;
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

	public String getId() {
		return id;
	}



	public int getNbAgent() {
		return nbAgent;
	}

	public int getNbHost() {
		return nbHost;
	}

	public double getHostCapacityMean() {
		return hostCapacityMean;
	}

	public double getMinState() {
		return minState;
	}


	public double getAveState() {
		return aveState;
	}

	public double getMaxState() {
		return maxState;
	}

	public double getMinStab() {
		return minStab;
	}

	public double getAveStab() {
		return aveStab;
	}

	public double getMaxStab() {
		return maxStab;
	}

	public SocialChoiceType getSocChoice() {
		return socChoice;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getNashValue() {
		return nashValue;
	}

	public double getUtilValue() {
		return utilValue;
	}

	public int getSimultaneousCandidature() {
		return simultaneousCandidature;
	}

	public int getSimultaneousAcceptation() {
		return simultaneousAcceptation;
	}

	public long getMaxComputingTime() {
		return maxComputingTime;
	}

	public long getTime() {
		return time;
	}

	public String entete() {
		return "nbAgent ; nbHost ; instance size ; agent number	 ; hostCapacityMean ; hostCapacityPercent" +
				"minState ; aveState ; maxState ;	" +
				"minStab ; aveStab ; maxStab ; " +
				"minValue ; nashValue ; utilValue ; socChoice ; " +
				"simultaneousCandidature ; simultaneousAcceptation ; maxComputingTime ; " +
				"time ; id";
	}
	
	public int hashcode(){
		
	}

	@Override
	public int compareTo(IndivdualExperiementLog that) {
		double fixedResources=
				((double)ReplicationExperimentationParameters.startingNbAgents/
						(double)ReplicationExperimentationParameters.startingNbHosts);

		if (this.hostCapacityMean!=fixedResources && that.hostCapacityMean!=fixedResources){
			if (this.getHostCapacityMean()/this.nbAgent==that.getHostCapacityMean()/that.nbAgent){
				return this.getNbAgent()-that.getNbAgent();
			} else {
				return (int) ((this.getHostCapacityMean()/this.nbAgent)-(that.getHostCapacityMean()/that.nbAgent));
			}
		} else if (this.hostCapacityMean==fixedResources && that.hostCapacityMean==fixedResources){
			return this.getNbAgent()-that.getNbAgent();
		} else if  (this.hostCapacityMean!=fixedResources && that.hostCapacityMean==fixedResources){
			return -1;
		} else { //(this.hostCapacityMean==fixedResources && that.hostCapacityMean!=fixedResources){
			return 1;
		}
	}
	
	public String toString(){
		String result =	getNbAgent()+" ; "+getNbHost()+" ; "+ getNbAgent()*nbHost+" ; "+( getNbAgent()+getNbHost())+" ; "+
				getHostCapacityMean()+" ; "+getHostCapacityMean()/nbAgent+" ; "+
				getMinState()+" ; "+getAveState()+" ; "+getMaxState()+" ; "+
				getMinStab()+" ; "+getAveStab()+" ; "+getMaxStab()+" ; "+
				getMinValue()+" ; "+getNashValue()+" ; "+getUtilValue()+" ; "+getSocChoice()+" ; "+
				getSimultaneousCandidature()+" ; "+getSimultaneousAcceptation()+" ; "+getMaxComputingTime()+" ; "+
				+getTime()+" ; "+getId();
		//		result=result.replaceAll(".", ",");
		return result;
	}
}