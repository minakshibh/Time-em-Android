package com.time_em.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskEntry implements Parcelable{
	
	private int id, activityId, taskId, userId;
	private String taskName, comments, startTime, createdDate, endTime, selectedDate, token;
	private Double timeSpent, signedInHours;
	private Boolean isActive = true;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSelectedDate() {
		return selectedDate;
	}
	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Double getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Double timeSpent) {
		this.timeSpent = timeSpent;
	}
	public Double getSignedInHours() {
		return signedInHours;
	}
	public void setSignedInHours(Double signedInHours) {
		this.signedInHours = signedInHours;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public TaskEntry(){

	}
	public TaskEntry(Parcel source) {
		id = source.readInt();
		activityId = source.readInt();
		taskId = source.readInt();
		userId = source.readInt();
		taskName = source.readString();
		comments = source.readString();
		startTime = source.readString();
		createdDate = source.readString();
		endTime = source.readString();
		selectedDate = source.readString();
		token = source.readString();
		timeSpent = source.readDouble();
		signedInHours = source.readDouble();
		isActive = source.readByte() != 0;
	}

	public static final Parcelable.Creator<TaskEntry> CREATOR
			= new Parcelable.Creator<TaskEntry>()
	{
		public TaskEntry createFromParcel(Parcel in)
		{
			return new TaskEntry(in);
		}

		public TaskEntry[] newArray (int size)
		{
			return new TaskEntry[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(activityId);
		dest.writeInt(taskId);
		dest.writeInt(userId);
		dest.writeString(taskName);
		dest.writeString(comments);
		dest.writeString(startTime);
		dest.writeString(createdDate);
		dest.writeString(endTime);
		dest.writeString(selectedDate);
		dest.writeString(token);
		dest.writeDouble(timeSpent);
		dest.writeDouble(signedInHours);
		dest.writeByte((byte) (isActive ? 1 : 0));
	}
}
