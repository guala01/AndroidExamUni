package com.example.myapplication

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.Date

class Message(val mId:String, val mText:String, var mUser:IUser, var mDate:Date, var mUrl: String?):IMessage,MessageContentType.Image {

    override fun getId(): String {
        return mId;
    }

    override fun getText(): String {
        return mText;
    }

    override fun getUser(): IUser {
        return mUser;
    }

    override fun getCreatedAt(): Date {
        return mDate;
    }
    override fun getImageUrl(): String? {
        if (mUrl.equals("")) {
            return null;
        }
        return mUrl;
    }
}