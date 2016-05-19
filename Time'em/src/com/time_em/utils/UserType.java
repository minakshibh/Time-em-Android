package com.time_em.utils;

public enum UserType {
	User(1), Admin(2), SuperVisor(3);
	
	int value;
	
	UserType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
