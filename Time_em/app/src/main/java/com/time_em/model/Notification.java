package com.time_em.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification  implements Parcelable{

    int notificationId, senderId,UserId,notificationTypeId;
    String notificationType, attachmentPath, subject, message, createdDate, senderFullName,TimeZone,IsOffline,UniqueNumber;

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
        TimeZone = in.readString();
        notificationTypeId =in.readInt();
        IsOffline = in.readString();
        UserId = in.readInt();
        UniqueNumber =in.readString();
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
        dest.writeString(TimeZone);
        dest.writeInt(notificationTypeId);
        dest.writeString(IsOffline);
        dest.writeInt(UserId);
        dest.writeString(UniqueNumber);
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

    public String getTimeZone() {
        return TimeZone;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getIsOffline() {
        return IsOffline;

    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(int notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getUniqueNumber() {
        return UniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        UniqueNumber = uniqueNumber;
    }

    public void setIsOffline(String isOffline) {
        IsOffline = isOffline;
    }

    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

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
