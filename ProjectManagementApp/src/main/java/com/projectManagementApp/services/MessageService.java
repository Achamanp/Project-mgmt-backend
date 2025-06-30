package com.projectManagementApp.services;

import java.util.List;

import com.projectManagementApp.entities.Message;

public interface MessageService {
	Message sendMessage(Long senderId, Long chatId, String content);
	
	List<Message> getMessagesByProjectId(Long projctId);

	List<Message> getMessagesByChatId(Long chatId);

}
