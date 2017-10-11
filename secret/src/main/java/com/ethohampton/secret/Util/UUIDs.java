package com.ethohampton.secret.Util;

import com.google.firebase.auth.FirebaseToken;

import java.util.UUID;

import javax.servlet.http.Cookie;

/**
 * Created by ethohampton on 9/6/17.
 * <p>
 * Utils for getting and setting UUID's
 */

public class UUIDs {
    public static final String TOKEN_NAME = "idToken";

    public static String getUUID(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_NAME)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static Cookie createUUIDCookie(String uuid) {
        Cookie cookie = new Cookie(TOKEN_NAME, uuid);
        cookie.setMaxAge(Constants.MAX_COOKIE_AGE * 20);//extend length of cookie by a little bit
        return cookie;
    }

    public static String createUUID() {
        return Utils.longHash(UUID.randomUUID().toString());
    }

    public static boolean isValid(FirebaseToken token) {
        //insure person has a uuid
        if (token.getUid() != null) {
            //insure they have verified their email
            if (token.isEmailVerified()) {
                return true;
            }
        }
        return false;
    }
}
