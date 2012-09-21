package frameworks;

import java.util.Random;

import frameworks.faulttolerance.solver.ResourceAllocationProblem;

public class tewt {

	
	public static void main(String[] args) {
		int n = 3,m = 2;
		
		double[] x=new double[n*m];
		double[] y=new double[n*m];
		double t;
		double[] w = new double[n];
		double[] l = new double[m];
		
		long seed = 5564;
		Random rand = new Random(seed);

		for (int i = 0; i < n; i++){
			w[i]=rand.nextInt(10);			
		}
		for (int j = 0; j < m; j++){
			l[j]=rand.nextDouble();
		}
		
		Boolean okU=null;
		boolean testU;
		Boolean okN=null;
		boolean testN;
		boolean terminatedU=false;
		boolean terminatedN=false;
		while (!terminatedU && !terminatedN){
			generateVector(n, m, x, y, rand);
			
			for (int k = 0; k < 10; k++){
				t = rand.nextDouble();
				testU = getUtilWelfare1(n, m, x, y, t, w, l)<getUtilWelfare2(n, m, x, y, t, w, l);
				testN = getNashWelfare1(n, m, x, y, t, w, l)<getNashWelfare2(n, m, x, y, t, w, l);
				
				if (okU==null){
					System.out.println(
							"utillllllllllllll\n"+
							"x is :\n --->"+ResourceAllocationProblem.print(x)
							+"\ny is :\n --->"+ResourceAllocationProblem.print(y)
							+"\nt is :  "+t
							+"\ntest is : "+testU);
					okU = testU;
				} else {
					if (okU != testU){
						System.out.println(
								"utillllllllllllll\n"+
								"x is :\n --->"+ResourceAllocationProblem.print(x)
								+"\ny is :\n --->"+ResourceAllocationProblem.print(y)
								+"\nt is :  "+t
								+"\ntest is : "+testU);
						terminatedU=true;
					}
				}
				
				if (okN==null){
					System.out.println(
							"nadh\n"+
							"x is :\n --->"+ResourceAllocationProblem.print(x)
							+"\ny is :\n --->"+ResourceAllocationProblem.print(y)
							+"\nt is :  "+t
							+"\ntest is : "+testN);
					okN = testN;
				} else {
					if (okN != testN){
						System.out.println(
								"nash\n"+
								"x is :\n --->"+ResourceAllocationProblem.print(x)
								+"\ny is :\n --->"+ResourceAllocationProblem.print(y)
								+"\nt is :  "+t
								+"\ntest is : "+testN);
						terminatedN=true;
					}
				}				
			}			
		}
	}

	public static void generateVector(int n, int m, double[] x, double[] y, Random rand){
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				x[getPos(i, j, m)] = rand.nextDouble();
				y[getPos(i, j, m)] = rand.nextDouble();
			}			
		}
	}
	public static double getUtilWelfare1(int n, int m, double[] x, double[] y, double t, double[] w, double[] l){
		double value1=0;
		for (int i = 0; i < n; i++){
			value1+=t*w[i]*(1-getF(i,m,x,l));
		}
		double value2=0;
		for (int i = 0; i < n; i++){
			value2+=(1-t)*w[i]*(1-getF(i,m,y,l));
		}
		return value1+value2;
	}

	public static double getUtilWelfare2(int n, int m, double[] x, double[] y, double t, double[] w, double[] l){
		double value=0;
		for (int i = 0; i < n; i++){
			value+=w[i]*(1-getF(i,m,x,y,t,l));
		}
		return value;
	}
	public static double getNashWelfare1(int n, int m, double[] x, double[] y, double t, double[] w, double[] l){
		double value1=1;
		for (int i = 0; i < n; i++){
			value1*=t*w[i]*(1-getF(i,m,x,l));
		}
		double value2=1;
		for (int i = 0; i < n; i++){
			value2*=(1-t)*w[i]*(1-getF(i,m,y,l));
		}
		return value1+value2;
	}

	public static double getNashWelfare2(int n, int m, double[] x, double[] y, double t, double[] w, double[] l){
		double value=1;
		for (int i = 0; i < n; i++){
			value*=w[i]*(1-getF(i,m,x,y,t,l));
		}
		return value;
	}
	public static double getF(int i, int m, double[] x, double[] l){
		double f=1;
		for (int j = 0; j < m; j++){
			f*=Math.pow(l[j], x[getPos(i,j,m)]);
		}
		return f;
	}
	
	public static double getF(int i, int m, double[] x, double[] y, double t, double[] l){
		double f=1;
		for (int j = 0; j < m; j++){
			f*=Math.pow(l[j], t*x[getPos(i,j,m)]+(1-t)*y[getPos(i,j,m)]);
		}
		return f;
	}
		
	protected static int getPos(int agent_i, int host_j, int m) {
		return agent_i*m+host_j;
	}
}
