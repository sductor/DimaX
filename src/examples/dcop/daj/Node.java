// ----------------------------------------------------------------------------
// $Id: Node.java,v 1.4 1997/11/14 16:38:59 schreine Exp schreine $
// components of network
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop.daj;

import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import examples.dcop.algo.BasicAlgorithm;


public class Node extends BasicCompetentAgent {

	// channels to receive messages from respectively send messages to
	private InChannelSet in = new InChannelSet();
	private OutChannelSet out = new OutChannelSet();
	private Program program; // program that node executes
	private int switches; // number of context switches occurred so far

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(Program prog) throws CompetenceException {
		super(new NodeIdentifier(((BasicAlgorithm)prog).getID()));
		init(prog);
	}

	// --------------------------------------------------------------------------
	// initialize node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	private void init(Program prog) {
		program = prog;
		program.setNode(this);
		switches = 0;
	}


	@Override
	public NodeIdentifier getIdentifier() {
		return (NodeIdentifier) super.getIdentifier();
	}
	
	
	
	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return switches;
	}

	//
	// Behavior
	//
	
	@ProactivityInitialisation
	public void initialisation(){
		((BasicAlgorithm)program).initialisation();
	}
	
	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@StepComposant
	public void run() {
		program.main();
	}

	@MessageHandler
	public void receiveMessage(DcopMessage m){
		in.getChannel(m.getSender().asInt()).write(m);
	}
	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(Channel channel) {
		in.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(Channel channel) {
		out.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return out;
	}
}
