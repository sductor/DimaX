package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.Experimentator;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import negotiation.negotiationframework.AllocationSocialWelfares;
import negotiation.negotiationframework.SocialChoiceFunctions;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
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
			0.3,
			0.6,
			1.});
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

	static boolean varyAccessibleHost=true;

	static boolean varyAgentSelection=true;
	static boolean varyHostSelection=false;

	static boolean varyHostDispo=true;
	static boolean varyHostFaultDispersion=true;

	static boolean varyAgentLoad=true;
	static boolean varyAgentLoadDispersion=true;

	static boolean varyAgentCriticity=true;
	static boolean varyAgentCriticityDispersion=true;

	static boolean varyFault=true;	
	static int dynamicCriticity=0; //-1 never dynamics, 1 always dynamics, 0 both

	//
	// Default values
	//

	static ReplicationExperimentationParameters getDefaultParameters(final File f) {
		return new ReplicationExperimentationParameters(
				f,
				Experimentator.myId,
				ExperimentationProtocol.nbAgents,
				ExperimentationProtocol.nbHosts,
				doubleParameters.get(3),//kaccessible
				doubleParameters.get(2),//dispo mean
				DispersionSymbolicValue.Nul,//dispo dispersion
				doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Nul,//load dispersion
				doubleParameters.get(2),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				ExperimentationProtocol.getKey4mirrorproto(),
				SocialChoiceFunctions.key4UtilitaristSocialWelfare,
				ExperimentationProtocol.getKey4greedyselect(),
				ExperimentationProtocol.getKey4allocselect(),
				false,
				doubleParameters2.get(0));
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
		if (ReplicationExperimentationProtocol.varyAccessibleHost)
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostDispo)
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostFaultDispersion)
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentLoad)
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentLoadDispersion)
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentCriticity)
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentCriticityDispersion)
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentSelection)
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostSelection)
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyOptimizers)
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyProtocol)
			simuToLaunch = this.varyProtocol(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyFault)
			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);

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
		for (final ReplicationExperimentationParameters p : exps)
			for (final String v : this.protos){
				final ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final String v : this.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._agentSelection=v;
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final String v : this.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.set_hostSelection(v);
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final String v : this.welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final Double v : this.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setkAccessible(v);
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final Double v : this.doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final DispersionSymbolicValue v : this.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostDisponibilityDispersion=v;
				result.add(n);
			}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final Double v : this.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final DispersionSymbolicValue v : this.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final Double v : this.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityMean=v;
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final DispersionSymbolicValue v : this.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			for (final Double v : this.doubleParameters2){
				final ReplicationExperimentationParameters n = p.clone();
				n.setMaxSimultFailure(v);
				result.add(n);
			}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			Collection<ReplicationExperimentationParameters> exps) {
		assert dynamicCriticity>=-1 && dynamicCriticity<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps)
			if (dynamicCriticity==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (dynamicCriticity==1){
				p.dynamicCriticity=true;
				result.add(p);
			} else {
				final ReplicationExperimentationParameters n = p.clone();
				n.dynamicCriticity=!p.dynamicCriticity;
				result.add(n);
			}
		return result;
	}
	
	/*
	 *
	 */

	public static final String resultPath;

	static {
		resultPath=LogService.getMyPath()+"result_"
				+ ExperimentationProtocol.nbAgents + "agents_"
				+ ExperimentationProtocol.nbHosts + "hosts_"
				+ ExperimentationProtocol._simulationTime / 60000
				+ "mins"
				+ (ReplicationExperimentationProtocol.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationProtocol.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationProtocol.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationProtocol.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationProtocol.varyAccessibleHost?"varyAccessibleHost":"")
				+ (ReplicationExperimentationProtocol.varyAgentLoad?"varyAgentLoad":"");
	}



	//
	// Distribution
	//

	final Integer maxNumberOfAgentPerMachine  =this.getMaxNumberOfAgentPerMachine(null)  ;
	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgentPerMachine(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine* (ExperimentationProtocol.nbAgents + ExperimentationProtocol.nbHosts)+1);
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