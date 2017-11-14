package com.ethohampton.secret;

import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.googlecode.objectify.Key;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by ethohampton on 8/18/17.
 * <p>
 * Database test
 */
public class DatabaseTest {
    private String secretID;
    private String commentID;
    private Secret secret = new Secret(System.currentTimeMillis(), "DatabaseTest", "testUser");
    private Comment comment = new Comment(System.currentTimeMillis(), "Database Comment", secret.getId(), "testUser");

    @Before
    public void setUp() throws Exception {
        Database.putSecret(secret);
        secretID = secret.getId();


        Database.putComment(comment);
        commentID = comment.getId();
    }

    @Test
    public void getSecret() throws Exception {
        assertEquals(Database.getSecret(secretID), secret);
    }

    @Test
    public void objectExists() throws Exception {
        assertTrue(Database.objectExists(Key.create(Secret.class, secretID)));
    }

    @Test
    public void getComment() throws Exception {
        assertEquals(Database.getComment(commentID), comment);
    }

    @Test
    public void getCommentsFromSecret() throws Exception {
        assertTrue(Database.getCommentsFromSecret(secretID, 0).contains(comment));
    }

}