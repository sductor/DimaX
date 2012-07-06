package negotiation.horizon;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

public abstract class AbstractInformation implements Information {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -7362387539574469973L;

    private final Long creationTime;
    private final AgentIdentifier myAgentIdentifier;

    public AbstractInformation(final AgentIdentifier myAgentIdentifier) {
	this.creationTime = new Date().getTime();
	this.myAgentIdentifier = myAgentIdentifier;
    }

    @Override
    public Long getCreationTime() {
	return this.creationTime;
    }

    @Override
    public AgentIdentifier getMyAgentIdentifier() {
	return this.myAgentIdentifier;
    }

    @Override
    public long getUptime() {
	return new Date().getTime() - this.creationTime;
    }

    @Override
    public int isNewerThan(Information that) {
	if (that instanceof AbstractInformation)
	    return (int) (this.creationTime - ((AbstractInformation) that).creationTime);
	else
	    throw new IllegalArgumentException();
    }

}
