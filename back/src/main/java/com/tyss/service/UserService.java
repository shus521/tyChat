package com.tyss.service;

import com.tyss.pojo.Users;
import com.tyss.pojo.vo.FriendRequestVO;
import com.tyss.pojo.vo.MyFriendsVO;
import org.apache.catalina.User;

import java.util.List;

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

    /**
     * 搜索好友
     * @param myUserId
     * @param friendUsername
     * @return
     */
    public Integer seacherFriends(String myUserId, String friendUsername);

    /**
     * 获取好友信息
     * @param friendUsername
     * @return
     */
    public Users queryUserByUsername(String friendUsername);

    /**
     * 添加好友请求记录
     * @param myUserId
     * @param friendUsername
     */
    public void sendFriendRequest(String myUserId, String friendUsername);

    /**
     * 获取好友请求
     * @param acceptUserId
     * @return
     */
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 通过好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void passFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    public List<MyFriendsVO> queryMyFriends(String userId);
}
