window.app = {
	serverUrl : 'http://192.168.43.200:8080',
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
	}
	
	
}
