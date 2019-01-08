window.app = {
	serverUrl : 'http://192.168.43.200:8080',
//	serverUrl : 'http://192.168.1.104:8080',
	fileUrl : 'http://47.93.36.58:88/ty/',
//	wsUrl : 'ws://192.168.1.104:8088/ws',
	wsUrl : 'ws://192.168.43.200:8088/ws',
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
		plus.nativeUI.toast(msg, {icon: "image/" + type + ".png",verticalAlign:"center"})
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
		
	}
	
}
