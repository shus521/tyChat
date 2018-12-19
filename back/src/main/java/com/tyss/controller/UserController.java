package com.tyss.controller;

import com.tyss.pojo.Users;
import com.tyss.pojo.bo.UsersBO;
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

        userService.updateUserInfo(users);

        return IMoocJSONResult.ok(users);
    }

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

        userService.updateUserInfo(users);

        return IMoocJSONResult.ok(users);
    }
}
