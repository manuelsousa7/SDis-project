package org.binas.security.domain;

import org.binas.security.exception.SecurityMagicException;

import javax.crypto.*;
import java.security.*;
import java.util.Arrays;


public class SecurityMagic {
    private static final String MAC_ALGO = "HmacSHA256";

    private Key sessionKey;
    private String plainText;

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

    public byte[] getMAC(){
        try{
           return makeMAC(this.getPlainText().getBytes(),this.sessionKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new SecurityMagicException();
    }

    public boolean checkMAC(byte[] cipherDigest){
        try{
            return verifyMAC(cipherDigest,this.getPlainText().getBytes(),this.sessionKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new SecurityMagicException();
    }


    private static byte[] makeMAC(byte[] bytes, Key key) throws Exception {
        Mac cipher = Mac.getInstance(MAC_ALGO);
        cipher.init(key);
        byte[] cipherDigest = cipher.doFinal(bytes);

        return cipherDigest;
    }

    private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, Key key) throws Exception {
        Mac cipher = Mac.getInstance(MAC_ALGO);
        cipher.init(key);
        byte[] cipheredBytes = cipher.doFinal(bytes);
        return Arrays.equals(cipherDigest, cipheredBytes);
    }

}
