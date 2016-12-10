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
}