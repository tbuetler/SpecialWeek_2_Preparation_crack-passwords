/*
 * BTI5205 Special Week 2, Berner Fachhochschule, Switzerland
 */
package ch.bfh.akka.crack.sequential;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for a pure sequential solution for cracking passwords.
 */
public class CrackPasswords {

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
     * Entry point of the program. Start a sequential Java program for cracking passwords.
     *
     * @param args arg[0] = file name of hashed passwords and arg[1] = file name of cleartext passwords
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        System.out.println("Reads a file containing hash of passwords");

        if (args.length != 2) {
            System.err.println("Program requires two file names as argument (hashed passwords and possible passwords)");
            return;
        }

        // We read the first file containing the hashed passwords
        List<String> lines = new LinkedList<>();
        String fileName = args[0];
        System.out.println("Reading file: " + fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();
            while (line != null) {
                //System.out.println("line = "+line);
                lines.addLast(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e1) {
            System.err.println("File " + fileName + " not found");
            return;
        } catch (IOException e2) {
            System.err.println("Problem reading the file " + fileName);
            return;
        }

        // We read the second file containing the clear text passwords
        List<String> passwords = new LinkedList<>();
        String fileNamePasswords = args[1];
        System.out.println("Reading file: " + fileNamePasswords);

        try (BufferedReader br = new BufferedReader(new FileReader(fileNamePasswords))) {
            String pwd1 = br.readLine();
            while (pwd1 != null) {
                passwords.addLast(pwd1);
                pwd1 = br.readLine();
            }
        } catch (FileNotFoundException e1) {
            System.err.println("File " + fileNamePasswords + " not found");
            return;
        } catch (IOException e2) {
            System.err.println("Problem reading the file " + fileNamePasswords);
            return;
        }

        // For each user and for each password, we check if the
        // password corresponds to the user :
        // hash(password + salt) is equal to the hashed password.
        for (String lineFile : lines) {
            String[] splitLine = lineFile.split(" ");
            if (splitLine.length != 3) {
                System.err.println("Malformed file" + fileName);
            }
            String user = splitLine[0];
            String hashedPassword = splitLine[2];
            String salt = splitLine[1];

            for (String pwd : passwords) {
                try {
                    String hashedPwd = hash("SHA-512", pwd + salt);
                    if (hashedPwd.equals(hashedPassword)) {
                        System.out.println(user + " " + pwd);
                    }
                } catch (NoSuchAlgorithmException e) {
                    System.err.println("Algorithm SHA 512 not available");
                    return;
                }
            }
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        long seconds = timeElapsed / 1000;
        long millis = timeElapsed % 1000;
        System.err.println("Process duration = " + seconds + "." + millis + " secs");
    }

}
