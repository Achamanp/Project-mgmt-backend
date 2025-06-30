package com.projectManagementApp.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectManagementApp.entities.Issue;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.payloads.ApiResponse;
import com.projectManagementApp.payloads.IssueDTO;
import com.projectManagementApp.payloads.IssueRequest;
import com.projectManagementApp.services.IssueService;
import com.projectManagementApp.services.UserService;

@RestController
@RequestMapping("/api/issues")
public class IssueController {
	
	@Autowired
	private IssueService issueService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/{issueId}")
	public ResponseEntity<ApiResponse<Issue>> getIssueById(@PathVariable Long issueId) {
	    try {
	        Issue issue = issueService.getIssueById(issueId); // assume this method throws ResourceNotFoundException if not found

	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("Issue fetched successfully");
	        response.setSuccess(true);
	        response.setData(issue);

	        return ResponseEntity.ok(response);

	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Handled by your GlobalExceptionHandler

	    } catch (Exception ex) {
	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("Something went wrong: " + ex.getMessage());
	        response.setSuccess(false);
	        response.setData(null);

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@PostMapping
	public ResponseEntity<IssueDTO> createIssue(
			@RequestBody IssueRequest issue){
		User tokenUser = this.userService.findUserProfileByJwt();
		User user = this.userService.findUserById(tokenUser.getUserId());
		
			Issue createdIssue = this.issueService.createIssue(issue, tokenUser);
			IssueDTO issueDTO = new IssueDTO();
			issueDTO.setDescription(createdIssue.getDescription());
			issueDTO.setDueDate(createdIssue.getDuedate());
			issueDTO.setPriority(createdIssue.getPriority());
			issueDTO.setProject(createdIssue.getProject());
			issueDTO.setStatus(createdIssue.getStatus());
			issueDTO.setTitle(createdIssue.getTitle());
			issueDTO.setTags(createdIssue.getTags());
			issueDTO.setAssignee(createdIssue.getAssignee());
			
			return ResponseEntity.ok(issueDTO);
		
	}
	
	@DeleteMapping("/{issueId}")
	public ResponseEntity<ApiResponse<String>> deleteIssue(
	        @PathVariable Long issueId) {

	    try {
	        // 1. Extract user from JWT
	        User user = this.userService.findUserProfileByJwt();

	        // 2. Delete the issue
	        String resultMessage = this.issueService.deleteIssue(issueId, user.getUserId());

	        // 3. Prepare successful response
	        ApiResponse<String> response = new ApiResponse<>();
	        response.setMessage(resultMessage);
	        response.setSuccess(true);
	        response.setData("Issue deleted with ID: " + issueId);

	        return ResponseEntity.ok(response);

	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Delegated to GlobalExceptionHandler

	    } catch (Exception ex) {
	        // Handle unexpected errors
	        ApiResponse<String> errorResponse = new ApiResponse<>();
	        errorResponse.setMessage("Failed to delete issue: " + ex.getMessage());
	        errorResponse.setSuccess(false);
	        errorResponse.setData(null);

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}
	
	@PutMapping("/{issueId}/assignee/{userId}")
	public ResponseEntity<ApiResponse<Issue>> addUserToIssue(
	        @PathVariable Long issueId,
	        @PathVariable Long userId) {

	  //  try {
	        Issue updatedIssue = issueService.addUserToIssue(issueId, userId);
	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("User assigned to issue successfully.");
	        response.setSuccess(true);
	        response.setData(updatedIssue);

	        return ResponseEntity.ok(response);

	   // } catch (ResourceNotFoundException ex) {
//	        throw ex; // Handled by GlobalExceptionHandler
//
//	    } catch (Exception ex) {
//	        ApiResponse<Issue> response = new ApiResponse<>();
//	        response.setMessage("Failed to assign user to issue: " + ex.getMessage());
//	        response.setSuccess(false);
//	        response.setData(null);
//
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	    }
	}

	@PutMapping("/{issueId}/status/{status}")
	public ResponseEntity<ApiResponse<Issue>> updateIssueStatus(
	        @PathVariable String status,
	        @PathVariable Long issueId) {

	    try {
	        Issue updatedIssue = issueService.updateStatus(issueId, status);

	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("Issue status updated successfully.");
	        response.setSuccess(true);
	        response.setData(updatedIssue);

	        return ResponseEntity.ok(response);

	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Let GlobalExceptionHandler handle this

	    } catch (IllegalArgumentException ex) {
	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("Invalid status: " + ex.getMessage());
	        response.setSuccess(false);
	        response.setData(null);
	        return ResponseEntity.badRequest().body(response);

	    } catch (Exception ex) {
	        ApiResponse<Issue> response = new ApiResponse<>();
	        response.setMessage("Failed to update issue status: " + ex.getMessage());
	        response.setSuccess(false);
	        response.setData(null);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	@GetMapping("/project/{projectId}")
	public ResponseEntity<ApiResponse<List<Issue>>> getIssueByProjectId(@PathVariable Long projectId) {
	    try {
	        List<Issue> issues = this.issueService.getIssueByProjectId(projectId);

	        ApiResponse<List<Issue>> apiResponse = new ApiResponse<>();
	        apiResponse.setSuccess(true);
	        apiResponse.setMessage("Issues fetched successfully");
	        apiResponse.setData(issues);

	        return ResponseEntity.ok(apiResponse);

	    } catch (Exception e) {
	        ApiResponse<List<Issue>> errorResponse = new ApiResponse<>();
	        errorResponse.setSuccess(false);
	        errorResponse.setMessage("Something went wrong: " + e.getMessage());
	        errorResponse.setData(null);

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

}
