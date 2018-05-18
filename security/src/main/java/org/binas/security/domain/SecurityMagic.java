package org.binas.security.domain;

import org.binas.security.exception.SecurityMagicException;

import javax.crypto.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;


/*
 *  The purpouse of this class is to simplify the calculation of
 *  a hash-based message authentication code to be used for integrity verification
 *  in other modules of this project.
 */
public class SecurityMagic {
    /*
     *  We are going to use HMAC with SHA-256 Cryptographic Hash Algorithm,
     *  because its probabiliy one of the most secure algorithms out there (2018)
     */
    private static final String MAC_ALGO = "HmacSHA256";
    private Key sessionKey;
    private String plainText;


    /*
     *  The constructor of this class receives a Key that we will use to
     *  to generate the HMAC. The plainText is the data that we want to protect
     *
     */
    public SecurityMagic(String plainText, Key sessionKey){
        if(plainText == null || sessionKey == null){
            throw new SecurityMagicException();
        }
        this.sessionKey = sessionKey;
        this.plainText = plainText;
    }

    public static String getMacAlgo() {
        return MAC_ALGO;
    }

    public Key getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(Key sessionKey) {
        if(sessionKey == null){
            throw new SecurityMagicException();
        }
        this.sessionKey = sessionKey;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        if(plainText == null){
            throw new SecurityMagicException();
        }
        this.plainText = plainText;
    }

    /* Returns MAC in byte array */
    public byte[] getMAC(){
        try{
           return makeMAC(this.getPlainText().getBytes(),this.sessionKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new SecurityMagicException();
    }

    /* Returns MAC in BASE 64 */
    public String getMAC64(){
        try{
            return Base64.getEncoder().encodeToString(makeMAC(this.getPlainText().getBytes(),this.sessionKey));
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new SecurityMagicException();
    }

    /* Receives another HMAC in bytes as argument, and checks if its the same as Class MAC */
    public boolean checkMAC(byte[] cipherDigest){
        try{
            return verifyMAC(cipherDigest,this.getPlainText().getBytes(),this.sessionKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new SecurityMagicException();
    }


    /* Generates HMAC */
    private static byte[] makeMAC(byte[] bytes, Key key) throws Exception {
        Mac cipher = Mac.getInstance(MAC_ALGO);
        cipher.init(key);
        byte[] cipherDigest = cipher.doFinal(bytes);

        return cipherDigest;
    }

    /* Verifies HMAC */
    private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, Key key) throws Exception {
        Mac cipher = Mac.getInstance(MAC_ALGO);
        cipher.init(key);
        byte[] cipheredBytes = cipher.doFinal(bytes);
        return Arrays.equals(cipherDigest, cipheredBytes);
    }

}
