package org.jrivets.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

public final class SymmetricCipher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SymmetricCipher.class);
    
    private final Cipher encryptor;

    private final Cipher decryptor;

    public SymmetricCipher(String base64Key) throws Exception {
        this(Base64.decodeBase64(base64Key));
    }
    
    public SymmetricCipher(byte[] secretKey) throws Exception {
        this(secretKey, new byte[16]);
    }
    
    public SymmetricCipher(byte[] secretKey, byte[] ivKey) throws Exception {
        IvParameterSpec ivspec = new IvParameterSpec(ivKey);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
        encryptor = getCipher(Cipher.ENCRYPT_MODE, keySpec, ivspec);
        decryptor = getCipher(Cipher.DECRYPT_MODE, keySpec, ivspec);
    }
    
    public String encrypt(String data) {
        return Base64.encodeBase64String(encrypt(data.getBytes()));
    }
    
    public String decrypt(String data) {
        return new String(decrypt(Base64.decodeBase64(data)));
    }
    
    public byte[] encrypt(byte[] data) {
        try {
            return encryptor.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public byte[] decrypt(byte[] data) {
        try {
            return decryptor.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static SymmetricCipher getInstance(String secret) {
        try {
            return new SymmetricCipher(secret);
        } catch (Exception e) {
            LOGGER.error("Could not create new instance: ", e);
        }
        return null;
    }
    
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey aesKey = kgen.generateKey();
        return aesKey.getEncoded();
    }
    
    private Cipher getCipher(int cipherMode, SecretKeySpec keySpec, IvParameterSpec ivspec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(cipherMode, keySpec, ivspec);
        return cipher;
    }

}