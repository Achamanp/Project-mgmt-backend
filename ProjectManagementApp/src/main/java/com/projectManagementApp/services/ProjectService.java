package com.projectManagementApp.services;

import java.util.List;

import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;

public interface ProjectService {
	
	public Project createProject(Project project , User user);
	
	List<Project> getProjectByTeam(User user, String category, String tag);
	
	Project getProjectById(Long projectId);
	
	void deleteProject(Long projectId, Long userId);
	
	Project updateProject(Project updatedProject, Long id); 
	
	void addUserToProject(Long projectId, Long userId);
	
	void removeUserToProject(Long projectId, Long userId);
	
	
	Chat getChatByProjectId(Long projectId);
	
	public List<Project> searchProjects(String keyword, User user);
}
