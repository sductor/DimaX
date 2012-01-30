package negotiation.experimentationframework;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import negotiation.experimentationframework.Laborantin.NotEnoughMachinesException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.APIAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;


/**
 * The experimentator is in charge of sequencially distribute the different experiences  launch
 * that are modelled as Laborantin.
 * The different experiences to launch are provided with the experimentation protocol
 *
 * @author Sylvain Ductor
 *
 */
public class Experimentator extends APIAgent{
	private static final long serialVersionUID = 6985131313855716524L;

	//
	// Accessors
	//

	ExperimentationProtocol myProtocol;
	final File f;

	//	//Integer represent the sum of the number of agent of each simulation that uses the given machine
	//	public final MachineNetwork machines;
	public static final AgentIdentifier myId = new AgentName("ziExperimentator");

	/*
	 *
	 */

	public final LinkedList<ExperimentationParameters> simuToLaunch;

	public final Map<AgentIdentifier, Laborantin> launchedSimu =
			new HashMap<AgentIdentifier, Laborantin>();
	public int awaitingAnswer;

	//
	// Constructor
	//

	public Experimentator(final ExperimentationProtocol myProtocol) throws CompetenceException {
		super(Experimentator.myId);
		//		this.machines = new MachineNetwork(machines);
		this.f = new File(ReplicationExperimentationProtocol.resultPath);
		//		Writing.log(
		//				this.f,
		//				myProtocol.getDescription(),
		//				true, false);
		this.myProtocol=myProtocol;
		this.simuToLaunch = myProtocol.generateSimulation();
		this.awaitingAnswer=this.simuToLaunch.size();
	}

	//
	// Methods
	//


	@ProactivityInitialisation
	public void initialise() throws CompetenceException{
		this.logMonologue("Experimentator created for:\n"+this.myProtocol.getDescription(),LogService.onBoth);//+" will use :"+getApi().getAvalaibleHosts());
		this.logMonologue("Generated "+this.simuToLaunch.size()+" simus of "
				+ExperimentationProtocol._simulationTime/60000+
				"mins  on "+this.getApi().getAvalaibleHosts().size()+" machine"//+ReplicationExperimentationProtocol.nbSimuPerMAchine+" simu per machine"
				,LogService.onBoth);
		this.launchSimulation();
	}

	//Executed initially then called by collect result
	public boolean launchSimulation() throws CompetenceException{
		this.logMonologue("Launching simulations --> Available Memory :"
				+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory(),LogService.onBoth);
		this.logWarning("--------------> Used Memory (MO) : "+((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024)/1000000),LogService.onBoth);

		if (this.awaitingAnswer==0){
			//Toute les expériences sont faites!!
			System.out.println("1");
			this.logMonologue("yyyyyyyyeeeeeeeeeeeeaaaaaaaaaaaaahhhhhhhhhhh!!!!!!!!!!!",LogService.onBoth);
			this.setAlive(false);
			this.logMonologue(this.myProtocol.getDescription(),LogService.onBoth);
			System.exit(1);
		} else if (!this.simuToLaunch.isEmpty()){
			//On lance de nouvelles expériences!

			this.logMonologue("launching new exp"+this.getLocations(),LogService.onBoth);
			ExperimentationParameters nextSimu = null;
			try {
				//				while (!this.simuToLaunch.isEmpty()){
				nextSimu = this.simuToLaunch.pop();
				final Laborantin l = this.myProtocol.createNewLaborantin(nextSimu, this.getApi());
				l.launchWith(this.getApi());
				this.startActivity(l);
				this.launchedSimu.put(l.getId(), l);
				//				}
				//				launchSimulation();
			} catch (final NotEnoughMachinesException e) {
				this.simuToLaunch.add(nextSimu);
				this.logMonologue("aaaaaaaaarrrrrrrrrrrrrrrrggggggghhhhhhhhhh\n"+this.getLocations(),LogService.onBoth);
			}
		}
		return true;
	}




	@MessageHandler
	@NotificationEnvelope
	public void collectResult(final NotificationMessage<SimulationEndedMessage> n) throws CompetenceException{
		this.logMonologue(n.getSender()+" is finished",LogService.onBoth);
		//		this.launchedSimu.get(n.getSender()).kill();
		this.launchedSimu.remove(n.getSender());
		//		laborantinLauncher.destroy(n.getSender());
		this.awaitingAnswer--;
		this.logMonologue("Available Memory Before GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
				+" used (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		System.gc();
		this.logMonologue("--> Available Memory After GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
				+" used (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		this.launchSimulation();
	}

	/*
	 * MAIN
	 */


	public void run(final String[] args)
			throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		if (args[0].equals("scheduled"))
			this.initAPI(false);//SCHEDULED
		else if  (args[0].equals("fipa"))
			this.initAPI(true);//FIPA
		else if  (args[0].equals("darx"))
			this.initAPI(7779,7778);//DARX LOCAL
		else if  (args[0].equals("deployed"))
			this.initAPI("lip6.xml");//DARX Deployed
		else
			throw new RuntimeException("unknonw args");
		this.launchMySelf();
	}
}


//		final List machines = new LinkedList<HostIdentifier>();
//		machines.add(new HostIdentifier("localhost", 7777));
