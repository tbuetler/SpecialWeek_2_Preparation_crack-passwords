/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 */
package ch.bfh.akka.crack.parallel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;

/**
 * The behavior of a password cracker actor.
 */
public class PasswordsActor extends AbstractOnMessageBehavior<PasswordsActor.Message> {

    /**
     * Return a string representing the hash of the given <code>textToHash</code>, using
     * the algorithm <code>algorithm</code>. The hash is returned encoded in Base64
     * using UTF-8.
     *
     * @param algorithm  for the hashing of the string
     * @param textToHash the text that is to be hashed
     * @return the base64 encoding of the hash code
     */
    public static String hash(String algorithm, String textToHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
        byte[] hashedByteArray = digest.digest(byteOfTextToHash);
        return Base64.getEncoder().encodeToString(hashedByteArray);
    }

    /**
     * The common abstraction of all messages for this actor.
     */
    public interface Message {}

    /*
     * TODO Provide a hierarchy of messages. The setup message should look like:
     *
     * The setup message containing an actor reference to the root actor for reporting the result as well
     * as the parameters for the part of the input data to work on.
     * @param rootRef the actor reference to the root actor
     * @param fileNameHashedPasswords the file name of the hashed passwords
     * @param fileNameCleartextPasswords the file name of the cleartext passwords
     * @param lineMin the number of the first line of the cleartext password file to operate on
     * @param lineMax the number of the last line of the cleartext password file to operate on
     */

    /* TODO Optional, an id, used for log entries. */


    /* TODO The actor reference to the root actor for sending the result. */


    /* TODO A container for the found passwords. */


    /*
     * TODO Factory method for creating the initial behavior of this actor.
     */

    /*
     * TODO Check constructor.
     */
    private PasswordsActor(ActorContext<Message> context) {
        super(context);
    }

    /**
     * TODO Dispatcher for the messages received.
     */
    @Override
    public Behavior<Message> onMessage(Message message) {
        // TODO Implement
        return null;
    }

    /*
     * TODO The handlers for the messages received.
     *
     * Hint: Use a CompletableFuture for the long-lasting task in conjunction with pipeToSelf,
     * see: https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html#send-future-result-to-self
     */

}
