package dima.introspectionbasedagents.services.core.information;

import dima.introspectionbasedagents.shells.CommunicatingCompetentComponent;

public interface Believer extends CommunicatingCompetentComponent{

	public OpinionService getMyOpinion();
}
