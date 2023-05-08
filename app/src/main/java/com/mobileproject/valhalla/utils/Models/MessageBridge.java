package com.mobileproject.valhalla.utils.Models;

import com.cometchat.pro.models.TextMessage;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

// this class will make the connection between the CometChat ( backend ) messages and ChatKit ( frontend ) messages
// by implementing the IMessage interface from ChatKit and receiving a TextMessage object form CometChat
// and convert the backend message to frontend message
public class MessageBridge implements IMessage {
    private TextMessage textMessage;

    public MessageBridge(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    @Override
    public String getId() {
        return textMessage.getMuid();
    }

    @Override
    public String getText() {
        return textMessage.getText();
    }

    @Override
    public IUser getUser() {
        return new UserBridge(textMessage.getSender());
    }

    @Override
    public Date getCreatedAt() {
        return new Date(textMessage.getSentAt() * 1000);
    }
}
