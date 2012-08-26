package frameworks.negotiation.opinion;

import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;

public interface Believer extends CommunicatingCompetentComponent{

	OpinionService getMyOpinion();

}
