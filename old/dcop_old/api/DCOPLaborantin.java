package examples.dcop_old.api;

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
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.SimulationEndedMessage;
import examples.dcop_old.algo.*;
import examples.dcop_old.algo.korig.AlgoKOptOriginal;
import examples.dcop_old.algo.topt.AlgoKOptAPO;
import examples.dcop_old.algo.topt.AlgoTOptAPO;
import examples.dcop_old.daj.Channel;
import examples.dcop_old.daj.Node;
import examples.dcop_old.daj.NodeIdentifier;
import examples.dcop_old.dcop.Constraint;
import examples.dcop_old.dcop.Graph;
import examples.dcop_old.dcop.Variable;
import examples.introspectionExamples.SimpleMessage;



public class DCOPLaborantin extends Laborantin {
	private static final long serialVersionUID = 380092569934615212L;

	//
	// Fields
	//

	public static Graph g;
	Algorithm algo;
	public static HashMap<Integer, Node> nodeMap;	
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


		nodeMap = new HashMap<Integer, Node>();

		for (Variable v : g.varMap.values()) {
			Node node = new Node("" + v.id, getAlgo(v));
			nodeMap.put(v.id, node);
			//			Channel.link(controller, node);
		}

		for (Constraint c : g.conList) {
			Node first = nodeMap.get(c.first.id);
			Node second = nodeMap.get(c.second.id);
			Channel.link2ways(first, second);
		}

		for (Node ag : nodeMap.values()){
			addAgent(ag);
			getGlobalObservingService().appIsStable.put(ag.getIdentifier(), false);
		}

		assert verification();
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

	public boolean verification(){

		for (Constraint c : g.conList){
			assert Graph.constraintExist(DCOPLaborantin.g, c.first.id, c.second.id);

			assert ((BasicAlgorithm) nodeMap.get(c.first.id).getProgram()).view.varMap.get(c.second.id)!=null;
			assert ((BasicAlgorithm) nodeMap.get(c.second.id).getProgram()).view.varMap.get(c.first.id)!=null;
			
			assert nodeMap.get(c.first.id).getIn().getChannel(new NodeIdentifier(c.second.id))!=null;
			assert nodeMap.get(c.second.id).getIn().getChannel(new NodeIdentifier(c.first.id))!=null;
			assert nodeMap.get(c.first.id).getOut().getChannel(new NodeIdentifier(c.second.id))!=null;
			assert nodeMap.get(c.second.id).getOut().getChannel(new NodeIdentifier(c.first.id))!=null;
		}

		for (BasicCompetentAgent a : getAgents()){
			Node n = (Node) a;
			for (Channel c : n.getIn().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(DCOPLaborantin.g, c.getOwner().getIdentifier().getAsInt(), c.getNeighbor());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor());	
			}
			for (Channel c : n.getOut().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(DCOPLaborantin.g, c.getOwner().getIdentifier().getAsInt(), c.getNeighbor());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor());	
			}
		}

		return true;
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