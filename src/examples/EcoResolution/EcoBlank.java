package examples.EcoResolution;

import java.util.Vector;

//************************************************************************
//************************************************************************
//La classe EcoBlank
//************************************************************************
//************************************************************************

class EcoBlank extends EcoTile {
	/**
	 *
	 */
	private static final long serialVersionUID = 5096641630319024633L;
	EcoBlank( final EcoPlace ec) {
		super( ec, 0);//par convention une instance de EcoBlank est un palet
		//particulier portant le N zero
	}
	@Override
	public boolean canEscapeOn( final EcoPlace ec) {
		return this.place.isAdjacentTo( ec);
	}
	@Override
	public boolean canEscapeWithConstraint( final EcoAgent constraint) {

		return true;
	}
   // public  boolean isFree() {return true}
  //  public  boolean isSatisfied();

	public boolean canSatisfyOnPlace() {
		//retourne true si son goalAgent est adjacent a sa position
		return this.place.isAdjacentTo( ( EcoPlace) this.goalAgent);
	}
	@Override
	public void  doEscapeActionWithConstraint( final EcoAgent ea) {
			final boolean b = this.isSatisfied();//on regarde si le palet etait satisfied
			this.move( (EcoPlace) ea);
			if ( b)
				this.state =  TRY_SATISFACTION;
	}
	@Override
	public  void  doEscapeAggressionWithConstraint( final EcoAgent constraint) {}
	@Override
	public  void  doSatisfactionActionOnPlace(final EcoAgent place) {}
	public void doSatisfactionAggressionOnPlace() {
		if ( this.place.isAdjacentTo( ( EcoPlace) this.goalAgent))
			( ( EcoPlace) this.goalAgent).freeWithConstraint( null);//##############
			//doSatisfactionActionOnPlace si liberation reussie : peut etre appel'e dans freeWithConstraint()
			//de constraint
		else {// on recherche une place intermediaire...
			final EcoPlace ec = ( EcoPlace) this.findSatisfactionPlace();
			ec.freeWithConstraint( null);//constraint : eviter le goalAgent
			this.doSatisfactionAggressionOnPlace();//>>>>>>>>>>>>refaire un doSatisfactionAggressionOnPlace
		}
	}
	@Override
	public EcoAgent findEscapePlaceWithConstraint( final EcoAgent constraint) {
		return constraint ;
	}
	@Override
	public EcoAgent findSatisfactionPlace() {//Choix d'une place intermediaire : la
	//plus proche du goalAgent telle que ( si possible) l'EcoTile qu'elle supporte ne soit pas
	//satisfied

		Vector list = this.place.adjacentPlaces(); //list des EcoCases adjacentes
		final Vector sauv = ( Vector) list.clone();//pour le cas ou l'on ne pourrait eviter de move un EcoTile
		// deja satisfied
		for( int i = 0; i < list.size(); i++) {
			final EcoPlace e = ( EcoPlace) list.elementAt( i);
			if ( e.tile.isSatisfied())// s'il existe dans "list" des places
				list.removeElement( e);// dont le "tile" est satisfied,
		}// on les retire de la list
		if ( list.isEmpty())
			list = sauv;//Obligation de move un EcoTile deja satisfied
		final EcoNPuzzle etq = this.place.owner;
		EcoPlace plusProcheDuBut = null ;
		int d = 10000;
		final Vector prohibitedPlaces = this.place.owner.lockedPlaces();
		if ( prohibitedPlaces.contains( this.goalAgent))
			prohibitedPlaces.removeElement( this.goalAgent);//
		for( int i = 0; i < list.size(); i++) {
			final EcoPlace aux = ( EcoPlace) list.elementAt( i);
			final int db = etq.distanceAvoidingProhibitedPlaces( aux, ( EcoPlace) this.goalAgent,
																	 prohibitedPlaces);
			if ( 0 < db && db < d) {
				plusProcheDuBut = aux;
				d = db;
			}
		}
		return plusProcheDuBut;
	}
	@Override
	public String toString() {//Trace graphique
		return new String( "Blank");
	}
}
