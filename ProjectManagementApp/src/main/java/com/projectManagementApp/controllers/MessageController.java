package com.projectManagementApp.controllers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.entities.Message;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.payloads.ApiResponse;
import com.projectManagementApp.payloads.CreateMessageRequest;
import com.projectManagementApp.services.MessageService;
import com.projectManagementApp.services.ProjectService;
import com.projectManagementApp.services.UserService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private MessageService messageService;
	
	@PostMapping("/send")
	public ResponseEntity<ApiResponse<Message>> sendMessage(@RequestBody CreateMessageRequest request) {
	    try {
	        // 1. Get the sender
	        User sender = userService.findUserById(request.getSenderId());
	        // 2. Validate project and chat
	        Chat chats = this.projectService.getProjectById(request.getProjectId()).getChat();
	        if (chats == null) {
	            throw new ResourceNotFoundException("Chat not found for the given project.");
	        }
	        // 3. Send the message
	        Message sentMessage = messageService.sendMessage(
	                request.getSenderId(), request.getProjectId(), request.getContent());
	        // 4. Wrap in ApiResponse
	        ApiResponse<Message> response = new ApiResponse<>();
	        response.setMessage("Message sent successfully.");
	        response.setSuccess(true);
	        response.setData(sentMessage);
	        return ResponseEntity.ok(response);
	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Handled by GlobalExceptionHandler
	    } catch (Exception ex) {
	        ApiResponse<Message> response = new ApiResponse<>();
	        response.setMessage("Failed to send message: " + ex.getMessage());
	        response.setSuccess(false);
	        response.setData(null);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	// Existing endpoint - Get messages by project ID
	@GetMapping("/chat/{projectId}")
	public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long projectId) {
	    try {
	        List<Message> messages = this.messageService.getMessagesByProjectId(projectId);
	        return ResponseEntity.ok(messages);
	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Handled by GlobalExceptionHandler
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	// New endpoint - Get messages by chat ID directly
	@GetMapping("/by-chat/{chatId}")
	public ResponseEntity<List<Message>> getMessagesByChatIdDirect(@PathVariable Long chatId) {
	    try {
	        List<Message> messages = this.messageService.getMessagesByChatId(chatId);
	        return ResponseEntity.ok(messages);
	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Handled by GlobalExceptionHandler
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	// Alternative endpoint with ApiResponse wrapper for consistency
	@GetMapping("/chats/{chatId}")
	public ResponseEntity<ApiResponse<List<Message>>> getMessagesByChatIdWrapped(@PathVariable Long chatId) {
	    try {
	        List<Message> messages = this.messageService.getMessagesByChatId(chatId);
	        
	        ApiResponse<List<Message>> response = new ApiResponse<>();
	        response.setMessage("Messages retrieved successfully.");
	        response.setSuccess(true);
	        response.setData(messages);
	        
	        return ResponseEntity.ok(response);
	    } catch (ResourceNotFoundException ex) {
	        throw ex; // Handled by GlobalExceptionHandler
	    } catch (Exception ex) {
	        ApiResponse<List<Message>> response = new ApiResponse<>();
	        response.setMessage("Failed to retrieve messages: " + ex.getMessage());
	        response.setSuccess(false);
	        response.setData(null);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
}