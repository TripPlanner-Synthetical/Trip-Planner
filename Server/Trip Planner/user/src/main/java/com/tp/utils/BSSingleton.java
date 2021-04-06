package com.tp.utils;

import com.tp.dao.UserDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BSSingleton {
    private static UserDao userDAO = null;
    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext(
                    "applicationContext.xml");
        }
        return context;
    }
    public static UserDao getUserDAO() {
        if (userDAO == null) {
            userDAO = (UserDao) getApplicationContext().getBean("UserDAO");
        }
        return userDAO;
    }
}
