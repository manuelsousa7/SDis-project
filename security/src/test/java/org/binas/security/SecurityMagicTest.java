package org.binas.security;

import org.binas.security.domain.SecurityMagic;
import org.binas.security.exception.SecurityMagicException;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

import java.security.Key;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static junit.framework.TestCase.*;

public class SecurityMagicTest {

    private SecurityMagic sm;

    @Before
    public void init(){
        try {
             Key kc = SecurityHelper.generateKeyFromPassword("ySudhFL");
             sm = new SecurityMagic("eu quero encriptar isto",kc);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void successMACCheck(){
        byte[] asd =  sm.getMAC();
        assertTrue(sm.checkMAC(asd));
    }

    @Test
    public void testEvilMACChange(){
        byte[] asd =  sm.getMAC();
        assertTrue(sm.checkMAC(asd));
        asd[0] = 12;
        assertFalse(sm.checkMAC(asd));
    }

    @Test
    public void testValidMAC(){
        assertEquals("50F840B31830D6071BE31FA6CE860F64E55D5AC0C02AFE61FA2B04EC6FDBB37D",printHexBinary(sm.getMAC()));
    }

    @Test
    public void testValidMAC64(){
        assertEquals("UPhAsxgw1gcb4x+mzoYPZOVdWsDAKv5h+isE7G/bs30=",sm.getMAC64());
    }

    @Test(expected = SecurityMagicException.class)
    public void testNullKey(){
        SecurityMagic sm = new SecurityMagic("encriptar isto",null);
    }

    @Test(expected = SecurityMagicException.class)
    public void testNullPlainText(){
        try {
            Key kc = SecurityHelper.generateKeyFromPassword("ySudhFL");
            sm = new SecurityMagic(null,kc);
            fail();
        } catch (SecurityMagicException sme){
            throw sme;
        } catch (Exception e){
            fail();
        }
    }
}
