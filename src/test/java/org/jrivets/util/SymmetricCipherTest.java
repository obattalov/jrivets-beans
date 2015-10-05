package org.jrivets.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SymmetricCipherTest {
    
    byte[] key;
    
    SymmetricCipher sc;
    
    @BeforeMethod
    public void init() throws Exception {
        key = SymmetricCipher.generateKey();
        System.out.println("Key=" + Arrays.toString(key));
        sc = new SymmetricCipher(key);
    }
    
    @Test
    public void encryptDecryptTest() throws Exception {
        standardTest();
        randomTest();
    }
    
    @Test
    public void reorderTest() throws Exception {
        Random rnd = new Random();
        byte[] bb = new byte[2048];
        rnd.nextBytes(bb);
        byte[] ebb = sc.encrypt(bb);             
        standardTest();
        byte[] dbb = sc.decrypt(ebb);
        assertTrue(Arrays.equals(dbb, bb));
    }
    
    @Test
    public void reInitTest() throws Exception {
        Random rnd = new Random();
        byte[] bb = new byte[2048];
        rnd.nextBytes(bb);
        byte[] ebb = sc.encrypt(bb);             
        sc = new SymmetricCipher(key);
        standardTest();
        byte[] dbb = sc.decrypt(ebb);
        assertTrue(Arrays.equals(dbb, bb));
    }
    
    @Test
    public void textTest() throws Exception {
        String key = Base64.encodeBase64String(SymmetricCipher.generateKey());
        sc = new SymmetricCipher(key);
        for (int i = 1; i < 1024; i++) {
            String text = RandomStringUtils.random(i);
            String encText = sc.encrypt(text);
            String decText = sc.decrypt(encText);
            assertEquals(text, decText);
        }
    }
    
    private void standardTest() throws IllegalBlockSizeException, BadPaddingException {
        Random rnd = new Random();
        for (int i = 1; i < 1024; i++) {
            byte[] bb = new byte[i];
            rnd.nextBytes(bb);
            byte[] ebb = sc.encrypt(bb);
            byte[] dbb = sc.decrypt(ebb);
            assertTrue(Arrays.equals(dbb, bb));
        }
    }
    
    private void randomTest() throws Exception {
        Random rnd = new Random();
        ArrayList<byte[]> bufs = new ArrayList<byte[]>();
        ArrayList<byte[]> ebufs = new ArrayList<byte[]>();
        for (int i = 100; i < 300; i++) {
            byte[] bb = new byte[i];
            rnd.nextBytes(bb);
            byte[] ebb = sc.encrypt(bb);
            bufs.add(bb);
            ebufs.add(ebb);
        }
        
        sc = new SymmetricCipher(key);
        for (int i = 0; i < 1000; i++) {
            int idx = rnd.nextInt(bufs.size());
            byte[] bb = bufs.get(idx);
            byte[] dbb = sc.decrypt(ebufs.get(idx));
            assertTrue(Arrays.equals(dbb, bb));
        }
    }
    
}
