/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 * Author: 		Tim Bütler
 * Disclaimer: 	Artificial Intelligence were in use for some code optimizations
 */

package ch.bfh.akka.crack.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

/**
 * The behavior of a password cracker actor.
 * This actor is responsible for taking a portion of the password file,
 * hashing them, and comparing them to the given hashed password file.
 * Once the actor finishes its job, it notifies the root actor.
 */
public class PasswordsActor extends AbstractOnMessageBehavior<PasswordsActor.Message> {

	// Reference to the root actor (PasswordsRoot) to send the FinishedMessage after work is done
	private final ActorRef<PasswordsRoot.Message> rootRef;

	// A unique identifier for this actor instance, useful for logging and tracking
	private final String actorId;

	/**
	 * Returns a hashed string using a specified algorithm and the text to hash.
	 * The result is encoded in Base64 format for easy comparison.
	 *
	 * @param algorithm  Algorithm used for hashing, e.g., SHA-512
	 * @param textToHash The plain text (e.g., password + salt) to be hashed
	 * @return The base64-encoded string of the hashed text
	 * @throws NoSuchAlgorithmException if the specified algorithm is invalid
	 */
	public static String hash(String algorithm, String textToHash) throws NoSuchAlgorithmException {
		// Get an instance of the digest algorithm
		MessageDigest digest = MessageDigest.getInstance(algorithm);

		// Convert the input text to bytes and hash it
		byte[] byteOfTextToHash = textToHash.getBytes(StandardCharsets.UTF_8);
		byte[] hashedByteArray = digest.digest(byteOfTextToHash);

		// Return the hash as a base64-encoded string
		return Base64.getEncoder().encodeToString(hashedByteArray);
	}

	/**
	 * Common interface for all messages that PasswordsActor can receive.
	 * In this system, actors exchange messages to coordinate actions.
	 */
	public interface Message {}

	/**
	 * Setup message to initialize the actor with necessary data such as
	 * file names and the range of lines to process.
	 */
	public record SetupMessage(ActorRef<PasswordsRoot.Message> rootRef, String hashedPasswords,
							   String cleartextPasswords, int startLine, int endLine) implements Message {}

	// A message to signal that the actor has finished its task
	public record FinishedMessage() implements Message {}

	/**
	 * Static factory method to create a new PasswordsActor.
	 *
	 * @param name    Name of the actor (used for logging)
	 * @param rootRef Reference to the root actor to report progress
	 * @return The behavior of the newly created PasswordsActor
	 */
	public static Behavior<Message> create(String name, ActorRef<PasswordsRoot.Message> rootRef) {
		// Use Behaviors.setup to instantiate the actor with context and state
		return Behaviors.setup(context -> new PasswordsActor(context, name, rootRef));
	}

	/**
	 * Constructor for PasswordsActor.
	 *
	 * @param context The actor's context (contains its environment and utilities)
	 * @param actorId A unique identifier for this actor, useful for logging
	 * @param rootRef The reference to the root actor to send messages back to it
	 */
	private PasswordsActor(ActorContext<Message> context, String actorId, ActorRef<PasswordsRoot.Message> rootRef) {
		super(context);
		this.actorId = actorId;
		this.rootRef = rootRef;
	}

	@Override
	public Behavior<Message> onMessage(Message message) {
		// Handle incoming messages by type
		return switch (message) {
			case SetupMessage sm -> handleSetup(sm); // If it's a SetupMessage, handle the setup
			default -> Behaviors.unhandled(); // Otherwise, mark it as unhandled
		};
	}

	/**
	 * Handles the SetupMessage, reading the necessary password files and
	 * initiating the password cracking process.
	 *
	 * @param setupMessage The setup message containing file names and line ranges
	 * @return Behavior The actor's new behavior (in this case, it stays the same)
	 */
	private Behavior<Message> handleSetup(SetupMessage setupMessage) {
		// Log that the setup has been received, indicating the start and end line for this actor's task
		getContext().getLog().info("{}: Received setup message for lines {} to {}...", actorId, setupMessage.startLine(), setupMessage.endLine());

		// Try to read the files and start the password-cracking process
		try {
			List<String> hashedLines = readFile(setupMessage.hashedPasswords());
			List<String> cleartextPasswords = readFileRange(setupMessage.cleartextPasswords(), setupMessage.startLine(), setupMessage.endLine());

			// Attempt to crack the passwords by comparing hashes
			crackPasswords(hashedLines, cleartextPasswords);
		} catch (IOException | NoSuchAlgorithmException e) {
			// Log any errors that occur during file reading or password cracking
			getContext().getLog().error("{}: Error occurred during password cracking.", actorId, e);
		} finally {
			// Once the actor is done with its work, send a FinishedMessage to the root actor
			rootRef.tell(new PasswordsRoot.FinishedMessage());
		}

		// Keep the same behavior after processing this message
		return Behaviors.same();
	}

	/**
	 * Reads all lines from the specified file.
	 *
	 * @param fileName The name of the file to read
	 * @return A list of lines from the file
	 * @throws IOException If an I/O error occurs
	 */
	private List<String> readFile(String fileName) throws IOException {
		List<String> lines = new LinkedList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			while (line != null) {
				lines.add(line); // Add each line to the list
				line = br.readLine(); // Read the next line
			}
		}
		return lines;
	}

	/**
	 * Reads a range of lines from a file, starting from startLine to endLine.
	 *
	 * @param fileName  The file to read from
	 * @param startLine The first line to read (inclusive)
	 * @param endLine   The last line to read (inclusive)
	 * @return A list of lines within the specified range
	 * @throws IOException If an I/O error occurs
	 */
	private List<String> readFileRange(String fileName, int startLine, int endLine) throws IOException {
		List<String> lines = new LinkedList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			int lineNumber = 0;
			// Read through the file until the desired range is reached
			while ((line = br.readLine()) != null) {
				if (lineNumber >= startLine && lineNumber <= endLine) {
					lines.add(line); // Add the line to the list if within range
				}
				lineNumber++;
				if (lineNumber > endLine) {
					break; // Stop reading once we've passed the end line
				}
			}
		}
		return lines;
	}

	/**
	 * Cracks the passwords by comparing each hashed password with the cleartext passwords.
	 *
	 * @param hashedLines      A list of hashed passwords from the file
	 * @param cleartextPasswords A list of cleartext passwords to test against
	 * @throws NoSuchAlgorithmException If the hashing algorithm isn't available
	 */
	private void crackPasswords(List<String> hashedLines, List<String> cleartextPasswords) throws NoSuchAlgorithmException {
		// Loop through each line in the hashed passwords file
		for (String lineFile : hashedLines) {
			String[] splitLine = lineFile.split(" ");

			// Check if the line is properly formatted (user, salt, hashed password)
			if (splitLine.length != 3) {
				getContext().getLog().error("Malformed line in hashed passwords file: {}", lineFile);
				continue; // Skip this malformed line
			}

			String user = splitLine[0]; // Extract the username
			String salt = splitLine[1]; // Extract the salt used for hashing
			String hashedPassword = splitLine[2]; // Extract the hashed password

			// Try each cleartext password and compare its hash with the hashed password
			for (String pwd : cleartextPasswords) {
				String hashedPwd = hash("SHA-512", pwd + salt); // Hash the cleartext password with the salt
				if (hashedPwd.equals(hashedPassword)) { // If the hashes match, we found the password
					getContext().getLog().info("{}: Password found for user {}: {}", actorId, user, pwd);
				}
			}
		}
	}
}