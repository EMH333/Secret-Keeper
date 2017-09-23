package com.ethohampton.secret;

import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Constants;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;

import java.util.List;

/**
 * Created by ethohampton on 12/15/16.
 * Connects to cloud data store
 */

public class Database {

    //saves question assumes key and other values have already been sent
    public static void putSecret(Secret secret) {
        OfyService.ofy().save().entity(secret);//.now().getString();
    }

    public static Secret getSecret(String id) {
        //System.out.println("Trying to get with id " + id);
        LoadResult<Secret> loadResult = OfyService.ofy().load().type(Secret.class).id(id);
        return loadResult.now();
    }

    public static Secret getSecret(com.googlecode.objectify.Key<Object> id) {
        //System.out.println("Trying to get with id " + id);
        LoadResult<Object> loadResult = OfyService.ofy().load().key(id);
        return (Secret) loadResult.now();
    }

    public static boolean objectExists(Key key) {
        return key != null && OfyService.ofy().load().filterKey(key).keys().first() != null;
    }


    //saves comment assuming key and other values have already been sent
    public static void putComment(Comment comment) {
        OfyService.ofy().save().entity(comment);//.now().getString();
    }

    //load a comment
    public static Comment getComment(String id) {
        LoadResult<Comment> loadResult = OfyService.ofy().load().type(Comment.class).id(id);
        return loadResult.now();
    }

    //returns a list of comments based on the id of the secret and an offset if the user has already loaded some comments
    public static List<Comment> getCommentsFromSecret(String secretID, int offset) {
        return OfyService.ofy().load().type(Comment.class).limit(Constants.MAX_COMMENTS_PER_LOAD).offset(offset).filter("referencedSecretID", secretID).list();// TODO: 8/13/17 Allow for sorting by popularity and upvotes as well as most recent
    }

}
