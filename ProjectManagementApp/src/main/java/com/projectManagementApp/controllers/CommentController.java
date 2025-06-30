package com.projectManagementApp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectManagementApp.entities.Comment;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.payloads.ApiResponse;
import com.projectManagementApp.payloads.CreateCommentRequest;
import com.projectManagementApp.services.CommentService;
import com.projectManagementApp.services.UserService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @RequestBody CreateCommentRequest req) {

        try {
            User user = this.userService.findUserProfileByJwt();

            Comment comment = this.commentService.createComment(
                    req.getIssueId(),
                    user.getUserId(),
                    req.getContent());

            ApiResponse<Comment> response = new ApiResponse<>();
            response.setMessage("Comment created successfully.");
            response.setSuccess(true);
            response.setData(comment);

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException ex) {
            throw ex; // Handled by GlobalExceptionHandler

        } catch (Exception ex) {
            ApiResponse<Comment> response = new ApiResponse<>();
            response.setMessage("Failed to create comment: " + ex.getMessage());
            response.setSuccess(false);
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId) {

        try {
            User user = this.userService.findUserProfileByJwt();

            this.commentService.deleteComment(commentId, user.getUserId());

            return ResponseEntity.ok("Comment deleted successfully.");

        } catch (ResourceNotFoundException ex) {
            throw ex; // Handled by GlobalExceptionHandler

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to delete this comment.");

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete comment: " + ex.getMessage());
        }
    }
    
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentByIssueId(
            @PathVariable Long issueId) {
        
        try {
            List<Comment> comments = this.commentService.findCommentByIssueId(issueId);

            ApiResponse<List<Comment>> response = new ApiResponse<>();
            response.setMessage("Comments fetched successfully.");
            response.setSuccess(true);
            response.setData(comments);

            return ResponseEntity.ok(response);

        } catch (ResourceNotFoundException ex) {
            throw ex; // Let GlobalExceptionHandler handle it

        } catch (Exception ex) {
            ApiResponse<List<Comment>> response = new ApiResponse<>();
            response.setMessage("Failed to fetch comments: " + ex.getMessage());
            response.setSuccess(false);
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
