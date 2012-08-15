package frameworks.negotiation.negotiationframework.opinion;

import java.util.HashMap;

import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import frameworks.negotiation.negotiationframework.opinion.OpinionDataBase.SimpleOpinion;

public class SimpleOpinionService 
extends SimpleObservationService implements OpinionService{

	HashMap<Class<? extends Information>, OpinionHandler<?>> myHandlers=
			new HashMap<Class<? extends Information>, OpinionHandler<?>>();

	public SimpleOpinionService(OpinionHandler<?>... myHandlers) {
		super();
		for (OpinionHandler<?> o : myHandlers){
			this.myHandlers.put(o.getInfoType(), o);
		}
	}

	@Override
	public void add(final Information information) {
		//force l'utilisation de l'opiniondatabase
		assert !myHandlers.containsKey(information.getClass()) || (this.infos.get(information.getClass()) ==null || this.infos.get(information.getClass()) instanceof OpinionDataBase);
		if (!this.infos.containsKey(information.getClass()) 
				&& myHandlers.containsKey(information.getClass())){
			this.infos.put(information.getClass(),
					new OpinionDataBase(
							getMyAgent().getIdentifier(), 
							myHandlers.get(information.getClass())));
		}
		assert !myHandlers.containsKey(information.getClass()) ||this.infos.get(information.getClass()) instanceof OpinionDataBase:information.getClass();

		if (information instanceof SimpleOpinion){
			assert myHandlers.containsKey(((SimpleOpinion)information).getMeanInfo().getClass()):((SimpleOpinion)information).getMeanInfo().getClass();
			assert this.infos.get(((SimpleOpinion)information).getMeanInfo().getClass()) instanceof OpinionDataBase:((SimpleOpinion)information).getMeanInfo().getClass();
			((OpinionDataBase)infos.get(((SimpleOpinion)information).getMeanInfo().getClass())).addOpinion((SimpleOpinion) information);
		} else 
			super.add(information);
	}

	@Override
	public <Info extends Information> Opinion<Info> getGlobalOpinion(
			Class<Info> myInfoType) throws NoInformationAvailableException, NoOpinionHandlerException {
		if (!myHandlers.containsKey(myInfoType))
			throw new NoOpinionHandlerException();
		assert myHandlers.containsKey(myInfoType) || (this.infos.get(myInfoType) ==null || this.infos.get(myInfoType) instanceof OpinionDataBase):myInfoType+" "+myHandlers;
		if (infos.get(myInfoType)!=null){
			return ((OpinionDataBase)infos.get(myInfoType)).getGlobalOpinion();
		} else
			throw new NoInformationAvailableException();
	}

	public OpinionHandler getHandler(Class myInfoType){
		return myHandlers.get(myInfoType);
	}
}
