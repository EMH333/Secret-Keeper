package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
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

        String secretId = req.getParameter("secretID");

        //gets string and checks if it should be added to the database
        String commentString = req.getParameter("comment");

        boolean addToDatabase = true;
        //insures string is not empty
        if (commentString.isEmpty() || secretId.isEmpty()) {
            addToDatabase = false;
            //check to see if we are filtering bad words and if we are then insures non are present
        } else if (!Database.objectExists(Key.create(secretId))) {
            addToDatabase = false;
        } else if (Constants.FILTER_WORDS) {
            if (!Filter.passesAllFilters(commentString)) {//if it does not pass all precheck filters
                addToDatabase = false;
            }
        }

        if (addToDatabase) {//if it passes all checks
            commentString = commentString.trim();
            Comment comment = new Comment(System.currentTimeMillis(), commentString, Key.create(secretId).getName());
            Database.putComment(comment);

            resp.getWriter().println("Success");
        } else {
            resp.getWriter().print("Something went wrong, please try again");
            resp.setStatus(400);
        }
    }


}
