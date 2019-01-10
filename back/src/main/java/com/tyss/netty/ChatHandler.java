package com.tyss.netty;

import java.util.ArrayList;
import java.util.List;
import com.tyss.enums.MsgActionEnum;
import com.tyss.service.UserService;
import com.tyss.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import com.tyss.SpringUtil;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	private static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) 
			throws Exception {
		// 获取客户端传输过来的消息
		String content = msg.text();

		Channel channel = ctx.channel();
		DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
		Integer action = dataContent.getAction();
		// 判断消息类型
		if (MsgActionEnum.CONNECT.type.equals(action)) {
			//1. 第一次连接，关联userId
			String senderId = dataContent.getChatMsg().getSenderId();
			UserChannelRel.put(senderId, channel);
		} else if(MsgActionEnum.CHAT.type.equals(action)) {
			//2.消息:保存内容到数据库,标记消息的签收状态
			ChatMsg chatMsg = dataContent.getChatMsg();
			String msgText = chatMsg.getMsg();
			String receiverId = chatMsg.getReceiverId();
			String senderId = chatMsg.getSenderId();
			UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
			chatMsg.setMsgId(msgId);

			DataContent dataContentMsg = new DataContent();
			dataContentMsg.setChatMsg(chatMsg);
			//发送消息
			Channel receiverChannel = UserChannelRel.get(receiverId);
			if (receiverChannel == null) {
				//用户离线，推送消息
			} else {
				//去channelGroup查找channel是否存在
				Channel findChannel = users.find(receiverChannel.id());
				if (findChannel != null) {
					receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
				} else {
					//用户离线，推送消息
				}
			}
		} else if(MsgActionEnum.SIGNED.type.equals(action)) {
			//3签收消息
			UserService userService = (UserService)SpringUtil.getBean("userServiceImpl");
			//需要签收的消息id
			String msgIdStr = dataContent.getExtand();
			String msgIds[] = msgIdStr.split(",");
			List<String> msgIdList = new ArrayList<>();
			for (String mid:msgIds) {
				if (StringUtils.isNotBlank(mid)) {
					msgIdList.add(mid);
				}
			}
			System.out.println(msgIdList.toString());
			if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
				userService.updateMsgSigned(msgIdList);
			}
		} else if(MsgActionEnum.KEEPALIVE.type.equals(action)) {
			//4. 心跳检测
		}
	}

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		users.remove(ctx.channel());
//		System.out.println("客户端断开，channle对应的长id为："
//							+ ctx.channel().id().asLongText());
//		System.out.println("客户端断开，channle对应的短id为："
//							+ ctx.channel().id().asShortText());
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//发生异常关闭连接
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
