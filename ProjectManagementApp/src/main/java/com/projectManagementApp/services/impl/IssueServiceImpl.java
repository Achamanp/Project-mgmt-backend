package com.projectManagementApp.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Issue;
import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.IssueException;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.payloads.IssueRequest;
import com.projectManagementApp.repositories.IssueRepository;
import com.projectManagementApp.services.IssueService;
import com.projectManagementApp.services.ProjectService;
import com.projectManagementApp.services.UserService;


@Service
public class IssueServiceImpl implements IssueService{
	
	
	@Autowired
	private IssueRepository issueRepository;
	
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;

	@Override
	public Issue getIssueById(Long issueId) {
		Optional<Issue> issue = this.issueRepository.findById(issueId);
		if(issue.isEmpty()) {
			throw new ResourceNotFoundException("No issue found with issueId" + issueId);

		}
		return issue.get();
		}

	@Override
	public List<Issue> getIssueByProjectId(Long projectId) {
		// TODO Auto-generated method stub
		return this.issueRepository.findByProjectId(projectId);
	}

	@Override
	public Issue createIssue(IssueRequest issueRequest, User user) {
		Project project = this.projectService.getProjectById(issueRequest.getProjectId());
		
		Issue issue = new Issue();
		
		issue.setTitle(issueRequest.getTitle());
		issue.setDescription(issueRequest.getDescription());
		issue.setStatus(issueRequest.getStatus());
		issue.setPriority(issueRequest.getPriority());
		issue.setDuedate(issueRequest.getDuedate());
		issue.setProject(project);
		issue.setDuedate(issueRequest.getDuedate());
		
		issue.setProject(project);
		
		
		return issueRepository.save(issue);
	}

	@Override
	public String deleteIssue(Long issueId, Long userId) {
		getIssueById(issueId);
		 this.issueRepository.deleteById(issueId);
		 return "Issue deleted Successfully";
	}

	@Override
	public List<Issue> getIssuesByAssigneeId(Long aasigneId) {
		
		return null;
	}

	@Override
	public List<Issue> searchIssues(String title, String statuss, String priority, Long assigneeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAssigneeForIssue(Long issueId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue addUserToIssue(Long issueId, Long userId) {
		// TODO Auto-generated method stub
		
		User user = this.userService.findUserById(userId);
		Issue issue = getIssueById(issueId);
		issue.setAssignee(user);
		return issueRepository.save(issue);
		
	}

	@Override
	public Issue updateStatus(Long issueId, String status) {
		// TODO Auto-generated method stub
		Issue issue = getIssueById(issueId);
		issue.setStatus(status);
		return issueRepository.save(issue);
	}

}
