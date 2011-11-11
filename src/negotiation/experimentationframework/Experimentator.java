package negotiation.experimentationframework;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import negotiation.experimentationframework.MachineNetwork.NotEnoughMachinesException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
import dimaxx.server.HostIdentifier;


/**
 * The experimentator is in charge of sequencially distribute the different experiences  launch
 * that are modelled as Laborantin.
 * The different experiences to launch are provided with the experimentation protocol  
 * 
 * @author Sylvain Ductor
 *
 */
public class Experimentator extends BasicCompetentAgent{
	private static final long serialVersionUID = 6985131313855716524L;

	//
	// Accessors
	//

	final ExperimentationProtocol myProtocol;
	final File f;
	//Integer represent the sum of the number of agent of each simulation that uses the given machine 
	public final MachineNetwork machines;

	/*
	 * 
	 */

	public final LinkedList<ExperimentationParameters> simuToLaunch;

	public final Map<AgentIdentifier, Laborantin> launchedSimu =
		new HashMap<AgentIdentifier, Laborantin>();
	public int awaitingAnswer=-1;

	//
	// Constructor
	//


	public Experimentator(final List<HostIdentifier> machines, ExperimentationProtocol myProtocol)
	throws CompetenceException, IllegalArgumentException, IllegalAccessException {
		super("zi experimentator");
		this.machines = new MachineNetwork(machines);
		this.f = new File(ReplicationExperimentationProtocol.resultPath);
//		Writing.log(
//				this.f,
//				myProtocol.getDescription(),
//				true, false);
		this.myProtocol=myProtocol;
		simuToLaunch = myProtocol.generateSimulation();

		this.logMonologue("Experimentator created for:\n"+myProtocol.getDescription()+" will use :"+machines);
	}

	//
	// Methods
	//	




	//Executed initially then called by collect result
	@ProactivityInitialisation
	public boolean launchSimulation() throws CompetenceException{
		this.logMonologue("Available Memory :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory(),LogService.onBoth);
		if (this.awaitingAnswer==0){
			this.setAlive(false);
			this.logMonologue(myProtocol.getDescription(),LogService.onBoth);
			this.logMonologue("yyyyyyyyeeeeeeeeeeeeaaaaaaaaaaaaahhhhhhhhhhh!!!!!!!!!!!",LogService.onBoth);
			System.exit(1);
		} else if (!this.simuToLaunch.isEmpty()){
			try {
				while (!this.simuToLaunch.isEmpty()){
					Laborantin l = myProtocol.createNewLaborantin(this.simuToLaunch.pop(), machines);
					l.addObserver(getIdentifier(), SimulationEndedMessage.class.getName());
					l.activateWithFipa();
					this.launchedSimu.put(l.getId(), l);
				}
			} catch (NotEnoughMachinesException e) {}
		}
		return true;
	}


	@MessageHandler
	@NotificationEnvelope
	public void collectResult(final NotificationMessage<SimulationEndedMessage> n) throws CompetenceException{
		logMonologue(n.getSender()+" is finished",LogService.onBoth);
		this.launchedSimu.get(n.getSender()).kill(machines);
		this.launchedSimu.remove(n.getSender());
		this.awaitingAnswer--;
		this.logMonologue("Available Memory Before GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
				+" free (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		System.gc();
		this.logMonologue("... After GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
				+" free (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		this.launchSimulation();
	}

	/*
	 * 
	 */
	
	public static void main(final String[] args)
	throws CompetenceException, IllegalArgumentException, IllegalAccessException{
		AgentManagementSystem.initAMS();
		final List machines = new LinkedList<HostIdentifier>();
		machines.add(new HostIdentifier("localhost", 7777));
		Experimentator exp = new Experimentator(machines, new ReplicationExperimentationProtocol());
		exp.activateWithFipa();
		exp.start();
	}
}
