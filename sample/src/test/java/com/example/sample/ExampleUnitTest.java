package com.example.sample;

import org.junit.Test;
import org.libsodium.jni.keys.KeyPair;
import org.libsodium.jni.keys.SigningKey;

import house.heka.secure.util.HekaSalt;

import static org.junit.Assert.*;
import static org.libsodium.jni.NaCl.sodium;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testGetSharedSecret() throws Exception {
        HekaSalt nacl = new HekaSalt();
        KeyPair mykeys = new KeyPair();
        SigningKey mysig = new SigningKey();
        nacl.generate(mykeys, mysig);

        KeyPair yourkeys = new KeyPair();
        SigningKey yoursig = new SigningKey();
        nacl.generate(mykeys, yoursig);

        byte[] mysecret = mykeys.getPrivateKey().toBytes();
        byte[] yourpublic = yourkeys.getPublicKey().toBytes();

        byte[] my_shared_secret = new byte[mysecret.length];

        sodium().crypto_scalarmult(my_shared_secret, mysecret, yourpublic);
        assertTrue(my_shared_secret.length > 0);

        byte[] mypublic = mykeys.getPublicKey().toBytes();
        byte[] yoursecret = yourkeys.getPrivateKey().toBytes();

        byte[] your_shared_secret = new byte[yoursecret.length];

        sodium().crypto_scalarmult(your_shared_secret, mysecret, yourpublic);
        assertTrue(your_shared_secret.length > 0);


    }
}