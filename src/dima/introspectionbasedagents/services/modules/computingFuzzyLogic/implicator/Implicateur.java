package dima.introspectionbasedagents.services.modules.computingFuzzyLogic.implicator;

import java.io.Serializable;

import dima.introspectionbasedagents.services.modules.computingFuzzyLogic.controller.FuzzySubSet;







/**
 *
 * @author Sylvain Ductor
 */
public interface Implicateur extends Serializable {

	public FuzzySubSet r(FuzzySubSet premisse, FuzzySubSet conclusion);
}
