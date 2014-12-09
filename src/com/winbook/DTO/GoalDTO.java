package com.winbook.DTO;

public class GoalDTO extends SuccessSliderDTO {
	private boolean isForPrioritization;
	private String details;
	
	public boolean isForPrioritization() {
		return isForPrioritization;
	}
	public void setForPrioritization(boolean forPrioritization) {
		this.isForPrioritization = forPrioritization;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	
}
