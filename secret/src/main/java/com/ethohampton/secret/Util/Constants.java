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

    public static final boolean FILTER_WORDS = true;//setting this to false disables the word filter

    public static final int MAX_SECRET_LENGTH = 2000;//characters max for a secret
    public static final int MIN_SECRET_LENGTH = 6;//characters min for a secret

    public static final int MAX_COOKIE_AGE = 60 * 60 * 24 * 7;//7 days till you have to enter another secret or login and give a secret
    public static final int MAX_SEEN_SECRETS_SIZE = 75;//max seen secrets before cookie gets too big

    public static final String BAD_WORDS_FILENAME = "WEB-INF/badwords.cvs";
    public static final String BAD_HASHES_FILENAME = "WEB-INF/banned_hashes.txt";

    public static final int MAX_COMMENTS_PER_LOAD = 25;


    public static final int CHANCE_TO_SHOW_DOWNVOTED_COMMENT = 8;//One in 8 chance that a negatively downvoted comment will be shown
    public static final int CONSIDERED_DOWNVOTED = -2;//a comment is considered to be downvoted when it has a score of -2

    public static final int MAX_RANDOM_LOOPS = 4;//Max times we try and find a secret that the person has not seen. Lowers costs
    /*
    public static long generateUUID(){
        Random r = new SecureRandom();
        return r.nextLong();
    }*/
}
