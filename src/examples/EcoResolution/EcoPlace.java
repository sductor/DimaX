package examples.EcoResolution;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

//************************************************************************
//************************************************************************
//La classe EcoPlace
//************************************************************************
//************************************************************************

class EcoPlace extends EcoAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = -6819748889137853371L;
	int row, col;
	EcoNPuzzle owner;
	EcoTile tile;
	boolean locked = false;
	boolean forbidden = false;
	boolean repainte = false;

	int size;
	int posX;
	int posY;

	EcoPlace( final EcoNPuzzle taq, final int l, final int c) {
		this.owner = taq;
		this.row = l;
		this.col = c;
		this.size = this.owner.support.size / this.owner.size;
		this.posX = c * this.size;
		this.posY = l * this.size;
	}
	public Vector adjacentPlaces() {
		//retourne la list des Ecocases adjacentes a this
		//( il y en a 2, 3, ou 4)
		final int ilc = this.owner.indexFirstRowColumnAllowed;

		final Vector adjPlaces = new Vector();
		if ( this.row > ilc)
			adjPlaces.addElement( this.owner.places[this.row-1][this.col]);
		if ( this.row + 1 < this.owner.size)
			adjPlaces.addElement( this.owner.places[this.row+1][this.col]);
		if ( this.col > ilc)
			adjPlaces.addElement( this.owner.places[this.row][this.col-1]);
		if ( this.col + 1 < this.owner.size)
			adjPlaces.addElement( this.owner.places[this.row][this.col+1]);
		return adjPlaces;
	}
	@Override
	public Vector bolts() {
		final Vector result = new Vector();
		result.addElement( this.tile);
		return result;
	}
	@Override
	public boolean canEscapeWithConstraint( final EcoAgent constraint) {
		return false;
	}
	@Override
	public  boolean canSatisfyOnPlace(final EcoAgent place) {return true;}
	@Override
	public  void  doEscapeActionWithConstraint( final EcoAgent constraint) {}
	@Override
	public  void  doEscapeAggressionWithConstraint( final EcoAgent constraint) {}
	@Override
	public  void  doSatisfactionActionOnPlace(final EcoAgent place) {}
	@Override
	public  void  doSatisfactionAggressionOnPlace(final EcoAgent place) {}
	@Override
	public  EcoAgent findEscapePlaceWithConstraint( final EcoAgent constraint) {return this;}
	@Override
	public  EcoAgent findSatisfactionPlace() {return this;}
	public boolean isAdjacentTo( final EcoPlace ec) {//renvoie true si ec est adjacent a "this"
		if ( this.row == ec.row && Math.abs( this.col - ec.col) == 1 ||
				this.col == ec.col && Math.abs( this.row - ec.row) == 1)
			return true;
		else
			return false;
	}
	@Override
	public boolean isFree() {
		return this.tile instanceof EcoBlank;
	}
	@Override
	public boolean isSatisfied() {
		return true;
	}
	public boolean  mouseDown() {
		if ( this.isAdjacentTo( this.owner.blank.place)) {
			final EcoPlace ec = this.owner.blank.place;
			this.switchTiles( ec);
			final Graphics g = this.owner.support.getGraphics();
			this.refresh( g);
			ec.refresh( g);
		}
		return true;
	}
	//********************************************************************
	// Methodes de EcoCases li'ees a l'interface graphique
	//********************************************************************
	public void refresh( final Graphics g) {
		if ( this.tile.number != 0) {
			Color c;
			switch( this.tile.state) {
				case EcoAgent. TRY_SATISFACTION :
					c = Color.blue;
					break;
				case EcoAgent.FORBIDDEN :
					c = Color.darkGray;
					break;
				case EcoAgent.SATISFIED :
					c = Color.gray;
					break;
				case EcoAgent.AGGRESSION_SATISFACTION :
					c = Color.red;
					break;
				case EcoAgent.AGGRESSION_ESCAPE :
					c = new Color( 153, 0, 153);
					break;
				default :
					c = Color.gray;
			}
			g.setColor( c );
			g.fill3DRect( this.posX, this.posY, this.size, this.size, true);//representation graphique d'un EcoPalets :
			//g.fill3DRect( posX + 1, posY + 1, size - 2, size - 2, false);// 3 rectangle "3D" imbriqu'es pour
			//g.fill3DRect( posX + 2, posY + 2, size - 4, size - 4, false);//creer un effet de relief
			g.setColor( Color.white);
			g.setFont( new Font( "Arial", Font.BOLD, this.size / 2));
			final FontMetrics fm = g.getFontMetrics();
			final String st = new Integer( this.tile.number).toString();
			g.drawString( st, this.posX + ( this.size - fm.stringWidth(st)) / 2,
					this.posY + ( this.size + fm.getAscent()) /2 - 4  );
		} else {
			g.setColor(  Color.white);
			g.fillRect( this.posX, this.posY, this.size, this.size);
		}
	}
	public void switchTiles( final EcoPlace ec) {//intervertit la place de 2 EcoPalets
		// se trouvant sur cette EcoPlace avec celui se trouvant sur l'EcoPlace ec
		final EcoTile aux = this.tile;
		this.tile = ec.tile;
		ec.tile.place = this;
		aux.place = ec;
		ec.tile = aux;
	}
	@Override
	public String toString() {
		return new String( "Patch[" + this.row + "][" + this.col + "]");
	}
}
