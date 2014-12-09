package com.winbook.domainobjects;

public class Wall {

	protected String name;
	protected boolean isEditAllowed;
	protected boolean isDeleteAllowed;
	protected boolean isPostsAllowed;
	protected Project project;

	public Wall(Project project, String name) {
		
		this.project = project;
		this.name = name;
		isEditAllowed = isDeleteAllowed = isPostsAllowed = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEditAllowed() {
		return isEditAllowed;
	}

	public void setEditAllowed(boolean isEditAllowed) {
		this.isEditAllowed = isEditAllowed;
	}

	public boolean isDeleteAllowed() {
		return isDeleteAllowed;
	}

	public void setDeleteAllowed(boolean isDeleteAllowed) {
		this.isDeleteAllowed = isDeleteAllowed;
	}

	public boolean isPostsAllowed() {
		return isPostsAllowed;
	}

	public void setPostsAllowed(boolean isPostsAllowed) {
		this.isPostsAllowed = isPostsAllowed;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	

	
	
}