package examples.lg.test;
import examples.lg.agent.LoneATNAgent;
import examples.lg.model.LetterGame;

public class LoneTest {

   public static void main(final String[] args) {
      try {
         final LoneATNAgent a1 = new LoneATNAgent(new LetterGame("HELLOWORLD", "ABBDA"));
         a1.setAtn(LoneATNAgent.buildATN());
         a1.activate();
         final LoneATNAgent a2 = new LoneATNAgent(new LetterGame("BONJOUR","HJZSE"));
         a2.setAtn(LoneATNAgent.buildATN());
         a2.activate();
         final LoneATNAgent a3 = new LoneATNAgent(new LetterGame("SOMEWORDS", "LDFRE"));
         a3.setAtn(LoneATNAgent.buildATN());
         a3.activate();
      } catch (final Exception e) {
         e.printStackTrace(System.out);
      }
   }
}
