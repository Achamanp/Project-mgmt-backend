package com.projectManagementApp.payloads;

public class InviteRequest {
	private String email;
	private Long projectId;
	
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public InviteRequest(Long projectId, String email) {
		super();
		this.projectId = projectId;
		this.email = email;
	}
	public InviteRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "InviteRequest [email=" + email + ", projectId=" + projectId + "]";
	}
	
	

}
