package com.example.myapplication

import com.stfalcon.chatkit.commons.models.IUser

class User(val Uid:String, val Uname:String, val Uavatar:String):IUser {
    override fun getId(): String {
        return Uid;
    }

    override fun getName(): String {
        return Uname;
    }

    override fun getAvatar(): String {
        return Uavatar;
    }
}