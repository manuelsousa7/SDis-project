This is a Java project that contains Java Cryptography API tests and
utility classes.

Tests:
- AsymCrypto generates a key pair and uses the public key to cipher and
the private key to decipher data (and then the other way around).

- SymCrypto generates a key and uses it to cipher and decipher data.
 
- Digest creates a cryptographic hash.

- Digital Signature shows data signing and verification with asymmetric keys.

- Message Authentication Code (MAC) shows data integrity verification with symmetric keys.

- XMLCrypto shows how to insert and retrieve cipher text in XML documents 
using base 64 encoding to represent bytes as text.

Utilities:
- The SymKey and AsymKeys examples show how to read and write cryptographic keys 
to and from files.

- SecureRandomNumber generates random numbers that are unpredictable 
(contrary to pseudo-random number generators).
The numbers are printed as hexadecimal values.

- ListAlgorithms presents the (long) list of available security providers and 
the cryptographic algorithms that they implement.


Instructions using Maven:
------------------------

To compile and execute all tests:
    mvn test

To execute a specific test suite:
    mvn test -Dtest=AsymCrypto*

To execute a specific test:
    mvn test -Dtest=AsymCrypto*#testCipherPrivate*


To run the default example using exec plug-in:
    mvn compile exec:java

To list available profiles (one for each example):
    mvn help:all-profiles

To run a specific example, select the profile with -P:
    mvn exec:java -P list-algos


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2017-03-20
leic-sod@disciplinas.tecnico.ulisboa.pt
