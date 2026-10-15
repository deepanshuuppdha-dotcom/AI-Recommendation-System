package com.recsys.util;

import com.recsys.dao.UserDAO;
import com.recsys.model.Role;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/** On first start, creates the demo admin and demo user with properly salted password hashes. */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserDAO dao = new UserDAO();
            if (dao.count() == 0) {
                dao.create("Admin", "admin@recsys.com", PasswordUtil.hash("Admin@123"), Role.ADMIN);
                dao.create("Demo User", "user@recsys.com", PasswordUtil.hash("User@1234"), Role.USER);
                LOG.info("Created demo accounts admin@recsys.com and user@recsys.com");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not seed demo users. Is MySQL running and schema.sql loaded?", e);
        }
    }
}
