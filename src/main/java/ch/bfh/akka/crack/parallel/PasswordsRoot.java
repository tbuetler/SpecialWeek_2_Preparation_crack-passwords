/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 * Author: 		Tim Bütler
 * Disclaimer: 	Artificial Intelligence were in use for some code optimizations
 */
package ch.bfh.akka.crack.parallel;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Root actor for the Akka password cracking system. It initializes and manages worker actors.
 */
public class PasswordsRoot extends AbstractOnMessageBehavior<PasswordsRoot.Message> {

	private int nbActorsCreated = 0; // Tracks the number of actors created
	private int nbActorsFinished = 0; // Tracks how many actors have finished their work
	private long startTime; // To track execution time
	private ActorRef<PasswordsActor.Message> passwordsActor; // Reference to worker actors

	/**
	 * Interface representing the messages exchanged with the root actor.
	 */
	public interface Message {}

	// Message to initialize the password cracking with number of actors and file paths
	public record InitMessage(int nbActors, String hashedPasswords, String cleartextPasswords) implements Message {}

	// Message sent by actors when they finish cracking
	public record FinishedMessage() implements Message {}

	// Create the root actor behavior
	public static Behavior<Message> create() {
		return Behaviors.setup(context -> new PasswordsRoot(context));
	}

	private PasswordsRoot(ActorContext<Message> context) {
		super(context);
	}

	@Override
	public Behavior<Message> onMessage(Message message) {
		return switch (message) {
			case InitMessage im -> {
				createPasswordsActors(im); // Start worker actors
				this.startTime = System.nanoTime(); // Record start time
				yield Behaviors.same();
			}

			case FinishedMessage fm -> {
				nbActorsFinished++; // Increment the number of finished actors
				getContext().getLog().info("--> Actor finished: {}/{}", nbActorsFinished, nbActorsCreated);

				// Check if all actors have completed their task
				if (nbActorsFinished == nbActorsCreated) {
					long durationNanos = System.nanoTime() - startTime; // Calculate total execution time
					long durationMillis = durationNanos / 1_000_000;
					long seconds = (durationMillis / 1_000) % 60;
					long minutes = (durationMillis / (1_000 * 60)) % 60;
					long hours = (durationMillis / (1_000 * 60 * 60)) % 24;
					String formattedDuration = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, durationMillis % 1_000);
					getContext().getLog().info("--> All actors have finished. Total duration: {}", formattedDuration);
					sendFinishedMessage();
				}
				yield Behaviors.same();
			}
			default -> Behaviors.unhandled();
		};
	}

	// Helper method to create worker actors for password cracking
	private Behavior<PasswordsRoot.Message> createPasswordsActors(InitMessage message) {
		this.getContext().getLog().info("-->> handling InitMessage");

		try {
			// Load the list of cleartext passwords
			List<String> clearTextPasswords = readFile(message.cleartextPasswords());
			int nbActors = message.nbActors;
			int chunckSize = clearTextPasswords.size() / nbActors; // Divide the work evenly among actors

			// Create the worker actors
			for (int i = 0; i < nbActors; i++) {
				int startLine = i * chunckSize;
				int endLine = (i == nbActors - 1) ? clearTextPasswords.size() - 1 : (i + 1) * chunckSize - 1;

				ActorRef<PasswordsActor.Message> passwordsActor = getContext().spawn(
						PasswordsActor.create("PasswordsActor" + i, getContext().getSelf()),
						"PasswordsActor" + i
				);

				// Send the setup message to the actor
				passwordsActor.tell(new PasswordsActor.SetupMessage(
						getContext().getSelf(),
						message.hashedPasswords(),
						message.cleartextPasswords(),
						startLine,
						endLine
				));
				nbActorsCreated++; // Track created actors
			}
		} catch (Exception e) {
			getContext().getLog().error("Error creating passwords actors", e);
			return Behaviors.stopped();
		}

		return Behaviors.same();
	}

	// Send a finished message to shut down the system
	public void sendFinishedMessage() {
		this.getContext().getLog().info("--> All actors have finished. Sending FinishedMessage to CrackPasswords...");
		getContext().getSystem().terminate(); // Terminate the system
		System.out.println("--> Crack Passwords Actor System terminated.\n");
	}

	// Helper method to read file contents into a list of strings
	private List<String> readFile(String fileName) throws IOException {
		List<String> lines = new LinkedList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine(); // Read the next line
			}
		}
		return lines;
	}
}