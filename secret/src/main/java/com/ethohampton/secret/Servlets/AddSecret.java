package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
import com.ethohampton.secret.Util.UUIDs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.inject.Singleton;

import java.io.IOException;

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

    /*

    FirebaseAuth.getInstance().verifyIdToken(idToken)
        .addOnSuccessListener(new OnSuccessListener<FirebaseToken>() {
            @Override
            public void onSuccess(FirebaseToken decodedToken) {
                String uid = decodedToken.getUid();
                // ...
            }
    });

     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        boolean addToDatabase = true;

        //gets string and checks if it should be added to the database
        String temp = req.getParameter("secret");

        //insures string is not empty
        if (temp == null || temp.isEmpty()) {
            addToDatabase = false;
            //check to see if we are filtering bad words and if we are then insures non are present
        } else if (Constants.FILTER_WORDS) {
            if (!Filter.passesAllFilters(temp)) {//if it does not pass all precheck filters
                addToDatabase = false;
            }
        } else if (Database.objectExists(Secret.createKey(temp))) {
            addToDatabase = false;
        }


        if (addToDatabase) {
            //attempts to get UUID for user, if that fails then it creates a new one and adds it to the users cookies
            String idToken = UUIDs.getUUID(req.getCookies());

            if (idToken != null && !idToken.isEmpty()) {
                FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken).getResult();

                if (UUIDs.isValid(token)) { // TODO: 10/9/17 Seperate the authentication logic and the actual insertion of the secret

                    temp = temp.trim();
                    Secret secret = new Secret(System.currentTimeMillis(), temp, token.getUid());
                    Database.putSecret(secret);

                    Cookie cookie = new Cookie("addedSecret", "1");
                    cookie.setMaxAge(Constants.MAX_COOKIE_AGE);//cookie is good a good amount of time
                    resp.addCookie(cookie);//add cookie to response

                    resp.getWriter().println("Success");
                } else {
                    resp.getWriter().println("Your token is not valid, please log in and insure you have confirmed your email");
                }
            } else {
                resp.getWriter().println("Please Login");
            }
        } else {
            resp.sendError(400, "Something went wrong, please try again");
        }
    }

}
