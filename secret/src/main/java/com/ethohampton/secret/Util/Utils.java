package com.ethohampton.secret.Util;

import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import javax.servlet.http.Cookie;

/**
 * Created by ethohampton on 6/5/17.
 * <p>
 * Utility methods
 */

public class Utils {

    /**
     * @param toHash the string to hash
     * @return the Murmur 32 hash of the input string
     */
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
    public static String secretToJSON(Secret s, String id, List<Comment> comments) {
        JsonObject o = new JsonObject();
        o.addProperty("secret", s.getSecret());
        o.addProperty("id", id);
        o.addProperty("votes", s.getUpvotes() - s.getDownvotes());//NOTE: This is a purposeful decision to block user from seeing downvotes
        if (comments != null) {//add comments if there are any
            o.add("comments", commentListToJSON(comments));//add comments to system
        }
        return o.toString();
    }

    public static String commentToJSON(Comment temp) {
        JsonObject c = new JsonObject();
        c.addProperty("comment", temp.getComment());
        c.addProperty("id", temp.getId());
        c.addProperty("votes", temp.getUpvotes() - temp.getDownvotes());
        return c.toString();
    }

    public static JsonArray commentListToJSON(List<Comment> comments) {
        JsonArray Jsoncomments = new JsonArray(comments.size());
        for (Comment temp :
                comments) {
            Jsoncomments.add(commentToJSON(temp));
        }
        return Jsoncomments;
    }



    /**
     * @param cookies cookies from the request
     * @return if the user has given a secret
     */
    public static boolean correctCookie(Cookie[] cookies) {
        boolean needToShare = true;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("addedSecret")) {
                    if (cookie.getValue().equals("1")) {
                        needToShare = false;
                    }
                }
            }
        }
        return needToShare;
    }
}
