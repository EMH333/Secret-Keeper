package com.ethohampton.secret.Util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by ethohampton on 8/1/17.
 * <p>
 * created to verify strings that come from server
 */

public class VerifyStrings {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    static {
        try {
            privateKey = LoadKeys.getPrivateKey("WEB_INF/keys/private_key.der");
            publicKey = LoadKeys.getPublicKey("WEB_INF/keys/public_key.der");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message the message to sign
     * @return the signature of the message
     * @throws SignatureException if it can not encode
     */
    public static String sign(String message) throws SignatureException {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(privateKey);
            sign.update(message.getBytes("UTF-8"));
            return new BASE64Encoder().encode(sign.sign());
        } catch (Exception ex) {
            throw new SignatureException(ex);
        }
    }

    /**
     * @param message   message to be validated
     * @param signature signature of message
     * @return if signature and message are valid
     * @throws SignatureException if it can not decode
     */
    public static boolean verify(String message, String signature) throws SignatureException {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(publicKey);
            sign.update(message.getBytes("UTF-8"));
            return sign.verify(new BASE64Decoder().decodeBuffer(signature));
        } catch (Exception ex) {
            throw new SignatureException(ex);
        }
    }
}
