package examples.dcop.api;

import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.shells.APIAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.SimulationEndedMessage;
import examples.dcop.algo.*;
import examples.dcop.algo.korig.AlgoKOptOriginal;
import examples.dcop.algo.topt.AlgoKOptAPO;
import examples.dcop.algo.topt.AlgoTOptAPO;
import examples.dcop.daj.Channel;
import examples.dcop.daj.Node;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Graph;
import examples.dcop.dcop.Variable;
import examples.introspectionExamples.SimpleMessage;



public class DCOPLaborantin extends Laborantin {
	private static final long serialVersionUID = 380092569934615212L;

	//
	// Fields
	//

	public Graph g;
	Algorithm algo;
	HashMap<Integer, Node> nodeMap;	
	int grouping;
	
	//
	// Competence
	//

	@Competence
	GlobalDCOPObservation gdo = new GlobalDCOPObservation(this);

	//
	// Constructor
	//


	public DCOPLaborantin(ExperimentationParameters p, APILauncherModule api,
			int numberOfAgentPerMAchine) throws CompetenceException,
			IfailedException, NotEnoughMachinesException {
		super(p, api, numberOfAgentPerMAchine);
	}
	//
	// Accessors
	//

	@Override
	protected GlobalDCOPObservation getGlobalObservingService() {
		return gdo;
	}



	@Override
	protected boolean simulationHasEnded() {
		this.logMonologue("I've finished!!",LogService.onBoth);
		//		this.getGlobalObservingService().writeResult();
		this.wwait(10000);
		this.notify(new SimulationEndedMessage());
		this.sendNotificationNow();
		this.logMonologue("my job is done! cleaning my lab bench...",LogService.onBoth);
		this.setAlive(false);
		//
		//		for (Integer i : app.nodeMap.keySet()){
		//			app.g.varMap.get(i).value = ((BasicAlgorithm) app.nodeMap.get(i).getProgram()).getValue();
		//		}
		//
		//		System.out.println("Quality:\t" + app.g.evaluate());
		//		System.out.println("GlobalTime:\t" + app.getNetwork().getScheduler().getTime());	

		return true;
	}


	//
	// Methods
	//

	@Override
	protected void instanciate(ExperimentationParameters p)
			throws IfailedException, CompetenceException {

		if (p instanceof FiledDCOPExperimentationParameters){
			FiledDCOPExperimentationParameters fp = (FiledDCOPExperimentationParameters) p;
			g = new Graph(fp.getFilename());
			//algo = Algorithm.MGM1;
			this.grouping = fp.getKort();
			this.algo = fp.getAlgo();	
		}


		int n = g.varMap.values().size();
		nodeMap = new HashMap<Integer, Node>();
		//		Node controller = new Node("Simulator",new Controller(this));

		for (Variable v : g.varMap.values()) {
			Node node = new Node("" + v.id, getAlgo(v));
			nodeMap.put(v.id, node);
			//			Channel.link(controller, node);
		}

		for (Constraint c : g.conList) {
			Node first = nodeMap.get(c.first.id);
			Node second = nodeMap.get(c.second.id);
			Channel.link(first, second);
			Channel.link(second, first);
		}

		for (Node ag : nodeMap.values()){
			getGlobalObservingService().appIsStable.put(ag.getIdentifier(), false);
		}
	}


	private BasicAlgorithm getAlgo(Variable v) {
		switch(this.algo){
		case TOPTAPO: 
			return new AlgoTOptAPO(v, grouping);
		case KOPTAPO: 
			return new AlgoKOptAPO(v, grouping);
		case KOPTORIG: 
			return new AlgoKOptOriginal(v, grouping);			
		default: return null;
		}
	}

//	/**
//	 * @param args
//	 * @throws CompetenceException 
//	 */
//	public static void main(String[] args) throws CompetenceException{
//		String temp = args[0];
//		DCOPApplication app = null;
//
//		int kort = Integer.parseInt(args[2]);	
//
//		//		int cycles = Integer.parseInt(args[3]);
//
//		app = new DCOPApplication(temp, kort, a);
//
//		/*HashMap<Integer, Integer> sol = app.g.branchBoundSolve(); 
//		for(Integer i : app.g.varMap.keySet()){ 
//			app.g.varMap.get(i).value = sol.get(i);
//		}
//		System.out.println("BEST " + app.g.evaluate());
//		 */
//		//		app.run();
//		app.construct();
//		app.initAPI(false);//SCHEDULED
//		app.launchMySelf();
//
//	}
}