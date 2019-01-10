window.app = {
	serverUrl : 'http://120.77.212.208:8080',
//	serverUrl : 'http://192.168.43.200:8080',
//	serverUrl : 'http://192.168.1.104:8080',
	fileUrl : 'http://47.93.36.58:88/ty/',
//	wsUrl : 'ws://192.168.1.104:8088/ws',
//	wsUrl : 'ws://192.168.43.200:8088/ws',
	wsUrl : 'ws://120.77.212.208:8088/ws',
	/**
	 * 判断字符串是否为空
	 * @param {Object} str
	 */
	isNotNull: function(str) {
		if(str != null && str != "" && str != undefined) {
			return true;
		}
		return false;
	},
	/**
	 * 消息提示
	 * @param {Object} msg
	 * @param {Object} type
	 */
	showTip: function(msg, type) {
		plus.nativeUI.toast(msg, {icon: "../../image/" + type + ".png",verticalAlign:"center"})
	},
	/**
	 * 设置全局用户缓存
	 * @param {Object} user
	 */
	setUserGlobalInfo: function(user) {
		var userInfoStr = JSON.stringify(user);
		plus.storage.setItem("userInfo", userInfoStr);
	},
	/**
	 * 获取用户全局缓存
	 */
	getUserGlobalInfo: function() {
		return JSON.parse(plus.storage.getItem("userInfo"));
	},
	
	/**
	 * 登出
	 */
	userLogout: function() {
		plus.storage.removeItem("userInfo");
	},
	
	/**
	 * 通讯录缓存
	 * @param {Object} contactList
	 */
	setContactList: function(contactList) {
		var contactListStr = JSON.stringify(contactList);
		plus.storage.setItem("contactList", contactListStr);
	},
	
	/**
	 * 获取本地缓存种的联系人列表
	 */
	getContactList: function() {
		var contactList = plus.storage.getItem("contactList");
		if (!this.isNotNull(contactList)) {
//			console.log("33333333");
			return [];
		}
//		console.log("454545" + JSON.stringify(JSON.parse(contactList)));
		return JSON.parse(contactList);
	},
	CONNECT:   1, //"第一次(或重连)初始化连接"),
	CHAT:      2, //"聊天消息"),	
	SIGNED:    3, //"消息签收"),
	KEEPALIVE: 4, //"客户端保持心跳"),
	PULL_FRIEND: 5,
	/**
	 * 和后端的 ChatMsg 聊天模型对象保持一致
	 * @param {Object} senderId
	 * @param {Object} receiverId
	 * @param {Object} msg
	 * @param {Object} msgId
	 */
	ChatMsg: function(senderId, receiverId, msg, msgId){
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msg = msg;
		this.msgId = msgId;
	},
	
	/**
	 * 构建消息 DataContent 模型对象
	 * @param {Object} action
	 * @param {Object} chatMsg
	 * @param {Object} extand
	 */
	DataContent: function(action, chatMsg, extand){
		this.action = action;
		this.chatMsg = chatMsg;
		this.extand = extand;
	},
	/**
	 * 保存用户的聊天记录
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} flag  判断本条消息是谁发送的 1我 2朋友
	 */
	saveUserChatHistory: function(myId, friendId, msg, flag) {
		var me = this;
		var chatKey = "chat-" + myId + "-" + friendId;
		//从本地缓存获取聊天记录
		var chatHistoryListStr = plus.storage.getItem(chatKey);
		var chatHistoryList;
		if(me.isNotNull(chatHistoryListStr)) {
			chatHistoryList = JSON.parse(chatHistoryListStr);
		} else {
			chatHistoryList = [];
		}
		
		var singleMsg = new me.ChatHistory(myId, friendId, msg, flag);
		chatHistoryList.push(singleMsg);
		plus.storage.setItem(chatKey, JSON.stringify(chatHistoryList));
	},
	ChatHistory: function(myId, friendId, msg, flag) {
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.flag = flag;
	},
	/**
	 * 获取用户聊天记录
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	getUserChatHistory: function(myId, friendId) {
		var me = this;
		var chatKey = "chat-" + myId + "-" + friendId;
		var chatHistoryListStr = plus.storage.getItem(chatKey);
		var chatHistoryList;
		if(me.isNotNull(chatHistoryListStr)) {
			chatHistoryList = JSON.parse(chatHistoryListStr);
		} else {
			chatHistoryList = [];
		}
		
		return chatHistoryList;
		
	},
	/**
	 * 聊天记录的快照，保存和好友聊天的最后一条消息
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead
	 */
	saveUserSnapshot: function(myId, friendId, msg, isRead) {
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		//从本地缓存获取聊天快照
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)) {
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
			//循环快照判断是否能匹配到friendId,能匹配则删除
			for(var i = 0; i < chatSnapshotList.length; i++) {
				if (chatSnapshotList[i].friendId == friendId) {
					chatSnapshotList.splice(i, 1);
					break;
				}
			}
		} else {
			chatSnapshotList = [];
		}
		
		var singleMsg = new me.ChatSnapshot(myId, friendId, msg, isRead);
		chatSnapshotList.unshift(singleMsg);
		plus.storage.setItem(chatKey, JSON.stringify(chatSnapshotList));
	},
	/**
	 * 快照对象
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead
	 */
	ChatSnapshot: function(myId, friendId, msg, isRead) {
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.isRead = isRead;
	},
	/**
	 * 获取聊天快照
	 * @param {Object} myId
	 */
	getUserChatSnapshot: function(myId) {
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)) {
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
		} else {
			chatSnapshotList = [];
		}
		
		return chatSnapshotList;
	},
	/**
	 * 标记未读消息为已读
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	readUserChatSnapshot: function(myId, friendId) {
		var me = this;
		var chatKey = "chat-snapshot" + myId;
		var chatSnapshotListStr = plus.storage.getItem(chatKey);
		var chatSnapshotList;
		if(me.isNotNull(chatSnapshotListStr)) {
			chatSnapshotList = JSON.parse(chatSnapshotListStr);
			for (var i = 0;i<chatSnapshotList.length;i++) {
				var item = chatSnapshotList[i];
				if (item.friendId == friendId) {
					item.isRead = true; //标记为已读
					chatSnapshotList.splice(i, 1, item); //替换原有的快照
					break;
				}
			}
			plus.storage.setItem(chatKey, JSON.stringify(chatSnapshotList));
		} else {
			return;
		}
	},
	/**
	 * 从本地联系人缓存获取好友信息
	 * @param {Object} friendId
	 */
	getFriendFromContactList: function(friendId) {
		var contactListStr = plus.storage.getItem("contactList");
		if (!this.isNotNull(contactListStr)) {
//			console.log("33333333");
			return null;
		}
		var contactList = JSON.parse(contactListStr);
		for (var i = 0;i< contactList.length;i++) {
			var friend = contactList[i];
			if (friend.friendUserId == friendId) {
				return friend;
				break;
			}
		}
	}
	
}
