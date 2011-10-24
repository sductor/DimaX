package examples.EcoResolution;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;
import java.util.Vector;

//************************************************************************
//************************************************************************
//La classe EcoNPuzzle
//************************************************************************
//************************************************************************

class EcoNPuzzle extends EcoAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = 8916255747047415464L;
	int size;																//size of the puzzle
	EcoBlank blank;													//the empty place of the puzzle;
	Vector suite;														//sequence of numbers without repetition
	EcoPlace places[][];												//the set of places of the puzzle;
	PuzzleFrame pzFrame;
	boolean satisfied = false;							//set to false when a satisfied tile is displaced
	EcoTile tiles[];														//onloy necessary for the goals' chaining
	int indexFirstRowColumnAllowed = 0;
	boolean clean = false;
	public static int duration ;						//interval between two moves

	//******************************************************************************
	// variables d'instances et methodes de EcoNPuzzle li'ees a l'interface graphique
	//******************************************************************************

	NPuzzleSupport support;
	public static final int tile_size = 50;
	public static final int max_size = 30;


	EcoNPuzzle( final PuzzleFrame f, final int t) {

		this.pzFrame = f;
		this.size = t;
		this.computeSpeed();
		this.support = new NPuzzleSupport( this);
	    this.suite = this.buildSuite( this.size * this.size);
	    this.places = new EcoPlace[this.size][this.size];
	    this.tiles = new EcoTile[ this.size * this.size];
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++) {
	            final int aux = ( ( Integer) this.suite.elementAt( i * this.size + j)).intValue();
	            final EcoPlace ec = new EcoPlace( this, i, j);
	            if ( aux  == 0) {
	                this.blank = new EcoBlank( ec);
	                ec.tile = this.blank;
	            } else
		            ec.tile = new EcoTile( ec, aux);
	 	        this.places[i][j] = ec;
		        this.tiles[aux] = ec.tile;
	        }
	}
	public Vector buildSuite( final int n) {
	    final Vector result = new Vector();
		final Random generator = new Random();
		generator.setSeed( System.currentTimeMillis());
		while ( result.size() < n) {
			int aux;
			aux = generator.nextInt();
   		    aux = Math.abs( aux %  n);
			final Integer i = new Integer( aux);
			if ( !result.contains( i))
				result.addElement( i);
		}
		return result;
	}
	@Override
	public  boolean canEscapeWithConstraint( final EcoAgent constraint) {return true;}
	@Override
	public boolean canSatisfyOnPlace(final EcoAgent a) {
		return true;
	}
	public boolean completeRowColumn( final int index) {
		for( int i = index; i < this.size; i++) {
			final EcoTile auxC = this.places[i][index].tile;
			final EcoTile auxL = this.places[index][i].tile;
			if ( auxC.equals( this.blank)  || auxL.equals( this.blank))
				return false;
			if ( !auxC.isSatisfied() || !auxL.isSatisfied())
				return false;
		}
		return true;
	}
	public void computeChainingList() {
	    Point p = new Point( 0, this.size - 1);
	    this.places[p.x][p.y].assignGoal( this);
	    while ( true) {
	        final EcoTile ep = this.tiles[( p.x * this.size + p.y + 1) % (this.size * this.size)];
	        ep.assignGoal( this.places[p.x][p.y]);
	        p = this.coorNextPlace( p.x, p.y);
	        if ( p.x >= this.size || p.y >= this.size)
	            break;
	        this.places[p.x][p.y].assignGoal( ep);
	    }
		final EcoBlank ev = ( EcoBlank) this.tiles[0];
		this.assignGoal( ev);
	}
	public void computeSpeed() {
		final int n = this.pzFrame.speedChoice.getSelectedIndex();
		switch ( n) {
			case 0 : duration = 600;break;
			case 1 : duration = 300;break;
			case 2 : duration = 0;break;
		}
	}
	public Point coorNextPlace( final int i, final int j) {
		if (  i % 2 == 0 && i < j && j <= this.size - 1)
			return new Point( i, j - 1);
		else if (  i % 2 == 1 && i <= j && j <= this.size - 1) {
			if ( j == this.size - 1)
				return new Point( i + 1, j);
			else
				return new Point( i, j + 1);
		} else if (  j % 2 == 0 && j <= i && i <= this.size - 1) {
			if ( i == this.size - 1)
				return new Point( i, j + 1);
			else
				return new Point( i + 1, j);
		} else if (  j % 2 == 1 && j < i && i <= this.size - 1)
			return new Point( i - 1, j);
		else
			return new Point( -1, -1);
	}
	public int distanceAvoidingProhibitedPlaces( final EcoPlace e1, final EcoPlace e2, final Vector prohibitedPlaces) {
		final boolean marks[][] = new boolean[this.size][this.size];
		final int distanceToTheSource[][] = new int[this.size][this.size];
		final Vector file = new Vector();
		if ( e1.equals( e2))
			return 0;
		if ( prohibitedPlaces.isEmpty())
			return this.manhattanDistance( e1, e2);
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++)
	            marks[i][j] = prohibitedPlaces.contains( this.places[i][j]) ? true : false;
	    marks[e1.row][e1.col] = true;
	    file.addElement( e1);
	    while ( !file.isEmpty()) {
		    final EcoPlace e = ( EcoPlace) file.firstElement();
	        final Vector adjPlaces = e.adjacentPlaces();
			for( int i = 0; i < adjPlaces.size(); i++) {
				final EcoPlace aux = ( EcoPlace) adjPlaces.elementAt( i);
				if ( !marks[aux.row][aux.col]) {
					distanceToTheSource[aux.row][aux.col] = distanceToTheSource[e.row][e.col] + 1;
					if ( aux.equals( e2))
						return distanceToTheSource[aux.row][aux.col];
				    marks[aux.row][aux.col] = true;
					file.addElement( aux);
				}
			}
			file.removeElementAt( 0);
		}
		return 10000;
	}
	public int distanceToTheBlank( final EcoPlace ec) {
		return -1;
	}
	public int distanceToTheBlankAvoidingProhibitedPlaces( final EcoPlace ec, final Vector prohibitedPlaces) {
		return  this.distanceAvoidingProhibitedPlaces(  ec, this.blank.place, prohibitedPlaces);
	}
	@Override
	public  void  doEscapeActionWithConstraint( final EcoAgent constraint) {}
	@Override
	public  void  doEscapeAggressionWithConstraint( final EcoAgent constraint) {}
	@Override
	public void doSatisfactionActionOnPlace(final EcoAgent place) {
		if ( !this.isSatisfied()) {
			this.reset();
			this.satisfied = true;
			this.computeChainingList();
		}
	}
	@Override
	public  void  doSatisfactionAggressionOnPlace(final EcoAgent place)  {}
	@Override
	public  EcoAgent findEscapePlaceWithConstraint( final EcoAgent constraint) {return null;}
	@Override
	public  EcoAgent findSatisfactionPlace() {return null;}
	public void forbidRowColumn() {
	    for ( int i = 0; i < this.size; i++) {
	        this.places[this.indexFirstRowColumnAllowed][i].tile.changeStateTo( EcoAgent.FORBIDDEN);
	        this.places[i][this.indexFirstRowColumnAllowed].tile.changeStateTo( EcoAgent.FORBIDDEN);
		}
		++this.indexFirstRowColumnAllowed;
		//removeAdj();
		if ( this.indexFirstRowColumnAllowed == this.size - 2 && this.unsolvablePuzzle())
			if ( this.pzFrame.active != null)
				this.pzFrame.active.stop();
	}
	public void interrupt() {
		this.satisfied = false;
		this.indexFirstRowColumnAllowed = 0;
		final Graphics g = this.support.getGraphics();
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++) {
				this.places[i][j].goalAgent = null;
				this.places[i][j].tile.goalAgent = null;
				this.places[i][j].locked = false;
				if ( this.places[i][j].tile.state != EcoAgent.TRY_SATISFACTION) {
					this.places[i][j].tile.state = EcoAgent.TRY_SATISFACTION;
	                this.places[i][j].refresh( g);
	            }
	        }
	}
	@Override
	public boolean isFree() {
		return true;
	}
	@Override
	public boolean isSatisfied() {
		return this.satisfied;
	}
	public Vector lockedPlaces() {
		final Vector list = new Vector();
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++)
	            if ( this.places[i][j].locked)
	                list.addElement( this.places[i][j]);
	    return list;
	}
	public int manhattanDistance( final EcoPlace e1, final EcoPlace e2) {
		final int l = Math.abs( e2.row - e1.row);
		final int c = Math.abs( e2.col - e1.col);
		return l + c;
	}
	public Vector nearestAdjacentPlaces( final EcoPlace e1, final EcoPlace e2) {
		final Vector list = e1.adjacentPlaces();
		final Vector tempResult = new Vector();
		Vector result = new Vector();
		int d = 10000;
		for( int i = 0; i < list.size(); i++) {
			final EcoPlace aux = ( EcoPlace) list.elementAt( i);
			final int dm = this.manhattanDistance( aux, e2);
			if (  dm <= d) {
				if (  dm != d)
					tempResult.removeAllElements();
				tempResult.addElement( aux);
				d = dm;
			}
		}
		if ( tempResult.size() > 1) {
			d = 10000;
			for( int i = 0; i < tempResult.size(); i++) {
				final EcoPlace aux = ( EcoPlace) tempResult.elementAt( i);
				final int dm = this.distanceToTheBlankAvoidingProhibitedPlaces( aux, this.lockedPlaces());
				if (  dm <= d) {
					if (  dm != d)
						result.removeAllElements();
					result.addElement( aux);
					d = dm;
				}
			}
		} else
			result = tempResult;
		return result;
	}
	public void reInit( final boolean ordered) {
		this.suite = this.buildSuite( this.size * this.size);
		this.tiles = new EcoTile[ this.size * this.size];
		this.satisfied = false;
		this.indexFirstRowColumnAllowed = 0;
		final Graphics g = this.support.getGraphics();
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++) {
	            int aux;
	            if ( ordered)
	                aux = (i * this.size + j + 1) % ( this.size * this.size);
	            else
	                aux =  ( ( Integer) this.suite.elementAt( i * this.size + j)).intValue();
	            if ( aux == 0) {
	                this.blank = new EcoBlank( this.places[i][j]);
	                this.places[i][j].tile = this.blank;
	            } else
		            this.places[i][j].tile = new EcoTile( this.places[i][j], aux);
		        this.tiles[aux] = this.places[i][j].tile;
				this.places[i][j].goalAgent = null;
				this.places[i][j].tile.goalAgent = null;
				this.places[i][j].locked = false;
	            this.places[i][j].refresh( g);
	        }
	}
	public void reset() {
	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++) {
				this.places[i][j].goalAgent = null;
				this.places[i][j].tile.goalAgent = null;
				this.places[i][j].locked = false;
				this.places[i][j].forbidden = false;
			}
		this.goalAgent = null;
	}
	public void run() {
		this.trySatisfaction();
	}
	public void unlockSystem() {

	    for ( int i =0; i < this.size; i++)
	        for ( int j = 0; j < this.size; j++)
				if ( this.places[i][j].locked)
					this.places[i][j].locked = false;
	}
	public boolean unsolvablePuzzle() {

		final EcoPlace ec1 = this.tiles[(this.size-2)*this.size + this.size - 1].place;
		final EcoPlace ec2 = this.tiles[(this.size-2)*this.size + this.size].place;
		final EcoPlace ec3 = this.tiles[this.size*this.size-1].place;
		final int Xa = ec2.row - ec1.row;
		final int Ya = ec2.col - ec1.col;
		final int Xb = ec3.row - ec2.row;
		final int Yb = ec3.col - ec2.col;
		final int n = Xa*Yb - Xb*Ya;
		return n > 0;
	}
}
