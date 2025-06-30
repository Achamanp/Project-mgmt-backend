package com.projectManagementApp.controllers;

import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.entities.Invitation;
import com.projectManagementApp.entities.Issue;
import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.InvalidTokenException;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.payloads.ApiResponse;
import com.projectManagementApp.payloads.InviteRequest;
import com.projectManagementApp.payloads.MessageResponse;
import com.projectManagementApp.services.InvitationService;
import com.projectManagementApp.services.ProjectService;
import com.projectManagementApp.services.UserService;

import org.eclipse.angus.mail.iap.ResponseInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private InvitationService invitationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Project>>> getProjects(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {

        try {
            User user = this.userService.findUserProfileByJwt();
            List<Project> projects = this.projectService.getProjectByTeam(user, category, tag);
            ApiResponse<List<Project>> response = new ApiResponse<>(true, "Projects fetched successfully", projects);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<List<Project>> errorResponse = new ApiResponse<>(false, "Failed to fetch projects: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Project>> getProjectById(
            @PathVariable Long projectId) {
        try {
            User user = this.userService.findUserProfileByJwt();
            Project project = projectService.getProjectById(projectId);
            
            // Optional: Add access control if needed, e.g., check if user is part of the project

            ApiResponse<Project> response = new ApiResponse<>(true, "Project fetched successfully", project);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ApiResponse<Project> errorResponse = new ApiResponse<>(false, "Failed to fetch project: " + e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping
    public ResponseEntity<ApiResponse<Project>> createProject(
            @RequestBody Project projectRequest) {
        try {
            User user = this.userService.findUserProfileByJwt();
            Project createdProject = projectService.createProject(projectRequest, user);

            ApiResponse<Project> response = new ApiResponse<>(
                    true,
                    "Project created successfully",
                    createdProject
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse<Project> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to create project: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @PathVariable Long projectId,
            @RequestBody Project updatedProjectData,
            @RequestHeader("Authorization") String jwt) {
        try {
            Project updatedProject = this.projectService.updateProject( updatedProjectData, projectId);

            ApiResponse<Project> response = new ApiResponse<>(
                    true,
                    "Project updated successfully",
                    updatedProject
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<Project> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to update project: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<String>> deleteProject(
            @PathVariable Long projectId) {
        try {
            User user = this.userService.findUserProfileByJwt();
            this.projectService.deleteProject(projectId, user.getUserId());

            ApiResponse<String> response = new ApiResponse<>(
                    true,
                    "Project deleted successfully",
                    null
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to delete project: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Project>>> searchProject(
            @RequestParam(required = false) String keyword) {
        try {
            User user = this.userService.findUserProfileByJwt();
            List<Project> projects = this.projectService.searchProjects(keyword,user);

            ApiResponse<List<Project>> response = new ApiResponse<>(
                    true,
                    "Projects fetched successfully",
                    projects
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<List<Project>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to search projects: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{projectId}/chats")
    public ResponseEntity<ApiResponse<Chat>> getChatByProjectId(
            @PathVariable Long projectId) {
        try {
            User user = this.userService.findUserProfileByJwt();
            Chat chat = this.projectService.getChatByProjectId(projectId);

            ApiResponse<Chat> response = new ApiResponse<>(
                    true,
                    "Chat fetched successfully",
                    chat
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<Chat> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to fetch chat: " + e.getMessage(),
                    null
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Project>> inviteProject(
            @RequestBody InviteRequest inviteRequest) {

        try {
            // 1. Get user from JWT
            User user = this.userService.findUserProfileByJwt();

            // 2. Optionally, check if user has access to invite to this project
            // e.g., only project owners/managers can invite others

            // 3. Send invitation
            this.invitationService.sendInvitation(inviteRequest.getEmail(), inviteRequest.getProjectId());

            // 4. Create response
            ApiResponse<Project> response = new ApiResponse<>();
            response.setMessage("Invitation sent successfully to " + inviteRequest.getEmail());
            response.setSuccess(true);
            response.setData(null); // Or return project info if you wish

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException ex) {
            throw ex; // Will be handled by GlobalExceptionHandler
        } catch (Exception ex) {
            ApiResponse<Project> errorResponse = new ApiResponse<>();
            errorResponse.setMessage("Failed to send invitation: " + ex.getMessage());
            errorResponse.setSuccess(false);
            errorResponse.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    
    @PostMapping("/accept-invite")
    public ResponseEntity<Invitation> acceptInviteProject(
            @RequestParam String token) throws InvalidTokenException {

        try {
            User user = this.userService.findUserProfileByJwt();

            Invitation invitation = this.invitationService.acceptInvitation(token, user.getUserId());

            // TODO: Optionally associate user with project
             projectService.addUserToProject(invitation.getProjectId(), user.getUserId());

            return ResponseEntity.ok(invitation);

        } catch (InvalidTokenException ex) {
            throw ex; // Delegated to GlobalExceptionHandler

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    

  
}
