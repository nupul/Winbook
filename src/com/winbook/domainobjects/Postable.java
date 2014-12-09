package com.winbook.domainobjects;

import java.util.List;

public abstract class Postable {
	
	private int id;
	private User author;
	private List<Comment> comments;
	
	/*
	 * Available for convenience only to have an 'empty' postable object whose fields need to be manually populated.
	 */
	public Postable(int id)
	{
		this.id = id;
	}
	
	public Postable(User author)
	{
		this.author = author;
	}
	
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


}
