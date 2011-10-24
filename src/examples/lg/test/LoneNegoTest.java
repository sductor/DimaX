package examples.lg.test;

import dima.basicagentcomponents.AgentName;
import examples.lg.agent.LoneNegoAgent;
import examples.lg.model.LetterGame;
/**
 * Insert the type's description here.
 * Creation date: (09/09/2003 17:17:17)
 * @author:
 */
public class LoneNegoTest {
/**
 * Insert the method's description here.
 * Creation date: (09/09/2003 17:17:31)
 * @param args java.lang.String[]
 */
public static void main(final String[] args)
{
	try
	{
		final LoneNegoAgent a1 = new LoneNegoAgent(new AgentName("a1"),new LetterGame("ZAHIA", "ZHEKA"));
		final LoneNegoAgent a2 = new LoneNegoAgent(new AgentName("a2"),new LetterGame("TAREK","TNRIA"));
		final LoneNegoAgent a3 = new LoneNegoAgent(new AgentName("a3"),new LetterGame("ARNAUD", "AANRD"));

		a1.addAquaintance(a2.getAddress());
		a1.addAquaintance(a3.getAddress());
		a2.addAquaintance(a1.getAddress());
		a2.addAquaintance(a3.getAddress());
		a3.addAquaintance(a1.getAddress());
		a3.addAquaintance(a2.getAddress());

		a1.activate();
		a2.activate();
		a3.activate();

	} catch (final Exception e)
	{
		e.printStackTrace(System.out);
	}
}
}
