package com.winbook.domainobjects;

import java.sql.Timestamp;

public class UserConfiguration {

	private boolean receiveEmailPerDay;
	private boolean receiveEmailOnEachUpdate;
	private Timestamp lastLogin;
	private boolean isShaper;
	
	public UserConfiguration()
	{
		receiveEmailOnEachUpdate = true;
		receiveEmailPerDay = false;
		isShaper = false;
	}
	
	public boolean isReceiveEmailPerDay() {
		return receiveEmailPerDay;
	}
	public void setReceiveEmailPerDay(boolean receiveEmailPerDay) {
		this.receiveEmailPerDay = receiveEmailPerDay;
		receiveEmailOnEachUpdate = !receiveEmailPerDay;
	}
	public boolean isReceiveEmailOnEachUpdate() {
		return receiveEmailOnEachUpdate;
	}
	public void setReceiveEmailOnEachUpdate(boolean receiveEmailOnEachUpdate) {
		this.receiveEmailOnEachUpdate = receiveEmailOnEachUpdate;
		receiveEmailPerDay = !receiveEmailOnEachUpdate;
	}
	public Timestamp getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}
	public boolean isShaper() {
		return isShaper;
	}
	public void setShaper(boolean isShaper) {
		this.isShaper = isShaper;
	}
	
	
	
}
