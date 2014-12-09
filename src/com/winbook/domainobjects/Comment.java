package com.winbook.domainobjects;

import java.sql.Timestamp;

public class Comment extends Postable{
	
	private String comment;
	private Timestamp timestamp;	
	private Postable postable;
	

	public Comment(User author, String comment)
	{
		super(author);
		this.comment = comment;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}


	public Postable getPostable() {
		return postable;
	}


	public void setPostable(Postable postable) {
		this.postable = postable;
	}
	
	
	
	
}
