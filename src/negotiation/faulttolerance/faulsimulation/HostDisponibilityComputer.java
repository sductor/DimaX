package negotiation.faulttolerance.faulsimulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.library.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import dimaxx.tools.distribution.PoissonLaw;
import dimaxx.tools.distribution.WeibullLaw;

public class HostDisponibilityComputer implements DimaComponentInterface {
	private static final long serialVersionUID = 1444858209007788890L;

	//
	// Fields
	//

	final long experienceTime;
	final long timeScale;
	final long eventFrequency;

	final double lambdaRepair;

	//
	// Constructor
	//

	public HostDisponibilityComputer(final ObservationService myAgentInformation,
			final long experienceTime, final long timeScale, final long eventFrequency,
			final double lambdaRepair) {
		super();
		//		this.myAgentInformation = myAgentInformation;
		this.experienceTime = experienceTime;
		this.timeScale = timeScale;
		this.eventFrequency = eventFrequency;
		this.lambdaRepair = lambdaRepair;
	}
	//
	// Methods
	//

	/*
	 * Fault evaluation
	 */

	public static Double getDisponibility(
			final ObservationService myAgentInformation,
			final ResourceIdentifier host) {
		return HostDisponibilityComputer.getDisponibility(
				HostDisponibilityComputer.getLambda(myAgentInformation,host),
				HostDisponibilityComputer.getUptime(myAgentInformation,host));
	}


	public static Double getDisponibility(
			final ObservationService myAgentInformation,
			final Collection<ResourceIdentifier> hosts) {
		final Map<ResourceIdentifier, Long> hosts_uptimes = new HashMap<ResourceIdentifier, Long>();
		final Map<ResourceIdentifier, Double> hosts_lambdas = new HashMap<ResourceIdentifier, Double>();

		for (final ResourceIdentifier h : hosts) {
			hosts_lambdas.put(h, HostDisponibilityComputer.getLambda(myAgentInformation,h));
			hosts_uptimes.put(h, HostDisponibilityComputer.getUptime(myAgentInformation,h));
		}

		return HostDisponibilityComputer.getDisponibility(hosts_lambdas, hosts_uptimes);
	}
	public static Double getDisponibility(
			final Collection<HostState> hosts) {
		final Map<ResourceIdentifier, Long> hosts_uptimes = new HashMap<ResourceIdentifier, Long>();
		final Map<ResourceIdentifier, Double> hosts_lambdas = new HashMap<ResourceIdentifier, Double>();

		for (final HostState h : hosts) {
			hosts_lambdas.put(h.getMyAgentIdentifier(), h.getLambda());
			hosts_uptimes.put(h.getMyAgentIdentifier(), h.getUptime());
		}

		return HostDisponibilityComputer.getDisponibility(hosts_lambdas, hosts_uptimes);
	}

	/*
	 * Fault Triggering
	 */

	/**
	 * return null if no event
	 *
	 * @param host
	 * @return
	 */
	public static FaultStatusMessage eventOccur(
			final ObservationService myAgentInformation,
			final ResourceIdentifier host) {
		try {
			if (HostDisponibilityComputer.eventOccur(
					HostDisponibilityComputer.getUptime(myAgentInformation,host),
					HostDisponibilityComputer.getLambda(myAgentInformation,host),
					!myAgentInformation.getInformation(HostState.class,
							host).isFaulty())) {
				if (myAgentInformation
						.getInformation(HostState.class, host).isFaulty())
					return new RepairEvent(host);
				else
					return new FaultEvent(host);
			} else
				return null;
		} catch (final NoInformationAvailableException e) {
			LogService
			.writeException("immmmmmmmmoooooooooooooossssssssssssiiiiiiiiiiblle");
			return null;
		}
	}

	public static void setFaulty(
			final ObservationService myAgentInformation,
			final FaultStatusMessage event) {
		try {
			if (event instanceof FaultEvent)
				myAgentInformation.getInformation(HostState.class,
						event.getHost()).setFaulty(true);
			else if (event instanceof RepairEvent)
				myAgentInformation.getInformation(HostState.class,
						event.getHost()).setFaulty(false);
			else
				throw new RuntimeException("impossiblle!!");
		} catch (final NoInformationAvailableException e) {
			LogService
			.writeException("immmmmmmmmoooooooooooooosssssssssssiiiiiiiiiiblle");
		}
	}

	public static Collection<? extends ResourceIdentifier> getHosts(final ObservationService myAgentInformation) {
		final Collection<ResourceIdentifier> hostAlive = new ArrayList<ResourceIdentifier>();
		for (final AgentIdentifier id : myAgentInformation
				.getKnownAgents())
			if (id instanceof ResourceIdentifier)
				hostAlive.add((ResourceIdentifier) id);
				return hostAlive;
	}
	//
	// Primitive
	//

	private static  Double getLambda(final ObservationService myAgentInformation, final ResourceIdentifier h) {
		try {
			return myAgentInformation.getInformation(HostState.class, h)
					.getLambda();
		} catch (final NoInformationAvailableException e) {
			LogService.writeException(
					"immmmmmmmmooooooooooooosssssssssssiiiiiiiiiiblle to find "+h
					+" knwon info "+myAgentInformation.show(HostState.class)
					+"\n"+myAgentInformation.toString(),e);

			return null;
		}
	}

	private static  Long getCreationtime(final ObservationService myAgentInformation, final ResourceIdentifier h) {
		try {
			return myAgentInformation.getInformation(HostState.class, h)
					.getCreationTime();
		} catch (final NoInformationAvailableException e) {
			LogService
			.writeException("immmmmmmmmooooooooooooosssssssssssiiiiiiiiiibllle",e);
			return null;
		}
	}

	private static long getUptime(final ObservationService myAgentInformation,final ResourceIdentifier h) {
		return new Date().getTime() - HostDisponibilityComputer.getCreationtime(myAgentInformation,h);
	}


	//
	// Primitive : dispo computation
	//

	private enum DisponibilityComputationType {
		Static, Weibull, Poisson
	}

	private  static final DisponibilityComputationType choosenType = DisponibilityComputationType.Static;//Poisson;//

	private  static Double getDisponibility(
			final Map<ResourceIdentifier, Double> hosts_lambdas,
			final Map<ResourceIdentifier, Long> hosts_uptimes) {
		if (hosts_lambdas.isEmpty())
			return 0.;
		else {
			Double panne = new Double(1);
			for (final ResourceIdentifier h : hosts_lambdas.keySet())
				panne *= 1 - HostDisponibilityComputer.getDisponibility(hosts_lambdas.get(h),
						hosts_uptimes.get(h));

					return 1 - panne;
		}
	}

	private static Double getDisponibility(final Double lambda, final long uptime) {
		switch (HostDisponibilityComputer.choosenType) {
		case Static:
			return lambda;
		case Weibull:
			return WeibullLaw
					.getWeibullLaw(
							(uptime + 10 * ReplicationExperimentationParameters._host_maxFaultfrequency)
							/ ReplicationExperimentationParameters._timeScale,
							ReplicationExperimentationParameters._kValue, lambda,
							ReplicationExperimentationParameters._theta);
		case Poisson:
			final long nbInterval = ReplicationExperimentationProtocol._simulationTime / ReplicationExperimentationParameters._host_maxFaultfrequency;
			return PoissonLaw.getPoissonLaw(lambda * nbInterval, 1);
		default:
			throw new RuntimeException("impossible");
		}
	}

	private  static boolean eventOccur(final long uptime, final Double lambda,
			final boolean triggerAFault) {
		switch (HostDisponibilityComputer.choosenType) {
		case Static:
			final Random rand = new Random();
			if (triggerAFault)
				return rand.nextDouble() > lambda;
				else
					return rand.nextDouble() > ReplicationExperimentationParameters._lambdaRepair;
		case Weibull:
			if (triggerAFault)
				return WeibullLaw.eventOccur(uptime / ReplicationExperimentationParameters._timeScale,
						ReplicationExperimentationParameters._kValue, lambda,
						ReplicationExperimentationParameters._theta);
			else
				return WeibullLaw.eventOccur(uptime / ReplicationExperimentationParameters._timeScale,
						ReplicationExperimentationParameters._kRepair,
						ReplicationExperimentationParameters._lambdaRepair, 0.);
		case Poisson:
			final long nbInterval = ReplicationExperimentationProtocol._simulationTime / ReplicationExperimentationParameters._host_maxFaultfrequency;
			if (triggerAFault)
				return PoissonLaw.eventOccur(lambda * nbInterval, 1);
			else
				return PoissonLaw.eventOccur(ReplicationExperimentationParameters._lambdaRepair * nbInterval, 1);
		default:
			throw new RuntimeException("impossible");
		}
	}
}

// @Override
// public String toString(){
// return this.lambdaValues.toString();
// }
//
// @Override
// public Collection<ResourceIdentifier> getHosts() {
// return this.lambdaValues.keySet();
// }
//
// @Override
// public void add(final ResourceIdentifier h, final Double lambda, final Long
// creationTime){
// this.lambdaValues.put(h, lambda);
// this.creationTimes.put(h, creationTime);
// }
//
// protected void resetUptime(final ResourceIdentifier h){
// this.creationTimes.put(h, new Date().getTime());
// }

// public void set(final ResourceIdentifier h, final Double lambda){
// this.lambdaValues.put(h, lambda);
// }

// public void remove(final ResourceIdentifier h){
// creationTimes.remove(h);
// lambdaValues.remove(h);
// }
//
//
//
//
//
//

//
//
//
//
//
// public void setMyDisponibility(final ResourceIdentifier host,final double
// dispo) {
// lambdaValues.put(host, 1 - dispo);
// }