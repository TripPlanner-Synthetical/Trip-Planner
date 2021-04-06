package com.tp.controller;

import com.tp.bean.UserBean;
import com.tp.dao.UserDao;
import com.tp.utils.BSSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    @Produces("text/plain;charset=UTF-8")
    public String userLogin(@RequestParam("name") String name,@RequestParam("password") String password)
 {
        List<UserBean> users = new ArrayList<UserBean>();
             users=userDao.findByName(name);
        if (users.size() == 1 && users.get(0).getPassword().equals(password)) {
            return "200";
        }
        return "300";
    }



    @RequestMapping(path = "/register", method = RequestMethod.POST)
    @ResponseBody
    @Produces("text/plain;charset=UTF-8")
    public String userRegister(
            @RequestParam("name") String name,@RequestParam("password") String password) {
        String result = "";
       if(name!=null&&!"".equals(name)&&userDao.findByName(name).size()>0) {
            result = result + "该用户名已被注册";
        }
        else {
            result = result + "注册成功";
            UserBean user = new UserBean();
            user.setUsername(name);
            user.setPassword(password);
            userDao.save(user);
        }
        return result;
    }
}


