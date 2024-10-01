/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 * Author: 		Tim Bütler
 * Disclaimer: 	Artificial Intelligence were in use for some code optimizations
 */
package ch.bfh.akka.crack.parallel;

import akka.actor.typed.ActorSystem;
import java.time.LocalDateTime;

/**
 * Main class for the Akka password cracker program.
 */
public class CrackPasswords {

	/**
	 * Entry point of the program. Start an actor system and send an initial message to the root actor.
	 *
	 * @param args arg[0] = numberOfActors, arg[1] = file name of hashed passwords, arg[2] = file name of cleartext passwords
	 */
	public static void main(String[] args) {
		/* If we were to run this from the terminal, we'd use this block for argument validation.
		 * It's commented out here because we're hardcoding some values below for convenience.
		 */
        /*
        if (args.length != 3) {
            System.err.println("Usage: program-name nbOfActors fileNameHashedPasswords fileNameCleartextPasswords");
            return;
        }

        int nbActors = Integer.parseInt(args[0]);  // Parse number of actors
        String hashedPasswords = args[1];          // Path to file containing hashed passwords
        String cleartextPasswords = args[2];       // Path to file containing cleartext passwords

        // Check if number of actors is valid (must be 1, 2, or multiple of 2)
        if (nbActors <= 0 || (nbActors > 1 && nbActors % 2 != 0)) {
            usage();
            return;
        }
        */

		// Hardcoded values for testing without terminal arguments
		LocalDateTime currentDateTime = LocalDateTime.now(); // Get current timestamp
		int nbActors = 5_000_000;                                   // Number of actors for parallelism
		String hashedPasswords = "hashed-passwords.txt";    // File containing hashed passwords
		String cleartextPasswords = "5-million-passwords.txt"; // File with potential cleartext passwords

		// Start the actor system for parallel password cracking
		ActorSystem<PasswordsRoot.Message> passwordsRoot = ActorSystem.create(PasswordsRoot.create(), "CrackPasswordsSystem");

		// Logging the initialization and start time
		System.out.println("--> Crack Passwords Actor System initialized");
		System.out.println("--> System started at: " + currentDateTime);

		// Sending an initial message to the root actor with the required data
		passwordsRoot.tell(new PasswordsRoot.InitMessage(nbActors, hashedPasswords, cleartextPasswords));
	}

	// Helper method to print usage instructions if the user inputs incorrect arguments
	private static void usage() {
		System.err.println("""
                Usage: program-name nbOfActors fileNameHashedPasswords fileNameCleartextPasswords
                    where nbOfActors must be 1, 2 or multiple of 2
                """);
	}
}