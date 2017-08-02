package com.ethohampton.secret.Servlets;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ethohampton on 12/15/16.
 * gets a question
 */
@Singleton
public class GetSecret extends HttpServlet {
    public GetSecret() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        resp.sendRedirect("/index.html");

    }
}
