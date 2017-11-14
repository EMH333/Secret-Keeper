package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
import com.ethohampton.secret.Util.UUIDs;
import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 8/13/17.
 * <p>
 * Allows for people to add comments to a secret
 */
@Singleton
public class AddComment extends BasicServlet {

    public AddComment() {
        super();
        if (!Filter.hasConfigs())
            Filter.loadConfigs();//loads bad words from proper location
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        String secretID = req.getParameter("secretID");
        String commentID = req.getParameter("commentID");

        //gets string and checks if it should be added to the database
        String commentString = req.getParameter("comment");

        boolean addToDatabase = true;
        ApiFuture<FirebaseToken> tokenTask = null;

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
        if (commentString == null || secretID == null || secretID.isEmpty() || secretID.isEmpty()) {
            addToDatabase = false;
            //check to see if we are filtering bad words and if we are then insures non are present
        } else if (!Database.objectExists(Key.create(secretID))) {
            addToDatabase = false;
        } else if (Constants.FILTER_WORDS) {
            if (!Filter.passesAllFilters(commentString)) {//if it does not pass all precheck filters
                addToDatabase = false;
            }
        }

        //insures comment that this comment is referencing exists
        if (commentID != null && !commentID.isEmpty()) {
            if (!Database.objectExists(Key.create(commentID))) {
                addToDatabase = false;
            }
        }

        //verifys token that was procured using async code above (a 2 in one :) yay)
        try {// TODO: 10/25/17 Insure we don't skip validation
            assert tokenTask != null;
            if (addToDatabase && !UUIDs.isValid(tokenTask.get())) {
                addToDatabase = false;
                resp.getWriter().println("Your token is not valid, please log in and insure you have confirmed your email");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (addToDatabase) {//if it passes all checks

            commentString = commentString.trim();
            String referenceKey = "";
            if (commentID != null) {
                referenceKey = Key.create(commentID).getName();
            }
            Comment comment = null;
            // TODO: 10/29/17 handle exceptions
            try {
                comment = new Comment(System.currentTimeMillis(), commentString, Key.create(secretID).getName(), tokenTask.get().getUid()).setReferencedCommentID(referenceKey);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Database.putComment(comment);

            resp.getWriter().println("Success");
        } else {
            resp.getWriter().print("Something went wrong, please try again");
            resp.setStatus(400);
        }
    }


}
