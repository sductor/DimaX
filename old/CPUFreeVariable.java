package frameworks.faulttolerance.dcop.dcop;

import frameworks.negotiation.rationality.AgentState;


public class CPUFreeVariable extends ReplicationVariable {


	public CPUFreeVariable(int i, int d, AgentState s, DcopReplicationGraph g) {
		super(i,d,s,g);
	}
	//	public ClassicalVariable(String s, DcopAbstractGraph g) {
	//		super(Integer.parseInt(s.split(" ")[1]),Integer.parseInt(s.split(" ")[3]),g);
	//		assert g!=null;
	//		init();
	//	}


	public double evaluate() {
		if (getValue() == -1) {
			return -1;
		}
		double reward = 0;
		for (ReplicationConstraint c : getNeighbors()) {
			double v = c.evaluate();
			if (v == -1)
				return -1;
			reward += v;
		}
		return reward;
	}
	//	public double evaluate() {
	//		if (getValue() == -1) {
	//			return -1;
	//		}
	//		double reward = 0;
	//		for (ReplicationConstraint c : getNeighbors()) {
	//			double v = c.evaluate();
	//			if (v == -1)
	//				return -1;
	//			reward += v;
	//		}
	//		return reward;
	//	}
	@Override
	public int getDomain() {
		return domain;
	}
}
