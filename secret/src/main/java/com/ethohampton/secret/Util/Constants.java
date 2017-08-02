package com.ethohampton.secret.Util;

/**
 * Created by ethohampton on 12/16/16.
 * Constants for use
 */

public class Constants {
    public static final String SEPARATOR = "|";
    public static final String ESCAPED_SEPARATOR = "\\|";//note this is escaped
    public static final boolean USE_MEMCACHE_FOR_RANDOM = true;
    public static final Long UPDATE_TIME = 30 * 1000L;//30 seconds in milliseconds wait between updates
    public static final int UPDATE_COUNT_LIMIT = 500;//limit to 500 records so we don't overload on memory

    public static final boolean FILTER_WORDS = true;

    public static final int MAX_SECRET_LENGTH = 2000;//characters max for a secret
    public static final int MIN_SECRET_LENGTH = 6;//characters min for a secret

    public static final int MAX_COOKIE_AGE = 60 * 60 * 24 * 4;//4 days till you have to enter another secret
    /*
    public static long generateUUID(){
        Random r = new SecureRandom();
        return r.nextLong();
    }*/
}
