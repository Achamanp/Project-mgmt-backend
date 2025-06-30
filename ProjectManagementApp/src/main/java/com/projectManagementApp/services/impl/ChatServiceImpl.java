package com.projectManagementApp.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectManagementApp.entities.Chat;
import com.projectManagementApp.repositories.ChatRepository;
import com.projectManagementApp.services.ChatService;

@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatRepository chatRepository;
	
	

	@Override
	public Chat createChat(Chat chat) {
		
		// TODO Auto-generated method stub
		
		return chatRepository.save(chat);
	}

}
