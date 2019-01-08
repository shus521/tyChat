package com.tyss.netty;

import java.io.Serializable;

public class DataContent implements Serializable {
    private static final long serialVersionUID = 6082622003248100958L;
    private Integer action;    //动作类型
    private ChatMsg chatMsg;  //聊天内容
    private String extand;    //扩展字段

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public ChatMsg getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public String getExtand() {
        return extand;
    }

    public void setExtand(String extand) {
        this.extand = extand;
    }
}
