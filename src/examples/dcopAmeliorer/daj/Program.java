// ----------------------------------------------------------------------------
// $Id: Program.java,v 1.3 1997/11/14 16:38:59 schreine Exp schreine $
// program executed on network node
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcopAmeliorer.daj;


public abstract class Program {

	// node on which program executes
	public Node node = null;

	// --------------------------------------------------------------------------
	// set program node to `n`
	// --------------------------------------------------------------------------
	protected void setNode(final Node n) {
		this.node = n;
	}

	// --------------------------------------------------------------------------
	// user program executed by node
	// --------------------------------------------------------------------------
	protected abstract void main();

	// --------------------------------------------------------------------------
	// status text displayed for node
	// --------------------------------------------------------------------------
	protected String getText() {
		return "(no information)";
	}

	// --------------------------------------------------------------------------
	// return output channel set
	// --------------------------------------------------------------------------
	final public OutChannelSet out() {
		return this.node.getOut();
	}

	// --------------------------------------------------------------------------
	// return output channel numbered `i`
	// --------------------------------------------------------------------------
	final public OutChannel out(final int i) {
		return this.node.getOut().getChannel(i);
	}

	// --------------------------------------------------------------------------
	// return input channel set
	// --------------------------------------------------------------------------
	final public InChannelSet in() {
		return this.node.getIn();
	}

	// --------------------------------------------------------------------------
	// return input channel numbered `i`
	// --------------------------------------------------------------------------
	final public InChannel in(final int i) {
		return this.node.getIn().getChannel(i);
	}

	// --------------------------------------------------------------------------
	// get number of scheduling operations for this node
	// --------------------------------------------------------------------------
	public int getTime() {
		return this.node.getSwitches();
	}

	//	// --------------------------------------------------------------------------
	//	// invoke scheduler
	//	// --------------------------------------------------------------------------
	//	protected void yield() {
	//		node.getNetwork().getScheduler().schedule();
	//	}
	//
	//	// --------------------------------------------------------------------------
	//	// sleep for `n` scheduling periods
	//	// --------------------------------------------------------------------------
	//	protected void sleep(int n) {
	//		for (int i = 0; i < n; i++)
	//			yield();
	//	}
	//
	//	// --------------------------------------------------------------------------
	//	// exit from network execution with code `n`
	//	// --------------------------------------------------------------------------
	//	protected void exit(int n) {
	//		Network network = node.getNetwork();
	//		if (network.getVisualizer() != null)
	//			network.print("Execution has terminated with exit code " + String.valueOf(n));
	//		network.exit(n);
	//	}
	//
	//	// --------------------------------------------------------------------------
	//	// state that global network state fulfills assertion `a`
	//	// --------------------------------------------------------------------------
	//	protected void test(GlobalAssertion a) {
	//		Network network = node.getNetwork();
	//		Program programs[] = network.getPrograms();
	//		if (!a.test(programs)) {
	//			network.print("Global assertion violated: " + a.getText());
	//			network.exit(-1);
	//		}
	//	}
	//
	//	// --------------------------------------------------------------------------
	//	// interrupt execution until continued by user
	//	// --------------------------------------------------------------------------
	//	protected void interrupt() {
	//		Visualizer visualizer = node.getNetwork().getVisualizer();
	//		if (visualizer != null) visualizer.stop();
	//	}
}
