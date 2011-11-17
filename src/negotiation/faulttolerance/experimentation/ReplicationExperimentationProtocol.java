package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;

import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.experimentationframework.Experimentator;
import negotiation.experimentationframework.IfailedException;
import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
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
	// Experimentations
	// /////////////////////////////////
	@Override
	public LinkedList<ExperimentationParameters> generateSimulation() {
		String usedProtocol, agentSelection, hostSelection;
		final File f = new File(ReplicationExperimentationProtocol.resultPath);
//		f.mkdirs();
		final LinkedList<ExperimentationParameters> simuToLaunch = 
				new LinkedList<ExperimentationParameters>();

		// /
//
		usedProtocol = ReplicationExperimentationParameters.key4mirrorProto;
		agentSelection = ReplicationExperimentationParameters.key4greedySelect;
		hostSelection = ReplicationExperimentationParameters.key4greedySelect;
		this.addSimus(usedProtocol, agentSelection, hostSelection, f,
				simuToLaunch);
////
		usedProtocol = ReplicationExperimentationParameters.key4CentralisedstatusProto;
		agentSelection = ReplicationExperimentationParameters.key4greedySelect;
		hostSelection = ReplicationExperimentationParameters.key4rouletteWheelSelect;
		this.addSimus(usedProtocol, agentSelection, hostSelection, f,
				simuToLaunch);

		usedProtocol = ReplicationExperimentationParameters.key4statusProto;//key4mirrorProto;//key4CentralisedstatusProto;//
		agentSelection = ReplicationExperimentationParameters.key4greedySelect;//key4rouletteWheelSelect;//
		hostSelection = ReplicationExperimentationParameters.key4greedySelect;
		this.addSimus(usedProtocol, agentSelection, hostSelection, f,
				simuToLaunch);

		return simuToLaunch;
	}

	private void addSimus(String usedProtocol, String agentSelection,
			String hostSelection, File f,
			LinkedList<ExperimentationParameters> simuToLaunch) {
		simuToLaunch.add(new ReplicationExperimentationParameters(
				f,Experimentator.myId,
				ReplicationExperimentationProtocol.nbAgents,
				ReplicationExperimentationProtocol.nbHosts, 
				ReplicationExperimentationProtocol.knownHostsPercent, 
				0.2,
				ZeroOneSymbolicValue.Faible, 
				usedProtocol, 
				agentSelection,
				hostSelection));

		simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
				ReplicationExperimentationProtocol.nbAgents,
				ReplicationExperimentationProtocol.nbHosts, 1, .4,
				ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
				hostSelection));

		simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
				ReplicationExperimentationProtocol.nbAgents,
				ReplicationExperimentationProtocol.nbHosts, 1, .6,
				ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
				hostSelection));

		simuToLaunch.add(new ReplicationExperimentationParameters(f,Experimentator.myId,
				ReplicationExperimentationProtocol.nbAgents,
				ReplicationExperimentationProtocol.nbHosts, 1, .8,
				ZeroOneSymbolicValue.Faible, usedProtocol, agentSelection,
				hostSelection));
	}

	//
	// Configuration statique
	// /////////////////////////////////

	//
	// Simulation Configuration
	//

	public static final long _simulationTime = (long) (60000 * 0.25);
	public static final long _state_snapshot_frequency = ReplicationExperimentationProtocol._simulationTime / 5;
	// public static final long _simulationTime = (long) (60000*7);
	// public static final long _state_snapshot_frequency=_simulationTime/4;

	// public static final int nbAgents =1;
	// public static final int nbHosts = 2;
	public static final int nbAgents = 3;
	public static final int nbHosts = 3;
//	public static final int nbAgents = 50;
//	public static final int nbHosts = 70;
//	public static final int nbAgents = 100;
//	public static final int nbHosts = 60;
	// public static final int nbAgents =100;
	// public static final int nbHosts = 30;
	// public static final int nbAgents =100;
	// public static final int nbHosts = 20;
	// public static final int nbAgents =2000;
	// public static final int nbHosts = 600;
	public static final int nbSimuPerMAchine = 1;

	public static final Double knownHostsPercent = 1.;

	public static final Double hostMaxProc = 5.;
	public static final Double hostMaxMem = 5.;
	
	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect = -1;//500;//

	public static final long _initiatorPropositionFrequency = -1;// (long) (_timeToCollect*0.5);//(long)
																	//
	// public static final long _initiator_analysisFrequency = (long)
	// (_timeToCollect*2);

	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;

	//
	// Quantile
	//

	public static final String reliabilityObservationKey = "reliabilityNotif4quantile";
	public static final long _reliabilityObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
																														// (0.25*_contractExpirationTime);
	public static final int firstTercile = 33;// percent
	public static final int lastTercile = 66;// percent
	public static final double alpha_low = 1;
	public static final double alpha_high = 1;

	//
	// System Dynamicity
	//

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final Long _criticity_update_frequency = null;// (long)
		
	//
	//
	//

	public static final String _agentPrefKey_Relia="onlyRelia";
	public static final String _agentPrefKey_loadNRelia="firstLoadSecondRelia";
	
	
	// (1.5*_contractExpirationTime);

	// public static final double _dispoMax = 0.7;
	// public static final double _dispoVariationProba = 0./100.;
	// public static final double _dispoVariationAmplitude = 10./100.;
	// public static final long _dispo_update_frequency =
	// 2*_quantileInfoFrequency;

	//
	// FaultSimulation
	//

	/*
	 * lambda haut => weibull bas weibull bas => eventOccur haut lambda = prob
	 * de panne disp = 1 - lambda (useStaticDispo = true) disp = weibull
	 * (useStaticDispo = false)
	 * 
	 * 
	 * **
	 * 
	 * k bas => eventOccur tot et pour tout le monde
	 */

	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final double _host_maxSimultaneousFailure = 1;// 0.25;
	public static final long _timeScale = 10 * ReplicationExperimentationProtocol._host_maxFaultfrequency;
	public static final double _kValue = 7;
	public static final double _lambdaRepair = 1;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;

	/*
	 *
	 */

	public static final DispersionSymbolicValue hostDisponibilityDispersion = DispersionSymbolicValue.Nul;

	public static final ZeroOneSymbolicValue agentCriticityMean = ZeroOneSymbolicValue.Moyen;
	public static final DispersionSymbolicValue agentCriticityDispersion = DispersionSymbolicValue.Fort;

	public static final DispersionSymbolicValue agentLoadDispersion = DispersionSymbolicValue.Nul;

	@Override
	public int getMaxNumberOfAgentPerMachine(HostIdentifier id) {
		return ReplicationExperimentationProtocol.nbSimuPerMAchine
				* (ReplicationExperimentationProtocol.nbAgents + ReplicationExperimentationProtocol.nbHosts);
	}

	@Override
	public int getNumberOfMachinePerSimulation() {
		return 1;
	}

	public static final String resultPath = LogService.getMyPath()+"result_"			
			+ ReplicationExperimentationProtocol.nbAgents + "agents_"
			+ ReplicationExperimentationProtocol.nbHosts + "hosts_"
			+ ReplicationExperimentationProtocol._simulationTime / 60000
			+ "mins";

	//
	//
	//

	// public static void main(final String[] args) throws
	// IllegalArgumentException, IllegalAccessException{
	// System.out.println(StaticParameters.write());
	// }

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
				l = new ReplicationLaborantin(p, api);
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
