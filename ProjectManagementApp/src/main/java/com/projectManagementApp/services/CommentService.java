 package com.projectManagementApp.services;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.projectManagementApp.entities.Comment;

public interface CommentService {
	Comment createComment(Long issueId, Long userId, String comment) ;
	
	void deleteComment(Long commentId, Long userId) throws AccessDeniedException;
	
	List<Comment> findCommentByIssueId(Long issueId);
}

