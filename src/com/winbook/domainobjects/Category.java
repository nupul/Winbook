package com.winbook.domainobjects;

import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import com.winbook.exceptions.TooLongNameException;

/*
 * Category class for adding labels to win conditions. Note this class accepts color codes in HEX format e.g., "#bb880c" - you pass the hex codes as 
 * a string.
 * 
 * The max length of a categoryName is 60 characters as per the DB's column limit of VARCHAR(60) chosen during the design
 * 
 * NOTE there is no check made w.r.t. the correctness of the string, it's assumed that it's upto the application layer to handle the correct
 * format. 
 */
public class Category extends Postable{
	
	private String categoryName;
	private String hexColorCode;
	private Wall wall;
	private boolean MMF;
	
	public Category(User creator, String categoryName, String hexColorCode)
	{
		super(creator);
		this.categoryName = categoryName;
		this.hexColorCode = hexColorCode;
	}
	
	public Category(int categoryId)
	{
		super(categoryId);
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getHexColorCode() {
		return hexColorCode;
	}

	public void setHexColorCode(String hexColorCode) {
		this.hexColorCode = hexColorCode;
	}

	public Wall getWall() {
		return wall;
	}

	public void setWall(Wall wall) {
		this.wall = wall;
	}
	
	public boolean isMMF() {
		return MMF;
	}

	public void setMMF(boolean isMMF) {
		MMF = isMMF;
	}
	
}
