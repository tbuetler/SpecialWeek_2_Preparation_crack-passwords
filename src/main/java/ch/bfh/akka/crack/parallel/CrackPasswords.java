/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 */
package ch.bfh.akka.crack.parallel;

/**
 * Main class for the Akka password cracker program.
 */
public class CrackPasswords {

    /**
     * Entry point of the program. Start an actor system and send an initial message to the root actor.
     *
     * @param args arg[0] = numberOfActors, arg[1] = file name of hashed passwords and arg[2] = file name of cleartext passwords
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: program-name nbOfActors fileNameHashedPasswords fileNameCleartextPasswords");
            return;
        }

        int nbActors = Integer.parseInt(args[0]);
        // sanity check nbOfActors (must be 1, 2 or multiple of 2
        if (nbActors <= 0) {
            usage();
            return;
        } else if (nbActors > 1 && !(nbActors % 2 == 0)) {
            usage();
            return;
        }

        /* TODO Create an Akka actor system. */

        /* TODO Create the init message containing all relevant information. */

        /* TODO Sent the init message to the root actor. */

        // Now terminate main thread
    }

    private static void usage() {
        System.err.println("""
        Usage: program-name nbOfActors fileNameHashedPasswords fileNameCleartextPasswords
            where nbOfActors must be 1, 2 or multiple of 2
        """);
    }
}
