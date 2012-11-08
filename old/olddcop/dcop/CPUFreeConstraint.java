package frameworks.faulttolerance.olddcop.dcop;


public class CPUFreeConstraint extends MemFreeConstraint{


	public double[][] f;

	private int[] m1;
	private int[] m2;
	private double m;

	public CPUFreeConstraint(ReplicationVariable a, ReplicationVariable b) {
		super(a,b);
		f = new double[d1][d2];
		m = -1;
		m1 = new int[d1];
		m2 = new int[d2];
	}

	
//	public String toString() {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("CONSTRAINT ");
//		buffer.append(first.id);
//		buffer.append(" ");
//		buffer.append(second.id);
//		buffer.append(Helper.newline);
//		for (int i = 0; i < d1; i++) {
//			for (int j = 0; j < d2; j++) {
//				buffer.append("F ");
//				buffer.append(i);
//				buffer.append(" ");
//				buffer.append(j);
//				buffer.append(" ");
//				buffer.append(f[i][j]);
//				buffer.append(Helper.newline);
//			}
//		}
//		return buffer.toString();
//	}

	public double[] encode() {
		double[] msg = new double[4 + d1 * d2];
		msg[0] = first.id;
		msg[1] = d1;
		msg[2] = second.id;
		msg[3] = d2;
		for (int i = 0; i < d1; i++){
			for (int j = 0; j < d2; j++) {
				msg[4 + i * d2 + j] = f[i][j];
			}
		}
		return msg;
	}
	
	public void cache() {
		if (m >= 0)
			return;
		for (int i = 0; i < d1; i++) {
			int best = -1;
			double max = -1;
			for (int j = 0; j < d2; j++) {
				if (f[i][j] > max) {
					max = f[i][j];
					best = j;
				}
			}
			m1[i] = best;
			if (max > m)
				m = max;
		}
		for (int i = 0; i < d2; i++) {
			int best = -1;
			double max = -1;
			for (int j = 0; j < d1; j++) {
				if (f[j][i] > max) {
					max = f[j][i];
					best = j;
				}
			}
			m2[i] = best;
		}
	}

	public double evaluate() {
		//System.out.println("(" + first.id + "," + first.value + ") (" + second.id + "," + second.value + ")");
		if (Helper.app != null)
			Helper.app.numberEval++;
		if (first.getValue() == -1 && second.getValue() == -1)
			return m;
		if (first.getValue() == -1 && second.getValue() != -1){
			if (m2[second.getValue()]==-1)
				return Double.NEGATIVE_INFINITY;
			return f[m2[second.getValue()]][second.getValue()];
		}			
		if (first.getValue() != -1 && second.getValue() == -1){
			if (m1[first.getValue()]==-1)
				return Double.NEGATIVE_INFINITY;
			return f[first.getValue()][m1[first.getValue()]];
		}			
		return f[first.getValue()][second.getValue()];
	}

	public double evaluate(Integer val1, Integer val2) {
		return f[val1][val2];
	}
}
