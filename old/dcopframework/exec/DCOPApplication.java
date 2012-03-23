package negotiation.dcopframework.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import negotiation.dcopframework.algo.Algorithm;
import negotiation.dcopframework.algo.BasicAlgorithm;
import negotiation.dcopframework.algo.TerminateMessage;
import negotiation.dcopframework.algo.korig.AlgoKOptOriginal;
import negotiation.dcopframework.algo.topt.AlgoKOptAPO;
import negotiation.dcopframework.algo.topt.AlgoTOptAPO;
import negotiation.dcopframework.daj.Message;
import negotiation.dcopframework.daj.Node;
import negotiation.dcopframework.daj.Program;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Graph;
import negotiation.dcopframework.dcop.Helper;
import negotiation.dcopframework.dcop.Variable;
import negotiation.dcopframework.dimaxinterface.Stats;
import daj.Application;


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

	public void floatingMessage(final Message msg) {
		this.numberMessages++;
		this.sizeofMessages += msg.getSize();
	}

	public DCOPApplication(final String filename, final int r, final int cycles,
			final int kort, final Algorithm a, final boolean isWin, final boolean s, final int ws) {
		super("DCOP Application", 2 * DCOPApplication.radius, 2 * DCOPApplication.radius);

		this.numberMessages = 0;
		this.sizeofMessages = 0;

		this.numberEval = 0;
		this.numberConflicts = 0;
		this.wastedCycles = 0;

		this.g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;

		this.quality = new int[cycles];
		this.msgsize = new int[cycles];
		this.nummsg = new int[cycles];
		this.nEval = new int[cycles];
		this.nConflicts = new int[cycles];
		this.wCycles = new int[cycles];

		Helper.app = this;
		this.cycles = cycles;

		this.isWin = isWin;
		this.s = s;
		this.ws = ws;
	}

	public DCOPApplication(final String filename, final int cycles, final int kort, final Algorithm a, final boolean isWin, final boolean s, final int ws) {
		super();

		this.numberMessages = 0;
		this.sizeofMessages = 0;

		this.numberEval = 0;
		this.numberConflicts = 0;
		this.wastedCycles = 0;

		this.g = new Graph(filename);
		//algo = Algorithm.MGM1;
		this.grouping = kort;
		this.algo = a;
		this.quality = new int[cycles];
		this.msgsize = new int[cycles];
		this.nummsg = new int[cycles];
		this.nEval = new int[cycles];
		this.nConflicts = new int[cycles];
		this.wCycles = new int[cycles];

		Helper.app = this;
		this.cycles = cycles;

		this.isWin = isWin;
		this.s = s;
		this.ws = ws;
	}
	public int[] getQuality() {
		return this.quality;
	}
	public int[] getMsgsize() {
		return this.msgsize;
	}
	public int[] getNummsg() {
		return this.nummsg;
	}
	public int[] getNConflicts() {
		return this.nConflicts;
	}

	public int[] getNEval() {
		return this.nEval;
	}

	public int[] getWCycles() {
		return this.wCycles;
	}
	@Override
	public void construct() {
		final int n = this.g.varMap.values().size();
		final double delta = 2 * Math.PI / n;
		double angle = 0;
		this.nodeMap = new HashMap<Integer, Node>();
		final Node controller = this.node(new Controller(this), "Simulator",
				20 + DCOPApplication.radius - 30, 20 + DCOPApplication.radius - 30);

		for (final Variable v : this.g.varMap.values()) {
			final Node node = this.node(this.getAlgo(v), "" + v.id, (int) (20 + (DCOPApplication.radius - 30)
					* (1 + Math.cos(angle))), (int) (20 + (DCOPApplication.radius - 30)
							* (1 + Math.sin(angle))));
			this.nodeMap.put(v.id, node);
			angle += delta;
			this.link(controller, node);
		}
		for (final Constraint c : this.g.conList) {
			final Node first = this.nodeMap.get(c.first.id);
			final Node second = this.nodeMap.get(c.second.id);
			this.link(first, second);
			this.link(second, first);
		}
	}

	@Override
	public void resetStatistics() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getText() {
		return "A DCOP Application";
	}
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String temp = args[0];
		DCOPApplication app = null;
		final boolean isGUI = true;
		final boolean isWin = true;
		final boolean s = true;
		final int ws = 7;

		final int kort = Integer.parseInt(args[2]);
		Algorithm a = Algorithm.KOPTAPO;
		if (args[1].equalsIgnoreCase("TOPT")) {
			a = Algorithm.TOPTAPO;
		} else if (args[1].equalsIgnoreCase("KOriginal")) {
			a = Algorithm.KOPTORIG;
		}

		final int cycles = Integer.parseInt(args[3]);

		if (isGUI) {
			app = new DCOPApplication(temp, DCOPApplication.radius, cycles, kort, a, isWin, s, ws);
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

		for (final Integer i : app.nodeMap.keySet()) {
			app.g.varMap.get(i).value = ((BasicAlgorithm) app.nodeMap.get(i).getProgram()).getValue();
		}

		System.out.println("Quality:\t" + app.g.evaluate());
		System.out.println("GlobalTime:\t" + app.getNetwork().getScheduler().getTime());
	}

	private BasicAlgorithm getAlgo(final Variable v) {
		switch(this.algo){
		case TOPTAPO:
			if(!this.isWin) {
				return new AlgoTOptAPO(v, this.grouping);
			} else {
				return new AlgoTOptAPO(v, this.grouping, this.s, this.ws);
			}
		case KOPTAPO:
			if(!this.isWin) {
				return new AlgoKOptAPO(v, this.grouping);
			} else {
				return new AlgoKOptAPO(v, this.grouping, this.s, this.ws);
			}
		case KOPTORIG: return new AlgoKOptOriginal(v, this.grouping);
		default: return null;
		}
	}
}

class Controller extends Program {
	private final DCOPApplication app;
	public Controller(final DCOPApplication a) {
		this.app = a;
	}
	@Override
	protected void main() {
		boolean done = true;
		int resultid = -1;
		int present = -1;
		while (true) {
			done = true;
			present = this.getTime();
			if (present > this.app.cycles - 1) {
				this.out().broadcast(new TerminateMessage());
				break;
			}
			if (present % 1 == 0) {
				final Program[] prog = this.node.getNetwork().getPrograms();
				for (final Program element : prog) {
					if (element == this) {
						continue;
					}
					final BasicAlgorithm p = (BasicAlgorithm) element;
					this.app.g.varMap.get(p.getID()).value = p.getValue();
					if (!p.isStable()) {
						done = false;
					}
				}
				if (this.app.g.checkValues()) {
					this.app.quality[present] = this.app.g.evaluate();
					this.app.nummsg[present] = this.app.numberMessages;
					this.app.msgsize[present] = this.app.sizeofMessages;

					this.app.nEval[present] = this.app.numberEval;
					this.app.nConflicts[present] = this.app.numberConflicts;
					this.app.wCycles[present] = this.app.wastedCycles;

					System.out.println(this.getTime() + "\t" + (this.app.g.checkValues() ? this.app.quality[present]
							: "NA") + "\t"
							//+ app.numberMessages
							//+ "\t" + app.sizeofMessages
							);

					if (resultid != -1) {
						for (int i = resultid + 1; i < present; ++i) {
							this.app.quality[i] = this.app.quality[resultid];
							this.app.nummsg[i] = this.app.nummsg[resultid];
							this.app.msgsize[i] = this.app.msgsize[resultid];

							this.app.nEval[i] = this.app.nEval[resultid];
							this.app.nConflicts[i] = this.app.nConflicts[resultid];
							this.app.wCycles[i] = this.app.wCycles[resultid];
						}
					} else {
						for(int i = 0; i < present; ++i){
							this.app.quality[i] = this.app.quality[present];
							this.app.nummsg[i] = this.app.nummsg[present];
							this.app.msgsize[i] = this.app.msgsize[present];

							this.app.nEval[i] = this.app.nEval[present];
							this.app.nConflicts[i] = this.app.nConflicts[present];
							this.app.wCycles[i] = this.app.wCycles[present];
						}
					}
					resultid = present;
				}

				/*
				 * if (done) { count++; } else count = 0; if (count == 5) {
				 * out().broadcast(new TerminateMessage()); break; }
				 */
				if (done || present == this.app.cycles - 1) {
					this.out().broadcast(new TerminateMessage());
					break;
				}
			}
			this.yield();
		}
		for (int i = resultid + 1; i < this.app.cycles; ++i) {
			this.app.quality[i] = this.app.quality[resultid];
			this.app.nummsg[i] = this.app.nummsg[resultid];
			this.app.msgsize[i] = this.app.msgsize[resultid];

			this.app.nEval[i] = this.app.nEval[resultid];
			this.app.nConflicts[i] = this.app.nConflicts[resultid];
			this.app.wCycles[i] = this.app.wCycles[resultid];
		}
		if(this.app.algo != Algorithm.KOPTORIG){
			this.app.activetnodeT = 0;
			this.app.allstats = new ArrayList<Stats>();
			this.app.groupK = new int[this.app.nodeMap.size()];
			int i = 0;
			for(final Node n : this.app.nodeMap.values()){
				final BasicAlgorithm ba = (BasicAlgorithm)n.getProgram();
				this.app.allstats.addAll(ba.statList);
				if(ba instanceof AlgoTOptAPO && !((AlgoTOptAPO)ba).trivial) {
					this.app.activetnodeT++;
				}
				if(ba instanceof AlgoKOptAPO){
					final AlgoKOptAPO kopt = (AlgoKOptAPO)ba;
					this.app.groupK[i++] = kopt.localTreeMap.size();
				}
				this.app.totalLockReq += ba.nlockReq;
			}
			this.app.totalLockReq /= this.app.nodeMap.size();
			Collections.sort(this.app.allstats);
		}
	}
}
