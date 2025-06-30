package com.projectManagementApp.payloads;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;

public class IssueDTO {
	
	 private Long id;
	    private String title;
	    private String description;
	    private String status;
	    private String priority;
	    private LocalDate dueDate;
	    private List<String> tags = new ArrayList<>();
	    
	    private Long projectId;
	    private Long assigneeId;

	
	private Project project;
	
	
	private User assignee;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Long getProjectId() {
		return projectId;
	}


	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}


	public String getPriority() {
		return priority;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}


	public LocalDate getDueDate() {
		return dueDate;
	}


	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}


	public List<String> getTags() {
		return tags;
	}


	public void setTags(List<String> tags) {
		this.tags = tags;
	}


	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public User getAssignee() {
		return assignee;
	}


	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}


	public Long getAssigneeId() {
		return assigneeId;
	}


	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;
	}
	
	

}
