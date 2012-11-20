package frameworks.negotiation.opinion;

import java.util.HashMap;

import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import frameworks.negotiation.opinion.OpinionDataBase.SimpleOpinion;

public class SimpleOpinionService
extends SimpleObservationService implements OpinionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8909704451654710378L;
	HashMap<Class<? extends Information>, OpinionHandler<?>> myHandlers=
			new HashMap<Class<? extends Information>, OpinionHandler<?>>();

	public SimpleOpinionService(final OpinionHandler<?>... myHandlers) {
		super();
		for (final OpinionHandler<?> o : myHandlers){
			this.myHandlers.put(o.getInfoType(), o);
		}
	}

	@Override
	public void add(final Information information) {
		//force l'utilisation de l'opiniondatabase
		assert !this.myHandlers.containsKey(information.getClass()) || this.infos.get(information.getClass()) ==null || this.infos.get(information.getClass()) instanceof OpinionDataBase;
		if (!this.infos.containsKey(information.getClass())
				&& this.myHandlers.containsKey(information.getClass())){
			this.infos.put(information.getClass(),
					new OpinionDataBase(
							this.getMyAgent().getIdentifier(),
							this.myHandlers.get(information.getClass())));
		}
		assert !this.myHandlers.containsKey(information.getClass()) ||this.infos.get(information.getClass()) instanceof OpinionDataBase:information.getClass();

		if (information instanceof SimpleOpinion){
			assert this.myHandlers.containsKey(((SimpleOpinion)information).getMeanInfo().getClass()):((SimpleOpinion)information).getMeanInfo().getClass();
			assert this.infos.get(((SimpleOpinion)information).getMeanInfo().getClass()) instanceof OpinionDataBase:((SimpleOpinion)information).getMeanInfo().getClass();
			((OpinionDataBase)this.infos.get(((SimpleOpinion)information).getMeanInfo().getClass())).addOpinion((SimpleOpinion) information);
		} else {
			super.add(information);
		}
	}

	@Override
	public <Info extends Information> Opinion<Info> getGlobalOpinion(
			final Class<Info> myInfoType) throws NoInformationAvailableException, NoOpinionHandlerException {
		if (!this.myHandlers.containsKey(myInfoType)) {
			throw new NoOpinionHandlerException();
		}
		assert this.myHandlers.containsKey(myInfoType) || this.infos.get(myInfoType) ==null || this.infos.get(myInfoType) instanceof OpinionDataBase:myInfoType+" "+this.myHandlers;
		if (this.infos.get(myInfoType)!=null){
			return ((OpinionDataBase)this.infos.get(myInfoType)).getGlobalOpinion();
		} else {
			throw new NoInformationAvailableException();
		}
	}

	public OpinionHandler getHandler(final Class myInfoType){
		return this.myHandlers.get(myInfoType);
	}
}
