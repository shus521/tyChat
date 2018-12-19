package com.tyss.service;

import com.tyss.pojo.Users;
import org.apache.catalina.User;

public interface UserService {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否可以登录
     * @param username
     * @param pwd
     * @return
     */
    public Users queryUserForLogin(String username, String pwd);

    /**
     * 注册用户
     * @param user
     * @return
     */
    public Users saveUser(Users user);

    /**
     * 修改用户信息
     * @param user
     */
    public Users updateUserInfo(Users user);
}
