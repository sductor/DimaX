package dimaxx.tools.computingFuzzyLogic.implicator;

import java.io.Serializable;

import dimaxx.tools.computingFuzzyLogic.controller.FuzzySubSet;





/**
 *
 * @author Sylvain Ductor
 */
public interface Implicateur extends Serializable {

	public FuzzySubSet r(FuzzySubSet premisse, FuzzySubSet conclusion);
}
