package negotiation.experimentationframework;

import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.support.GimaObject;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;

public class ObservingStatusService extends
BasicAgentCommunicatingCompetence<Laborantin> {
	private static final long serialVersionUID = 5142247796368825154L;



	public static final String reliabilityObservationKey = "reliabilityNotif4quantile";

	//
	// Fields
	//

	private final ExperimentationParameters p;
	/* Quantile */
	StatusQuantityTrunk[] statusEvolution;
	final HeavyDoubleAggregation agentsStatusObservation = new HeavyDoubleAggregation();

	//
	// Constructor
	//

	public ObservingStatusService(final Laborantin ag, final ExperimentationParameters p)
			throws UnrespectedCompetenceSyntaxException {
		super(ag);
		this.p = p;
		this.getSimulationParameters();
		this.statusEvolution =
				new StatusQuantityTrunk[ExperimentationParameters.getNumberOfTimePoints()];
		this
		.getSimulationParameters();
		for (int i = 0; i < ExperimentationParameters.getNumberOfTimePoints(); i++)
			this.statusEvolution[i] = new StatusQuantityTrunk();
	}

	//
	// Accessors
	//

	public boolean iObserveStatus() {
		return this.isActive();
	}

	protected ExperimentationParameters getSimulationParameters() {
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

	@MessageHandler
	@NotificationEnvelope(ObservingStatusService.reliabilityObservationKey)
	public void updateAgent4StatusObservation(
			final NotificationMessage<Double> n) {
		// System.out.println("yoopppppppppppppppppppppppphooooooooooiiiiiiiiiiiiiiii");
		this.agentsStatusObservation.add(n.getNotification());
		// System.out.println(this.agentsStatusObservation
		//+"  "+this.agentsStatusObservation.getQuantile(
		//ReplicationExperimentationProtocol.firstTercile,
		// 100)+"   "+
		// this.agentsStatusObservation.getQuantile(
		//ReplicationExperimentationProtocol.lastTercile,
		// 100));
	}

	//
	// Primitives
	//

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

	public synchronized void writeStatusResult() {

		String result =
				"t (seconds in percent);\t lost;\t fragile;\t " +
						"thrifty (empty);\t thrifty;\t thrifty (full);\t wastefull;\t =\n";
		this
		.getSimulationParameters();
		for (int i = 0; i < ExperimentationParameters.getNumberOfTimePoints(); i++)
			result += this.getSimulationParameters()
			.geTime(i)
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

		LogService.logOnFile(this.getSimulationParameters()
				.getF(), result, true, false);
	}


	public class StatusQuantityTrunk extends GimaObject {

		/**
		 *
		 */
		private static final long serialVersionUID = 3740320075407434400L;

		int nbAgentLost = 0;

		int nbAgentFragile = 0;
		int nbAgentThrifty = 0;
		int nbAgentFull = 0;
		int nbAgentWastefull = 0;
		int nbAgentEmpty = 0;

		public void incr(final ReplicationResultAgent s) {
			if (s.getDisponibility()==0)
				this.nbAgentLost++;
			else
				switch (s.getStatus()) {
				case Fragile:
					this.nbAgentFragile++;
					break;
				case Thrifty:
					this.nbAgentThrifty++;
					break;
				case Full:
					this.nbAgentFull++;
					break;
				case Wastefull:
					this.nbAgentWastefull++;
					break;
				case Empty:
					this.nbAgentEmpty++;
					break;
				}
		}

		public double getTotal() {
			return (double) this.nbAgentFragile + this.nbAgentThrifty
					+ this.nbAgentFull + this.nbAgentWastefull
					+ this.nbAgentEmpty+ this.nbAgentLost;
		}
	}


}