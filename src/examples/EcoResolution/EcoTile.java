package examples.EcoResolution;

import java.awt.Graphics;
import java.util.Vector;

//************************************************************************
//************************************************************************
//La classe EcoTile
//************************************************************************
//************************************************************************

class EcoTile extends EcoAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = -8375313043902696106L;
	EcoPlace place;
	int number;


	EcoTile( final EcoPlace ec, final int num){
		this.place = ec;
		this.number = num;
	}
	public boolean canEscapeOn( final EcoPlace ec) {
		final boolean b1 = this.place.isAdjacentTo( ec);
		final boolean b2 = ec.tile instanceof EcoBlank;
		return b1 && b2;
	}
	@Override
	public boolean canEscapeWithConstraint( final EcoAgent constraint) {
		return false;
	}
	@Override
	public boolean canSatisfyOnPlace(final EcoAgent p) {
		final boolean b1 = this.place.isAdjacentTo( ( EcoPlace) this.goalAgent);
		final boolean b2 = ( ( EcoPlace) this.goalAgent).tile instanceof EcoBlank;
		return b1 && b2;
	}
	@Override
	public void changeStateTo( final int s) {
		if ( this.state != EcoAgent.FORBIDDEN) {
			super.changeStateTo( s);
			final Graphics g = this.place.owner.support.getGraphics();
			this.place.refresh( g);
			this.place.owner.computeSpeed();
			try { Thread.sleep(  EcoNPuzzle.duration / 4);
			} catch (final Exception e) { }
		}
	}
	@Override
	public void  doEscapeActionWithConstraint( final EcoAgent constraint) {
		if ( this.canEscapeOn( ( EcoPlace) constraint)) {
			final boolean b = this.isSatisfied();
			this.place.locked = false;
			this.move( (EcoPlace) constraint);
			if ( b) {
				this.changeStateTo(EcoAgent.TRY_SATISFACTION);
				final EcoNPuzzle etq = this.place.owner;
				etq.satisfied = false;
			} else if ( this.isSatisfied()) {
				this.place.locked = true;
			}
		}
	}
	@Override
	public void doEscapeAggressionWithConstraint( final EcoAgent constraint) {
		if ( !this.canEscapeWithConstraint( constraint)) {
			this.place.locked = true;
			final boolean b = this.place.owner.manhattanDistance( this.place, ( EcoPlace) constraint) == 1;
			if ( !this.canEscapeOn( ( EcoPlace) constraint) && b) {
				( ( EcoPlace) constraint).freeWithConstraint( this.place);
				this.doEscapeAggressionWithConstraint( constraint);
			}
		}
	}
	@Override
	public void doSatisfactionActionOnPlace(final EcoAgent p) {
		if ( !this.isSatisfied()) {
			if ( this.canSatisfyOnPlace(p)) {
				this.place.locked = false;
				this.move( ( EcoPlace) this.goalAgent);
				this.place.locked = true;
			} else {
				this.doSatisfactionAggressionOnPlace(p);
				this.doSatisfactionActionOnPlace(p);
			}
		}
	}
	@Override
	public void doSatisfactionAggressionOnPlace(final EcoAgent p) {
		if ( !this.canSatisfyOnPlace(p)) {
			this.place.locked = true;
			if ( this.place.isAdjacentTo( ( EcoPlace) this.goalAgent)) {
				( ( EcoPlace) this.goalAgent).freeWithConstraint( this.findConstraintForSatisfaction());
				if ( this.state != EcoAgent.AGGRESSION_SATISFACTION) {
					this.changeStateTo( EcoAgent.AGGRESSION_SATISFACTION);
				}
			} else {
				final EcoPlace ec = ( EcoPlace) this.findSatisfactionPlace();
				if ( ec.tile instanceof EcoBlank) {
					this.move( ec);
				} else {
					ec.freeWithConstraint( this.goalAgent);
				}
				this.doSatisfactionAggressionOnPlace(p);
			}
		}
	}
	public EcoAgent findConstraintForSatisfaction() {
		final EcoPlace ec = ( EcoPlace) this.goalAgent;
		final Vector list = ec.adjacentPlaces();
		if ( ec.goalAgent instanceof EcoNPuzzle) {
			return this.place;
		}
		final EcoTile ep = ( EcoTile) ec.goalAgent;
		if ( ep.isSatisfied() && list.contains( ep.place)) {
			return ep.place;
		} else {
			return this.place;
		}
	}
	@Override
	public EcoAgent findEscapePlaceWithConstraint( final EcoAgent constraint) {
		Vector list = this.place.adjacentPlaces();
		if ( constraint != null) {
			list.removeElement(constraint);
		}
		if ( this.goalAgent != null && list.contains( this.goalAgent) && !( ( EcoPlace) this.goalAgent).locked) {
			return this.goalAgent;
		}
		final EcoPlace ec = this.place.owner.blank.place;
		if (  list.contains( ec)) {
			return ec;
		}
		final Vector vect = new Vector();
		for( int i = 0; i < list.size(); i++) {
			final EcoPlace e = ( EcoPlace) list.elementAt( i);
			if ( !e.locked) {
				vect.addElement( e);
			}
		}
		list = vect;
		final EcoNPuzzle etq = this.place.owner;
		if ( !list.isEmpty()) {
			int d = 10000;
			final Vector prohibitedPlaces = etq.lockedPlaces();
			Vector l_aux = new Vector();
			for( int i = 0; i < list.size(); i++) {
				final EcoPlace aux = ( EcoPlace) list.elementAt( i);
				final int db = etq.distanceToTheBlankAvoidingProhibitedPlaces( aux, prohibitedPlaces);
				if (  db <= d) {
					if (  db != d) {
						l_aux.removeAllElements();
					}
					l_aux.addElement( aux);
					d = db;
				}
			}

			if ( d >= 10000) {
				etq.unlockSystem();
				return this.findEscapePlaceWithConstraint( constraint);
			} else {
				final Vector l_aux2 = new Vector();
				if ( l_aux.size() > 1) {
					for( int i = 0; i < l_aux.size(); i++) {
						final EcoPlace ee = ( EcoPlace) l_aux.elementAt( i);
						if ( !ee.tile.isSatisfied()) {
							l_aux2.addElement( ee);
						}
					}
					if ( !l_aux2.isEmpty()) {
						l_aux = l_aux2;
					}
				}
				return ( EcoPlace) l_aux.firstElement();
			}
		} else {
			etq.unlockSystem();
			return this.findEscapePlaceWithConstraint( constraint);
		}

	}
	@Override
	public EcoAgent findSatisfactionPlace() {
		final EcoNPuzzle etq = this.place.owner;
		final Vector list = etq.nearestAdjacentPlaces( this.place, ( EcoPlace) this.goalAgent);
		final Vector l = new Vector();
		for( int i = 0; i < list.size(); i++) {
			final EcoPlace ec = ( EcoPlace) list.elementAt( i);
			if ( !ec.tile.isSatisfied()) {
				l.addElement( ec);
			}
		}
		final EcoPlace ec = l.isEmpty() ? ( EcoPlace) list.firstElement() : ( EcoPlace) l.firstElement();
		return ec;
	}
	@Override
	public void informDependantsOfSatisfaction() {
		this.place.locked = true;
		final EcoNPuzzle etq = this.place.owner;
		final int i = this.place.row;
		final int j = this.place.col;
		if ( i == etq.indexFirstRowColumnAllowed && etq.completeRowColumn( i)
				|| j == etq.indexFirstRowColumnAllowed && etq.completeRowColumn( j)) {
			etq.forbidRowColumn();
		}
		super.informDependantsOfSatisfaction();
	}
	/**
	 * Tests wheter a proactive object is active or no ie whether the ProactiveComponent.
	 */
	@Override
	public  boolean isActive()
	{return this.isSatisfied();}
	@Override
	public boolean isFree() {
		return true;
	}
	@Override
	public boolean isSatisfied() {
		return this.place == this.goalAgent;
	}
	public void move( final EcoPlace ec) {
		final EcoNPuzzle etq = this.place.owner;
		final EcoPlace aux = this.place;
		aux.locked = false;
		aux.switchTiles( ec);
		etq.pzFrame.nbDeplacements++;
		etq.pzFrame.refresh(aux, ec);
		final int i = ec.row;
		final int j = ec.col;
		if ( i == etq.indexFirstRowColumnAllowed && etq.completeRowColumn( i)
				|| j == etq.indexFirstRowColumnAllowed && etq.completeRowColumn( j)) {
			etq.forbidRowColumn();
		}
		etq.computeSpeed();
		try {
			Thread.sleep( EcoNPuzzle.duration/4);
		} catch (final Exception e) { }

	}
}
