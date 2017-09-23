package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
import com.ethohampton.secret.Util.UUIDs;
import com.googlecode.objectify.Key;

import java.io.IOException;

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
        //insures string is not empty
        if (commentString.isEmpty() || secretID.isEmpty()) {
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

        if (addToDatabase) {//if it passes all checks
            //attempts to get UUID for user, if that fails then it creates a new one and adds it to the users cookies
            String uuid = UUIDs.getUUID(req.getCookies());
            if (uuid == null || uuid.isEmpty()) {
                uuid = UUIDs.createUUID();
                resp.addCookie(UUIDs.createUUIDCookie(uuid));
            }

            commentString = commentString.trim();
            String referenceKey = "";
            if (commentID != null) {
                referenceKey = Key.create(commentID).getName();
            }
            Comment comment = new Comment(System.currentTimeMillis(), commentString, Key.create(secretID).getName(), uuid).setReferencedCommentID(referenceKey);
            Database.putComment(comment);

            resp.getWriter().println("Success");
        } else {
            resp.getWriter().print("Something went wrong, please try again");
            resp.setStatus(400);
        }
    }


}
