package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.OfyService;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Utils;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

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
    private static final Logger LOG = Logger.getLogger(RandomSecret.class.getName());

    private Long lastUpdated = 0L;
    private List<Key<Secret>> keys;//TODO make this a global cache using memcache (although slower, would generally update faster)
    private Secret blankSecret = new Secret(0, "You need to enter a secret in the system before you can view other secrets!", null);

    public RandomSecret() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        ArrayList<String> seen = new ArrayList<>();

        //checks to see if user has given a secret to the system and tells them to if they have not
        Cookie[] cookies = req.getCookies();
        boolean needToShare = Utils.correctCookie(cookies);
        if (needToShare) {
            resp.getWriter().print(Utils.secretToJSON(blankSecret, "null", null));
            return;
        }

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("seenSecrets")) {
                    Gson gson = new Gson();
                    seen = gson.fromJson(cookie.getValue(), ArrayList.class);
                }
            }
        }

        //update keys if needed, this prevents unnecessary database calls
        if ((lastUpdated + Constants.UPDATE_TIME < System.currentTimeMillis()) || keys.isEmpty()) {
            keys = OfyService.ofy().load().type(Secret.class).limit(Constants.UPDATE_COUNT_LIMIT).keys().list();
            lastUpdated = System.currentTimeMillis();
        }


        Random r = new Random();
        try {
            //if there are no secrets in the system, then return an error
            if (keys.size() < 1) {
                resp.sendError(503, "No secrets in the system");
                return;
            }
            int i = r.nextInt(keys.size());

            //get question
            Key<Object> key = keys.get(i).getRoot();

            while (seen.size() >= keys.size()) {//insures we don't stop the server
                seen.clear();
            }

            while (seen.contains(keyHash(key))) {
                i = r.nextInt(keys.size());
                //get another question
                key = keys.get(i).getRoot();
            }

            Secret q = Database.getSecret(key);

            //insure down voted secrets aren't shown very often
            boolean go = false;
            while (((q.getUpvotes() - q.getDownvotes()) < Constants.CONSIDERED_DOWNVOTED) && !go) {
                //pick and see if a comment has a chance to be shown
                int rand = r.nextInt(Constants.CHANCE_TO_SHOW_DOWNVOTED_COMMENT);
                if (rand == Constants.CHANCE_TO_SHOW_DOWNVOTED_COMMENT - 1) {
                    go = true;
                    LOG.info("Showing a downvoted comment");
                } else {
                    //copied from above (lines 85 thru 92)
                    //if not then find a new comment to show and repeat
                    int loop2 = 0;
                    while (seen.contains(keyHash(key)) && loop2 < Constants.MAX_RANDOM_LOOPS) {
                        i = r.nextInt(keys.size());
                        //get another question
                        key = keys.get(i).getRoot();

                        q = Database.getSecret(key);
                        loop2++;
                    }
                }

            }


            /*
             //makes sure question is not null
             if (q == null) {
             resp.sendError(404, "Invalid ID");
             return;
             }
             */

            List<Comment> comments = null;
            if (req.getParameter("loadComments").equals("true")) {//loads comments if asked
                comments = Database.getCommentsFromSecret(q.getId(), 0);
            }
            //send response
            resp.getWriter().println(Utils.secretToJSON(q, key.toWebSafeString(), comments));//print JSON of secret, including comments if asked
            seen.add(keyHash(key));

            if (seen.size() > Constants.MAX_SEEN_SECRETS_SIZE) {
                seen.remove(0);//removes the oldest secret from the list so it does not get too long
            }

            //turn arraylist to json
            Gson gson = new Gson();
            String json = gson.toJson(seen);
            //add list to cookie
            Cookie seenSecrets = new Cookie("seenSecrets", json);
            seenSecrets.setMaxAge(60 * 60 * 24 * 7 * 2);//cookie good for 2 weeks
            resp.addCookie(seenSecrets);
        } catch (Exception e) {
            resp.sendError(500);
            e.printStackTrace();
        }


    }

    private String keyHash(Key<Object> key) {
        return Utils.shortHash(String.valueOf(key.hashCode()));
    }
}
