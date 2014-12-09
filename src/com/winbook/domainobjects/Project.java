package com.winbook.domainobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Project {
	
	private String name;
	private String subtitle;
	private Timestamp dateOfCreation;
	private String description;
	private User creator;
	
	private List<Wall> walls;
	private List<User> members;
	
	public Project(User creator, String name)
	{
		this.creator = creator;
		this.name = name;
		description = WinbookConstants.DEFAULT_PROJECT_DESCRIPTION;
	}
	
	public Project(User creator, String name, String description)
	{
		this.creator = creator;
		this.name = name;
		this.description = description;
	}
			
	public Project(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public Timestamp getDateOfCreation() {
		return dateOfCreation;
	}
	public void setDateOfCreation(Timestamp dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public User getCreator() {
		return creator;
	}
	
	

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}

	public void addWall(Wall wall) {
		// TODO Auto-generated method stub
		if(walls == null)
			walls = new ArrayList<Wall>();
		
		walls.add(wall);
		
	}
	

}
