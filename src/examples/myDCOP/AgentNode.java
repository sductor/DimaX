package examples.myDCOP;

import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class AgentNode extends BasicCompetentAgent{

	public AgentNode(Integer newId) throws CompetenceException {
		super(newId.toString());
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -1745198966122356287L;
	// channels to receive messages from respectively send messages to
	private final InChannelSet in = new InChannelSet();
	private final OutChannelSet out = new OutChannelSet();
	private Program program; // program that node executes
	private int switches; // number of context switches occurred so far

	// --------------------------------------------------------------------------
	// create node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	public Node(final Program prog) throws CompetenceException {
		super(new NodeIdentifier(((BasicAlgorithm)prog).getID()));
		this.init(prog);
	}

	// --------------------------------------------------------------------------
	// initialize node with `prog` to execute in network `net`
	// --------------------------------------------------------------------------
	private void init(final Program prog) {
		this.program = prog;
		this.program.setNode(this);
		this.switches = 0;
	}


	@Override
	public NodeIdentifier getIdentifier() {
		return (NodeIdentifier) super.getIdentifier();
	}



	// --------------------------------------------------------------------------
	// increase number of switches
	// --------------------------------------------------------------------------
	public void incSwitches() {
		this.switches++;
	}

	// --------------------------------------------------------------------------
	// get number of switches
	// --------------------------------------------------------------------------
	public int getSwitches() {
		return this.switches;
	}

	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initialisation(){
		((BasicAlgorithm)this.program).initialisation();
	}

	// --------------------------------------------------------------------------
	// executed when node starts execution
	// --------------------------------------------------------------------------
	@StepComposant
	public void run() {
		this.program.main();
	}

	@MessageHandler
	public void receiveMessage(final DcopMessage m){
		this.in.getChannel(m.getSender().asInt()).write(m);
	}
	// --------------------------------------------------------------------------
	// add `channel` to set of inchannels
	// --------------------------------------------------------------------------
	public void inChannel(final Channel channel) {
		this.in.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// add `channel` to set of outchannels
	// --------------------------------------------------------------------------
	public void outChannel(final Channel channel) {
		this.out.addChannel(channel);
	}

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	public String getText() {
		return this.program.getText();
	}

	// --------------------------------------------------------------------------
	// program executed by node
	// --------------------------------------------------------------------------
	public Program getProgram() {
		return this.program;
	}

	// --------------------------------------------------------------------------
	// set of in channels
	// --------------------------------------------------------------------------
	public InChannelSet getIn() {
		return this.in;
	}

	// --------------------------------------------------------------------------
	// set of out channels
	// --------------------------------------------------------------------------
	public OutChannelSet getOut() {
		return this.out;
	}
}
