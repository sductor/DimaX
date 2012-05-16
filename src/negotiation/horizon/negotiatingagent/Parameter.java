package negotiation.horizon.negotiatingagent;

import dima.support.GimaObject;

@Deprecated
public class Parameter<Type extends Comparable<Type>> extends GimaObject
	implements Comparable<Parameter<Type>> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 1126352430396250864L;

    private final Type param;

    public Parameter(final Type param) {
	this.param = param;
    }

    @Override
    public int compareTo(Parameter<Type> o) {
	return this.param.compareTo(o.param);
    }
}
