package com.projectManagementApp.services;

import java.util.List;
import java.util.Optional;

import com.projectManagementApp.entities.Issue;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.payloads.IssueRequest;

public interface IssueService {
	
	Issue getIssueById(Long issueId);
	
	List<Issue> getIssueByProjectId(Long projectId);
	
	Issue createIssue(IssueRequest issueRequest, User user) ;
	
	
	String deleteIssue(Long issueId, Long userId);
	
	List<Issue> getIssuesByAssigneeId(Long aasigneId);
	
	List<Issue> searchIssues(String title, String statuss, String priority, Long assigneeId);
	
	
	List<User> getAssigneeForIssue(Long issueId);
	
	
	Issue addUserToIssue(Long issueId, Long userId);
	
	
	Issue updateStatus(Long issueId, String status);
	
	

}
