package negotiation.dcopframework.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import negotiation.dcopframework.algo.*;
import negotiation.dcopframework.daj.Node;
import negotiation.dcopframework.daj.Program;

import negotiation.dcopframework.algo.Algorithm;
import negotiation.dcopframework.algo.BasicAlgorithm;
import negotiation.dcopframework.algo.TerminateMessage;
import negotiation.dcopframework.algo.korig.AlgoKOptOriginal;
import negotiation.dcopframework.algo.topt.AlgoKOptAPO;
import negotiation.dcopframework.algo.topt.AlgoTOptAPO;
import negotiation.dcopframework.daj.Message;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;
import negotiation.dcopframework.dcop.Variable;
import negotiation.dcopframework.exec.Controller;
import negotiation.dcopframework.exec.DCOPApplication;
import negotiation.dcopframework.exec.Stats;

public class DCOPApplication extends Application {
	private static final long serialVersionUID = 380092569934615212L;

	public Graph g;
	Algorithm algo;
	HashMap<Integer, Node> nodeMap;
	private static final int radius = 200;
	
	public int cycles;

	public int numberMessages;
	public int sizeofMessages;
	public int numberEval;
	public int numberConflicts;
	public int wastedCycles;
	
	public int[] quality;
	public int[] msgsize;
	public int[] nummsg;
	
	public int[] nEval;
	public int[] nConflicts;
	public int[] wCycles;
	
	int grouping;
	
	boolean isWin;
	boolean s;
	int ws;

	public int activetnodeT;
	public double totalLockReq;
	public int[] groupK;
	
	public ArrayList<Stats> allstats;
	
	public void floatingMessage(Message msg) {
		numberMessages++;
		sizeofMessages += msg.getSize();
	}

	public DCOPApplication(String filename, int r, int cycles, int kort, Algorithm a, boolean isWin, boolean s, int ws) {
		super("DCOP Application", 2 * radius, 2 * radius);

		numberMessages = 0;
		sizeofMessages = 0;
	
		numberEval = 0;
		numberConflicts = 0;
		wastedCycles = 0;
		
		g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;		
		
		quality = new int[cycles];
		msgsize = new int[cycles];
		nummsg = new int[cycles];		
		nEval = new int[cycles];
		nConflicts = new int[cycles];
		wCycles = new int[cycles];
		
		Helper.app = this;		
		this.cycles = cycles;
		
		this.isWin = isWin;
		this.s = s;
		this.ws = ws;
	}

	public DCOPApplication(String filename, int cycles, int kort, Algorithm a, boolean isWin, boolean s, int ws) {
		super();

		numberMessages = 0;
		sizeofMessages = 0;

		numberEval = 0;
		numberConflicts = 0;
		wastedCycles = 0;
		
		g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;
		quality = new int[cycles];
		msgsize = new int[cycles];
		nummsg = new int[cycles];
		nEval = new int[cycles];
		nConflicts = new int[cycles];
		wCycles = new int[cycles];
		
		Helper.app = this;		
		this.cycles = cycles;
		
		this.isWin = isWin;
		this.s = s;
		this.ws = ws;
	}
	public int[] getQuality() {
		return quality;
	}
	public int[] getMsgsize() {
		return msgsize;
	}
	public int[] getNummsg() {
		return nummsg;
	}
	public int[] getNConflicts() {
		return nConflicts;
	}

	public int[] getNEval() {
		return nEval;
	}

	public int[] getWCycles() {
		return wCycles;
	}
	@Override
	public void construct() {
		int n = g.varMap.values().size();
		double delta = 2 * Math.PI / n;
		double angle = 0;
		nodeMap = new HashMap<Integer, Node>();
		Node controller = node(new Controller(this), "Simulator",
				20 + (radius - 30), 20 + (radius - 30));

		for (Variable v : g.varMap.values()) {
			Node node = node(getAlgo(v), "" + v.id, (int) (20 + (radius - 30)
					* (1 + Math.cos(angle))), (int) (20 + (radius - 30)
					* (1 + Math.sin(angle))));
			nodeMap.put(v.id, node);
			angle += delta;
			link(controller, node);
		}
		for (Constraint c : g.conList) {
			Node first = nodeMap.get(c.first.id);
			Node second = nodeMap.get(c.second.id);
			link(first, second);
			link(second, first);
		}
	}

	@Override
	public void resetStatistics() {
		// TODO Auto-generated method stub
	}

	public String getText() {
		return "A DCOP Application";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String temp = args[0];
		DCOPApplication app = null;
		boolean isGUI = true;
		boolean isWin = true;
		boolean s = true;
		int ws = 7;

		int kort = Integer.parseInt(args[2]);		
		Algorithm a = Algorithm.KOPTAPO;
		if (args[1].equalsIgnoreCase("TOPT"))
			a = Algorithm.TOPTAPO;
		else if (args[1].equalsIgnoreCase("KOriginal"))
			a = Algorithm.KOPTORIG;
		
		int cycles = Integer.parseInt(args[3]);
		
		if (isGUI) {
			app = new DCOPApplication(temp, radius, cycles, kort, a, isWin, s, ws);
		} else {
			app = new DCOPApplication(temp, cycles, kort, a, isWin, s, ws);
		}
		
		/*HashMap<Integer, Integer> sol = app.g.branchBoundSolve(); 
		for(Integer i : app.g.varMap.keySet()){ 
			app.g.varMap.get(i).value = sol.get(i);
		}
		System.out.println("BEST " + app.g.evaluate());
		*/
		Helper.app = app;
		app.run();

		for (Integer i : app.nodeMap.keySet()){
			app.g.varMap.get(i).value = ((BasicAlgorithm) app.nodeMap.get(i).getProgram()).getValue();
		}

		System.out.println("Quality:\t" + app.g.evaluate());
		System.out.println("GlobalTime:\t" + app.getNetwork().getScheduler().getTime());		
	}
	
	private BasicAlgorithm getAlgo(Variable v) {
		switch(this.algo){
			case TOPTAPO: 
				if(!isWin){
					return new AlgoTOptAPO(v, grouping);
				}
				else{
					return new AlgoTOptAPO(v, grouping, s, ws);
				}
			case KOPTAPO: 
				if(!isWin){
					return new AlgoKOptAPO(v, grouping);
				}
				else{
					return new AlgoKOptAPO(v, grouping, s, ws);
				}
			case KOPTORIG: return new AlgoKOptOriginal(v, grouping);			
			default: return null;
		}
	}
}

class Controller extends Program {
	private DCOPApplication app;
	public Controller(DCOPApplication a) {
		app = a;
	}
	@Override
	protected void main() {
		boolean done = true;
		int resultid = -1;
		int present = -1;
		while (true) {
			done = true;
			present = getTime();
			if (present > (app.cycles - 1)) {
				out().broadcast(new TerminateMessage());
				break;
			}
			if (present % 1 == 0) {
				Program[] prog = this.node.getNetwork().getPrograms();
				for (int i = 0; i < prog.length; i++) {
					if (prog[i] == this)
						continue;
					BasicAlgorithm p = (BasicAlgorithm) prog[i];
					app.g.varMap.get(p.getID()).value = p.getValue();
					if (!p.isStable())
						done = false;
				}
				if (app.g.checkValues()) {
					app.quality[present] = app.g.evaluate();
					app.nummsg[present] = app.numberMessages;
					app.msgsize[present] = app.sizeofMessages;
					
					app.nEval[present] = app.numberEval;
					app.nConflicts[present] = app.numberConflicts;
					app.wCycles[present] = app.wastedCycles;					
					
					System.out.println(getTime() + "\t" + (app.g.checkValues() ? app.quality[present]
							: "NA") + "\t" 
					//+ app.numberMessages
					//+ "\t" + app.sizeofMessages
					);					
					
					if (resultid != -1) {
						for (int i = resultid + 1; i < present; ++i) {
							app.quality[i] = app.quality[resultid];
							app.nummsg[i] = app.nummsg[resultid];
							app.msgsize[i] = app.msgsize[resultid];
							
							app.nEval[i] = app.nEval[resultid];
							app.nConflicts[i] = app.nConflicts[resultid];
							app.wCycles[i] = app.wCycles[resultid];
						}
					}
					else{
						for(int i = 0; i < present; ++i){
							app.quality[i] = app.quality[present];
							app.nummsg[i] = app.nummsg[present];
							app.msgsize[i] = app.msgsize[present];
							
							app.nEval[i] = app.nEval[present];
							app.nConflicts[i] = app.nConflicts[present];
							app.wCycles[i] = app.wCycles[present];
						}
					}
					resultid = present;				
				}

				/*
				 * if (done) { count++; } else count = 0; if (count == 5) {
				 * out().broadcast(new TerminateMessage()); break; }
				 */
				if (done || present == (app.cycles - 1)) {
					out().broadcast(new TerminateMessage());
					break;
				}
			}
			yield();
		}
		for (int i = resultid + 1; i < app.cycles; ++i) {
			app.quality[i] = app.quality[resultid];
			app.nummsg[i] = app.nummsg[resultid];
			app.msgsize[i] = app.msgsize[resultid];
			
			app.nEval[i] = app.nEval[resultid];
			app.nConflicts[i] = app.nConflicts[resultid];
			app.wCycles[i] = app.wCycles[resultid];
		}
		if(app.algo != Algorithm.KOPTORIG){
			app.activetnodeT = 0;		
			app.allstats = new ArrayList<Stats>();
			app.groupK = new int[app.nodeMap.size()];
			int i = 0;
			for(Node n : app.nodeMap.values()){
				BasicAlgorithm ba = (BasicAlgorithm)n.getProgram(); 			
				app.allstats.addAll(ba.statList);
				if(ba instanceof AlgoTOptAPO && !((AlgoTOptAPO)ba).trivial){
					app.activetnodeT++;
				}	
				if(ba instanceof AlgoKOptAPO){
					AlgoKOptAPO kopt = (AlgoKOptAPO)ba;				
					app.groupK[i++] = kopt.localTreeMap.size();
				}
				app.totalLockReq += ba.nlockReq;
			}
			app.totalLockReq /= app.nodeMap.size();
			Collections.sort(app.allstats);
		}
	}
}
