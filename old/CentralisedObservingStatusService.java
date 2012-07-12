package negotiation.faulttolerance.experimentation;

import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.NegotiationParameters;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.support.GimaObject;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;

public class CentralisedObservingStatusService extends
BasicCommunicatingCompetence<Laborantin> {
	private static final long serialVersionUID = 5142247796368825154L;




	//
	// Fields
	//

	private final ReplicationExperimentationParameters p;
	/* Quantile */
	StatusQuantityTrunk[] statusEvolution;
	final HeavyDoubleAggregation agentsStatusObservation = new HeavyDoubleAggregation();

	//
	// Constructor
	//

	public CentralisedObservingStatusService(final Laborantin ag, final ReplicationExperimentationParameters p)
			throws UnrespectedCompetenceSyntaxException {
		super(ag);
		this.p = p;
		this.getSimulationParameters();
		this.statusEvolution =
				new StatusQuantityTrunk[ObservingGlobalService.getNumberOfTimePoints()];
		this.getSimulationParameters();
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			this.statusEvolution[i] = new StatusQuantityTrunk();
		}
	}

	//
	// Accessors
	//

	public boolean iObserveStatus() {
		return this.isActive();
	}

	protected ReplicationExperimentationParameters getSimulationParameters() {
		return this.p;
	}

	//
	// Methods
	//


	public void incr(final ReplicationResultAgent ag, final int i) {
		this.statusEvolution[i].incr(ag);
	}

	//
	// Behaviors
	//


	//
	// Status
	//


	
//	@MessageHandler
//	@NotificationEnvelope(CentralisedObservingStatusService.reliabilityObservationKey)
//	public void updateAgent4StatusObservation(
//			final NotificationMessage<ReplicaState> n) {
//		// System.out.println("yoopppppppppppppppppppppppphooooooooooiiiiiiiiiiiiiiii");
//		this.agentsStatusObservation.add(n.getNotification().getMyReliability());
//		// System.out.println(this.agentsStatusObservation
//		//+"  "+this.agentsStatusObservation.getQuantile(
//		//ReplicationExperimentationProtocol.firstTercile,
//		// 100)+"   "+
//		// this.agentsStatusObservation.getQuantile(
//		//ReplicationExperimentationProtocol.lastTercile,
//		// 100));
//	}

	//
	// Primitives
	//


	public synchronized void writeStatusResult() {

		String result =
				"t (seconds in percent);\t lost;\t fragile;\t " +
						"thrifty (empty);\t thrifty;\t thrifty (full);\t wastefull;\t =\n";
		this
		.getSimulationParameters();
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			result +=ObservingGlobalService.geTime(i)
					/ 1000.
					+ " ;\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentLost,this.getSimulationParameters().nbAgents)
					+ ";\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentFragile,this.getSimulationParameters().nbAgents)
					+ ";\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentEmpty,this.getSimulationParameters().nbAgents)
					+ ";\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentThrifty,this.getSimulationParameters().nbAgents)
					+ ";\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentFull,this.getSimulationParameters().nbAgents)
					+ ";\t "
					+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentWastefull,this.getSimulationParameters().nbAgents)
					+ " ("
					+ this.statusEvolution[i].getTotal()
					/ this.getSimulationParameters().nbAgents
					+ ")\n";
		}

		LogService.logOnFile(this.getSimulationParameters()
				.getResultPath(), result, true, false);
	}




}



//		private void updateAnAgentStatus(final ReplicationAgentResult ag,
//				final int i) {
//			if (!ag.isLastInfo())
//				ReplicationLaborantin.this.myStatusObserver.statusEvolution[i]
//						.incr(ag.getStatus());

//			ReplicationLaborantin.this.myStatusObserver.statusEvolution[i]
//					.setNbAgentLost(ReplicationLaborantin.this
//							.getSimulationParameters().nbAgents
//							- ReplicationLaborantin.this.getAliveAgentsNumber());
//		}
//
//@StepComposant()
//@Transient
//public boolean initialynotifyMyState4Status() {
//	this.notifyMyReliability4Status();
//	return true;
//}
//
//@StepComposant(ticker = NegotiationParameters._statusObservationFrequency)
//public void notifyMyReliability4Status() {
//	// logMonologue("relia send to "+observer.getObserver(ReplicationExperimentationProtocol.reliabilityObservationKey));
//	this.notify(
//			this.getMyAgent().getMyCurrentState().getMyReliability(),
//			CentralisedObservingStatusService.reliabilityObservationKey);
//}