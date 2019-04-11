package com.sg.hackamu.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ChatMessage implements Parcelable {
    private String messageText;
    private String senderuuid;
    private String recieveruuid;
    private long messageTime=new Date().getTime();

    public ChatMessage(String messageText, String senderuuid,String recieveruuid) {
        this.messageText = messageText;
        this.recieveruuid=recieveruuid;
        this.senderuuid=senderuuid;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderuuid() {
        return senderuuid;
    }

    public void setSenderuuid(String senderuuid) {
        this.senderuuid = senderuuid;
    }

    public String getRecieveruuid() {
        return recieveruuid;
    }

    public void setRecieveruuid(String recieveruuid) {
        this.recieveruuid = recieveruuid;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    protected ChatMessage(Parcel in) {
        messageText = in.readString();
        senderuuid = in.readString();
        recieveruuid = in.readString();
        messageTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageText);
        dest.writeString(senderuuid);
        dest.writeString(recieveruuid);
        dest.writeLong(messageTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}