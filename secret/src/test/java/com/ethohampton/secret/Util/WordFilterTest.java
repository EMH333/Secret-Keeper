package com.ethohampton.secret.Util;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by ethohampton on 7/5/17.
 * Testing for word filter
 */
public class WordFilterTest {
    @Before
    public void setUp() throws Exception {
        WordFilter.loadConfigs("secret/src/main/webapp/WEB-INF/badwords.cvs");
    }

    @Test
    public void badWordsFound() throws Exception {
        assertFalse(WordFilter.badWordsFound("shit").isEmpty());
        assertFalse(WordFilter.badWordsFound("ethan hampton").isEmpty());
    }

    @Test
    public void passesAllFilters() throws Exception {
        assertFalse(WordFilter.passesAllFilters("123452"));
        assertFalse(WordFilter.passesAllFilters("abcd"));
        assertFalse(WordFilter.passesAllFilters("www.google.com"));
        assertFalse(WordFilter.passesAllFilters("google.com"));
        assertFalse(WordFilter.passesAllFilters("https://e.co"));


        assertTrue(WordFilter.passesAllFilters("this is a really quick test"));
    }

}