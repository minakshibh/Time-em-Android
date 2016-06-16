package com.time_em.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

import com.time_em.android.R;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.Notification;
import com.time_em.model.SpinnerData;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.utils.Utils;

public class Time_emJsonParser {

	public JSONObject jObject;
	public Context context;
	public boolean isError;
	public String message;

	public Time_emJsonParser(Context context) {
		this.context = context;
	}

	public User getUserDetails(String webResponse, String method) {
		User user = new User();

		try {
			jObject = new JSONObject(webResponse);
			user = parseJson(jObject, method);

			isError = jObject.getBoolean("isError");
			message = jObject.getString("ReturnMessage");
			
			if(isError)
				user = null;
			else{
				Utils.saveInSharedPrefs(context, "loginId", user.getLoginID());
				Utils.saveInSharedPrefs(context, "webResponse", webResponse);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			user = null;
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return user;
	}
	
	public ArrayList<User> getTeamList(String webResponse, String method) {
		ArrayList<User> teamList = new ArrayList<User>();
//		{"KeyValueViewModel":null,
//		"ReturnKeyValueViewModel":null,
//		"UserTaskActivityViewModel":null,
//		"AppUserViewModel":
//			[{"Id":10,
//			"LoginId":"mark","FirstName":"Mark","LastName":"Petersen","FullName":"Mark Petersen",
//			"LoginCode":"1006","SupervisorId":2,"UserTypeId":3,"Department":null,"DepartmentId":0,
//			"Company":null,"CompanyId":2,"Worksite":null,"WorksiteId":0,"Project":null,
//			"ProjectId":0,"IsSecurityPin":null,"SignInAt":null,"SignOutAt":null,"ActivityId":0,
//			"TaskActivityId":0,"SignedInHours":0.0,"NFCTagId":null,"IsNightShift":false,
//			"IsSignedIn":false}
		String timeStamp="";
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");
			timeStamp = jObject.getString("TimeStamp");
			
			JSONArray teamArray = jObject.getJSONArray("AppUserViewModel");
			
			for(int i = 0; i<teamArray.length(); i++){
				User user = parseJson(teamArray.getJSONObject(i), method);
				teamList.add(user);
			}
			Utils.saveInSharedPrefs(context, HomeActivity.user.getId()+context.getResources().getString(R.string.teamTimeStampStr), timeStamp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			teamList = new ArrayList<User>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return teamList;
	}

	public User parseJson(JSONObject jObject, String methodName) {

		User user = new User();
		try {
			user.setId(jObject.getInt("Id"));
			user.setLoginID(jObject.getString("LoginId"));
			user.setFirstName(jObject.getString("FirstName"));
			user.setLastName(jObject.getString("LastName"));
			user.setFullName(jObject.getString("FullName"));
			user.setLoginCode(jObject.getString("LoginCode"));
			user.setSupervisorId(jObject.getInt("SupervisorId"));
			user.setUserTypeId(jObject.getInt("UserTypeId"));
			user.setDepartmentId(jObject.getInt("DepartmentId"));
			user.setCompany(jObject.getString("Company"));
			user.setCompanyId(jObject.getInt("CompanyId"));
			user.setWorksite(jObject.getString("Worksite"));
			user.setWorkSiteId(jObject.getInt("WorksiteId"));
			user.setProject(jObject.getString("Project"));
			user.setProjectId(jObject.getInt("ProjectId"));
			user.setIsSecurityPin(jObject.getString("IsSecurityPin"));
			user.setNfcTagId(jObject.getString("NFCTagId"));
			user.setActivityId(jObject.getInt("ActivityId"));
			
			if(methodName.equals(Utils.loginAPI) || methodName.equals((Utils.pinAuthenticationAPI))){
				user.setSupervisor(jObject.getString("Supervisor"));
				user.setUserType(jObject.getString("UserType"));
				user.setDepartment(jObject.getString("Department"));
				user.setReferenceCount(jObject.getBoolean("RefrenceCount"));
				user.setToken(jObject.getString("Token"));
				user.setSignedIn(jObject.getBoolean("IsSignIn"));
				user.setEmail(jObject.getString("Email"));
				user.setPhoneNumber(jObject.getString("PhoneNumber"));

			}else{
				user.setActive(jObject.getBoolean("isActive"));
				user.setSignInAt(jObject.getString("SignInAt"));
				user.setSignOutAt(jObject.getString("SignOutAt"));
				user.setTaskActivityId(jObject.getInt("TaskActivityId"));
				user.setSignedHours(jObject.getDouble("SignedInHours"));
				user.setSignedIn(jObject.getBoolean("IsSignedIn"));
				user.setNightShift(jObject.getBoolean("IsNightShift"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			user = new User();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		return user;
	}

	public Boolean authorizePIN(String webResponse){
		try{
			jObject = new JSONObject(webResponse);
//			{"isError":false,"isAuthorised":true}
			if(jObject.getBoolean("isAuthorised"))
				return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public boolean parseDeleteTaskResponse(String webResponse){
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
		}catch (Exception e){
			e.printStackTrace();
		}
		Utils.showToast(context, message);
		return isError;
	}

	public ArrayList<Notification> parseNotificationList(String webResponse){

		String timeStamp="";
		ArrayList<Notification> notificationList = new ArrayList<Notification>();
		Resources res = context.getResources();
		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");
			timeStamp = jObject.getString("TimeStamp");

			JSONArray jArray = jObject.getJSONArray("notificationslist");
			for(int i = 0; i<jArray.length(); i++){
				JSONObject taskObject = jArray.getJSONObject(i);
				Notification notification = new Notification();
				notification.setNotificationId(taskObject.getInt("NotificationId"));
				notification.setSenderId(taskObject.getInt("Senderid"));
				notification.setNotificationType(taskObject.getString("NotificationTypeName"));
				notification.setAttachmentPath(taskObject.getString("AttachmentFullPath"));
				notification.setSubject(taskObject.getString("Subject"));
				notification.setMessage(taskObject.getString("Message"));
				notification.setCreatedDate(taskObject.getString("createdDate"));
				notification.setSenderFullName(taskObject.getString("SenderFullName"));

				notificationList.add(notification);
			}

			Utils.saveInSharedPrefs(context, HomeActivity.user.getId()+res.getString(R.string.notificationTimeStampStr), timeStamp);

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			notificationList = new ArrayList<Notification>();
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		//if(isError)
			//Utils.showToast(context, message);

		return notificationList;
	}

	public ArrayList<TaskEntry> parseTaskList(String webResponse, int userId, String selectedDate){
		String image=null,video=null;
		String timeStamp="";
		ArrayList<TaskEntry> taskList = new ArrayList<TaskEntry>();
		Resources res = context.getResources();
		try{
		jObject = new JSONObject(webResponse);
		isError = jObject.getBoolean("IsError");
		message = jObject.getString("Message");
		timeStamp = jObject.getString("TimeStamp");
		
		JSONArray jArray = jObject.getJSONArray("UserTaskActivityViewModel");
		for(int i = 0; i<jArray.length(); i++){
			JSONObject taskObject = jArray.getJSONObject(i);
			TaskEntry task = new TaskEntry();
			task.setId(taskObject.getInt("Id"));
			task.setActivityId(taskObject.getInt("ActivityId"));
			task.setTaskId(taskObject.getInt("TaskId"));
			task.setUserId(taskObject.getInt("UserId"));
			task.setTaskName(taskObject.getString("TaskName"));
			task.setTimeSpent(taskObject.getDouble("TimeSpent"));
			task.setComments(taskObject.getString("Comments"));
			task.setSignedInHours(taskObject.getDouble("SignedInHours"));
			task.setStartTime(taskObject.getString("StartTime"));
			task.setCreatedDate(taskObject.getString("CreatedDate"));
			task.setEndTime(taskObject.getString("EndTime"));
			task.setSelectedDate(taskObject.getString("SelectedDate"));
			task.setToken(taskObject.getString("Token"));
			task.setIsActive(taskObject.getBoolean("isActive"));
			image=taskObject.getString("AttachmentImageFile");
			if(image!=null && !image.equalsIgnoreCase("null")) {
				task.setAttachmentImageFile(image);
			}
			 video=taskObject.getString("AttachmentVideoFile");
			if(video!=null && !video.equalsIgnoreCase("null")) {
				task.setAttachmentImageFile(video);
			}
			taskList.add(task);
		}
		
		Utils.saveInSharedPrefs(context, userId+"-"+selectedDate+"-"+res.getString(R.string.taskTimeStampStr), timeStamp);
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			taskList = new ArrayList<TaskEntry>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return taskList;
	}
	public ArrayList<SpinnerData> parseNotificationType(String webResponse){

		ArrayList<SpinnerData> notificationTypeList = new ArrayList<SpinnerData>();

		SpinnerData notificationTypeHeader = new SpinnerData();
		notificationTypeHeader.setId(0);
		notificationTypeHeader.setName("Select Notification Type");
		notificationTypeList.add(notificationTypeHeader);
		try{
			JSONArray jArray = new JSONArray(webResponse);

			for(int i = 0; i<jArray.length(); i++){
				JSONObject notificationObject = jArray.getJSONObject(i);

				SpinnerData notificationType = new SpinnerData();
				notificationType.setId(notificationObject.getInt("id"));
				notificationType.setName(notificationObject.getString("Name"));

				notificationTypeList.add(notificationType);
			}

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			notificationTypeList = new ArrayList<SpinnerData>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		return notificationTypeList;
	}

	public ArrayList<User> parseActiveUsers(String webResponse){
		ArrayList<User> userList = new ArrayList<User>();
		String timeStamp="";
		Resources res = context.getResources();

		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");
			timeStamp = jObject.getString("TimeStamp");

			JSONArray jArray = jObject.getJSONArray("activeuserlist");

			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				User user = new User();
				user.setId(userObject.getInt("userid"));
				user.setFullName(userObject.getString("FullName"));

				userList.add(user);
			}

			Utils.saveInSharedPrefs(context, HomeActivity.user.getId() + res.getString(R.string.activeUsersTimeStampStr), timeStamp);

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			userList = new ArrayList<User>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return userList;
	}

	/*public Boolean parseChangeStatusResponse(String webResponse,
			String methodName) {
		int id = 0;
		String signInAt = "", signOutAt = "";
=======
	*/
	public ArrayList<User> parseSignInChangeStatusResponse(String webResponse,String methodName) {
		int id = 0, activeId=0;
		String signInAt = "";
		ArrayList<User> array_user = new ArrayList<User>();
		JSONArray jArray=null;
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
			if(!isError) {
				if (!message.contains("User already")) {
					jArray = jObject.getJSONArray("SignedinUser");

					for (int i = 0; i < jArray.length(); i++) {

						User user = new User();
						JSONObject taskObject = jArray.getJSONObject(i);

						activeId = taskObject.getInt("Id");
						user.setActivityId(activeId);

						id = taskObject.getInt("UserId");
						user.setId(id);

						signInAt = taskObject.getString("SignInAt");
						user.setSignInAt(signInAt);

						array_user.add(user);

						///save id to home screen
						if (id == HomeActivity.user.getId()) {
							if (methodName.equals(Utils.signInAPI)) {
								id = taskObject.getInt("Id");
								if (isError)
									HomeActivity.user.setSignedIn(false);
								else {
									HomeActivity.user.setSignedIn(true);
									HomeActivity.user.setActivityId(id);
								}
							} else {
								//signOutAt = jObject.getString("SignOutAt");
								if (isError)
									HomeActivity.user.setSignedIn(true);
								else {
									HomeActivity.user.setSignedIn(false);
									HomeActivity.user.setActivityId(0);
								}
							}

							Utils.saveInSharedPrefs(context, "isSignedIn", String.valueOf(HomeActivity.user.isSignedIn()));
							Utils.saveInSharedPrefs(context, "activityId", String.valueOf(HomeActivity.user.getActivityId()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.showToast(context, message);

		return array_user;
	}
	public ArrayList<User> parseSignOutChangeStatusResponse(String webResponse,String methodName) {
		int id = 0, activeId=0;
		String  signOutAt = "",SignInAt="";
		ArrayList<User> array_user = new ArrayList<User>();
		JSONArray jArray=null;
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
			if(!isError) {

				if (!message.contains("User already")) {
					jArray = jObject.getJSONArray("SignedOutUser");

					for (int i = 0; i < jArray.length(); i++) {

						User user = new User();
						JSONObject taskObject = jArray.getJSONObject(i);

					//	activeId = taskObject.getInt("Id");
					//	user.setActivityId(activeId);

						id = taskObject.getInt("UserId");
						user.setId(id);

						SignInAt = taskObject.getString("SignInAt");
						user.setSignInAt(SignInAt);

						signOutAt = taskObject.getString("SignOutAt");
						user.setSignInAt(signOutAt);

						array_user.add(user);

					/*	///save id to home screen
						if (id == HomeActivity.user.getId()) {
							if (methodName.equals(Utils.signInAPI)) {
								id = taskObject.getInt("Id");
								if (isError)
									HomeActivity.user.setSignedIn(false);
								else {
									HomeActivity.user.setSignedIn(true);
									HomeActivity.user.setActivityId(id);
								}
							} else {
								//signOutAt = jObject.getString("SignOutAt");
								if (isError)
									HomeActivity.user.setSignedIn(true);
								else {
									HomeActivity.user.setSignedIn(false);
									HomeActivity.user.setActivityId(0);
								}
							}

							Utils.saveInSharedPrefs(context, "isSignedIn", String.valueOf(HomeActivity.user.isSignedIn()));
							Utils.saveInSharedPrefs(context, "activityId", String.valueOf(HomeActivity.user.getActivityId()));
						}*/
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.showToast(context, message);

		return array_user;
	}
	public ArrayList<SpinnerData> parseAssignedProjects(String webResponse) {
		ArrayList<SpinnerData> taskList = new ArrayList<SpinnerData>();
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");

			if(!isError) {
				JSONArray jArray = jObject.getJSONArray("ReturnKeyValueViewModel");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject taskObject = jArray.getJSONObject(i);
					SpinnerData task = new SpinnerData();
					task.setId(taskObject.getInt("TaskId"));
					task.setName(taskObject.getString("TaskName"));
					taskList.add(task);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return taskList;
	}
	public ArrayList<TaskEntry> parseGraphsData(String webResponse){
		ArrayList<TaskEntry> arrayTaskEntry = new ArrayList<TaskEntry>();

		Resources res = context.getResources();

		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");


			JSONArray jArray = jObject.getJSONArray("TasksList");

			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				TaskEntry timerTask = new TaskEntry();
				timerTask.setTimeSpent(userObject.getDouble("timespent"));
				SimpleDateFormat dateFormatter = new SimpleDateFormat("dd");
				String str_date=userObject.getString("date");
				//String str_date="11-June-07";
				DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
				Date date = formatter.parse(str_date);
				timerTask.setCreatedDate(dateFormatter.format(date));
				//timerTask.setCreatedDate();
				arrayTaskEntry.add(timerTask);
			}



		}catch (Exception e) {
			// TODO Auto-generated catch block
			arrayTaskEntry = new ArrayList<TaskEntry>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return arrayTaskEntry;
	}
	public ArrayList<TaskEntry> parseGraphsSignInOut(String webResponse){
		ArrayList<TaskEntry> arrayTaskEntry = new ArrayList<TaskEntry>();

		Resources res = context.getResources();

		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");


			JSONArray jArray = jObject.getJSONArray("UsersList");

			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				TaskEntry timerTask = new TaskEntry();
				timerTask.setSignedInHours(userObject.getDouble("signedin"));
				timerTask.setSignedOutHours(userObject.getDouble("signedout"));
				SimpleDateFormat dateFormatter = new SimpleDateFormat("dd");
				String str_date=userObject.getString("date");
				//String str_date="11-June-07";
				DateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
				Date date = formatter.parse(str_date);
				timerTask.setCreatedDate(dateFormatter.format(date));
				//timerTask.setCreatedDate();
				arrayTaskEntry.add(timerTask);
			}



		}catch (Exception e) {
			// TODO Auto-generated catch block
			arrayTaskEntry = new ArrayList<TaskEntry>();
			e.printStackTrace();
			Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return arrayTaskEntry;
	}

}
