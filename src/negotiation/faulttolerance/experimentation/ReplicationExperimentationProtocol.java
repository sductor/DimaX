package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import negotiation.negotiationframework.rationality.SocialChoiceFunctions;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationProtocol;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

public class ReplicationExperimentationProtocol extends
ExperimentationProtocol {
	private static final long serialVersionUID = 3221531706912973963L;


	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//

	static List<String> protos = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.getKey4mirrorproto(),
			ReplicationExperimentationProtocol.getKey4centralisedstatusproto(),
			ReplicationExperimentationProtocol.getKey4statusproto()});
	static List<String> welfare = Arrays.asList(new String[]{
			SocialChoiceFunctions.key4leximinSocialWelfare,
			SocialChoiceFunctions.key4NashSocialWelfare,
			SocialChoiceFunctions.key4UtilitaristSocialWelfare});
	static List<String> select = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.getKey4greedyselect(),
			ReplicationExperimentationProtocol.getKey4roulettewheelselect()});//,key4AllocSelect
	static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Max});
	static List<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.5,
			1.});
	//	static List<Double> doubleParameters = Arrays.asList(new Double[]{
	//			0.1,
	//			0.3,
	//			0.6,
	//			1.});
	static List<Double> doubleParameters2 = Arrays.asList(new Double[]{
			0.,
			0.5,
			1.});
	static List<Double> doubleParameters3 = Arrays.asList(new Double[]{
			0.,
			0.25,
			0.5,
			0.75,
			1.});
	//pref TODO : Non imple chez l'agent!!
	//	Collection<String> agentPref = Arrays.asList(new String[]{
	//			ReplicationExperimentationProtocol.key4agentKey_Relia,
	//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
	//	static final String key4agentKey_Relia="onlyRelia";
	//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Variation configuration
	//

	static boolean varyProtocol=false;
	static boolean  varyOptimizers=true;

	static boolean varyAgentsAndhosts=false;

	static boolean varyAccessibleHost=false;

	static boolean varyAgentSelection=false;
	static boolean varyHostSelection=false;

	static boolean varyHostDispo=false;
	static boolean varyHostFaultDispersion=false;

	static boolean varyAgentLoad=false;
	static boolean varyAgentLoadDispersion=false;

	static boolean varyHostCapacity=false;
	static boolean varyHostCapacityDispersion=false;

	static boolean varyAgentCriticity=false;
	static boolean varyAgentCriticityDispersion=false;

	static boolean varyFault=false;
	static int dynamicCriticity=-1; //-1 never dynamics, 1 always dynamics, 0 both

	//
	// Default values
	//

	static ReplicationExperimentationParameters getDefaultParameters(final File f) {
		return new ReplicationExperimentationParameters(
				f,
				Experimentator.myId,
				ReplicationExperimentationProtocol.startingNbAgents,
				ReplicationExperimentationProtocol.startingNbHosts,
				ReplicationExperimentationProtocol.doubleParameters.get(2),//kaccessible
				ReplicationExperimentationProtocol.doubleParameters.get(1),//dispo mean
				DispersionSymbolicValue.Fort,//dispo dispersion
				0.5,//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Fort,//load dispersion
				2*ReplicationExperimentationProtocol.doubleParameters.get(1),//capacity mean
				DispersionSymbolicValue.Nul,//capcity dispersion
				ReplicationExperimentationProtocol.doubleParameters.get(1),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				ReplicationExperimentationProtocol.getKey4mirrorproto(),
				SocialChoiceFunctions.key4UtilitaristSocialWelfare,
				ReplicationExperimentationProtocol.getKey4greedyselect(),
				ReplicationExperimentationProtocol.getKey4allocselect(),
				false,
				ReplicationExperimentationProtocol.doubleParameters2.get(0));
	}


	//
	// Primitives
	//

	@Override
	public LinkedList<ExperimentationParameters> generateSimulation() {
		final String usedProtocol, agentSelection, hostSelection;
		final File f = new File(ReplicationExperimentationProtocol.resultPath);
		//		f.mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new LinkedList<ReplicationExperimentationParameters>();
		simuToLaunch.add(ReplicationExperimentationProtocol.getDefaultParameters(f));
		if (ReplicationExperimentationProtocol.varyAgentsAndhosts) {
			simuToLaunch = this.varyAgentsAndhosts(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAccessibleHost) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
		if (ReplicationExperimentationProtocol.varyFault) {
			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
		}

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters> comp = new Comparator<ExperimentationParameters>() {

			@Override
			public int compare(final ExperimentationParameters o1,
					final ExperimentationParameters o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters> simus = new LinkedList<ExperimentationParameters>(simuToLaunch);
		Collections.sort(simus,comp);
		return simus;
	}



	/*
	 *
	 */

	private Collection<ReplicationExperimentationParameters> varyProtocol(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.protos){
				final ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._agentSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.set_hostSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationProtocol.welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentsAndhosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=(int)(v*ReplicationExperimentationProtocol.startingNbAgents);
				n.nbHosts=(int)(v*ReplicationExperimentationProtocol.startingNbHosts);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setkAccessible(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostDisponibilityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityMean=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationProtocol.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationProtocol.doubleParameters2){
				final ReplicationExperimentationParameters n = p.clone();
				n.setMaxSimultFailure(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert ReplicationExperimentationProtocol.dynamicCriticity>=-1 && ReplicationExperimentationProtocol.dynamicCriticity<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (ReplicationExperimentationProtocol.dynamicCriticity==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (ReplicationExperimentationProtocol.dynamicCriticity==1){
				p.dynamicCriticity=true;
				result.add(p);
			} else {
				final ReplicationExperimentationParameters n = p.clone();
				n.dynamicCriticity=!p.dynamicCriticity;
				result.add(n);
			}
		}
		return result;
	}

	/*
	 *
	 */

	public static final String resultPath;

	static {
		resultPath=LogService.getMyPath()+"result_"
				+ ExperimentationProtocol._simulationTime / 60000
				+ "mins"
				+ (ReplicationExperimentationProtocol.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationProtocol.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationProtocol.varyAccessibleHost?"varyAccessibleHost":"")
				+ (ReplicationExperimentationProtocol.varyAgentLoad?"varyAgentLoad":"")
				+ (ReplicationExperimentationProtocol.varyHostCapacity?"varyHostCapacity":"");
	}



	//
	// Distribution
	//

	final Integer maxNumberOfAgentPerMachine  =this.getMaxNumberOfAgentPerMachine(null)  ;
	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgentPerMachine(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine*
				(ReplicationExperimentationProtocol.startingNbAgents + ReplicationExperimentationProtocol.startingNbHosts)+1);
	}
	//	public int getMaxNumberOfAgentPerMachine(HostIdentifier id) {
	//		return new Integer(10);
	//	}


	//
	// Creation de laborantin
	// /////////////////////////////////

	@Override
	public ReplicationLaborantin createNewLaborantin(
			final ExperimentationParameters para, final APILauncherModule api)
					throws NotEnoughMachinesException, CompetenceException, IfailedException {
		ReplicationLaborantin l = null;
		final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
		//		boolean erreur = true;
		//		while (erreur)
		//			try {
		l = new ReplicationLaborantin(p, api,this.getMaxNumberOfAgentPerMachine(null));
		//				erreur = false;
		//			} catch (final IfailedException e) {
		//				LogService.writeException(
		//						"retrying to launch simu " + p.getName()
		//						+ " failure caused by : ", e.e);
		//				erreur = true;
		//			}

		return l;
	}



	/***
	 * Constantes
	 */

	/* FAULTS
	 *
	 * * lambda haut => weibull bas weibull bas => eventOccur haut lambda = prob
	 * de panne disp = 1 - lambda (useStaticDispo = true) disp = weibull
	 * (useStaticDispo = false)
	 *
	 *
	 * **
	 *
	 * k bas => eventOccur tot et pour tout le monde
	 */

	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final long _timeScale = 10 * ReplicationExperimentationProtocol._host_maxFaultfrequency;
	public static final double _kValue = 7;
	public static final double _lambdaRepair = 1;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;

	//
	// Quantile
	//

	public static final long _reliabilityObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
	// (0.25*_contractExpirationTime);
	public static final int firstTercile = 33;// percent
	public static final int lastTercile = 66;// percent
	public static final double alpha_low = 1;
	public static final double alpha_high = 1;

	//
	// System Dynamicity
	//

	/*
	 * Criticité
	 */

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final long _criticity_update_frequency = 2*ReplicationExperimentationProtocol._timeToCollect;// (long)

	// public static final double _dispoMax = 0.7;
	// public static final double _dispoVariationProba = 0./100.;
	// public static final double _dispoVariationAmplitude = 10./100.;
	// public static final long _dispo_update_frequency =2*_quantileInfoFrequency;
	

	//
	// Configuration statique
	// /////////////////////////////////

	//
	// Simulation Configuration
	//


	/**
	 * 
	 */

	public static final int startingNbAgents = 10;
	public static final int startingNbHosts = 5;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect =50;//500;//
	public static final long _initiatorPropositionFrequency = -1;//(long) (ExperimentationProtocol._timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	private final static String key4CentralisedstatusProto = "Centralised status protocol";
	private final static String key4statusProto = "status protocol";
	private final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	private final static String key4greedySelect = "greedy select";
	private final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4AllocSelect = "alloc select";

	
	
	
	
	public static String getKey4greedyselect() {
		return ReplicationExperimentationProtocol.key4greedySelect;
	}

	public static String getKey4roulettewheelselect() {
		return ReplicationExperimentationProtocol.key4rouletteWheelSelect;
	}

	public static String getKey4allocselect() {
		return ReplicationExperimentationProtocol.key4AllocSelect;
	}

	public static String getKey4mirrorproto() {
		return ReplicationExperimentationProtocol.key4mirrorProto;
	}

	public static String getKey4centralisedstatusproto() {
		return ReplicationExperimentationProtocol.key4CentralisedstatusProto;
	}

	public static String getKey4statusproto() {
		return ReplicationExperimentationProtocol.key4statusProto;
	}

	public static String getKey4multilatproto() {
		return ReplicationExperimentationProtocol.key4multiLatProto;
	}
}



//
//
//

// public static void main(final String[] args) throws
// IllegalArgumentException, IllegalAccessException{
// System.out.println(StaticParameters.write());
// }

//
////
//// Experimentations
//// /////////////////////////////////
//@Override
//public LinkedList<ExperimentationParameters> generateSimulation() {
//	String usedProtocol, agentSelection, hostSelection;
//	final File f = new File(ReplicationExperimentationProtocol.resultPath);
////	f.mkdirs();
//	final LinkedList<ExperimentationParameters> simuToLaunch =
//			new LinkedList<ExperimentationParameters>();
//
//	// /
////
//	usedProtocol = ReplicationExperimentationParameters.key4mirrorProto;
//	agentSelection = ReplicationExperimentationParameters.key4greedySelect;
//	hostSelection = ReplicationExperimentationParameters.key4greedySelect;
//	this.addSimus(usedProtocol, agentSelection, hostSelection, f,
//			simuToLaunch);
//////
//	usedProtocol = ReplicationExperimentationParameters.key4CentralisedstatusProto;
//	agentSelection = ReplicationExperimentationParameters.key4greedySelect;
//	hostSelection = ReplicationExperimentationParameters.key4rouletteWheelSelect;
//	this.addSimus(usedProtocol, agentSelection, hostSelection, f,
//			simuToLaunch);
//
//	usedProtocol = ReplicationExperimentationParameters.key4statusProto;//key4mirrorProto;//key4CentralisedstatusProto;//
//	agentSelection = ReplicationExperimentationParameters.key4greedySelect;//key4rouletteWheelSelect;//
//	hostSelection = ReplicationExperimentationParameters.key4greedySelect;
//	this.addSimus(usedProtocol, agentSelection, hostSelection, f,
//			simuToLaunch);
//
//	return simuToLaunch;
//}
//
//private void addSimus(String usedProtocol, String agentSelection,
//		String hostSelection, File f,
//		LinkedList<ExperimentationParameters> simuToLaunch) {
//	simuToLaunch.add(new ReplicationExperimentationParameters(
//			f,Experimentator.myId,
//			ReplicationExperimentationProtocol.nbAgents,
//			ReplicationExperimentationProtocol.nbHosts,
//			ReplicationExperimentationProtocol.knownHostsPercent,
//			0.2,
//			ZeroOneSymbolicValue.Faible,
//			usedProtocol,
//			agentSelection,
//			hostSelection));
//
//	simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
//			ReplicationExperimentationProtocol.nbAgents,
//			ReplicationExperimentationProtocol.nbHosts, 1, .4,
//			ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
//			hostSelection));
//
//	simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
//			ReplicationExperimentationProtocol.nbAgents,
//			ReplicationExperimentationProtocol.nbHosts, 1, .6,
//			ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
//			hostSelection));
//
//	simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
//			ReplicationExperimentationProtocol.nbAgents,
//			ReplicationExperimentationProtocol.nbHosts, 1, .8,
//			ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
//			hostSelection));
//}