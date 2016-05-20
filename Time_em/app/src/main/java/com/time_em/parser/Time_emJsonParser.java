package com.time_em.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

import com.time_em.android.R;
import com.time_em.dashboard.HomeActivity;
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
			Utils.alertMessage(context, e.getMessage());
		}
//		Utils.alertMessage(context, message);

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
			Utils.alertMessage(context, e.getMessage());
		}

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
			Utils.alertMessage(context, e.getMessage());
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
	
	public ArrayList<TaskEntry> parseTaskList(String webResponse, int userId){
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
			
			taskList.add(task);
		}
		
		Utils.saveInSharedPrefs(context, userId+res.getString(R.string.taskTimeStampStr), timeStamp);
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			taskList = new ArrayList<TaskEntry>();
			e.printStackTrace();
			Utils.alertMessage(context, e.getMessage());
		}
		return taskList;
	}
	
	public Boolean parseChangeStatusResponse(String webResponse,
			String methodName) {
		int id = 0;
		String signInAt = "", signOutAt = "";
		try {

			jObject = new JSONObject(webResponse);
			isError = jObject.getBoolean("isError");
			message = jObject.getString("Message");
			id = jObject.getInt("UserId");
			signInAt = jObject.getString("SignInAt");

			if (methodName.equals(Utils.signInAPI)) {
				id = jObject.getInt("Id");
				if (isError)
					HomeActivity.user.setSignedIn(false);
				else {
					HomeActivity.user.setSignedIn(true);
					HomeActivity.user.setActivityId(id);
				}
			} else {
				signOutAt = jObject.getString("SignOutAt");
				if (isError)
					HomeActivity.user.setSignedIn(true);
				else {
					HomeActivity.user.setSignedIn(false);
					HomeActivity.user.setActivityId(0);
				}
			}

			Utils.saveInSharedPrefs(context, "isSignedIn", String.valueOf(HomeActivity.user.isSignedIn()));
			Utils.saveInSharedPrefs(context, "activityId", String.valueOf(HomeActivity.user.getActivityId()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.alertMessage(context, message + " activity id: " + id);

		return isError;
	}
}
