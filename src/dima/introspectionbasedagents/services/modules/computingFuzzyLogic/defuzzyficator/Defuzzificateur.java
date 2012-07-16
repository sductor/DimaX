package dima.introspectionbasedagents.services.modules.computingFuzzyLogic.defuzzyficator;

import java.io.Serializable;

import dima.introspectionbasedagents.services.modules.computingFuzzyLogic.controller.FuzzySubSet;







/**
 *
 * @author Sylvain Ductor
 */
public interface Defuzzificateur extends Serializable {

	public double defuzz(FuzzySubSet a);
}
