package com.ethohampton.secret.Util;

import com.ethohampton.secret.Objects.Secret;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;

import javax.servlet.http.Cookie;

/**
 * Created by ethohampton on 6/5/17.
 * <p>
 * Utility methods
 */

public class Utils {
    public static String hash(String toHash) {
        toHash = toHash.trim();
        toHash = toHash.toLowerCase();
        toHash = toHash.replaceAll("\\s+", "");

        HashFunction hf = Hashing.murmur3_32();
        HashCode hc = hf.newHasher().putString(toHash, Charsets.UTF_8).hash();
        return hc.toString();
    }

    /**
     * @param s  the secret
     * @param id the id to show the user, should be easy to find in database
     * @return JSON representation of the secret object
     */
    public static String secretToJSON(Secret s, String id) {
        JsonObject o = new JsonObject();
        o.addProperty("secret", s.getSecret());
        o.addProperty("id", id);
        o.addProperty("votes", s.getUpvotes() - s.getDownvotes());//NOTE: This is a purposeful decision to block user from seeing downvotes
        return o.toString();
    }


    /**
     * @param cookies cookies from the request
     * @return if the user has given a secret in a valid time span that is signed by the server
     */
    public static boolean correctCookie(Cookie[] cookies) {
        boolean needToShareFirst = true;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("addedSecret")) {
                    String[] parts = cookie.getValue().split(Constants.SEPARATOR);//splits cookie into parts
                    if (parts.length == 2) {
                        if (Long.valueOf(parts[0]) > System.currentTimeMillis()) {//confirms cookie is not expired
                            if (VerifyStrings.verify(parts[0], parts[1])) {//verifys time stamp and signature
                                needToShareFirst = false;
                            }
                        }
                    }
                }
            }
        }
        return needToShareFirst;
    }
}
