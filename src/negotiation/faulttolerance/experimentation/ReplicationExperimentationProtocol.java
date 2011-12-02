package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.Experimentator;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import negotiation.negotiationframework.AllocationSocialWelfares;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.ZeroOneSymbolicValue;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

public class ReplicationExperimentationProtocol implements
ExperimentationProtocol {

	//
	// Configuration statique
	// /////////////////////////////////


	public static final int nbAgents = 18;
	public static final int nbHosts = 12;

	//
	// Simulation Configuration
	//

	public static final long _simulationTime = (long) (60000 * 1);
	public static final long _state_snapshot_frequency = ReplicationExperimentationProtocol._simulationTime / 6;

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect = -1;//500;//
	public static final long _initiatorPropositionFrequency = -1;// (long) (_timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;

	/**
	 * Clés statiques
	 */

	//Optimisations
	public final static String key4leximinSocialWelfare="leximin";
	public final static String key4NashSocialWelfare="nash";
	public final static String key4UtilitaristSocialWelfare="utilitarist";

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	public final static String key4CentralisedstatusProto = "Centralised status protocol";
	public final static String key4statusProto = "status protocol";
	public final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	public final static String key4greedySelect = "greedy select";
	public final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4AllocSelect = "alloc select";

	//pref
	public static final String key4agentKey_Relia="onlyRelia";
	public static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Set of values
	//

	Collection<String> protos = Arrays.asList(new String[]{key4mirrorProto,key4CentralisedstatusProto,key4statusProto});
	Collection<String> welfare = Arrays.asList(new String[]{key4leximinSocialWelfare,key4NashSocialWelfare,key4UtilitaristSocialWelfare});
	Collection<String> select = Arrays.asList(new String[]{key4greedySelect,key4rouletteWheelSelect});//,key4AllocSelect
	Collection<String> agentPref = Arrays.asList(new String[]{key4agentKey_Relia,key4agentKey_loadNRelia});
	Collection<Double> doubleParameters = Arrays.asList(new Double[]{0.1,0.3,0.6,1.});
	Collection<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{DispersionSymbolicValue.Nul,DispersionSymbolicValue.Moyen});//,DispersionSymbolicValue.Max

	//
	// Methods : Génération de simulation
	//


	static boolean  varyAgentSelection=false;
	static boolean varyHostSelection=false;
	static boolean varyProtocol=true;
	static boolean varyHostDispo=true;
	static boolean  varyOptimizers=false;
	static boolean varyAccessibleHost=true;
	static boolean varyAgentLoad=true;
	static boolean varyHostFaultDispersion=false;
	static boolean varyAgentLoadDispersion=false;
	
	public LinkedList<ExperimentationParameters> generateSimulation() {
		String usedProtocol, agentSelection, hostSelection;
		final File f = new File(ReplicationExperimentationProtocol.resultPath);
		//		f.mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch = 
				new LinkedList<ReplicationExperimentationParameters>();
		simuToLaunch.add(ReplicationExperimentationParameters.getGeneric(f));
		if (varyAgentSelection)
			simuToLaunch = varyAgentSelection(simuToLaunch);
		if (varyHostSelection)
			simuToLaunch = varyHostSelection(simuToLaunch);
		if (varyProtocol)
			simuToLaunch = varyProtocol(simuToLaunch);
		if (varyHostDispo)
			simuToLaunch = varyHostDispo(simuToLaunch);
		if (varyAgentLoad)
			simuToLaunch = varyAgentLoad(simuToLaunch);
		if (varyOptimizers)
			simuToLaunch = varyOptimizers(simuToLaunch);
		if (varyAccessibleHost)
			simuToLaunch = varyAccessibleHost(simuToLaunch);
		if (varyHostFaultDispersion)
			simuToLaunch = varyHostFaultDispersion(simuToLaunch);
		if (varyAgentLoadDispersion)
			simuToLaunch = varyAgentLoadDispersion(simuToLaunch);

		Comparator<ExperimentationParameters> comp = new Comparator<ExperimentationParameters>() {

			@Override
			public int compare(ExperimentationParameters o1,
					ExperimentationParameters o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		LinkedList<ExperimentationParameters> simus = new LinkedList<ExperimentationParameters>(simuToLaunch);
		Collections.sort(simus,comp);
		return simus;
	}

	

	//
	// Distribution
	//

	 final Integer maxNumberOfAgentPerMachine  =getMaxNumberOfAgentPerMachine(null)  ;
	 final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgentPerMachine(HostIdentifier id) {
		return new Integer((int) nbSimuPerMAchine* (nbAgents + nbHosts)+1);
	}
//	public int getMaxNumberOfAgentPerMachine(HostIdentifier id) {
//		return new Integer(10);
//	}

	/*
	 * 
	 */

	private Collection<ReplicationExperimentationParameters> varyProtocol(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (String v : protos){
				ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (String v : select){
				ReplicationExperimentationParameters n =  p.clone();
				n._agentSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (String v : select){
				ReplicationExperimentationParameters n =  p.clone();
				n.set_hostSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (String v : welfare){
				ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (Double v : doubleParameters){
				ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		}
		return result;	
	}

	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (Double v : doubleParameters){
				ReplicationExperimentationParameters n =  p.clone();
				n.setkAccessible(v);
				result.add(n);
			}
		}
		return result;		
	}
	private Collection<ReplicationExperimentationParameters> varyAgentLoad(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (Double v : doubleParameters){
				ReplicationExperimentationParameters n = (ReplicationExperimentationParameters) p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}				
		}	
		return result;		
	}
	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (DispersionSymbolicValue v : dispersion){
				ReplicationExperimentationParameters n = (ReplicationExperimentationParameters) p.clone();
				n.hostDisponibilityDispersion=v;
				result.add(n);
			}				
		}	
		return result;		
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(Collection<ReplicationExperimentationParameters> exps){
		Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (ReplicationExperimentationParameters p : exps){
			for (DispersionSymbolicValue v : dispersion){
				ReplicationExperimentationParameters n = (ReplicationExperimentationParameters) p.clone();
				n.agentLoadDispersion=v;
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
			+ nbAgents + "agents_"
			+ nbHosts + "hosts_"
			+ ReplicationExperimentationProtocol._simulationTime / 60000
			+ "mins"
			+ (varyAgentSelection==true?"varyAgentSelection":"")
			+ (varyHostSelection?"varyHostSelection":"")
			+ (varyProtocol?"varyProtocol":"")
			+ (varyHostDispo?"varyHostDispo":"")
			+ (varyHostSelection?"varyHostSelection":"")
			+ (varyOptimizers?"varyOptimizers":"")
			+ (varyAccessibleHost?"varyAccessibleHost":"")
			+ (varyAgentLoad?"varyAgentLoad":"");
	}

				//
				// Primitive
				// /////////////////////////////////

				@Override
				public ReplicationLaborantin createNewLaborantin(
						ExperimentationParameters para, APILauncherModule api)
								throws NotEnoughMachinesException, CompetenceException {
		ReplicationLaborantin l = null;
		final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
		boolean erreur = true;
		while (erreur)
			try {
				l = new ReplicationLaborantin(p, api,getMaxNumberOfAgentPerMachine(null));
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