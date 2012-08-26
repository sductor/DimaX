package dima.introspectionbasedagents.modules.distribution;

import java.util.Date;
import java.util.Random;

import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;





//Wikipedia :
//La distribution de Weibull est souvent utilis�e dans le domaine de l'analyse de la dur�e de vie, gr�ce � sa flexibilit� : comme dit pr�c�demment, elle permet de repr�senter au moins approximativement une infinit� de lois de probabilit�.
//Si le taux de panne diminue au cours du temps alors, k < 1. Si le taux de panne est constant dans le temps alors, k = 1. Si le taux de panne augmente avec le temps alors, k > 1. La compr�hension du taux de panne peut fournir une indication au sujet de la cause des pannes.
//Un taux de panne d�croissant rel�ve d'une "mortalit� infantile". Ainsi, les �l�ments d�fectueux tombent en panne rapidement, et le taux de panne diminue au cours du temps, quand les �l�ments fragiles sortent de la population.
//Un taux de panne constant sugg�re que les pannes sont li�es � une cause stationnaire.
//Un taux de panne croissant sugg�re une "usure ou un probl�me de fiabilit�" : les �l�ments ont de plus en plus de chances de tomber en panne quand le temps passe.
//On dit que la courbe de taux de panne est en forme de baignoire. Selon l'appareil, baignoire sabot ou piscine. Les fabricants et distributeurs ont tout int�r�t � bien maitriser ces informations par type de produits afin d'adapter :
//les dur�es de garantie (gratuites ou payantes)
//le planning d'entretien (voir MTBF)

public class WeibullLaw {


	public static boolean eventOccur(
			final long uptime, final Double k, final Double lambda, final Double theta){
		final Random rand = new Random();
		return rand.nextDouble() > WeibullLaw.getWeibullLaw(uptime, lambda, k, theta);
	}


	/*
	 *
	 */


	public static  Double getWeibullLaw(final long x,  final Double k, final Double lambda, final Double theta){
		return 1 - WeibullLaw.computeWeibullLaw(x,k,lambda,theta);
	}

	public static  Double computeWeibullLaw(final long x,  final Double k, final Double lambda, final Double theta){
		if (x < theta) {
			return 0.;
		} else {
			return  - Math.expm1(- Math.pow((x-theta)/lambda, k));
		}
	}




	public static void main(final String[] args) throws InterruptedException{
		final Date t = new Date();
		final double theta = ReplicationExperimentationParameters._theta;
		final long timeScale = ReplicationExperimentationParameters._timeScale;
		final double k = ReplicationExperimentationParameters._kValue;
		//			double lambda = 0.5;
		while(true){
			final long uptime = new Date().getTime() - t.getTime();
			for (double lambda = 0.1; lambda <= 1 ; lambda+=0.3) {
				System.out.println("lambda="+lambda+",uptime="+uptime+" : "+WeibullLaw.eventOccur(uptime/timeScale, k, lambda, theta)+"  -> "+WeibullLaw.getWeibullLaw(uptime/timeScale, lambda, k, theta));
			}
			System.out.println("***************");
			//			for (double k = 0; k < 3 ; lambda+=0.5)
			//				System.out.println("lambda="+lambda+",uptime="+uptime+" : "+eventOccur(uptime/timeScale, k, lambda, theta)+"  -> "+getWeibullLaw(uptime/timeScale, lambda, k, theta));
			//			System.out.println("***************");
			Thread.sleep(ReplicationExperimentationParameters._host_maxFaultfrequency);
		}
	}
}
//System.out.println("0 : "+uptime/10000+" : "+eventOccur(uptime/10000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime/10000, lambda, k, theta));
//System.out.println("0 : "+uptime+" : "+eventOccur(uptime, k, lambda, theta)+"  -> "+getWeibullLaw(uptime, lambda, k, theta));
//System.out.println("1 : "+uptime*100+" : "+eventOccur(uptime*100, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*100, lambda, k, theta));
//System.out.println("2 : "+uptime*10000+" : "+eventOccur(uptime*10000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*10000, lambda, k, theta));
//System.out.println("3 : "+uptime*1000000+" : "+eventOccur(uptime*1000000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*1000000, lambda, k, theta));
//System.out.println("4 : "+uptime*100000000+" : "+eventOccur(uptime*100000000, k, lambda, theta)+"  -> "+getWeibullLaw(uptime*100000000, lambda, k, theta));
//