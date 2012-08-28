package negotiation.dcopframework;

import negotiation.dcopframework.daj.Node;
import negotiation.dcopframework.daj.Program;

public class DiCOPmaXNode extends Node{
	
	String networkName;
	DiCOPmaXNodeTask myTask;
			
	public DiCOPmaXNode(Network net, Program prog, String label, int x, int y) {
		super(net, prog, label, x, y);
		
		networkName = net.toString();
		
		myTask = new DiCOPmaXNodeTask(this.toString());
		myTask.setNode(this);
	}

	public DiCOPmaXNode(Network net, Program prog) {
		super(net, prog);
		
		networkName = net.toString();
		
		myTask = new DiCOPmaXNodeTask(this.toString());
		myTask.setNode(this);
	}

	// --------------------------------------------------------------------------
	// return network of node
	// --------------------------------------------------------------------------
	public Network getNetwork() {
		return ((DiCOPmaxNetworkTask) myTask.getTask(networkName)).myNetwork;
	}

}

class DiCOPmaXNodeTask extends DarXAdvancedTask {

	
	private DiCOPmaXNode myNode;
	
	public DiCOPmaXNodeTask(String name) {
		super(name);
	}

	public void setNode(DiCOPmaXNode diCOPmaXNode) {
		this.myNode = diCOPmaXNode;
		
	}
	
}