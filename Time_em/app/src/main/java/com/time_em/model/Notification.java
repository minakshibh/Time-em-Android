package com.time_em.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification  implements Parcelable{

    int notificationId, senderId;
    String notificationType, attachmentPath, subject, message, createdDate, senderFullName;

    public Notification(){

    }
    protected Notification(Parcel in) {
        notificationId = in.readInt();
        senderId = in.readInt();
        notificationType = in.readString();
        attachmentPath = in.readString();
        subject = in.readString();
        message = in.readString();
        createdDate = in.readString();
        senderFullName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(notificationId);
        dest.writeInt(senderId);
        dest.writeString(notificationType);
        dest.writeString(attachmentPath);
        dest.writeString(subject);
        dest.writeString(message);
        dest.writeString(createdDate);
        dest.writeString(senderFullName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }
}