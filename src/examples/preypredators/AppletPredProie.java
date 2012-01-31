package examples.preypredators;

/**
 * Insert the type's description here.
 * Creation date: (04/11/00 11:49:55)
 * @author: Administrator
 */
import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;

public class AppletPredProie extends Applet {
	/**
	 *
	 */
	private static final long serialVersionUID = 8029015053058371401L;
	Font font;
	String str;
	int xPos;
	private PredProieAPI windowPredProie;

	/**
	 * AppletPredProie constructor comment.
	 */
	public AppletPredProie() {
		//super();
		this.font = new Font("Dialog", 1, 24);
		this.str = "Predator";
		this.xPos = 10;
	}
	private void handleException(final Throwable exception)
	{
	}
	@Override
	public void init()
	{
		super.init();
		try
		{
			this.setName("Predator Applet");
			this.setLayout(null);
			this.setBackground(SystemColor.textHighlightText);
			this.setSize(580, 460);
			this.windowPredProie = new PredProieAPI();
			this.windowPredProie.setVisible(true);
		}
		catch (final Throwable ivjExc)
		{
			this.handleException(ivjExc);
		}
	}
	@Override
	public void paint(final Graphics g)
	{
		g.setFont(this.font);
		g.setColor(Color.black);
		g.drawString(this.str, this.xPos, 50);
	}
}
