package framework.dcop.dimaxinterface;

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
import examples.introspectionExamples.SimpleMessage;
import framework.dcop.algo.*;
import framework.dcop.algo.korig.AlgoKOptOriginal;
import framework.dcop.algo.topt.AlgoKOptAPO;
import framework.dcop.algo.topt.AlgoTOptAPO;
import framework.dcop.dcop.Constraint;
import framework.dcop.dcop.Graph;
import framework.dcop.dcop.Variable;
import framework.dcop.dimaxdaj.Channel;
import framework.dcop.dimaxdaj.Node;
import framework.experimentation.ExperimentationParameters;
import framework.experimentation.ExperimentationResults;
import framework.experimentation.IfailedException;
import framework.experimentation.Laborantin;
import framework.experimentation.ObservingGlobalService;
import framework.experimentation.SimulationEndedMessage;



public class DCOPApplication extends APIAgent {
	private static final long serialVersionUID = 380092569934615212L;

	public Graph g;
	Algorithm algo;
	HashMap<Integer, Node> nodeMap;	
	HashMap<AgentIdentifier,Boolean> appIsStable = new HashMap<AgentIdentifier, Boolean>();
	int grouping;



	public DCOPApplication(String filename, int kort, Algorithm a) throws CompetenceException {
		super("DCOP Application "+filename);
		g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;		
	}



	public void construct() throws CompetenceException {
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
			appIsStable.put(ag.getIdentifier(), false);
		}
	}


	public String getText() {
		return "A DCOP Application";
	}
	/**
	 * @param args
	 * @throws CompetenceException 
	 */
	public static void main(String[] args) throws CompetenceException{
		String temp = args[0];
		DCOPApplication app = null;

		int kort = Integer.parseInt(args[2]);		
		Algorithm a = Algorithm.KOPTAPO;
		if (args[1].equalsIgnoreCase("TOPT"))
			a = Algorithm.TOPTAPO;
		else if (args[1].equalsIgnoreCase("KOriginal"))
			a = Algorithm.KOPTORIG;

//		int cycles = Integer.parseInt(args[3]);

		app = new DCOPApplication(temp, kort, a);

		/*HashMap<Integer, Integer> sol = app.g.branchBoundSolve(); 
		for(Integer i : app.g.varMap.keySet()){ 
			app.g.varMap.get(i).value = sol.get(i);
		}
		System.out.println("BEST " + app.g.evaluate());
		 */
//		app.run();
		app.construct();
		app.initAPI(false);//SCHEDULED
		app.launchMySelf();
			
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

	@MessageHandler
	@NotificationEnvelope(BasicAlgorithm.stabilityNotificationKey)
	public void receiveStability(final NotificationMessage<Boolean> m){
		appIsStable.put(m.getSender(), m.getNotification());
	}
	
	@StepComposant()
	@Transient
	public boolean endSimulation(){
		for (Boolean b : appIsStable.values()){
			if (b == false)
				return false;
		}
		
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



//	@Override
//	protected ObservingGlobalService getGlobalObservingService() {
//		// TODO Auto-generated method stub
//		return null;
//	}
}