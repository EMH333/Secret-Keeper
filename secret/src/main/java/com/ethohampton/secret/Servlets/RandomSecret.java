package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.OfyService;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Utils;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by ethohampton on 12/17/16.
 * <p>
 * gets a random question
 */
@Singleton
public class RandomSecret extends BasicServlet {

    private Long lastUpdated = 0L;
    private List<Key<Secret>> keys;//TODO make this a global cache using memcache (although slower, would generally update faster)

    public RandomSecret() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");


        //checks to see if user has given a secret to the system and tells them to if they have not
        Cookie[] cookies = req.getCookies();
        boolean needToShare = Utils.correctCookie(cookies);
        if (needToShare) {
            resp.getWriter().print(Utils.secretToJSON(new Secret(0, "You need to enter a secret in the system before you can view other secrets!"), "null"));
            return;
        }

        //update keys if needed, this prevents unnecessary database calls
        if ((lastUpdated + Constants.UPDATE_TIME < System.currentTimeMillis())||keys.isEmpty()) {
            keys = OfyService.ofy().load().type(Secret.class).limit(Constants.UPDATE_COUNT_LIMIT).keys().list();
            lastUpdated = System.currentTimeMillis();
        }


        Random r = new Random();
        try {
            //if there are no secrets in the system, then return an error
            if(keys.size() < 1){
                resp.sendError(503, "No secrets in the system");
                return;
            }
            int i = r.nextInt(keys.size());

            //get question
            Key<Object> key = keys.get(i).getRoot();
            Secret q = Database.get(key);
            //makes sure question is not null
            if (q == null) {
                resp.sendError(404, "Invalid ID");
                return;
            }
            //send response
            resp.getWriter().println(Utils.secretToJSON(q,key.toWebSafeString()));

        } catch (Exception e) {
            resp.sendError(500);
            e.printStackTrace();
        }


    }
}
