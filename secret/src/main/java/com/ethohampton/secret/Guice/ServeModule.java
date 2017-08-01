package com.ethohampton.secret.Guice;

// Copyright (c) 2010 Tim Niblett All Rights Reserved.
//
// File:        ServeModule.java  (05-Oct-2010)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Servlets.AddSecret;
import com.ethohampton.secret.Servlets.GetSecret;
import com.ethohampton.secret.Servlets.RandomSecret;
import com.ethohampton.secret.Servlets.VoteSecret;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cache.AsyncCacheFilter;

import java.util.Map;
import java.util.logging.Logger;


public class ServeModule extends ServletModule {
    private static final Logger LOG = Logger.getLogger(ServeModule.class.getName());

    private final String userBaseUrl;

    public ServeModule(String userBaseUrl) {
        Preconditions.checkArgument(userBaseUrl != null && !userBaseUrl.endsWith("/"));
        this.userBaseUrl = userBaseUrl;
    }

    private static Map<String, String> map(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0, "You have to have an even number of map params");
        Map<String, String> map = Maps.newHashMap();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }

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

        if (ServeLogic.isDevelopmentServer()) {
            //do things here only in development
            LOG.info("This is a dev server");

            //loads 4 test secrets to save dev time
            ObjectifyService.run(new VoidWork() {
                public void vrun() {
                    Database.put(new Secret(System.currentTimeMillis(), "This is a test"));
                    Database.put(new Secret(System.currentTimeMillis(), "This is a test 1"));
                    Database.put(new Secret(System.currentTimeMillis(), "This is a test 2"));
                    Database.put(new Secret(System.currentTimeMillis(), "This is a test 3"));
                }
            });
        }
    }
}