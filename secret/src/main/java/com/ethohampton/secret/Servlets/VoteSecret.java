package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Utils;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 7/31/17.
 * <p>
 * adds votes to a secret
 */
@Singleton
public class VoteSecret extends BasicServlet {
    private static final Logger LOG = Logger.getLogger(VoteSecret.class.getName());

    public VoteSecret() {
        super();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        //checks to see if user has given a secret to the system and tells them to if they have not
        Cookie[] cookies = req.getCookies();
        boolean needToShare = Utils.correctCookie(cookies);
        if (needToShare) {
            resp.getWriter().println("Please authenticate");
            resp.setStatus(404);
            return;
        }

        //gets string
        String id = req.getParameter("id");
        boolean isUpVote = Boolean.parseBoolean(req.getParameter("upvote"));

        try {
            //get question
            Secret temp = Database.get(Key.create(id));
            if (temp == null) {
                resp.sendError(404, "Question not found");
            } else {

                //FIXME: 7/31/17 add caching to prevent resorces from writing so many votes
                if (isUpVote) {
                    temp.addUpVote();
                } else {
                    temp.addDownVote();
                }

                Database.put(temp);//put back in database

                //formats question and sends response
                resp.getWriter().println(Utils.secretToJSON(temp, id));
            }
        } catch (IllegalArgumentException e) {
            resp.getWriter().println("Invalid ID");
            resp.sendError(404);
        }


    }
}
