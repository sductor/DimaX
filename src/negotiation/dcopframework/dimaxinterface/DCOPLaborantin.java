package negotiation.dcopframework.dimaxinterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import negotiation.dcopframework.algo.Algorithm;
import negotiation.dcopframework.daj.Node;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;

import dima.introspectionbasedagents.services.DuplicateCompetenceException;
import dima.introspectionbasedagents.services.UnInstanciedCompetenceException;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class DCOPLaborantin extends APILauncherModule {

	private Graph g;
	private Algorithm algo;
	private HashMap<Integer, Node> nodeMap;
	
	private  int cycles;

	private  int numberMessages;
	private  int sizeofMessages;
	private  int numberEval;
	private  int numberConflicts;
	private  int wastedCycles;
	
	private  int[] quality;
	private  int[] msgsize;
	private  int[] nummsg;
	
	private  int[] nEval;
	private  int[] nConflicts;
	private  int[] wCycles;
	
	private  int grouping;
	
	private  boolean s;
	private int ws;

	private  int activetnodeT;
	private  double totalLockReq;
	private  int[] groupK;
	
	public ArrayList<Stats> allstats;
	
	public DCOPLaborantin(String newId,
			String filename, int cycles, int kort, Algorithm a, boolean isWin, boolean s, int ws) 
			throws UnInstanciedCompetenceException,	DuplicateCompetenceException {
		super(newId);
		
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

	@Override
	public Collection<? extends BasicCompetentAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}
}
