package com.ethohampton.secret.Util;

import java.util.UUID;

import javax.servlet.http.Cookie;

/**
 * Created by ethohampton on 9/6/17.
 * <p>
 * Utils for getting and setting UUID's
 */

public class UUIDs {
    public static String getUUID(Cookie[] cookies) {
        if (cookies.length >= 1) {
            for (Cookie c :
                    cookies) {
                if (c.getName().equals("uuid")) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static Cookie createUUIDCookie(String uuid) {
        Cookie cookie = new Cookie("uuid", uuid);
        cookie.setMaxAge(Constants.MAX_COOKIE_AGE * 20);//extend length of cookie by a little bit
        return cookie;
    }

    public static String createUUID() {
        return Utils.longHash(UUID.randomUUID().toString());
    }
}
