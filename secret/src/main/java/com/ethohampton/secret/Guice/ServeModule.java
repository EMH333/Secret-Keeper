package com.ethohampton.secret.Guice;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Servlets.AddComment;
import com.ethohampton.secret.Servlets.AddSecret;
import com.ethohampton.secret.Servlets.GetComment;
import com.ethohampton.secret.Servlets.GetSecret;
import com.ethohampton.secret.Servlets.RandomSecret;
import com.ethohampton.secret.Servlets.VoteSecret;
import com.ethohampton.secret.Util.UUIDs;
import com.ethohampton.secret.Util.Utils;
import com.google.common.base.Preconditions;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cache.AsyncCacheFilter;

import java.util.logging.Logger;


public class ServeModule extends ServletModule {
    private static final Logger LOG = Logger.getLogger(ServeModule.class.getName());

    private final String userBaseUrl;

    public ServeModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    /*
    private static Map<String, String> map(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0, "You have to have an even number of map params");
        Map<String, String> map = Maps.newHashMap();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }
    */

    @Override
    protected void configureServlets() {
        filter("/*").through(AsyncCacheFilter.class);

        bind(ObjectifyFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(ObjectifyFilter.class);

        //filter("/*").through(AppstatsFilter.class, map("calculateRpcCosts", "true"));

        serve(userBaseUrl + "/add").with(AddSecret.class);
        serve(userBaseUrl + "/get").with(GetSecret.class);
        serve(userBaseUrl + "/random").with(RandomSecret.class);
        serve(userBaseUrl + "/vote").with(VoteSecret.class);

        serve(userBaseUrl + "/addcomment").with(AddComment.class);
        serve(userBaseUrl + "/getcomment").with(GetComment.class);

        if (ServeLogic.isDevelopmentServer()) {
            //do things here only in development
            LOG.info("This is a dev server");

            //loads 4 test secrets to save dev time
            ObjectifyService.run(new VoidWork() {
                public void vrun() {
                    Database.putSecret(new Secret(System.currentTimeMillis(), "This is a test", UUIDs.createUUID()));
                    Database.putSecret(new Secret(System.currentTimeMillis(), "This is a test 1", UUIDs.createUUID()));
                    Database.putSecret(new Secret(System.currentTimeMillis(), "This is a test 2", UUIDs.createUUID()));
                    Database.putSecret(new Secret(System.currentTimeMillis(), "This is a test 3", UUIDs.createUUID()));

                    Database.putComment(new Comment(System.currentTimeMillis(), "Test Comment", Utils.shortHash("This is a test"), UUIDs.createUUID()));
                }
            });
        }
    }
}