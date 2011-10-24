package dima.introspectionbasedagents.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;

/**
 * Les methodes MessageHandler doivent prendre exactement un argument de
 * type Message.
 * Ces methodes sont associés à des envellopes.
 * Si aucune annotation définissant une envellope n'est fournit,
 * la méthode est associé a la classe de son argument.
 * Le shell parcourt la liste des messages reçuent.
 * Recupere leur envellope grace a la méthode getMyEnvellope()
 * et recherche la méthode associé à la meme envellope.
 * Si elle n'est pas trouvée, le shell recherche l'envellope du supertype du message,
 * puis du supertype du supertype, etc.
 * Si la méthode getMyEnvellope() n'existe pas,
 * on associe au message l'envellope correspondant a sa classe.
 *
 * @{public Envellope makeEnvellope(Method mt, Class<? extends
 *          AbstractMessage> m)} et {gpublic Envellope getMyEnvellope()} et
 *          en ajoutant un constructeur ne prenant pas d'argument
 * @see FipaACLMessage
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageHandler {}