package com.winbook.domainobjects;

import java.sql.Timestamp;

public class Agreement extends Postable {

	private int priority;
	private int easeOfDevelopmentRating;
	private int businessValueRating;
	private Timestamp timestamp;
	private WinCondition winConditionCovered;
	private Option optionAgreedTo;
	
	public Agreement(User stakeholderInAgreement, WinCondition winConditionCovered)
	{
		this(stakeholderInAgreement, winConditionCovered,null);
	}
	
	public Agreement(User stakeholderInAgreement,WinCondition winConditionCovered, Option optionAgreedTo)
	{
		super(stakeholderInAgreement);
		
		if(optionAgreedTo!=null)
			this.optionAgreedTo = optionAgreedTo;
		
		this.winConditionCovered = winConditionCovered;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getEaseOfDevelopmentRating() {
		return easeOfDevelopmentRating;
	}

	public void setEaseOfDevelopmentRating(int easeOfDevelopmentRating) {
		this.easeOfDevelopmentRating = easeOfDevelopmentRating;
	}

	public int getBusinessValueRating() {
		return businessValueRating;
	}

	public void setBusinessValueRating(int businessValueRating) {
		this.businessValueRating = businessValueRating;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public WinCondition getWinConditionCovered() {
		return winConditionCovered;
	}

	public void setWinConditionCovered(WinCondition winConditionCovered) {
		this.winConditionCovered = winConditionCovered;
	}

	public Option getOptionAgreedTo() {
		return optionAgreedTo;
	}

	public void setOptionAgreedTo(Option optionAgreedTo) {
		this.optionAgreedTo = optionAgreedTo;
	}
	
	
}
