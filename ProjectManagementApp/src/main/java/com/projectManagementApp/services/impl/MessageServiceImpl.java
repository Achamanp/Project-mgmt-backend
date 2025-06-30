package com.projectManagementApp.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.entities.Message;
import com.projectManagementApp.entities.User;
import com.projectManagementApp.globalException.ResourceNotFoundException;
import com.projectManagementApp.repositories.ChatRepository;
import com.projectManagementApp.repositories.MessageRepository;
import com.projectManagementApp.repositories.UserRepository;
import com.projectManagementApp.services.MessageService;
import com.projectManagementApp.services.ProjectService;

@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private ProjectService projectService;
    
    @Override
    public Message sendMessage(Long senderId, Long projectId, String content) {
        // Find the user
        Optional<User> userOptional = this.userRepository.findById(senderId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID: " + senderId);  
        }
        User user = userOptional.get();
        
        // Get the chat associated with the project
        Chat chat = this.projectService.getProjectById(projectId).getChat();
        if (chat == null) {
            throw new ResourceNotFoundException("Chat not found for project ID: " + projectId);
        }
        
        // Create and save the message
        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setSender(user);
        
        Message savedMessage = this.messageRepository.save(message);
        
        // Add message to chat's message list (if the relationship is bidirectional)
        chat.getMessages().add(savedMessage);
        // Note: You might want to save the chat as well if the relationship requires it
        // this.chatRepository.save(chat);
        
        return savedMessage;
    }
    
    @Override
    public List<Message> getMessagesByProjectId(Long projectId) {
        // Get chat by project ID
        Chat chat = this.projectService.getChatByProjectId(projectId);
        if (chat == null) {
            throw new ResourceNotFoundException("Chat not found for project ID: " + projectId);
        }
        
        // Get messages ordered by creation time
        return this.messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
    }
    
    @Override
    public List<Message> getMessagesByChatId(Long chatId) {
        // Verify chat exists
        Optional<Chat> chatOptional = this.chatRepository.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ResourceNotFoundException("Chat not found with ID: " + chatId);
        }
        
        return this.messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
    }
}