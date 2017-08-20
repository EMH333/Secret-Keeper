package com.ethohampton.secret;

import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by ethohampton on 12/14/16.
 * objectify service
 */

public class OfyService {
    static {
        factory().register(Secret.class);
        factory().register(Comment.class);
    }
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
