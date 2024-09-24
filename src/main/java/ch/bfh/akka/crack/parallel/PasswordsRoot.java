/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 */
package ch.bfh.akka.crack.parallel;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;

/**
 * The root actor of the Akka passwords system. It spawns as many as passwords actors
 * as being told by an initial message. The initial message must also contain the
 * names of files to read.
 */
public class PasswordsRoot extends AbstractOnMessageBehavior<PasswordsRoot.Message> {

    /** The common abstraction of all messages for this actor. */
    public interface Message {}

    /*
     * TODO Provide a hierarchy of messages. The init message should contain:
     * @param numberOfPasswordsActors
     * @param fileNameHashedPasswords
     * @param fileNameCleartextPasswords
     */

    /* TODO An interval (in milliseconds), used for displaying liveliness. */

    /* TODO A timer key. */

    /* TODO The number of password cracker actors to spawn. */

    /* TODO A counter for remembering the number of actors have been terminated. */


    /* TODO Stores the start time (in nanoseconds). */

    /* TODO The timer system (aka timer scheduler). */


    /*
     * TODO Factory method for creating this behavior.
     */

    /*
     * TODO Check constructor
     */
    private PasswordsRoot(ActorContext<Message> context) {
        super(context);
        // TODO Check...
    }

    /*
     * TODO Dispatcher for the messages received.
     */
    @Override
    public Behavior<Message> onMessage(Message message) {
        // TODO Implement
        return null;
    }

    /*
     * TODO The handlers for the messages received.
     */

}
