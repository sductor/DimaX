package negotiation.horizon.negotiatingagent;

import java.util.HashMap;

import dima.basicinterfaces.DimaComponentInterface;

public class InterfacesParameters<Identifier extends HorizonIdentifier> extends
	HashMap<Identifier, LinkParameters> implements DimaComponentInterface {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 6701844582979452499L;

    public boolean isValid() {
	for (LinkParameters params : this.values()) {
	    if (!params.isValid())
		return false;
	}
	return true;
    }

    @Override
    public String toString() {
	StringBuilder str = new StringBuilder();
	str.append("(");
	for (LinkParameters params : this.values()) {
	    str.append(params + ", ");
	}
	str.setCharAt(str.lastIndexOf(", "), ')');
	return str.toString();
    }
}
