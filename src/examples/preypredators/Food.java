package examples.preypredators;

/**
 * Insert the type's description here.
 * Creation date: (04/11/00 13:40:55)
 * @author: Administrator
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

public class Food extends Actor implements Animal {
	/**
	 *
	 */
	private static final long serialVersionUID = 7852251723963606200L;
	public int QuiSuisJe = 2;
	private int energy;
	private Point pos;
	public boolean isAlive = true;
	private World world;
	private int distanceView;
	/**
	 * Proie constructor comment.
	 */
	public Food() {
		super();
		boolean pasPlace = true;
		while (pasPlace) {
			final java.util.Random r = new java.util.Random();
			final int laValeurX = r.nextInt(Actor.grid.getTailleGrille());
			final int laValeurY = r.nextInt(Actor.grid.getTailleGrille());
			synchronized (Actor.Tableau) {
				if (Actor.Tableau[laValeurX][laValeurY]==null) {
					this.dessinerProie(laValeurX,laValeurY);
					this.setPosX(laValeurX);
					this.setPosY(laValeurY);
					System.out.println("Prey is placed: " + laValeurX + ", " + laValeurY);
					Actor.Tableau[laValeurX][laValeurY]=this;
					pasPlace = false;
				}
			}
		}
	}
	/**
	 * Predator constructor comment.
	 */
	public Food(final World world) {
		super();
		this.energy = 500;
		this.distanceView = 2;
		this.pos = new Point();
		this.world = world;
	}
	/*private static int deadProies;
	private int posX;
	private int posY;
	private static Grid fpp = FramePredProie.getGrille();

	Dimension d = fpp.getSize();
	int xoff = d.width / fpp.getTailleGrille();
	int yoff = d.height / fpp.getTailleGrille();
	int xoffqua = 3 * xoff / 4;
	int yoffqua = 3 * yoff / 4;
	int xoffmid = xoff / 2;
	int yoffmid = yoff / 2;*/

	public void dessinerProie(final int xpos, final int ypos) {
		final Graphics g = this.getGrille().getGraphics();
		this.dessinerActeur(g,xpos,ypos,Color.red);
	}
	/*private static int deadProies;
	private int posX;
	private int posY;
	private static Grid fpp = FramePredProie.getGrille();

	Dimension d = fpp.getSize();
	int xoff = d.width / fpp.getTailleGrille();
	int yoff = d.height / fpp.getTailleGrille();
	int xoffqua = 3 * xoff / 4;
	int yoffqua = 3 * yoff / 4;
	int xoffmid = xoff / 2;
	int yoffmid = yoff / 2;*/

	public void dessinerProie(final int xpos, final int ypos, final Color col) {
		final Graphics g = this.getGrille().getGraphics();
		this.dessinerActeur(g,xpos,ypos,col);
	}
	@Override
	public int getDistanceView(){
		return this.distanceView;
	}
	public boolean getEmptyPlace(final int x, final int y) {
		final Vector env = this.world.getEnvironement(this);
		int i;
		final Point pt = new Point (x,y);
		if (x == this.world.getSize()|y == this.world.getSize()) return false;
		for (i=0;i<env.size();i++)
			if (! pt.equals(((Animal) env.elementAt(i)).getPos()))
				return true;
		return false;
	}
	@Override
	public Point getPos(){
		return this.pos;
	}
	public Point getRandomPlace() {
		final Point res = new Point (this.pos.x,this.pos.y);
		final Vector env = this.world.getEnvironement(this);
		switch((int) (Math.random() * 7D)){
			case 0:
				if(res.y > 0)
					res.y--;

				break;

			case 1:
				if(res.x > 0)
					res.x--;
				break;

			case 2:
				if(res.x < this.world.getSize())
					res.x++;
				break;

			case 3:
				if(res.y < this.world.getSize())
					res.y++;
				break;

			case 4:
				if(res.x < this.world.getSize()&&res.y > 0){
					res.x++;
					res.y--;
				}
				break;

			case 5:
				if(res.y < this.world.getSize()&&res.x > 0){
					res.x--;
					res.y++;
				}
				break;

			case 6:
				if(res.x < this.world.getSize()&&this.pos.y < this.world.getSize()){
					res.x++;
					res.y++;
				}
				break;
			case 7:
				if(res.x > 0&&res.y > 0){
					res.x--;
					res.y--;
				}
				break;
		}


		return res;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/11/00 21:20:33)
	 */
	public Animal getTarget() {
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (09/11/00 21:20:33)
	 */
	@Override
	public Animal getTarget(final Vector v) {
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:44:14)
	 * @return boolean
	 */
	@Override
	public boolean isActive() {
		return this.isAlive;
	}
	public boolean isEmptyPlace(final int x, final int y) {
		final Vector env = this.world.getEnvironement(this);
		int i;
		final Point pt = new Point (x,y);
		if (x == this.world.getSize()|y == this.world.getSize()) return false;
		if (x < 0|y < 0) return false;
		for (i=0;i<env.size();i++)
			if (! pt.equals(((Animal) env.elementAt(i)).getPos()))
				return true;
		return false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:45:41)
	 */
	@Override
	public void proactivityInitialize(){
		this.pos.setLocation((int)(Math.random() * this.world.getSize()), (int)(Math.random() * this.world.getSize()));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/11/00 13:46:28)
	 */
	@Override
	public synchronized void proactivityTerminate(){
		System.out.println("Food kaput - Position: "+ this.pos.x+" "+this.pos.y);
	}
	@Override
	public void step() {
		Animal currentAnimal;
		final Vector env = this.world.getEnvironement(this);
		int distX = 0;
		int distY = 0;
		int nbPredators = 0;

		this.energy--;
		if(this.energy == 0){
			this.isAlive = false;
			return;
		}
		this.dessinerProie(this.pos.x,this.pos.y,Color.lightGray);
		//regarde si predateur sur la meme case
		for(int i=0; i<env.size() ; i++)
			if(this.getPos().equals(((Animal)env.elementAt(i)).getPos()) && env.elementAt(i) instanceof Predator){
				this.isAlive = false;
				((Predator) env.elementAt(i)).addEnergy(this.energy);
				System.out.println("Food eated at: "+this.pos.x+" "+this.pos.y);
				return;
			}
		//Deplacement loin des mchants ...
		//On calcul le barycentre des mchants
		for (int i=0; i<env.size(); i++){
			currentAnimal =(Animal) env.elementAt(i);
			if (this.world.distance(this,currentAnimal)<= this.distanceView&& currentAnimal instanceof Predator){
				distX = distX + currentAnimal.getPos().x;
				distY = distY + currentAnimal.getPos().y;
				nbPredators ++;
			}
		}
		if (nbPredators == 0){
			Point pt;
			pt = this.getRandomPlace();
			this.pos = pt;
			this.dessinerProie(this.pos.x,this.pos.y);
			return;
		}

		distX = distX / nbPredators;
		distY = distY / nbPredators;

		final int dirX = distX - this.pos.x;
		final int dirY = distY - this.pos.y;


		if (dirX == 0 && dirY == 0){
			if (this.getEmptyPlace(this.pos.x+1,this.pos.y+1)){
				this.pos.x ++;
				this.pos.y ++;
			}
			if (this.getEmptyPlace(this.pos.x+1,this.pos.y)) this.pos.x ++;


			if (this.getEmptyPlace(this.pos.x,this.pos.y+1)) this.pos.y ++;

			if (this.getEmptyPlace(this.pos.x-1,this.pos.y)) this.pos.x --;


			if (this.getEmptyPlace(this.pos.x,this.pos.y-1)) this.pos.y --;

			if (this.getEmptyPlace(this.pos.x-1,this.pos.y-1)){
				this.pos.x --;
				this.pos.y --;
			}
			if (this.getEmptyPlace(this.pos.x-1,this.pos.y+1)){
				this.pos.x --;
				this.pos.y ++;
			}
			if (this.getEmptyPlace(this.pos.x+1,this.pos.y-1)){
				this.pos.x ++;
				this.pos.y --;
			}

		}

		if (dirX > 0){
			this.pos.x --;
			if (dirY > 0) this.pos.y --;
			if (dirY < 0&&this.pos.y < this.world.getSize()) this.pos.y ++;
		}

		if (dirX < 0&&this.pos.x < this.world.getSize()) {
			this.pos.x ++;
			if (dirY > 0) this.pos.y --;
			if (dirY < 0&&this.pos.y < this.world.getSize()) this.pos.y ++;
		}
		if (dirX == 0){
			if (dirY > 0) this.pos.y --;
			if (dirY < 0&&this.pos.y < this.world.getSize())  this.pos.y ++;
		}


		this.dessinerProie(this.pos.x,this.pos.y);
	}
}
