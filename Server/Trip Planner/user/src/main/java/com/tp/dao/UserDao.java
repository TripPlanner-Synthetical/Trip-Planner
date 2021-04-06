package com.tp.dao;

import com.tp.bean.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

@Service
public class UserDao implements Serializable {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(UserBean user) {
        try {
            mongoTemplate.save(user);
        } catch (RuntimeException re) {
            throw re;
        }
    }

    public void delete(UserBean user) {
        try {
            mongoTemplate.remove(user);
        } catch (RuntimeException re) {
            throw re;
        }
    }

    public List findById(int id) {
        try {
            Query query=new Query(Criteria.where("id").is(id));
            List<UserBean> user =  mongoTemplate.find(query , UserBean.class);
            return user;
        } catch (RuntimeException re) {
            throw re;
        }
    }
    public List findByName(String name) {
        try {
            Query query=new Query(Criteria.where("name").is(name));
            List<UserBean> user =  mongoTemplate.find(query , UserBean.class);
            return user;
        } catch (RuntimeException re) {
            throw re;
        }
    }


}
