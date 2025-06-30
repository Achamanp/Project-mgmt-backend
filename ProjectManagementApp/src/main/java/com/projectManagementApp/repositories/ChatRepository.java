package com.projectManagementApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>{
	

}
