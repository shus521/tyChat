package com.tyss.mapper;

import com.tyss.pojo.Users;
import com.tyss.pojo.vo.FriendRequestVO;
import com.tyss.pojo.vo.MyFriendsVO;
import com.tyss.utils.MyMapper;

import java.util.List;


/**
 * @author ty
 */
public interface UsersMapperCustom extends MyMapper<Users> {
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    public List<MyFriendsVO> queryMyFriends(String userId);

    public void batchUpdateMsgSigned(List<String> msgIdList);
}