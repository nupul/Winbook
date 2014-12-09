package com.winbook.domainobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Issue extends Postable{

	
	private String status;
	private String issue;
	private Timestamp timestamp;
	private WinCondition winCondition;		
	private List<Option> options;
	
	/*
	 * Initialize issue with win condition object
	 */
	public Issue(User author, WinCondition wincondtion, String issue)
	{
		super(author);
		this.winCondition = wincondtion;
		this.issue = issue;
	}

	/*
	 * Initialize issue with ONLY id of corresponding win condition
	 */
	public Issue(User author, int winConditionId, String issue)
	{
		super(author);
		winCondition = new WinCondition(winConditionId);
		this.issue = issue;
		
	}
	
	/*
	 * Create an issue reference ONLY initialized by it's id
	 */
	public Issue(int issueId)
	{
		super(issueId);
	}
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIssue() {
		return issue;
	}
	
	public void setIssue(String issue) {
		this.issue = issue;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public WinCondition getWinCondition() {
		return winCondition;
	}

	public void setWinCondition(WinCondition winCondition) {
		this.winCondition = winCondition;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public void addOption(Option option)
	{
		if(options==null)
			options = new ArrayList<Option>();
		
		options.add(option);
		
	}
	
	
}
