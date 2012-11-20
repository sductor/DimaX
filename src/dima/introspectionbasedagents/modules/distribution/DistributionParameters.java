package dima.introspectionbasedagents.modules.distribution;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;






//Distribution de valeurs comprises entre 0 et 1
public class DistributionParameters<K> extends HashMap<K, Double> implements
Serializable {

	private static final long serialVersionUID = 6930366920402958016L;

	//
	// Fields
	//

	private final NormalLaw g;

	//
	// Constructor
	//


	public DistributionParameters(final Collection<K> population,
			final double moyenne, final double ecartType, final Random rand) {
		this.g = new NormalLaw(moyenne, ecartType, rand);

		for (final K individu : population) {
			this.put(individu,  this.g.nextValue());
		}
	}

	/**
	 * Normalis� entre [0,1] si withExtrem et ]0,1[ sinon
	 */
	public DistributionParameters(final Collection<K> population,
			final ZeroOneSymbolicValue moyenne,final DispersionSymbolicValue ecartType, final Random rand, final boolean withExtrem) {
		this.g = new NormalLaw(moyenne, ecartType, rand);

		for (final K individu : population) {
			if (withExtrem){
				this.put(individu,  this.g.nextNormalizedValue());
			} else {
				this.put(individu,  this.g.nextNormalizedNonExtremeValue());
			}
		}
	}
	/**
	 * Normalis�entre [0,1] si withExtrem et ]0,1[ sinon
	 * @param population
	 * @param moyenne
	 * @param ecartType
	 * @param rand
	 * @param withExtrem
	 */
	public DistributionParameters(final Collection<K> population,
			final Double moyenne,final DispersionSymbolicValue ecartType, final Random rand, final boolean withExtrem) {
		this.g = new NormalLaw(moyenne, ecartType, rand);

		for (final K individu : population) {
			if (withExtrem){
				this.put(individu,  this.g.nextNormalizedValue());
			} else {
				this.put(individu,  this.g.nextNormalizedNonExtremeValue());
			}
		}
	}
}



//for  (final K individu : this.keySet())
//	this.put(individu, this.get(individu)/max);


//protected double computeValue(final Dispersion heterogeneite,
//		final double centralValue) {
//	final Random rand = new Random();
//	final double kValue = Math.min(
//			Math.max(centralValue+ Math.pow(heterogeneite.getNumericValue(), 2)/ 2* (2 * rand.nextInt(2) - 1) * rand.nextDouble()
//					// Nombre
//					// aleatoire
//					// entre -1 et 1
//					, 0), 1);
//	return kValue;
//}
//public Float getFloat(final K id) {
//	return new Float(get(id));
//}
