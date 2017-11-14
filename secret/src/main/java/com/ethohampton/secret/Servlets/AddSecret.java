package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
import com.ethohampton.secret.Util.UUIDs;
import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by ethohampton on 12/16/16.
 * <p>
 * adds vote for one answer
 */
@Singleton
public class AddSecret extends BasicServlet {
    public AddSecret() {
        super();
        if (!Filter.hasConfigs())
            Filter.loadConfigs();//loads bad words from proper location
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        boolean addToDatabase = true;
        ApiFuture<FirebaseToken> tokenTask = null;
        //gets string and checks if it should be added to the database
        String rawSecret = req.getParameter("secret");

        //attempts to get UUID for user, if that fails then it creates a new one and adds it to the users cookies
        String idToken = req.getParameter(UUIDs.TOKEN_NAME);
        if (idToken != null && !idToken.isEmpty()) {
            try {
                tokenTask = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resp.getWriter().println("Please Login");
            addToDatabase = false;
        }

        //insures string is not empty
        if (rawSecret == null || rawSecret.isEmpty()) {
            addToDatabase = false;
            resp.getWriter().println("Please enter a secret");
            //check to see if we are filtering bad words and if we are then insures non are present
        } else if (Constants.FILTER_WORDS) {
            if (!Filter.passesAllFilters(rawSecret)) {//if it does not pass all precheck filters
                addToDatabase = false;
                resp.getWriter().println("Please make sure you are using polite words :)");
            }
        } else if (Database.objectExists(Secret.createKey(rawSecret))) {
            addToDatabase = false;
            resp.getWriter().println("That secret already exists");
        }

        //verifies token that was procured using async code above (a 2 in one :) yay)
        try {
            assert tokenTask != null;
            if (addToDatabase && !UUIDs.isValid(tokenTask.get())) {
                addToDatabase = false;
                resp.getWriter().println("Your token is not valid, please log in and insure you have confirmed your email");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            addToDatabase = false;
            resp.getWriter().println("There appears to have been a problem, please try again");
        }


        if (addToDatabase) {
            rawSecret = rawSecret.trim();
            Secret secret = null;
            try {
                secret = new Secret(System.currentTimeMillis(), rawSecret, tokenTask.get().getUid());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                resp.setStatus(500);
                resp.getWriter().println("An error occured at the last moment, please try again");
            }
            Database.putSecret(secret);

            Cookie cookie = new Cookie("addedSecret", "1");
            cookie.setMaxAge(Constants.MAX_COOKIE_AGE);//cookie is good a good amount of time
            resp.addCookie(cookie);//add cookie to response

            resp.getWriter().println("Success");
        } else {
            resp.setStatus(400);

        }
    }

}
