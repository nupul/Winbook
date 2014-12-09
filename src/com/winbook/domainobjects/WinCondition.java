package com.winbook.domainobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinCondition extends Postable{

	
	private String winCondition;
	private Timestamp timestamp;
	private String status;
	private List<Issue> issues;
	private List<Category> categories;
	private List<Agreement> agreements;
	private Wall wall;
	
	public WinCondition(User author, String winCondition)
	{
		super(author);
		this.winCondition = winCondition;
	}
	
	/*
	 * This constructor is used for simplicity. Only the id of the win condition is to be required but ALL the win condition's data is NOT INITIALIZED 
	 * and must be done so explicitly
	 */
	public WinCondition(int winConditionId)
	{
		super(winConditionId);
	}

	public String getWinCondition() {
		return winCondition;
	}

	public void setWinCondition(String winCondition) {
		this.winCondition = winCondition;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
	
	public void addIssue(Issue issue) 
	{
		if(issues==null)
			issues = new ArrayList<Issue>();
		
		issues.add(issue);
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void addCategory(Category category) {
		if(categories==null)
			categories = new ArrayList<Category>();
		
		categories.add(category);
	}
	
	
	public List<Agreement> getAgreements() {
		//return agreements==null ? Collections.<Agreement>emptyList(): agreements;
		return agreements;
	}

	public void setAgreements(List<Agreement> agreements) {
		this.agreements = agreements;
	}

	public void addAgreement(Agreement agreement) {
		if(agreements==null)
			agreements = new ArrayList<Agreement>();
		
		agreements.add(agreement);
	}
	
	public Wall getWall() {
		return wall;
	}

	public void setWall(Wall wall) {
		this.wall = wall;
	}
	
	
	
	
}
