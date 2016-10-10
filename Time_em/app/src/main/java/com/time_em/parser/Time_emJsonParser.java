package com.time_em.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import com.time_em.android.R;
import com.time_em.dashboard.HomeActivity;
import com.time_em.model.Company;
import com.time_em.model.MultipartDataModel;
import com.time_em.model.Notification;
import com.time_em.model.SpinnerData;
import com.time_em.model.SyncData;
import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.model.UserWorkSite;
import com.time_em.model.WorkSiteList;
import com.time_em.model.mutiUserworkSiteList;
import com.time_em.utils.PrefUtils;
import com.time_em.utils.Utils;

public class Time_emJsonParser {

	//todo classes
	public JSONObject jObject;
	public Context context;
	public boolean isError;
	//todo variables
	public String message;

	public Time_emJsonParser(Context context) {
		this.context = context;
	}

	public User getUserDetails(String webResponse, String method) {
		User user = new User();

		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("ReturnMessage");
			
			if(isError) {
				user = null;
				Utils.showToast(context,message);
			}
			else{
				user = parseJson(jObject, method);
				Utils.saveInSharedPrefs(context, "loginId", user.getLoginID());
				Utils.saveInSharedPrefs(context, "webResponse", webResponse);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			user = null;
			e.printStackTrace();
			Utils.showToast(context, Utils.Api_error);
		}

		//if(isError)
			//Utils.showToast(context, message);

		return user;
	}
	
	public ArrayList<User> getTeamList(String webResponse, String method) {
		ArrayList<User> teamList = new ArrayList<User>();
		String timeStamp="";
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");
			timeStamp = jObject.getString("TimeStamp");

			JSONArray teamArray = jObject.getJSONArray("AppUserViewModel");

			if(!isError) {
				for (int i = 0; i < teamArray.length(); i++) {
					User user = parseJson(teamArray.getJSONObject(i), method);
					teamList.add(user);
				}
				Utils.saveInSharedPrefs(context, Utils.getSharedPrefs(context,PrefUtils.KEY_USER_ID) +
						PrefUtils.getStringPreference(context,PrefUtils.KEY_COMPANY)+
						context.getResources().getString(R.string.teamTimeStampStr), timeStamp);
				if (message != null) {
					if (message.contains("No Record")) {
						Utils.showToast(context, "No Record Found");
					}
				}
			}else{
				Utils.showToast(context, message);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			teamList = new ArrayList<User>();
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		//if(isError)
			//Utils.showToast(context, message);

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
			try {
				user.setCompanyId(jObject.getInt("CompanyId"));
			}catch (Exception e)
			{
			e.printStackTrace();
			}
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
				try {
					user.setPin(jObject.getString("Pin"));
				}catch(Exception e)
				{e.printStackTrace();

				}

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
			//Utils.showToast(context, e.getMessage());
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
		//Utils.showToast(context, message);
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
				notification.setSenderId(taskObject.getString("Senderid"));
				notification.setNotificationType(taskObject.getString("NotificationTypeName"));
				notification.setAttachmentPath(taskObject.getString("AttachmentFullPath"));
				notification.setSubject(taskObject.getString("Subject"));
				notification.setMessage(taskObject.getString("Message"));
				notification.setCreatedDate(taskObject.getString("createdDate"));
				notification.setSenderFullName(taskObject.getString("SenderFullName"));
				notification.setCompanyId(taskObject.getString("CompanyId"));
				notification.setIsOffline("false");
				notification.setUniqueNumber("-1");
				notificationList.add(notification);
			}

			Utils.saveInSharedPrefs(context, Utils.getSharedPrefs(context,PrefUtils.KEY_USER_ID)+PrefUtils.getStringPreference(context,PrefUtils.KEY_COMPANY)+res.getString(R.string.notificationTimeStampStr), timeStamp);

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
			task.setCompanyId(taskObject.getString("CompanyId"));
			image=taskObject.getString("AttachmentImageFile");
			if(image!=null && !image.equalsIgnoreCase("null")) {
				task.setAttachmentImageFile(image);
			}
			 video=taskObject.getString("AttachmentVideoFile");
			if(video!=null && !video.equalsIgnoreCase("null")) {
				task.setAttachmentImageFile(video);
			}
			task.setIsoffline("false");
			task.setUniqueNumber("-1");
			//task.setSignedOutHours(0.0);
			taskList.add(task);
		}
		
		Utils.saveInSharedPrefs(context, userId+"-"+selectedDate+"-"+res.getString(R.string.taskTimeStampStr), timeStamp);
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			taskList = new ArrayList<TaskEntry>();
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		//if(isError)
			//Utils.showToast(context, message);

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
			//Utils.showToast(context, e.getMessage());
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

			JSONArray jArray = null;
			try {
				jArray=jObject.getJSONArray("activeuserlist");
			}catch(Exception e){
				e.printStackTrace();
			}


			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				User user = new User();
				user.setId(userObject.getInt("userid"));
				user.setFullName(userObject.getString("FullName"));
				user.setCompanyId(userObject.getInt("CompanyId"));
				userList.add(user);
			}

			Utils.saveInSharedPrefs(context,  Utils.getSharedPrefs(context,PrefUtils.KEY_USER_ID)
					+PrefUtils.getStringPreference(context,PrefUtils.KEY_COMPANY) + res.getString(R.string.activeUsersTimeStampStr), timeStamp);

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			//userList = new ArrayList<User>();
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		//if(isError)
		//	Utils.showToast(context, message);

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

						//todo save id to home screen
						String getSPrefsId = Utils.getSharedPrefs(context, PrefUtils.KEY_USER_ID);
						int getId=0;
						try{
							getId=Integer.parseInt(getSPrefsId);
						}catch (Exception e){
							e.printStackTrace();
						}
						if (id == getId) {
							if (methodName.equals(Utils.signInAPI)) {
								int ActivityId = taskObject.getInt("Id");
								if (isError)
									//HomeActivity.user.setSignedIn(false);
									PrefUtils.setBooleanPreference(context,PrefUtils.KEY_IS_SIGNED_IN,false);
								else {
									PrefUtils.setBooleanPreference(context,PrefUtils.KEY_IS_SIGNED_IN,true);
									//HomeActivity.user.setSignedIn(true);
									//HomeActivity.user.setActivityId(id);
									PrefUtils.setIntegerPreference(context,PrefUtils.KEY_ACTIVITY_ID,ActivityId);
								}
							} else {
								//signOutAt = jObject.getString("SignOutAt");
								if (isError) {
									//HomeActivity.user.setSignedIn(true);
									PrefUtils.setBooleanPreference(context,PrefUtils.KEY_IS_SIGNED_IN,true);
								}
								else {
									//HomeActivity.user.setSignedIn(false);
									//HomeActivity.user.setActivityId(0);
									PrefUtils.setBooleanPreference(context,PrefUtils.KEY_IS_SIGNED_IN,false);
									PrefUtils.setIntegerPreference(context,PrefUtils.KEY_ACTIVITY_ID,0);
								}
							}

							//Utils.saveInSharedPrefs(context, PrefUtils.KEY_IS_SIGNED_IN, String.valueOf(HomeActivity.user.isSignedIn()));
							//Utils.saveInSharedPrefs(context, PrefUtils.KEY_ACTIVITY_ID, String.valueOf(HomeActivity.user.getActivityId()));
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
	public ArrayList<SpinnerData> parseAssignedProjects(String webResponse,int companyID) {
		ArrayList<SpinnerData> taskList = new ArrayList<SpinnerData>();
		try {
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("IsError");
			message = jObject.getString("Message");

			SpinnerData task_header = new SpinnerData();
			task_header.setId(-1);
			task_header.setName("Select Project/Task");
			task_header.setCompanyId(companyID);
			taskList.add(task_header);

			task_header = new SpinnerData();
			task_header.setId(0);
			task_header.setName("Add New Task");
			task_header.setCompanyId(companyID);
			taskList.add(task_header);
			if(!isError) {
				JSONArray jArray = jObject.getJSONArray("ReturnKeyValueViewModel");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject taskObject = jArray.getJSONObject(i);
					SpinnerData task = new SpinnerData();
					task.setId(taskObject.getInt("TaskId"));
					task.setName(taskObject.getString("TaskName"));
					task.setCompanyId(taskObject.getInt("CompanyId"));
					taskList.add(task);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
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
			try {
				isError = jObject.getBoolean("IsError");
			}catch (Exception e){e.printStackTrace();}
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
			//Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return arrayTaskEntry;
	}
	public ArrayList<TaskEntry> parseGraphsSignInOut(String webResponse){
		ArrayList<TaskEntry> arrayTaskEntry = new ArrayList<TaskEntry>();
		try{
			jObject = new JSONObject(webResponse);
			try {
				isError = jObject.getBoolean("IsError");
			}catch (Exception e){e.printStackTrace();}
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
			//Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return arrayTaskEntry;
	}

	public SyncData getSynOfflineData(String webResponse){
		//ArrayList<SyncData> arraySyncData = new ArrayList<SyncData>();
		SyncData syncData=new SyncData();
		ArrayList<TaskEntry> arrayTaskEntry = new ArrayList<TaskEntry>();
		ArrayList<Notification> arrayNotification = new ArrayList<Notification>();

		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");



			JSONArray jArray = jObject.getJSONArray("UsersData");

			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				TaskEntry timerTask = new TaskEntry();
				timerTask.setId(userObject.getInt("Id"));
				timerTask.setUserId(userObject.getInt("UserId"));
				timerTask.setUniqueNumber(userObject.getString("UniqueNumber"));
				arrayTaskEntry.add(timerTask);
			}
			JSONArray nArray = jObject.getJSONArray("NotificationData");
			for(int j = 0; j<nArray.length(); j++){
				JSONObject userObject = nArray.getJSONObject(j);

				Notification notification = new Notification();
				notification.setNotificationId(userObject.getInt("Id"));
				notification.setUserId(userObject.getInt("UserId"));
				notification.setUniqueNumber(userObject.getString("UniqueNumber"));
				arrayNotification.add(notification);
			}
			syncData.setArray_taks(arrayTaskEntry);
			syncData.setArray_noitification(arrayNotification);
			//arraySyncData.add(syncData);

		}catch (Exception e) {
			// TODO Auto-generated catch block
			arrayTaskEntry = new ArrayList<TaskEntry>();
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return syncData;
	}
	/*[
	{
		"CreatedDate": "06-28-2016",
			"workSiteList": [
		{
			"WorkSiteId": 3004,
				"WorkSiteName": "OSBORNE PARK",
				"WorkingHour": 0.3
		},
		{
			"WorkSiteId": 3004,
				"WorkSiteName": "OSBORNE PARK",
				"WorkingHour": 0.23
		}
		]
	}
	]*/

	public ArrayList<UserWorkSite> getUserWorkSite(String webResponse){
		ArrayList<UserWorkSite> arrayUserWorkSite = new ArrayList<UserWorkSite>();
		ArrayList<WorkSiteList> arrayWorkSiteList= null;


		try{

			JSONArray mainArray = new JSONArray(webResponse);
			for(int j = 0; j<mainArray.length(); j++) {
				UserWorkSite userWorkSite=new UserWorkSite();
				jObject = mainArray.getJSONObject(j);
				//isError = jObject.getBoolean("isError");
				//message = jObject.getString("Message");
				arrayWorkSiteList= new ArrayList<WorkSiteList>();
				JSONArray jArray = jObject.getJSONArray("workSiteList");

				for (int i = 0; i < jArray.length(); i++) {

					JSONObject userObject = jArray.getJSONObject(i);
					WorkSiteList workSiteList = new WorkSiteList();
					workSiteList.setWorkSiteId(userObject.getString("WorkSiteId"));
					workSiteList.setWorkSiteName(userObject.getString("WorkSiteName"));
					workSiteList.setWorkingHour(userObject.getString("WorkingHour"));
					workSiteList.setTimeIn(userObject.getString("TimeIn"));
					workSiteList.setTimeOut(userObject.getString("TimeOut"));
					arrayWorkSiteList.add(workSiteList);
				}
				userWorkSite.setArraylist_WorkSiteList(arrayWorkSiteList);
				userWorkSite.setDate(jObject.getString("CreatedDate"));
				arrayUserWorkSite.add(userWorkSite);
			}

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);


		return arrayUserWorkSite;
	}

	public ArrayList<UserWorkSite> getUserListWorkSite(String webResponse){
		ArrayList<UserWorkSite> arrayUserWorkSite = new ArrayList<UserWorkSite>();
		ArrayList<mutiUserworkSiteList> multiUserarrayUserWorkSite= null;
		ArrayList<WorkSiteList> arrayWorkSiteList= null;

		try{

			JSONArray mainArray = new JSONArray(webResponse);
			for(int j = 0; j<mainArray.length(); j++) {
				UserWorkSite userWorkSite=new UserWorkSite();


				jObject = mainArray.getJSONObject(j);

				multiUserarrayUserWorkSite=new ArrayList<>();
				JSONArray jArray = jObject.getJSONArray("mutiUserworkSiteList");


				for (int i = 0; i < jArray.length(); i++) {



					JSONObject userObject = jArray.getJSONObject(i);

					JSONArray jArray2 = userObject.getJSONArray("workSiteList");

					arrayWorkSiteList= new ArrayList<WorkSiteList>();

					for(int k =0; k < jArray2.length();k++){
						JSONObject userObject2 = jArray2.getJSONObject(i);
						WorkSiteList workSiteList = new WorkSiteList();
						workSiteList.setWorkSiteId(userObject2.getString("WorkSiteId"));
						workSiteList.setWorkSiteName(userObject2.getString("WorkSiteName"));
						workSiteList.setWorkingHour(userObject2.getString("WorkingHour"));
						workSiteList.setTimeIn(userObject2.getString("TimeIn"));
						workSiteList.setTimeOut(userObject2.getString("TimeOut"));
						arrayWorkSiteList.add(workSiteList);

					}
					mutiUserworkSiteList userworkSiteList=new mutiUserworkSiteList();
					userworkSiteList.setDate(userObject.getString("CreatedDate"));
					userworkSiteList.setArraylist_WorkSiteList(arrayWorkSiteList);
					multiUserarrayUserWorkSite.add(userworkSiteList);

				}
				//userWorkSite.setArraylist_WorkSiteList(arrayWorkSiteList);
				userWorkSite.setArraylist_multiUserWorkSiteList(multiUserarrayUserWorkSite);
				userWorkSite.setUserId(jObject.getString("UserId"));
				//userWorkSite.setDate(jObject.getString("CreatedDate"));
				arrayUserWorkSite.add(userWorkSite);

			}

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Utils.showToast(context, e.getMessage());
		}

		if(isError)
			Utils.showToast(context, message);

		return arrayUserWorkSite;
	}


	public String getTaskId(String webResponse){
		String Id="0";
		try{
			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
			Id = jObject.getString("Id");


		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		if(!isError)
		{
			return Id;
		}else{
			return Id;
		}
	}
	public ArrayList<Company> parseCompanyList(String webResponse){
		ArrayList<Company> arrayTaskEntry = new ArrayList<Company>();

		Resources res = context.getResources();

		try{
			//jObject = new JSONObject(webResponse);
			//isError = jObject.getBoolean("IsError");
			//message = jObject.getString("Message");


			JSONArray jArray = new JSONArray(webResponse);

			for(int i = 0; i<jArray.length(); i++){
				JSONObject userObject = jArray.getJSONObject(i);

				Company timerTask = new Company();
				timerTask.setId(userObject.getString("Id"));
				timerTask.setKey(userObject.getString("Key"));
				timerTask.setValue(userObject.getString("Value"));
				timerTask.setStrKey(userObject.getString("strKey"));
				timerTask.setRefrenceCount(userObject.getString("RefrenceCount"));
				timerTask.setToken(userObject.getString("Token"));

				arrayTaskEntry.add(timerTask);
			}



		}catch (Exception e) {
			// TODO Auto-generated catch block
			arrayTaskEntry = new ArrayList<Company>();
			e.printStackTrace();

		}

		if(isError)
			Utils.showToast(context, message);

		return arrayTaskEntry;
	}
}
