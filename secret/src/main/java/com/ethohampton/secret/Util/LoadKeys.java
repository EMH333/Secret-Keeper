package com.ethohampton.secret.Util;


import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by ethohampton on 8/1/17.
 * <p>
 * Loads RSA public and private keys
 */

public class LoadKeys {


    // TODO: 8/1/17 Properly load keys!!!
    // STOPSHIP: 8/1/17 See above
    public static PrivateKey getPrivateKey(String filename)
            throws Exception {

        byte[] keyBytes = IOUtils.toByteArray(new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)))), "UTF-8");

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublicKey(String filename)
            throws Exception {

        byte[] keyBytes = IOUtils.toByteArray(new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)))), "UTF-8");

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
