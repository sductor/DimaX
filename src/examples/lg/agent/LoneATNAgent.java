package  examples.lg.agent;


import java.util.Vector;

import dima.tools.automata.ATN;
import dima.tools.automata.State;
import dima.tools.automata.Transition;
import examples.lg.model.LetterGame;

public class LoneATNAgent extends LoneDomain {

   // actions

   // actions

   // actions

   // actions

   /**
	 *
	 */
	private static final long serialVersionUID = 661476885922682560L;

public LoneATNAgent(final LetterGame agame) {
      this.game = agame;
   }

   @Override
public void setAtn(final ATN newAtn) {
 this.atn = newAtn;
 this.currentState = this.atn.getInitialState();
}



   public static ATN buildATN() {
      // initializing states
      final State st_end = new State("end"); st_end.beFinal();
      final State st_displaying = new State("displaying"); st_displaying.beNormal();
      final State st_intermediary = new State("intermediary"); st_intermediary.beNormal();
      final State st_start = new State("start"); st_start.beInitial();

      // transition lists
      final Vector tl_displaying = new Vector();
      final Vector tl_intermediary = new Vector();
      final Vector tl_start = new Vector();

      //transitions
      final Transition display_noConditionTransition = new Transition("noCondition","display", st_start);
      tl_displaying.addElement(display_noConditionTransition);
      st_displaying.setTransitionList(tl_displaying);

      final Transition dropLetter_hasNotNeededLetterTransition = new Transition("hasNotNeededLetter","dropLetter", st_displaying);
      tl_intermediary.addElement(dropLetter_hasNotNeededLetterTransition);
      final Transition useLetter_hasNeededLetterTransition = new Transition("hasNeededLetter","useLetter", st_displaying);
      tl_intermediary.addElement(useLetter_hasNeededLetterTransition);
      st_intermediary.setTransitionList(tl_intermediary);

      final Transition noAction_isCompleteTransition = new Transition("isComplete","noAction", st_end);
      tl_start.addElement(noAction_isCompleteTransition);
      final Transition noAction_isNotCompleteTransition = new Transition("isNotComplete","noAction", st_intermediary);
      tl_start.addElement(noAction_isNotCompleteTransition);
      st_start.setTransitionList(tl_start);

      final Vector finalStates = new Vector();
      finalStates.addElement(st_end);
      return new ATN(st_start, finalStates);
   }

   // conditions

   public boolean hasNotNeededLetter() {
      return !this.hasNeededLetter();
   }

   public boolean isNotComplete() {
      return !this.isComplete();
   }

   // actions

}