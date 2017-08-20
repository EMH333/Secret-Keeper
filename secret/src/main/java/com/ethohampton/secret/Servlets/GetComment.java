package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Comment;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Utils;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/15/16.
 * gets a question
 */
@Singleton
public class GetComment extends BasicServlet {
    public GetComment() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        //resp.sendError(403);

        //gets string
        String id = req.getParameter("id");

        // Long id = 0L;
        // try {
        //     id = Long.parseLong(id);
        // } catch (NumberFormatException e) {
        //      resp.sendError(404, "Invalid ID");
        // }


        //get question
        Comment comment = Database.getComment(id);
        if (comment == null) {
            if (Database.objectExists(Key.<Object>create(Secret.class, id))) {//if the id is for a secret
                List<Comment> list = Database.getCommentsFromSecret(id, 0);//get list of comments from database and print them in a JSON array
                resp.getWriter().println(Utils.commentListToJSON(list).toString());
            } else {//not a comment or a secret
                resp.sendError(404, "Comment not found");
            }
        } else {
            //formats question and sends response
            resp.getWriter().println(Utils.commentToJSON(comment));
        }


    }
}
