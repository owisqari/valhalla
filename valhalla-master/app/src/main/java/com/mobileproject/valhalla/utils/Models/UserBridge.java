package com.mobileproject.valhalla.utils.Models;

import com.cometchat.pro.models.User;
import com.stfalcon.chatkit.commons.models.IUser;

// this class will make the connection between the CometChat ( backend ) user and ChatKit ( frontend ) user
// by implementing the IUser interface from ChatKit and receiving a User object form CometChat
// and convert the backend user to frontend user
public class UserBridge implements IUser {
    private User user;

    public UserBridge(User user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getUid();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar();
    }
}
