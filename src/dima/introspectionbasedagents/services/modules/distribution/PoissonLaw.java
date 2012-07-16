package dima.introspectionbasedagents.services.modules.distribution;

import java.util.Random;


public class PoissonLaw {

	/* 1 intervalle de temps : toutes les fois ou eventOccur est appellé.
	 * une exp = n intervalle de temps
	 * en moyenne m pannes par intervalle de temps (m in [0, k])
	 * lambda = eventFrequency * n
	 * k = 1 car au plus une faute par hote par intervalle (reste en panne jusqu'a la réparation a la prochaine étape)
	 */


	public static Double getPoissonLaw(final Double lambda, final int k) {
		return Math.exp(-lambda)*Math.pow(lambda, k)/PoissonLaw.factorial(k);
	}

	public static boolean eventOccur(final Double lambda, final int k) {
		final Random rand = new Random();
		return rand.nextDouble() > PoissonLaw.getPoissonLaw(lambda, k);
	}


	//
	// Primitive
	//


	// Evaluate n!
	private static long factorial( final int n )
	{
		return PoissonLaw.factorial_aux(n, 1);
	}

	// Evaluate n!
	private static long factorial_aux( final int n, final long accu )
	{
		if( n <= 1 ) {
			return accu;
		} else {
			return PoissonLaw.factorial_aux( n - 1, n * accu );
		}
	}
}


//private double minInclude; //lower bound (included)
//private double maxExclude; //upper bound (Excluded)
//private int nbfaults;   //number of fault that may appear


///*
//private List<IntervalAndfaults> nbFaultProbability;
//
///**
//*
//* @param lambda law parameter
//* @param nbclass number of faults that may occur simultaneously (in one minute)
//*
//*/
//public LoiPoisson(double lambda,int nbclass){
//
// nbFaultProbability=new ArrayList<IntervalAndfaults>();
//
// double currentUpperBound=0.0;//max 1
//
// //1° compute the probability occurrence for each class
// for (int i=0;i<=nbclass;i++){
//     double fishProba=Poisson(lambda,i);
//     nbFaultProbability.add(new IntervalAndfaults(currentUpperBound,currentUpperBound+fishProba,i));
//     currentUpperBound=currentUpperBound+fishProba;
// }

//
///**
//*
//* Find the number of fault to trigger according to the Poisson law
//* @param value
//* @return
//*/
//public int getNbFaults(double value) {
//  boolean trouve=false;
//  int nbfaults=0;
//  int i=0;
//  while(!trouve && i<nbFaultProbability.size()){
//      IntervalAndfaults intF=nbFaultProbability.get(i);
//      trouve=intF.isIn(value);
//      if(trouve){
//          nbfaults=intF.getNbfaults();
//      }
//      i++;
//  }
//  return nbfaults;
//
//}
///**
//*
//* @param value
//* @return true if the value is in
//*/
//private boolean isIn(double value) {
//return (value<maxExclude && value>=minInclude);
//}
//
//}*/

//
//
//public class InitSimulationFaults {
//
//
//   public static void main(String arg[]){
//
//       //0) create Default fault database
//       //FaultAndRepair_Interface faultdatabase=FaultAndRepair_Interface.load(fileName);
//       FaultAndRepair_Interface faultdatabase2=FaultAndRepair_Interface.createDefaultDB();
//
//
//       //1) build the law considering experimental parameters
//       System.out.println("Poisson law");
//       LoiPoisson lp= new LoiPoisson(0.05,4);
//       System.out.println(lp.toString());
//
//       //2) create the faultSimulationList using the fault db, the choosed law and the experiment duration
//       FaultsToTrigger faultSimulationList=computeFaultList(faultdatabase2,lp,86400);
//
//       //Launch simu
//       //TODO Lancer la simulation
//
//   }
//
//   private static FaultsToTrigger computeFaultList(FaultAndRepair_Interface faultdatabase, LoiPoisson lp, int simuLength) {
//
//       FaultsToTrigger faultSimulationlist=new FaultsToTrigger();
//
//       Random r=new Random();
//       //for each time step
//       for(int i=0;i<simuLength;i++){
//           double value=r.nextDouble();
//           //get the number of fault to trigger
//           int nbfaults=lp.getNbFaults(value);
//
//           //randomly choose the fault from the fault db
//           for(int k=1;k<nbfaults;k++){
//               Fault f=faultdatabase.getOneFaultRandomly();
//               //store the fault into the simulationList
//               faultSimulationlist.addFault(i,f);
//           }
//       }
//       return faultSimulationlist;
//   }
//}



//
//	public static boolean eventOccur(
//			long uptime, final Double k, final Double lambda, final Double theta){
//		final Random rand = new Random();
//		return rand.nextDouble() > getWeibullLaw(uptime, lambda, k, theta);
//	}
//
//
//	/*
//	 *
//	 */
//
//
//	public static  Double getWeibullLaw(int k,  final Double lambda){
//		return  Math.exp(-lambda) * Math.pow(lambda, k)/ factoriel(k) );
//	}
//
//
//	public static void main(String[] args) throws InterruptedException{
//		Date t = new Date();
//		double k = 1;
//		double lambda = 0.9;
//		double theta = 0.;
//		while(true){
//			Thread.sleep(2000);
//			long uptime = new Date().getTime() - t.getTime();
//			System.out.println("0 : "+eventOccur(uptime, k, lambda, theta)+"  -> "+getWeibullLaw(uptime, lambda, k, theta));
//			System.out.println("1 : "+eventOccur(uptime*100, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*100, lambda, k, theta));
//			System.out.println("2 : "+eventOccur(uptime*10000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*10000, lambda, k, theta));
//			System.out.println("3 : "+eventOccur(uptime*1000000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*1000000, lambda, k, theta));
//		}
//	}
//
//	private static int factoriel(int x){
//
//		int i = 1;
//		int resultat = 1;
//		while (i <= x){
//			resultat = resultat * i;
//
//			i++;
//		}
//		return resultat;
//	}