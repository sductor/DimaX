package examples.dcop.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.jdom.JDOMException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.ObservingGlobalService;
import examples.dcop.algo.AlgoMGM1;
import examples.dcop.algo.Algorithm;
import examples.dcop.algo.BasicAlgorithm;
import examples.dcop.algo.korig.AlgoKOptOriginal;
import examples.dcop.algo.topt.AlgoKOptAPO;
import examples.dcop.algo.topt.AlgoTOptAPO;
import examples.dcop.daj.Channel;
import examples.dcop.daj.Node;
import examples.dcop.daj.NodeIdentifier;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Graph;
import examples.dcop.dcop.Variable;

public class DCOPExperimentationParameters extends ExperimentationParameters{

	//
	// Fields
	//

	String filename;
	public static Graph g;
	Algorithm algo;
	public static HashMap<Integer, Node> nodeMap;	
	int grouping;

	//
	// Competence
	//

	@Competence
	GlobalDCOPObservation gdo = new GlobalDCOPObservation();

	//
	// Constructor
	// 

	public DCOPExperimentationParameters(
			String experimentatorId,
			String resultPath){
		super(experimentatorId, resultPath);		
	}
	
	public DCOPExperimentationParameters(
			String experimentatorId,
			String resultPath,
			String filename, 
			int grouping, 
			String algo) {
		super(experimentatorId, resultPath);
		this.filename = filename;
		this.grouping = grouping;

		this.algo = Algorithm.KOPTAPO;
		if (algo.equalsIgnoreCase("TOPT"))
			this.algo = Algorithm.TOPTAPO;
		else if (algo.equalsIgnoreCase("KOriginal"))
			this.algo = Algorithm.KOPTORIG;
	}

	//
	// Accessors
	//

	@Override
	public Integer getNumberOfAgentPerMachine() {
		return Integer.MAX_VALUE;
	}

	@Override
	protected GlobalDCOPObservation getGlobalObservingService() {
		return gdo;
	}

	//
	// Protocol
	//

	@Override
	public LinkedList<ExperimentationParameters> generateSimulation(String[] args) {
		LinkedList<ExperimentationParameters> expPs = new LinkedList<ExperimentationParameters>();
		expPs.add(new DCOPExperimentationParameters(this.experimentatorId, this.filename, "conf/1.dcop", 1, "TOPT"));
		return expPs;
	}

	//
	// Instanciation
	//

	@Override
	public void initiateParameters() {

		g = new Graph(filename);
		nodeMap = new HashMap<Integer, Node>();
	}

	@Override
	public boolean isInitiated() {
		return g!=null;
	}

	@Override
	protected Collection<? extends BasicCompetentAgent> instanciate()
			throws IfailedException, CompetenceException {

		for (Variable v : g.varMap.values()) {
			Node node = new Node(getAlgo(v));
			nodeMap.put(v.id, node);
			//			Channel.link(controller, node);
		}

		for (Constraint c : g.conList) {
			Node first = nodeMap.get(c.first.id);
			Node second = nodeMap.get(c.second.id);
			Channel.link(first, second);
		}

		for (Node ag : nodeMap.values()){
			getGlobalObservingService().appIsStable.put(ag.getIdentifier(), false);
		}

		assert verification();
		return nodeMap.values();
	}


	private BasicAlgorithm getAlgo(Variable v) {
//		return new AlgoMGM1(v);
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
			assert Graph.constraintExist(g, c.first.id, c.second.id);

			assert ((BasicAlgorithm) nodeMap.get(c.first.id).getProgram()).view.varMap.get(c.second.id)!=null;
			assert ((BasicAlgorithm) nodeMap.get(c.second.id).getProgram()).view.varMap.get(c.first.id)!=null;

			assert nodeMap.get(c.first.id).getIn().getChannel(c.second.id)!=null;
			assert nodeMap.get(c.second.id).getIn().getChannel(c.first.id)!=null;
			assert nodeMap.get(c.first.id).getOut().getChannel(c.second.id)!=null;
			assert nodeMap.get(c.second.id).getOut().getChannel(c.first.id)!=null;
		}

		for (BasicCompetentAgent a : nodeMap.values()){
			Node n = (Node) a;
			for (Channel c : n.getIn().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(g, c.getOwner().getIdentifier().asInt(), c.getNeighbor().asInt());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor().asInt());	
			}
			for (Channel c : n.getOut().getChannels()){
				assert c.getOwner().equals(n);
				assert Graph.constraintExist(g, c.getOwner().getIdentifier().asInt(), c.getNeighbor().asInt());
				assert ((BasicAlgorithm)n.getProgram()).view.varMap.containsKey(c.getNeighbor().asInt());	
			}
		}

		return true;
	}

	//
	// Main
	//

	public static void main(final String[] args) throws CompetenceException, IllegalArgumentException, IllegalAccessException, JDOMException, IOException{
		final Experimentator exp = new Experimentator(new DCOPExperimentationParameters("ziDcopExperimentator","dcopResult"));
		exp.run(args);
	}
}
