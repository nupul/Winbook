package com.winbook.domainobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Option extends Postable{
	
	
	private String status;
	private String option;
	private Timestamp timestamp;
	private Issue issue;
	private List<Agreement> agreements;
	
	/*
	 * Initialize an option with the corresponding Issue object
	 */
	public Option(User author, Issue issue, String option)
	{
		super(author);
		this.issue = issue;
		this.option = option;
		
	}

	/*
	 * Convenience constructor for creating option with reference to it's Issue Id without the need of loading a populated Issue object.
	 */
	public Option(User author, int issueId, String option)
	{
		this(author, new Issue(issueId),option);
	}
	
	/*
	 * Creating an option reference with it's ID alone
	 */
	public Option(int optionId)
	{
		super(optionId);
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
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
	
	
}///:~
