package dima.introspectionbasedagents.services.core.opinion;

import dima.introspectionbasedagents.shells.CommunicatingCompetentComponent;

public interface Believer extends CommunicatingCompetentComponent{

	OpinionService getMyOpinion();

}
