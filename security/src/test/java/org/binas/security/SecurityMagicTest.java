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
//            CipheredView cipheredSessionKey = new CipheredView();
//            cipheredSessionKey.setData("asdassa".getBytes());
//            Key kc = SecurityHelper.generateKeyFromPassword("ySudhFL");
//            SessionKey asd = new SessionKey(cipheredSessionKey,kc);
//             sm = new SecurityMagic("ola",asd);
        } catch (Exception e){

        }
    }

    @Test
    public void testXOR(){
//        System.out.println(sm.getMAC());
    }
}
