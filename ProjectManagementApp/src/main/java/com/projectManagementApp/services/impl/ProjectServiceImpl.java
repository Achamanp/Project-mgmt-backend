package com.projectManagementApp.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.repositories.ProjectRepository;
import com.projectManagementApp.services.ChatService;
import com.projectManagementApp.services.ProjectService;
import com.projectManagementApp.services.UserService;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatService chatService;

	@Override
	public Project createProject(Project project, User user) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User owner = this.userService.findUserByEmail(email);

		Project newProject = new Project();
		newProject.setName(project.getName());
		newProject.setCategory(project.getCategory());
		newProject.setTags(project.getTags());
		newProject.setDescription(project.getDescription());
		newProject.setOwner(owner);
		newProject.getTeam().add(owner);

		// Save project first
		Project savedProject = this.projectRepository.save(newProject);

		// Create and link chat
		Chat chat = new Chat();
		chat.setProject(savedProject);
		chat.setName(savedProject.getName()); // ✅ This line fixes the validation error

		Chat projectChat = chatService.createChat(chat);

		// Link chat to project
		savedProject.setChat(projectChat);

		return savedProject;
	}


	@Override
	public List<Project> getProjectByTeam(User user, String category, String tag) {
	    // Get initial projects by team or owner
	    List<Project> projects = this.projectRepository.findByTeamContainingOrOwner(user, user);
	    
	    // Filter by category if provided
	    if (category != null && !category.trim().isEmpty()) {
	        projects = projects.stream()
	            .filter(project -> project.getCategory().equals(category))
	            .collect(Collectors.toList());
	    }
	    
	    // Filter by tag if provided
	    if (tag != null && !tag.trim().isEmpty()) {
	        projects = projects.stream()
	            .filter(project -> project.getTags().contains(tag))
	            .collect(Collectors.toList());
	    }
	    
	    return projects;
	}
	@Override
	public Project getProjectById(Long projectId) {
		Optional<Project> optionalProject = this.projectRepository.findById(projectId);
		if(optionalProject.isEmpty()) {
			throw new ResourceNotFoundException("Project not found with id " + projectId);
		}
		return optionalProject.get();
	}

	@Override
	public void deleteProject(Long projectId, Long userId) {
		// TODO Auto-generated method stub
		projectRepository.deleteById(projectId);
		
	}

	@Override
	public Project updateProject(Project updatedProject, Long id) {
		Project project = getProjectById(id);
		
		project.setName(updatedProject.getName());
		project.setDescription(updatedProject.getDescription());
		project.setTags(updatedProject.getTags());
		// TODO Auto-generated method stub
		return this.projectRepository.save(project);
	}

	@Override
	public void addUserToProject(Long projectId, Long userId) {
		Project project = getProjectById(projectId);
		User user = this.userService.findUserById(userId);
		for(User member: project.getTeam()) {
			if(member.getUserId().equals(userId)) {
				return;
			}
		}
		
			project.getChat().getUsers().add(user);
			project.getTeam().add(user);
		
		projectRepository.save(project);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUserToProject(Long projectId, Long userId) {
		// TODO Auto-generated method stub
		Project project = getProjectById(projectId);
		User user = this.userService.findUserById(userId);
		if(project.getTeam().contains(user)) {
			project.getChat().getUsers().remove(user);
			project.getTeam().remove(user);
		}
		projectRepository.save(project);
		
	}

	@Override
	public Chat getChatByProjectId(Long projectId) {
		Project project = getProjectById(projectId);
		return project.getChat();
	}

	@Override
	public List<Project> searchProjects(String keyword, User user) {
		// TODO Auto-generated method stub
		List<Project> project = this.projectRepository.
				findByNameContainingAndTeamContains(keyword, user);
		return project;
	}

}
