package com.seeyon.v3x.collaboration.webmodel;

public class SimpleTemplete {

	private Long id;
	private Long memberId;
	private String subject;
	private Integer standardDuration;
	
	public Integer getStandardDuration() {
		return standardDuration;
	}
	public void setStandardDuration(Integer standardDuration) {
		this.standardDuration = standardDuration;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
