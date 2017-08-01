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

/*
    private String keysToString(List<Key<Secret>> keys){
        StringBuilder sb=new StringBuilder();
        for (Key<Secret> l:keys) {
            sb.append(l.toWebSafeString());
            sb.append(Constants.SEPARATOR);
        }
        return sb.toString();
    }
    private List<Key<Secret>> keysFromString(String string){
        String[] array = string.split(Constants.SEPARATOR);
        List<Key<Secret>> keys = new ArrayList<Key<Secret>>();
        for(String s:array){
            keys.add(Key.valueOf(s));
        }
        return keys;
    }
    */
}
