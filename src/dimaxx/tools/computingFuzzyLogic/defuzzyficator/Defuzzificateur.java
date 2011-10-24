package dimaxx.tools.computingFuzzyLogic.defuzzyficator;

import java.io.Serializable;

import dimaxx.tools.computingFuzzyLogic.controller.FuzzySubSet;





/**
 *
 * @author Sylvain Ductor
 */
public interface Defuzzificateur extends Serializable {

	public double defuzz(FuzzySubSet a);
}
