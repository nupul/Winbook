package com.winbook.domainobjects;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.restlet.security.Role;

public class User {

	private String emailId;
	private String firstName, lastName;
	private String password;
	private String nickname;
	private String displayName;
	private String avatarURL;
	
	private UserConfiguration configuration = new UserConfiguration();
	private List<? extends Postable> postsByUser;
	private List<Project> projectsMemberOf;
	private List<Project> projectsNotMemberOf;
	
	public List<Project> getProjectsMemberOf() {
		return projectsMemberOf == null? new ArrayList<Project>(): projectsMemberOf;
	}
	public void setProjectsMemberOf(List<Project> projectsMemberOf) {
		this.projectsMemberOf = projectsMemberOf;
	}
	public List<Project> getProjectsNotMemberOf() {
		return projectsNotMemberOf == null? new ArrayList<Project>():projectsNotMemberOf;
	}
	public void setProjectsNotMemberOf(List<Project> projectsNotMemberOf) {
		this.projectsNotMemberOf = projectsNotMemberOf;
	}
	public User(String emailId) {
		
		this.emailId = emailId;

	}
	public List<? extends Postable> getPostsByUser() {
		return postsByUser;
	}
	public void setPostsByUser(List<? extends Postable> postsByUser) {
		this.postsByUser = postsByUser;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatarURL() {
		return avatarURL;
	}
	public void setAvatarURL(String avatar) {
		this.avatarURL = avatar;
	}
	public UserConfiguration getConfiguration() {
		return configuration==null ? new UserConfiguration(): configuration;
	}
	public void setConfiguration(UserConfiguration configuration) {
		this.configuration = configuration;
	}
	public List<Project> getProjects() {
		return projectsMemberOf;
	}
	public void setProjects(List<Project> projects) {
		this.projectsMemberOf = projects;
	}
	
	public String getDisplayName()
	{
		if(nickname == null || nickname.length()==0)
			displayName =  firstName+" "+lastName;
		
		else
			displayName = nickname;
		
		return displayName;
	}
	
	
	
}
