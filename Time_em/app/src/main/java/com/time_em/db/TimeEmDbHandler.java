package com.time_em.db;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.time_em.model.TaskEntry;
import com.time_em.model.User;
import com.time_em.team.UserListActivity.TeamAdapter;

public class TimeEmDbHandler extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	@SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.time_em.android/databases/";

	private static String DB_NAME = "TimeEm_db.sqlite";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	private String TABLE_TASK = "Task";
	private String TABLE_TEAM = "Team";

	// fields for task table
	private String Id = "Id";
	private String ActivityId = "ActivityId";
	private String TaskId = "TaskId";
	private String UserId = "UserId";
	private String TaskName = "TaskName";
	private String Comments = "Comments";
	private String StartTime = "StartTime";
	private String CreatedDate = "CreatedDate";
	private String EndTime = "EndTime";
	private String SelectedDate = "SelectedDate";
	private String Token = "Token";
	private String TimeSpent = "TimeSpent";
	private String SignedInHours = "SignedInHours";

	// fields for user table
	// private String UserId = "UserId";
	private String SupervisorId = "SupervisorId";
	private String UserTypeId = "UserTypeId";
	private String DepartmentId = "DepartmentId";
	private String CompanyId = "CompanyId";
	private String WorkSiteId = "WorkSiteId";
	private String ProjectId = "ProjectId";
	// private String ActivityId = "ActivityId";
	private String TaskActivityId = "TaskActivityId";
	private String LoginID = "LoginID";
	private String SignOutAt = "SignOutAt";
	private String SignInAt = "SignInAt";
	private String FirstName = "FirstName";
	private String LastName = "LastName";
	private String FullName = "FullName";
	private String LoginCode = "LoginCode";
	private String Supervisor = "Supervisor";
	private String UserType = "UserType";
	private String Department = "Department";
	private String Company = "Company";
	private String Worksite = "Worksite";
	private String Project = "Project";
	private String IsSecurityPin = "IsSecurityPin";
	private String NfcTagId = "NfcTagId";
	// private String Token="Token";
	private String ReferenceCount = "ReferenceCount";
	private String IsSignedIn = "IsSignedIn";
	private String IsNightShift = "IsNightShift";
	private String SignedHours = "SignedHours";

	SQLiteCursor cursor;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */

	public TimeEmDbHandler(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TASK_TABLE = "CREATE TABLE if NOT Exists " + TABLE_TASK
				+ "(" + Id + " TEXT," + ActivityId + " TEXT," + TaskId
				+ " TEXT," + UserId + " TEXT," + TaskName + " TEXT," + Comments
				+ " TEXT," + StartTime + " TEXT," + CreatedDate + " TEXT,"
				+ EndTime + " TEXT," + SelectedDate + " TEXT," + Token
				+ " TEXT," + TimeSpent + " TEXT," + SignedInHours + " TEXT)";

		String CREATE_USER_TABLE = "CREATE TABLE if NOT Exists " + TABLE_TEAM
				+ "(" + UserId + " TEXT," + SupervisorId + " TEXT,"
				+ UserTypeId + " TEXT," + DepartmentId + " TEXT," + CompanyId
				+ " TEXT," + WorkSiteId + " TEXT," + ProjectId + " TEXT,"
				+ ActivityId + " TEXT," + TaskActivityId + " TEXT," + LoginID
				+ " TEXT," + SignOutAt + " TEXT," + SignInAt + " TEXT,"
				+ FirstName + " TEXT," + LastName + " TEXT," + FullName
				+ " TEXT," + LoginCode + " TEXT," + Supervisor + " TEXT,"
				+ UserType + " TEXT," + Department + " TEXT," + Company
				+ " TEXT," + Worksite + " TEXT," + Project + " TEXT,"
				+ IsSecurityPin + " TEXT," + NfcTagId + " TEXT," + Token
				+ " TEXT," + ReferenceCount + " TEXT," + IsSignedIn + " TEXT,"
				+ IsNightShift + " TEXT," + SignedHours + " TEXT)";

		db.execSQL(CREATE_TASK_TABLE);
		db.execSQL(CREATE_USER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void deleteTaskTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TASK, null, null);
		db.close();
	}

	public void deleteTeamTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TASK, null, null);
		db.close();
	}

	public void updateTask(ArrayList<TaskEntry> taskList) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (int i = 0; i < taskList.size(); i++) {
			TaskEntry taskEntry = taskList.get(i);
			String selectQuery = "SELECT  * FROM " + TABLE_TASK + " where "
					+ Id + "=" + taskEntry.getId();
			try {
				ContentValues values = new ContentValues();
				values.put(Id, String.valueOf(taskEntry.getId()));
				values.put(ActivityId,
						String.valueOf(taskEntry.getActivityId()));
				values.put(TaskId, String.valueOf(taskEntry.getTaskId()));
				values.put(UserId, String.valueOf(taskEntry.getUserId()));
				values.put(TaskName, taskEntry.getTaskName());
				values.put(Comments, taskEntry.getComments());
				values.put(StartTime, taskEntry.getStartTime());
				values.put(CreatedDate, taskEntry.getCreatedDate());
				values.put(EndTime, taskEntry.getEndTime());
				values.put(SelectedDate, taskEntry.getSelectedDate());
				values.put(Token, taskEntry.getToken());
				values.put(TimeSpent, String.valueOf(taskEntry.getTimeSpent()));
				values.put(SignedInHours,
						String.valueOf(taskEntry.getSignedInHours()));

				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					// updating row
					if(!taskEntry.getIsActive())
						db.delete(TABLE_TASK, Id + " = ?",new String[] { String.valueOf(taskEntry.getId()) });
					else
						db.update(TABLE_TASK, values, Id + " = ?",new String[] { String.valueOf(taskEntry.getId()) });
				} else {
					if(taskEntry.getIsActive())
						db.insert(TABLE_TASK, null, values);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}

	public void updateTeam(ArrayList<User> team) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (int i = 0; i < team.size(); i++) {
			User user = team.get(i);
			String selectQuery = "SELECT  * FROM " + TABLE_TEAM + " where "
					+ UserId + "=" + user.getId();
			try {
				ContentValues values = new ContentValues();

				values.put(UserId, user.getId());
				values.put(SupervisorId, String.valueOf(user.getSupervisorId()));
				values.put(UserTypeId, String.valueOf(user.getUserTypeId()));
				values.put(DepartmentId, String.valueOf(user.getDepartmentId()));
				values.put(CompanyId, String.valueOf(user.getCompanyId()));
				values.put(WorkSiteId, String.valueOf(user.getWorkSiteId()));
				values.put(ProjectId, String.valueOf(user.getProjectId()));
				values.put(ActivityId, String.valueOf(user.getActivityId()));
				values.put(TaskActivityId,
						String.valueOf(user.getTaskActivityId()));
				values.put(LoginID, user.getLoginID());
				values.put(SignOutAt, user.getSignOutAt());
				values.put(SignInAt, user.getSignInAt());
				values.put(FirstName, user.getFirstName());
				values.put(LastName, user.getLastName());
				values.put(FullName, user.getFullName());
				values.put(LoginCode, user.getLoginCode());
				values.put(Supervisor, user.getSupervisor());
				values.put(UserType, user.getUserType());
				values.put(Department, user.getDepartment());
				values.put(Company, user.getCompany());
				values.put(Worksite, user.getWorksite());
				values.put(Project, user.getProject());
				values.put(IsSecurityPin, user.getIsSecurityPin());
				values.put(NfcTagId, user.getNfcTagId());
				values.put(Token, user.getToken());
				values.put(ReferenceCount,
						String.valueOf(user.isReferenceCount()));
				values.put(IsSignedIn, String.valueOf(user.isSignedIn()));
				values.put(IsNightShift, String.valueOf(user.isNightShift()));
				values.put(SignedHours, String.valueOf(user.getSignedHours()));

				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					if(!user.isActive())
						db.delete(TABLE_TEAM, UserId + " = ?", new String[] { String.valueOf(user.getId()) });
					else
						db.update(TABLE_TEAM, values, UserId + " = ?", new String[] { String.valueOf(user.getId()) });
				} else {
					if(user.isActive())
						db.insert(TABLE_TEAM, null, values);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
	}

	/*
	 * public void markCheckPointAsChecked(String checkPointId, String
	 * reportedTime, SQLiteDatabase db) { // SQLiteDatabase db =
	 * this.getWritableDatabase();
	 * 
	 * try{ ContentValues values = new ContentValues(); values.put(CheckedTime,
	 * reportedTime); values.put(isChecked, "true");
	 * 
	 * 
	 * int a=db.update(TABLE_SCHEDULE, values, CheckPoint_Id + " = ?", new
	 * String[] { String.valueOf(checkPointId) });
	 * 
	 * }catch(Exception e){ e.printStackTrace(); } // db.close(); }
	 */

	public ArrayList<TaskEntry> getTaskEnteries(int userId) {
		ArrayList<TaskEntry> taskEntryList = new ArrayList<TaskEntry>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TASK + " where "
				+ UserId + "=" + userId;
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					TaskEntry taskEntry = new TaskEntry();
					taskEntry.setId(Integer.valueOf(cursor.getString(cursor
							.getColumnIndex(Id))));
					taskEntry.setActivityId(Integer.valueOf(cursor
							.getString(cursor.getColumnIndex(ActivityId))));
					taskEntry.setTaskId(Integer.valueOf(cursor.getString(cursor
							.getColumnIndex(TaskId))));
					taskEntry.setUserId(Integer.valueOf(cursor.getString(cursor
							.getColumnIndex(UserId))));
					taskEntry.setTaskName(cursor.getString(cursor
							.getColumnIndex(TaskName)));
					taskEntry.setComments(cursor.getString(cursor
							.getColumnIndex(Comments)));
					taskEntry.setStartTime(cursor.getString(cursor
							.getColumnIndex(StartTime)));
					taskEntry.setCreatedDate(cursor.getString(cursor
							.getColumnIndex(CreatedDate)));
					taskEntry.setEndTime(cursor.getString(cursor
							.getColumnIndex(EndTime)));
					taskEntry.setSelectedDate(cursor.getString(cursor
							.getColumnIndex(SelectedDate)));
					taskEntry.setToken(cursor.getString(cursor
							.getColumnIndex(Token)));
					taskEntry.setTimeSpent(Double.valueOf(cursor
							.getString(cursor.getColumnIndex(TimeSpent))));
					taskEntry.setSignedInHours(Double.valueOf(cursor
							.getString(cursor.getColumnIndex(SignedInHours))));

					taskEntryList.add(taskEntry);
				} while (cursor.moveToNext());
			}

			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return taskEntryList;
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				cursor.getWindow().clear();
				cursor.close();
			}

			db.close();
			return taskEntryList;
		}
	}

	public ArrayList<User> getTeam() {
		ArrayList<User> team = new ArrayList<User>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_TEAM;
		SQLiteDatabase db = this.getReadableDatabase();

		try {
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					User user = new User();

					user.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(UserId))));
					user.setSupervisorId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(SupervisorId))));
					user.setUserTypeId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(UserTypeId))));
					user.setDepartmentId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DepartmentId))));
					user.setCompanyId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(CompanyId))));
					user.setWorkSiteId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(WorkSiteId))));
					user.setProjectId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ProjectId))));
					user.setActivityId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(ActivityId))));
					user.setTaskActivityId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(TaskActivityId))));
					user.setLoginID(cursor.getString(cursor.getColumnIndex(LoginID)));
					user.setSignOutAt(cursor.getString(cursor.getColumnIndex(SignOutAt)));
					user.setSignInAt(cursor.getString(cursor.getColumnIndex(SignInAt)));
					user.setFirstName(cursor.getString(cursor.getColumnIndex(FirstName)));
					user.setLastName(cursor.getString(cursor.getColumnIndex(LastName)));
					user.setFullName(cursor.getString(cursor.getColumnIndex(FullName)));
					user.setLoginCode(cursor.getString(cursor.getColumnIndex(LoginCode)));
					user.setSupervisor(cursor.getString(cursor.getColumnIndex(Supervisor)));
					user.setUserType(cursor.getString(cursor.getColumnIndex(UserType)));
					user.setDepartment(cursor.getString(cursor.getColumnIndex(Department)));
					user.setCompany(cursor.getString(cursor.getColumnIndex(Company)));
					user.setWorksite(cursor.getString(cursor.getColumnIndex(Worksite)));
					user.setProject(cursor.getString(cursor.getColumnIndex(Project)));
					user.setIsSecurityPin(cursor.getString(cursor.getColumnIndex(IsSecurityPin)));
					user.setNfcTagId(cursor.getString(cursor.getColumnIndex(NfcTagId)));
					user.setToken(cursor.getString(cursor.getColumnIndex(Token)));
					user.setReferenceCount(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ReferenceCount))));
					user.setSignedIn(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IsSignedIn))));
					user.setNightShift(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IsNightShift))));
					user.setSignedHours(Double.valueOf(cursor.getString(cursor.getColumnIndex(SignedHours))));

					team.add(user);
				} while (cursor.moveToNext());
			}

			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return team;
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				cursor.getWindow().clear();
				cursor.close();
			}

			db.close();
			return team;
		}
	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

}