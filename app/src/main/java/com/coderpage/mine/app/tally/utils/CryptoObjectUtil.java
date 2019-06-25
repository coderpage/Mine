package com.coderpage.mine.app.tally.utils;

import android.annotation.TargetApi;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * @author lc. 2019-06-25 16:00
 * @since 0.7.0
 */
@TargetApi(23)
public class CryptoObjectUtil {

    private static final String KEY_NAME = "com.coderpage.mine.fingerprint_authentication_key";

    private static final String KEYSTORE_NAME = "AndroidKeyStore";

    private static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KEY_ALGORITHM + "/" + BLOCK_MODE + "/" + ENCRYPTION_PADDING;

    private KeyStore mKeyStore;

    public CryptoObjectUtil() {
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mKeyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public FingerprintManagerCompat.CryptoObject buildCryptoObject() {
        try {
            Cipher cipher = createCipher(true);
            return new FingerprintManagerCompat.CryptoObject(cipher);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher createCipher(boolean retry) throws Exception {
        Key key = getKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            mKeyStore.deleteEntry(KEY_NAME);
            if (retry) {
                createCipher(false);
            } else {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        return cipher;
    }

    private Key getKey() throws Exception {
        if (!mKeyStore.isKeyEntry(KEY_NAME)) {
            createKey();
        }
        return mKeyStore.getKey(KEY_NAME, null);
    }

    private void createKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}
