package dima.introspectionbasedagents.services.modules.distribution;

import java.util.Random;

import dima.support.GimaObject;

/**
 * G�n�re des �l�ments d'une loi normale
 * @author Sylvain  Ductor
 */
public class NormalLaw extends GimaObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3469809445675586895L;

	Random rand = new Random();

	final double ecartType;
	final double moyenne;



	public NormalLaw(final double moyenne, final double ecartType) {
		super();
		this.ecartType = ecartType;
		this.moyenne = moyenne;
	}

	public NormalLaw(final ZeroOneSymbolicValue moyenne, final DispersionSymbolicValue ecartType) {
		this(moyenne.getNumericValue(), ecartType.getNumericValue());
	}

	public NormalLaw(final Double moyenne, final DispersionSymbolicValue ecartType) {
		this(moyenne, ecartType.getNumericValue());
	}
	/**
	 *
	 * @return a double generated using normal law
	 */
	public double nextValue() {
		final double randNumUni = this.rand.nextDouble();
		final double randNumBi = this.rand.nextDouble();

		// On r�cup�re un nombre pseudo-al�atoire selon une loi normale centr�e r�duite
		// Utilisation de l'algorithme de Box-Muller
		final double randNumNorm = Math.sqrt(-2.0*Math.log(randNumUni))*Math.cos(2*Math.PI*randNumBi);

		return this.moyenne + this.ecartType * randNumNorm;
	}
	/**
	 * !! moyenne appartient � [0,1]
	 * @return a double between moyenne + ou - 1
	 * v = Math.max(-1, Math.min(1, (ecartType/2.6) * randNumNorm) appartient  [-1,1]
	 * only 1% of value is casted to fit the m +[-1,1] interval (normal law property with 2,6 x sigma
	 * v2 = v *Math.min(1-moyenne, moyenne))
	 * on obtient un intervalle tq moyenne + v2 appartient � -1, 1 et soit centr� en moyenne
	 */
	public double nextNormalizedNonExtremeValue() {
		final double randNumUni = this.rand.nextDouble();
		final double randNumBi = this.rand.nextDouble();

		double result = 0;

		// On r�cup�re un nombre pseudo-al�atoire selon une loi normale centr�e r�duite
		// Utilisation de l'algorithme de Box-Muller
		final double randNumNorm = Math.sqrt(-2.0*Math.log(randNumUni))*Math.cos(2*Math.PI*randNumBi);
		result = this.moyenne +
				Math.max(-1, Math.min(1, this.ecartType/2.6 * randNumNorm))*Math.min(1-this.moyenne, this.moyenne);

		if (result==0) {
			result+=0.001;
		}
		if (result==1) {
			result-=0.001;
		}
		return result;
	}

	/**
	 * !! moyenne appartient � [0,1]
	 * @return a double between moyenne + ou - 1
	 * v = Math.max(-1, Math.min(1, (ecartType/2.6) * randNumNorm) appartient  [-1,1]
	 * only 1% of value is casted to fit the m +[-1,1] interval (normal law property with 2,6 x sigma
	 * v2 = v *Math.min(1-moyenne, moyenne))
	 * on obtient un intervalle tq moyenne + v2 appartient � -1, 1 et soit centr� en moyenne
	 */
	public double nextNormalizedValue() {
		final double randNumUni = this.rand.nextDouble();
		final double randNumBi = this.rand.nextDouble();

		// On r�cup�re un nombre pseudo-al�atoire selon une loi normale centr�e r�duite
		// Utilisation de l'algorithme de Box-Muller
		final double randNumNorm = Math.sqrt(-2.0*Math.log(randNumUni))*Math.cos(2*Math.PI*randNumBi);

		return this.moyenne +
				Math.max(-1, Math.min(1, this.ecartType/2.6 * randNumNorm))*Math.min(1-this.moyenne, this.moyenne);
	}
	/*
	 *
	 */
	public static void main(final String[] args){
		final NormalLaw d = new NormalLaw(0.7, 0.75);
		for (int i = 0; i < 200; i++) {
			System.out.println(d.nextNormalizedNonExtremeValue());
		}
	}

	///
	// Subclass
	//

	/**
	 * Enum of symbolic value between 0 and 1
	 * @author Sylvain Ductor
	 *
	 */
	public enum DispersionSymbolicValue {
		Nul(0.),
		Faible(0.65),
		Moyen(1.3),
		Fort(2),
		Max(2.6);

		private double numericValue;

		private DispersionSymbolicValue(final double numericValue) {
			this.numericValue = numericValue;
		}

		protected double getNumericValue() {
			return this.numericValue;
		}
	}
}