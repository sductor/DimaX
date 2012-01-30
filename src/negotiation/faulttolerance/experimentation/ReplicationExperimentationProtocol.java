package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.Experimentator;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import negotiation.negotiationframework.AllocationSocialWelfares;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

public class ReplicationExperimentationProtocol implements
ExperimentationProtocol {
	private static final long serialVersionUID = 3221531706912973963L;


	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//

	Collection<String> protos = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.key4mirrorProto,
			ReplicationExperimentationProtocol.key4CentralisedstatusProto,
			ReplicationExperimentationProtocol.key4statusProto});
	Collection<String> welfare = Arrays.asList(new String[]{
			AllocationSocialWelfares.key4leximinSocialWelfare,
			AllocationSocialWelfares.key4NashSocialWelfare,
			AllocationSocialWelfares.key4UtilitaristSocialWelfare});
	Collection<String> select = Arrays.asList(new String[]{
			ReplicationExperimentationProtocol.key4greedySelect,
			ReplicationExperimentationProtocol.key4rouletteWheelSelect});//,key4AllocSelect
	Collection<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
					DispersionSymbolicValue.Nul,
					DispersionSymbolicValue.Moyen,
					DispersionSymbolicValue.Max});
	Collection<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.3,
			0.6,
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
	
	static boolean  varyAgentSelection=true;
	static boolean varyHostSelection=true;	
	
	static boolean varyHostDispo=true;
	static boolean varyHostFaultDispersion=true;
	
	static boolean varyAgentLoad=true;
	static boolean varyAgentLoadDispersion=true;

	//
	// Default values
	//
	
	static ReplicationExperimentationParameters getDefaultParameters(final File f) {
		return new ReplicationExperimentationParameters(
				f,
				Experimentator.myId,
				ReplicationExperimentationProtocol.nbAgents,
				ReplicationExperimentationProtocol.nbHosts,
				1,
				0.6,
				DispersionSymbolicValue.Nul,
				0.3,
				DispersionSymbolicValue.Nul,
				ExperimentationProtocol.key4mirrorProto,
				AllocationSocialWelfares.key4leximinSocialWelfare,
				ExperimentationProtocol.key4greedySelect,
				ExperimentationProtocol.key4greedySelect);
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
		if (ReplicationExperimentationProtocol.varyAgentSelection)
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostSelection)
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyProtocol)
			simuToLaunch = this.varyProtocol(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostDispo)
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentLoad)
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyOptimizers)
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAccessibleHost)
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyHostFaultDispersion)
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		if (ReplicationExperimentationProtocol.varyAgentLoadDispersion)
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);

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

	/*
	 *
	 */

	public static final String resultPath;

	static {
		resultPath=LogService.getMyPath()+"result_"
				+ ReplicationExperimentationProtocol.nbAgents + "agents_"
				+ ReplicationExperimentationProtocol.nbHosts + "hosts_"
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
		return new Integer((int) this.nbSimuPerMAchine* (ReplicationExperimentationProtocol.nbAgents + ReplicationExperimentationProtocol.nbHosts)+1);
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
					throws NotEnoughMachinesException, CompetenceException {
		ReplicationLaborantin l = null;
		final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
		boolean erreur = true;
		while (erreur)
			try {
				l = new ReplicationLaborantin(p, api,this.getMaxNumberOfAgentPerMachine(null));
				erreur = false;
			} catch (final IfailedException e) {
				LogService.writeException(
						"retrying to launch simu " + p.getName()
						+ " failure caused by : ", e.e);
				erreur = true;
			}

		return l;
	}

	@Override
	public String getDescription() {
		try {
			String result = "**************\n";
			result += "Static parameters are :\n";
			for (final Field f : ReplicationExperimentationProtocol.class
					.getFields())
				result += f.getName() + " : "
						+ f.get(ReplicationExperimentationProtocol.class)
						+ "\n";
					result += "**************";
					return result;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
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