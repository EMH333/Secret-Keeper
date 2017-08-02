package com.ethohampton.secret.Servlets;

import com.ethohampton.secret.Database;
import com.ethohampton.secret.Objects.BasicServlet;
import com.ethohampton.secret.Objects.Secret;
import com.ethohampton.secret.Util.Constants;
import com.ethohampton.secret.Util.Filter;
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
        Filter.loadConfigs();//loads bad words from proper location
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");

        boolean addToDatabase;

        //gets string and checks if it should be added to the database
        String temp = req.getParameter("secret");

        addToDatabase = testSecret(temp);//insure secret is up to standard

        if (addToDatabase) {
            temp = temp.trim();
            Secret secret = new Secret(System.currentTimeMillis(), temp);
            Database.put(secret);

            Cookie cookie = new Cookie("addedSecret", "1");
            cookie.setMaxAge(Constants.MAX_COOKIE_AGE);//cookie is good a good amount of time
            resp.addCookie(cookie);//add cookie to response

            resp.getWriter().println("Success");
        } else {
            resp.getWriter().print("Something went wrong, please try again");
            resp.setStatus(400);
        }
    }
    
    public boolean testSecret(String secret){
        boolean add = true;
        //insures string is not empty
        if (secret.isEmpty()) {
            add = false;
            //check to see if we are filtering bad words and if we are then insures non are present
        } else if (Constants.FILTER_WORDS) {
            if (!Filter.passesAllFilters(secret)) {//if it does not pass all precheck filters
                add = false;
            }
        }


        return add;
    }
}
