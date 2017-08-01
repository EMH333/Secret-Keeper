package com.ethohampton.secret;

import com.ethohampton.secret.Objects.Secret;
import com.googlecode.objectify.LoadResult;

/**
 * Created by ethohampton on 12/15/16.
 * Connects to cloud data store
 */

public class Database {

    //saves question assumes key and other values have already been sent
    public static void put(Secret secret) {
        OfyService.ofy().save().entity(secret);//.now().getString();
//        System.out.println(get(id).getSecret());
    }

    public static Secret get(String id) {
        //System.out.println("Trying to get with id " + id);
        LoadResult<Secret> loadResult = OfyService.ofy().load().type(Secret.class).id(id);
        return loadResult.now();
    }

    public static Secret get(com.googlecode.objectify.Key<Object> id) {
        //System.out.println("Trying to get with id " + id);
        LoadResult<Object> loadResult = OfyService.ofy().load().key(id);
        return (Secret) loadResult.now();
    }

    public static boolean objectExists(com.googlecode.objectify.Key<Object> id) {
        return OfyService.ofy().load().filterKey(id).keys().first() != null;
    }


}
