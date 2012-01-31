package dima.tools.XCS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class implements a maze environment.
 * It reads in a maze from a file. This file must contain the maze in a nxm matrix where each
 * entry specifies one distinct position in the maze. If the animat reaches an edge and moves out of the edge,
 * it will reenter the maze from the other side (if not blocked). Obstacles are coded as 'O'or'0' and 'Q', empty positions as
 * '.'or'*', and food as 'F' and 'G'. A two bit or three bit coding can be chosen for the coding of the perceptions. In the
 * three bit coding the perceptions differentiate 'F' and 'G', and 'O'/'0' and 'Q' while they appear the same in the two
 * bit coding.
 * Eight movements are possible to the adjacent cells (if not blocked). The environment starts by positioning the animat
 * at a random position. It moves as long as no food position is reached. If a food position is reached, the environment
 * provides the payoff specified in maxPayoff. Also, the reset flag is set to true in this case.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class MazeEnvironment implements Environment, Serializable
{
	/*###################---- Constants which are preset ----###################*/

	/**
	 *
	 */
	private static final long serialVersionUID = 5519897845049102521L;

	/**
	 * The number of perceptions of the animat.
	 */
	private final int conLength=8;

	/**
	 * The number of generally possible movements.
	 */
	private final int nrActions=8;

	/**
	 * The payoff provided at a food position.
	 */
	private final int maxPayoff=1000;

	/**
	 * The binary code of an empty position (000).
	 */
	private final char[] freeAtt = {'0','0','0'};

	/**
	 * The binary code of a food-F position (110).
	 */
	private final char[] foodF = {'1','1','0'};

	/**
	 * The binary code of a food-G position (111).
	 */
	private final char[] foodG = {'1','1','1'};

	/**
	 * The binary code of an O-obstacle position (010).
	 */
	private final char[] obstacleO = {'0','1','0'};

	/**
	 * The binary code of a Q-obstacle position (011).
	 */
	private final char[] obstacleQ = {'0','1','1'};



	/*###################---- These variables are set in the constructor ----###################*/

	/**
	 * The attribute Length specifies the number of bits that code each perceived position in the maze.
	 * It can be set to 2 or 3.
	 */
	private int attributeLength;

	/**
	 * The matrix that codes the maze (in binary)
	 */
	private final char[][] maze;

	/**
	 * The size of the maze in positions.
	 */
	private int xsize,ysize;

	/**
	 * The current position of the animat.
	 */
	private int xcurrent, ycurrent;

	/**
	 * Flag which is set to true when food was reached.
	 */
	private boolean reset;

	/**
	 * The constructor reads in the specified maze file and sets its global parameters accordingly.
	 *
	 * @param inFileString must specify the file name where the maze is coded
	 * @param attLength specifies the number of bits that specify one attribute (either two or three).
	 * If wrongly specified, then it is set to two.
	 */
	public MazeEnvironment(final String inFileString, final int attLength)
	{
		if(attLength==3)
			this.attributeLength=3;
		else
			this.attributeLength=2;

		FileReader fr=null;
		BufferedReader br=null;
		final Vector mazeLines=new Vector();
		try{
			fr=new FileReader(inFileString);
			br=new BufferedReader(fr);
			this.xsize=0;
			this.ysize=0;
			while(br.ready()){
				final String in=br.readLine();
				if(this.xsize==0)
					this.xsize=in.length();
				final char[] oneLine=new char[this.xsize*this.attributeLength];
				for(int i=0; i<this.xsize; i++){
					final char att=in.charAt(i);
					switch(att){
					case '.': case '*': /* Intentionally to sum both cases! */
						for(int j=0; j<this.attributeLength; j++)
							oneLine[i*this.attributeLength + j]=this.freeAtt[j];
						break;
					case 'F':
						for(int j=0; j<this.attributeLength; j++)
							oneLine[i*this.attributeLength + j]=this.foodF[j];
						break;
					case 'G':
						for(int j=0; j<this.attributeLength; j++)
							oneLine[i*this.attributeLength + j]=this.foodG[j];
						break;
					case 'O':case '0': /* Intentionally to sum both cases */
						for(int j=0; j<this.attributeLength; j++)
							oneLine[i*this.attributeLength + j]=this.obstacleO[j];
						break;
					case 'Q':
						for(int j=0; j<this.attributeLength; j++)
							oneLine[i*this.attributeLength + j]=this.obstacleQ[j];
						break;
					default:
						System.out.println("Unknown Character: "+att);
						System.exit(0);
						break;
					}
				}
				mazeLines.addElement(oneLine);
			}
		}catch(final Exception e){System.out.println("Could not Read File!"+e);}
		this.ysize=mazeLines.size();
		this.maze=new char[this.xsize*this.attributeLength][this.ysize];

		for(int i=0; i<this.ysize; i++){
			final char[] line = (char [])mazeLines.elementAt(i);
			for(int j=0; j<line.length; j++)
				this.maze[j][i]=line[j];
		}
		this.reset=false;
		this.setRandomPosition();
	}
	/**
	 * Returns if the animat should be reseted.
	 * The reset flag is set to true once the animat reached a food position.
	 */
	@Override
	public boolean doReset()
	{
		return this.reset;
	}
	/**
	 * Executes the specified action in the environment and returns possible payoff.
	 *
	 * @param action The action to be executed.
	 */
	@Override
	public double executeAction(final int action)
	{
		if(action<0 || action>7){
			System.out.println("Not an action!");
			System.exit(0);
		}
		/* Test if new position is empty!!! */
		final int xaim=(this.xcurrent + (int)((Math.ceil((action-3)/4.)*2-1)*-1* Math.ceil(action%4/4.))+this.xsize)%this.xsize;
		final int yaim=(this.ycurrent + (int)((Math.ceil(Math.floor(action%7/2.)/4.)*2-1.)*Math.ceil((action+2)%4/4.))
				+this.ysize) % this.ysize;
		if(this.maze[xaim*this.attributeLength][yaim]!=this.obstacleO[0] || this.maze[1+ xaim*this.attributeLength][yaim]!=this.obstacleO[1]){
			this.xcurrent=xaim;
			this.ycurrent=yaim;
			if(this.maze[this.xcurrent*this.attributeLength][this.ycurrent]==this.foodF[0] && this.maze[1+ this.xcurrent*this.attributeLength][this.ycurrent]==this.foodF[1]){
				this.reset=true;
				return this.maxPayoff;
			}
			return 0.;
		}
		return 0.;
	}
	/**
	 * Returns the length of the perceptions.
	 */
	@Override
	public int getConditionLength()
	{
		return this.conLength * this.attributeLength;
	}
	/**
	 * Returns the current perceptions.
	 */
	@Override
	public String getCurrentState()
	{
		return this.getPerceptions();
	}
	/**
	 * Returns the maximal Payoff in the maze.
	 */
	@Override
	public int getMaxPayoff()
	{
		return this.maxPayoff;
	}
	/**
	 * Returns the number of actions possible in the maze environment.
	 */
	@Override
	public int getNrActions()
	{
		return this.nrActions;
	}
	/**
	 * Returns a String of the perceptions in the current position.
	 */
	private String getPerceptions()
	{
		final char[] perc=new char[this.getConditionLength()];
		for(int i=0; i<this.conLength; i++)
			for(int j=0; j<this.attributeLength; j++)
				perc[i*this.attributeLength + j] =
				this.maze[j + this.attributeLength *
				          ((this.xcurrent + (int)((Math.ceil((i-3)/4.)*2-1)*-1* Math.ceil(i%4/4.))+this.xsize)%this.xsize)]
				        		  [(this.ycurrent + (int)((Math.ceil(Math.floor(i%7/2.)/4.)*2-1.)*Math.ceil((i+2)%4/4.))+this.ysize)%this.ysize];
		return new String(perc);
	}
	/**
	 * Returns true since any maze is a multi-step environment.
	 */
	@Override
	public boolean isMultiStepProblem()
	{
		return true;
	}
	/**
	 * Resets the animat to a random empty position and returns the perceptions in this position.
	 */
	@Override
	public String resetState()
	{
		this.setRandomPosition();
		this.reset=false;
		return this.getPerceptions();
	}
	/**
	 * Sets the animat to a randomly selected empty position.
	 */
	private void setRandomPosition()
	{
		do{
			this.xcurrent=(int)(XCSConstants.drand()*this.xsize);
			this.ycurrent=(int)(XCSConstants.drand()*this.ysize);
		}while(this.maze[this.xcurrent*this.attributeLength][this.ycurrent]!=this.freeAtt[0] || this.maze[this.xcurrent*this.attributeLength+1][this.ycurrent]!=this.freeAtt[1]);
	}
	/**
	 * Returns always false since there is no real correct or wrong action in the maze environment.
	 */
	@Override
	public boolean wasCorrect()
	{
		return false;/* No correct or wrong action in this environment */
	}
}
