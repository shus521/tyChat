package com.tyss.controller;

import com.tyss.enums.OperatorFriendRequestTypeEnum;
import com.tyss.enums.SearchFriendsStatusEnum;
import com.tyss.pojo.Users;
import com.tyss.pojo.bo.UsersBO;
import com.tyss.pojo.vo.MyFriendsVO;
import com.tyss.pojo.vo.UsersVO;
import com.tyss.service.UserService;
import com.tyss.utils.FastDFSClient;
import com.tyss.utils.FileUtils;
import com.tyss.utils.IMoocJSONResult;
import com.tyss.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("u")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registOrLogin")
    public IMoocJSONResult registOrLogin(@RequestBody Users user) throws Exception {

        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名或密码不能为空");
        }

        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (usernameIsExist) {
            //登录
            userResult =  userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return IMoocJSONResult.errorMsg("用户名或密码不正确");
            }
        } else {
            //注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);

        return IMoocJSONResult.ok(usersVO);
    }


    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception {
        /* 获取前端上传的base64字符串，转换为文件对象 */
        String base64Data = usersBO.getFaceData();
        String userFacePath = "E:\\" + usersBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        //上传文件到fastDFS
        MultipartFile multipartFile= FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(multipartFile);
        System.out.println(url);

        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];


        Users users = new Users();
        users.setId(usersBO.getUserId());
        users.setFaceImage(thumpImgUrl);
        users.setFaceImageBig(url);

        Users user = userService.updateUserInfo(users);

        return IMoocJSONResult.ok(user);
    }

    /**
     * 修改昵称
     * @param usersBO
     * @return
     * @throws Exception
     */
    @PostMapping("/setNickname")
    public IMoocJSONResult setNickname(@RequestBody UsersBO usersBO) throws Exception {
        if ("".equals(usersBO.getNickname()) || usersBO.getNickname() == null) {
            return IMoocJSONResult.errorMsg("昵称不合法");
        }
        if (8< usersBO.getNickname().length() || 0 > usersBO.getNickname().length()) {
            return IMoocJSONResult.errorMsg("昵称长度不合法");
        }
        Users users = new Users();
        users.setId(usersBO.getUserId());
        users.setNickname(usersBO.getNickname());

        Users user = userService.updateUserInfo(users);
        return IMoocJSONResult.ok(user);
    }


    @PostMapping("/searchUser")
    public IMoocJSONResult searchUser(String myUserId, String friendUsername) throws Exception {
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return IMoocJSONResult.errorMsg("");
        }

        Integer status = userService.seacherFriends(myUserId, friendUsername);
        if (SearchFriendsStatusEnum.SUCCESS.status.equals(status)) {
            Users user = userService.queryUserByUsername(friendUsername);
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            return IMoocJSONResult.ok(usersVO);
        } else {
            String errMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(errMsg);
        }

    }


    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId, String friendUsername) throws Exception {
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendUsername)) {
            return IMoocJSONResult.errorMsg("");
        }

        Integer status = userService.seacherFriends(myUserId, friendUsername);
        if (SearchFriendsStatusEnum.SUCCESS.status.equals(status)) {
           userService.sendFriendRequest(myUserId, friendUsername);
        } else {
            String errMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(errMsg);
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/queryFriendRequests")
    public IMoocJSONResult queryFriendRequests(String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("");
        }
        //查询用户接收到的好友申请
        return IMoocJSONResult.ok(userService.queryFriendRequestList(userId));
    }

    /**
     * 通过或者忽略好友请求
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     * @throws Exception
     */
    @PostMapping("/operFriendRequest")
    public IMoocJSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType) throws Exception {
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || operType == null) {
            return IMoocJSONResult.errorMsg("");
        }

        //操作类型有误
        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
            return IMoocJSONResult.errorMsg("");
        }


        if (OperatorFriendRequestTypeEnum.IGNORE.type.equals(operType)) {
            //忽略好友请求，删除记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (OperatorFriendRequestTypeEnum.PASS.type.equals(operType)){
            //同意好友请求，删除记录，且添加好友
            userService.passFriendRequest(sendUserId, acceptUserId);
        }
        return IMoocJSONResult.ok();
    }

    /**
     * 查询我的好友列表
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/myFriends")
    public IMoocJSONResult myFriends(String userId) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("");
        }

        List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

        return IMoocJSONResult.ok(myFriends);
    }
}
