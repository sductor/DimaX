package frameworks.negotiation.negotiationframework.opinion;

import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;

public interface Believer extends CommunicatingCompetentComponent{

	OpinionService getMyOpinion();

}
