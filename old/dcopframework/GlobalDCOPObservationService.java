package framework.dcop.dimaxinterface;

import java.util.ArrayList;
import java.util.Collections;

import framework.dcop.algo.Algorithm;
import framework.dcop.algo.BasicAlgorithm;
import framework.dcop.algo.TerminateMessage;
import framework.dcop.algo.topt.AlgoKOptAPO;
import framework.dcop.algo.topt.AlgoTOptAPO;
import framework.dcop.dimaxdaj.Node;
import framework.dcop.dimaxdaj.Program;


public class GlobalDCOPObservationService {

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

	public int cycles;
	public int activetnodeT;
	public double totalLockReq;
	public int[] groupK;


	public ArrayList<Stats> allstats;
	
	public GlobalDCOPObservationService(){

		numberMessages = 0;
		sizeofMessages = 0;
	
		numberEval = 0;
		numberConflicts = 0;
		wastedCycles = 0;
		
		quality = new int[cycles];
		msgsize = new int[cycles];
		nummsg = new int[cycles];		
		nEval = new int[cycles];
		nConflicts = new int[cycles];
		wCycles = new int[cycles];
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
