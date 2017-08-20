package com.ethohampton.secret.Util;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by ethohampton on 8/18/17.
 * <p>
 * Tests profanity filter
 */
public class FilterTest {
    @Before
    public void setUp() throws Exception {
        Filter.loadConfigs();
    }

    @Test
    public void badWordsFound() throws Exception {
        assertFalse(Filter.badWordsFound("shit").isEmpty());
        assertFalse(Filter.badWordsFound("ethan hampton").isEmpty());
    }

    @Test
    public void passesAllFilters() throws Exception {
        assertFalse(Filter.passesAllFilters("123452"));
        assertFalse(Filter.passesAllFilters("abcd"));
        assertFalse(Filter.passesAllFilters("www.google.com"));
        assertFalse(Filter.passesAllFilters("google.com"));
        assertFalse(Filter.passesAllFilters("https://e.co"));


        assertTrue(Filter.passesAllFilters("this is a really quick test"));
    }

}