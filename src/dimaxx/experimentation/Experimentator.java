package dimaxx.experimentation;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.jdom.JDOMException;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.launch.APIAgent;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;


/**
 * The experimentator is in charge of sequencially distribute the different experiences  launch
 * that are modelled as Laborantin.
 * The different experiences to launch are provided with the experimentation protocol
 *
 * @author Sylvain Ductor
 *
 */
public final class Experimentator extends APIAgent{
	private static final long serialVersionUID = 6985131313855716524L;

	//
	// Accessors
	//

	ExperimentationParameters myProtocol;

	//	//Integer represent the sum of the number of agent of each simulation that uses the given machine
	//	public final MachineNetwork machines;

	/*
	 *
	 */

	public LinkedList<ExperimentationParameters> allSimu=new LinkedList();
	public LinkedList<ExperimentationParameters> simuToLaunch;
	final ExperimentLogger el;
	int iteartiontime;

	//	public final Map<AgentIdentifier, Laborantin> launchedSimu =
	//			new HashMap<AgentIdentifier, Laborantin>();
	public int awaitingAnswer;

	//
	// Constructor
	//

	public Experimentator(final ExperimentationParameters myProtocol, final ExperimentLogger el, final int iteartiontime) throws CompetenceException {
		super(myProtocol.experimentatorId);
		//		this.machines = new MachineNetwork(machines);
		//		Writing.log(
		//				this.f,
		//				myProtocol.getDescription(),
		//				true, false);
		this.myProtocol=myProtocol;
		this.el=el;
		this.iteartiontime=iteartiontime;
	}

	//
	// Methods
	//


	@ProactivityInitialisation
	public void initialise() throws CompetenceException{
		this.logWarning("Experimentator created for: "+this.simuToLaunch.size()+" experiences to launch! ",LogService.onBoth);//+" will use :"+getApi().getAvalaibleHosts());this.myProtocol.toString()
		this.logWarning(this.getDescription(),LogService.onBoth);
		this.launchSimulation();
	}

	//Executed initially then called by collect result
	public boolean launchSimulation() throws CompetenceException{
		this.logMonologue("Launching simulations --> Available Memory :"
				+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory(),LogService.onBoth);
		//		this.logWarning("--------------> Used Memory (MO) : "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024)/1000000,LogService.onBoth);

		if (this.awaitingAnswer==0){
			//Toute les expériences sont faites!!

			LogService.logOnFile(this.myProtocol.finalResultPath, "\n\nIteration number -"+this.iteartiontime+" : \n", true, false);
			//			el.write(myProtocol.finalResultPath);
			this.iteartiontime--;

			if (this.iteartiontime==0){
				this.logWarning("yyyyyyyyeeeeeeeeeeeeaaaaaaaaaaaaahhhhhhhhhhh!!!!!!!!!!!",LogService.onBoth);
				this.setAlive(false);
				//			this.logWarning(this.myProtocol.toString(),LogService.onBoth);
				System.exit(1);
			} else {
				this.simuToLaunch.addAll(this.allSimu);
				this.awaitingAnswer=this.simuToLaunch.size();
				this.launchSimulation();
			}
		} else if (!this.simuToLaunch.isEmpty()){
			//On lance de nouvelles expériences!

			//			this.logWarning("launching new exp"+this.getLocations(),LogService.onBoth);
			ExperimentationParameters nextSimu = null;
			try {
				//				while (!this.simuToLaunch.isEmpty()){

				nextSimu = this.simuToLaunch.pop();
				Laborantin l;
				try {

					ExperimentationParameters.currentlyInstanciating=true;
					l = nextSimu.createLaborantin(this.getApi());//new Laborantin(nextSimu, this.getApi(), nextSimu.getNumberOfAgentPerMachine());
					ExperimentationParameters.currentlyInstanciating=false;
					l.addObserver(this.getIdentifier(), SimulationEndedMessage.class);
					this.launch(l);
					this.startActivity(l);
					this.logMonologue(l.getId()+" is started",LogService.onBoth);
					//					this.launchedSimu.put(l.getId(), l);
				} catch (final IfailedException e) {
					this.logWarning("ABORTED!!!!!!!!!!!!!! : EXPERIMENTATION "+nextSimu+" ("+e+")", LogService.onBoth);
					this.awaitingAnswer--;
					this.launchSimulation();
				}

				//!!!!!!!!!!!!!!!!!!!!!!!!!!!! DECOMMENT THE FOLLOWING LINE TO LAUNCH MULTIPLE SIMULATIONS :
				//				launchSimulation();
			} catch (final NotEnoughMachinesException e) {
				this.simuToLaunch.add(nextSimu);
				this.logWarning("aaaaaaaaarrrrrrrrrrrrrrrrggggggghhhhhhhhhh : not ennough machine, retrying\n"+this.getLocations(),LogService.onBoth);
			}
		}
		return true;
	}




	@MessageHandler
	@NotificationEnvelope
	public void collectResult(final NotificationMessage<SimulationEndedMessage> n) throws CompetenceException{
		this.logMonologue(n.getSender()+" is finished",LogService.onBoth);
		this.logWarning(n.getSender()+" receive finish",LogService.onBoth);
		//		this.launchedSimu.get(n.getSender()).kill();
		//		this.launchedSimu.remove(n.getSender());
		//		laborantinLauncher.destroy(n.getSender());
		this.awaitingAnswer--;
		this.el.addAndWriteResults(n.getNotification().getOgs(),this.myProtocol.finalResultPath);
		//		this.logMonologue("Available Memory Before GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
		//				+" used (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		System.gc();
		//		this.logMonologue("--> Available Memory After GC :"+Runtime.getRuntime().freeMemory()+"/"+Runtime.getRuntime().totalMemory()
		//				+" used (ko): "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()/1024),LogService.onBoth);
		this.launchSimulation();
	}

	/*
	 * MAIN
	 */


	public void run(final String[] args)
			throws CompetenceException, IllegalArgumentException,
			IllegalAccessException, JDOMException, IOException,
			NotEnoughMachinesException, IfailedException{

		if (args[0].equals("scheduled")) {
			this.initAPI(false);//SCHEDULED
		} else if  (args[0].equals("fipa")) {
			this.initAPI(true);//FIPA
		} else if  (args[0].equals("darx")) {
			this.initAPI(7779,7778);//DARX LOCAL
		} else if  (args[0].equals("deployed")) {
			this.initAPI("lip6.xml");//DARX Deployed
		} else {
			throw new RuntimeException("unknonw args");
		}

		this.myProtocol.setMyAgent(this);
		this.simuToLaunch = this.myProtocol.generateSimulation();
		this.allSimu.addAll(this.simuToLaunch);
		Collections.sort(this.allSimu);
		this.awaitingAnswer=this.simuToLaunch.size();

		if (args[1].equals("nolog")) {
			LogService.setLog(false);
		} else if (args[1].equals("log")) {
			LogService.setLog(true);
		} else {
			throw new RuntimeException("unknonw args");
		}
		this.launchMySelf();
	}

	public String getDescription(){
		return "Experimentator of "+this.simuToLaunch.size()+" experiences of "+ExperimentationParameters._maxSimulationTime+" seconds ";
	}
}


//		final List machines = new LinkedList<HostIdentifier>();
//		machines.add(new HostIdentifier("localhost", 7777));
