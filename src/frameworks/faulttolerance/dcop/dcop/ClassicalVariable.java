package frameworks.faulttolerance.dcop.dcop;


public class ClassicalVariable extends AbstractVariable<Integer> {

	
	public ClassicalVariable(int i, int d, DcopAbstractGraph<Integer> g) {
		super(i,d,g);
	}
	public ClassicalVariable(String s, DcopAbstractGraph<Integer> g) {
		super(Integer.parseInt(s.split(" ")[1]),Integer.parseInt(s.split(" ")[3]),g);
		assert g!=null;
		init();
	}

	public double evaluate() {
		if (getValue() == -1) {
			return -1;
		}
		double reward = 0;
		for (AbstractConstraint<Integer> c : neighbors) {
			double v = c.evaluate();
			if (v == -1)
				return -1;
			reward += v;
		}
		return reward;
	}
	@Override
	public int getDomain() {
		return domain;
	}
}
