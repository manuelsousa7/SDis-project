package org.binas.security;

// provides helper methods to print byte[]

import java.security.SecureRandom;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

/**
 * Generate secure random numbers.
 */
public class SecureRandomNumber {

    public static void main(String[] args) throws Exception {

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        System.out.println("Generating random byte array ...");

        final byte array[] = new byte[32];
        random.nextBytes(array);

        System.out.print("Results: ");
        System.out.println(printHexBinary(array));
    }

}
