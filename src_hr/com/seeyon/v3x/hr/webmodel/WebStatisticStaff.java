package com.seeyon.v3x.hr.webmodel;

public class WebStatisticStaff {
	private String depName;
	private int count;
	private String post;
	private String level;
	private String education;
	private String gender;
	private String ageLevel;
	private String politicalPosition;
	
	public String getAgeLevel() {
		return ageLevel;
	}
	public void setAgeLevel(String ageLevel) {
		this.ageLevel = ageLevel;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getDepName() {
		return depName;
	}
	public void setDepName(String depName) {
		this.depName = depName;
	}
	public String getPoliticalPosition() {
		return politicalPosition;
	}
	public void setPoliticalPosition(String politicalPosition) {
		this.politicalPosition = politicalPosition;
	}

}
