package org.binas.security;

import org.binas.security.domain.SecurityMagic;
import org.junit.Before;
import org.junit.Test;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;

import java.security.Key;

public class SecurityMagicTest {

    private SecurityMagic sm;

    @Before
    public void init(){
        try {
            Key kc = SecurityHelper.generateKeyFromPassword("ySudhFL");
             sm = new SecurityMagic("ola",kc);
        } catch (Exception e){

        }
    }

    @Test
    public void testXOR(){
        byte[] asd =  sm.getMAC();
       System.out.print( asd);
        System.out.print(sm.checkMAC(asd));
        asd[0] = 12;
        System.out.print(sm.checkMAC(asd));
    }
}
