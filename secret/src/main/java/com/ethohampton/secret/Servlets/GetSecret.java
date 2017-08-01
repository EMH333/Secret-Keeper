package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Constants;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/15/16.
 * gets a question
 */
@Singleton
public class GetSecret extends BasicServlet {
    public GetSecret() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        //gets string
        String queryString = req.getQueryString();
/*
            Long id = 0L;
            try {
                id = Long.parseLong(queryString);
            } catch (NumberFormatException e) {
                resp.sendError(404, "Invalid ID");
            }
*/

        //get question
        Secret temp = Database.get(queryString);
        if (temp == null) {
            resp.sendError(404, "Question not found");
            return;
        }
        //formats question and sends response
        resp.getWriter().println(temp.getSecret() + Constants.SEPARATOR + temp.getCreationTime());

    }
}
