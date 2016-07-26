package com.time_em.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    int id = 0, supervisorId = 0, userTypeId = 0, departmentId = 0, companyId = 0, workSiteId = 0, projectId = 0, activityId = 0, taskActivityId = 0;
    String loginID = "", signOutAt = "", signInAt = "", firstName = "", lastName = "", fullName = "", loginCode = "",
            supervisor = "", userType = "", department = "", company = "", worksite = "", project = "", isSecurityPin = "",
            nfcTagId = "", token = "",Email="",PhoneNumber="",pin="1234";
    boolean referenceCount = false, isSignedIn = false, isNightShift = false, isActive;
    Double signedHours = Double.valueOf(0);

    //,"Pin":null
    //,"SignInAt":"2016-05-09T10:15:14.367","SignOutAt":"2016-05-09T10:16:14.553","TaskActivityId":0,"SignedInHours":0.02,

    public User(){

    }
    public User(Parcel in) {
        id = in.readInt();
        supervisorId = in.readInt();
        userTypeId = in.readInt();
        departmentId = in.readInt();
        companyId = in.readInt();
        workSiteId = in.readInt();
        projectId = in.readInt();
        activityId = in.readInt();
        taskActivityId = in.readInt();
        loginID = in.readString();
        signOutAt = in.readString();
        signInAt = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        fullName = in.readString();
        loginCode = in.readString();
        supervisor = in.readString();
        userType = in.readString();
        department = in.readString();
        company = in.readString();
        worksite = in.readString();
        project = in.readString();
        isSecurityPin = in.readString();
        nfcTagId = in.readString();
        token = in.readString();
        referenceCount = in.readByte() != 0;
        isSignedIn = in.readByte() != 0;
        isNightShift = in.readByte() != 0;
        isActive = in.readByte() != 0;
        Email=in.readString();
        PhoneNumber=in.readString();
        pin=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getWorkSiteId() {
        return workSiteId;
    }

    public void setWorkSiteId(int workSiteId) {
        this.workSiteId = workSiteId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWorksite() {
        return worksite;
    }

    public void setWorksite(String worksite) {
        this.worksite = worksite;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getIsSecurityPin() {
        return isSecurityPin;
    }

    public void setIsSecurityPin(String isSecurityPin) {
        this.isSecurityPin = isSecurityPin;
    }

    public String getNfcTagId() {
        return nfcTagId;
    }

    public void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(boolean referenceCount) {
        this.referenceCount = referenceCount;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean isSignedIn) {
        this.isSignedIn = isSignedIn;
    }

    // extra parameters for "My Team" listing

    public int getTaskActivityId() {
        return taskActivityId;
    }

    public void setTaskActivityId(int taskActivityId) {
        this.taskActivityId = taskActivityId;
    }

    public String getSignOutAt() {
        return signOutAt;
    }

    public void setSignOutAt(String signOutAt) {
        this.signOutAt = signOutAt;
    }

    public String getSignInAt() {
        return signInAt;
    }

    public void setSignInAt(String signInAt) {
        this.signInAt = signInAt;
    }

    public Double getSignedHours() {
        return signedHours;
    }

    public void setSignedHours(Double signedHours) {
        this.signedHours = signedHours;
    }

    public boolean isNightShift() {
        return isNightShift;
    }

    public void setNightShift(boolean isNightShift) {
        this.isNightShift = isNightShift;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pinno) {
        pin = pinno;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(supervisorId);
        dest.writeInt(userTypeId);
        dest.writeInt(departmentId);
        dest.writeInt(companyId);
        dest.writeInt(workSiteId);
        dest.writeInt(projectId);
        dest.writeInt(activityId);
        dest.writeInt(taskActivityId);
        dest.writeString(loginID);
        dest.writeString(signOutAt);
        dest.writeString(signInAt);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(fullName);
        dest.writeString(loginCode);
        dest.writeString(supervisor);
        dest.writeString(userType);
        dest.writeString(department);
        dest.writeString(company);
        dest.writeString(worksite);
        dest.writeString(project);
        dest.writeString(isSecurityPin);
        dest.writeString(nfcTagId);
        dest.writeString(token);
        dest.writeByte((byte) (referenceCount ? 1 : 0));
        dest.writeByte((byte) (isSignedIn ? 1 : 0));
        dest.writeByte((byte) (isNightShift ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(Email);
        dest.writeString(PhoneNumber);
        dest.writeString(pin);
    }
}
