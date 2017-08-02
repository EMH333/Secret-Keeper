package com.ethohampton.secret.Util;

import com.google.appengine.repackaged.com.google.api.client.util.Base64;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * Created by ethohampton on 8/1/17.
 * <p>
 * created to verify strings that come from server
 */

public class VerifyStrings {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static boolean loadedKeys = false;

    public static void loadKeys() {
        try {
            privateKey = LoadKeys.getPrivateKey("WEB_INF/keys/private_key.der");
            publicKey = LoadKeys.getPublicKey("WEB_INF/keys/public_key.der");
            loadedKeys = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isKeysLoaded() {
        return loadedKeys;
    }

    /**
     * @param message the message to sign
     * @return the signature of the message
     */
    public static String sign(String message) {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(privateKey);
            sign.update(message.getBytes("UTF-8"));
            return new String(Base64.encodeBase64(sign.sign()), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * @param message   message to be validated
     * @param signature signature of message
     * @return if signature and message are valid
     */
    public static boolean verify(String message, String signature) {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes("UTF-8"));
            return sign.verify(Base64.decodeBase64(signature.getBytes("UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
