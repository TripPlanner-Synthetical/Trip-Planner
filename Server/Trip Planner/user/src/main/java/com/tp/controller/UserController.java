package com.tp.controller;

import com.tp.bean.UserBean;
import com.tp.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Produces;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    @Produces("text/plain;charset=UTF-8")
    public String userLogin(@RequestParam("name") String name, @RequestParam("password") String password, HttpServletRequest request,
                            HttpServletResponse response)
 {
     HttpSession session=request.getSession();
         List<UserBean> users;
         int id=-1;
         users=userDao.findByName(name);
        if (users.size() == 1 && users.get(0).getPassword().equals(password)) {
            id=users.get(0).getId();
            if(id!=-1)
            {
                redisTemplate.opsForValue().set(session.getId(), String.valueOf(id));
            }
            else {
                return "300";
            }
            return "200";
        }
        return "300";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    @ResponseBody
    @Produces("text/plain;charset=UTF-8")
    public void logout(HttpServletRequest request,HttpServletResponse response){
        HttpSession session=request.getSession();
        session.invalidate();
        redisTemplate.delete(session.getId());

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
            int maxid=Integer.parseInt(userDao.findmaxId()+1);
            user.setId(maxid);
            userDao.save(user);
        }
        return result;
    }
}


