package negotiation.horizon.parameters;

import dima.basicinterfaces.DimaComponentInterface;

/**
 * Defines the methods available for all AllocableParameters or functional
 * parameters.
 * 
 * @author Vincent Letard
 */
public interface AllocableParameters extends DimaComponentInterface {
    /**
     * @return <code>true</code> if the objects corresponds to a possible
     *         situation.
     */
    public boolean isValid();
}
