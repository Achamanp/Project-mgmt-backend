package com.projectManagementApp.services.impl;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Comment;
import com.projectManagementApp.entities.Issue;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.repositories.CommentRepository;
import com.projectManagementApp.repositories.IssueRepository;
import com.projectManagementApp.repositories.UserRepository;
import com.projectManagementApp.services.CommentService;

@Service
public class CommentServiceImpl implements CommentService{
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired 
	private IssueRepository issueRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public Comment createComment(Long issueId, Long userId, String content) {
		// TODO Auto-generated method stub
		Optional<Issue> issueOptional = this.issueRepository.findById(issueId);
		Optional<com.projectManagementApp.entities.User> userOptional = this.userRepository.findById(userId);
		
		if(issueOptional.isEmpty()) {
			throw new ResourceNotFoundException("Issue Not found with id " + issueId);
		}
		if(userOptional.isEmpty()) {
			throw new ResourceNotFoundException("User not found with "+userId);
		}
		
		Issue issue = issueOptional.get();
		User user = userOptional.get();
		
		Comment comment = new Comment();
		comment.setIssue(issue);
		comment.setUser(user);
		comment.setCreatedDateTime(LocalDateTime.now());
		
		comment.setContent(content);
		
		Comment savedComent = this.commentRepository.save(comment);
		
		issue.getComments().add(savedComent);
		
		
		return savedComent;
	}

	@Override
	public void deleteComment(Long commentId, Long userId) throws AccessDeniedException {
		// TODO Auto-generated method stub
		
		Optional<Comment> commentOptional = this.commentRepository.findById(commentId);
		
		Optional<User> userOptional = this.userRepository.findById(userId);
		
		if(commentOptional.isEmpty()) {
			throw new ResourceNotFoundException("Comment Not found with id " + commentId);
		}
		
		if(userOptional.isEmpty()) {
			throw new ResourceNotFoundException("User not found with "+userId);
		}
		
		
		Comment comment = commentOptional.get();
		User user = userOptional.get();
		
		if(comment.getUser().equals(user)) {
			commentRepository.delete(comment);
		}else {
			throw new AccessDeniedException("User dows not have permission to delete the comment");
		}
		
	}

	@Override
	public List<Comment> findCommentByIssueId(Long issueId) {
		
		return this.commentRepository.findCommentByIssueId(issueId);
	}
	 
	

}
