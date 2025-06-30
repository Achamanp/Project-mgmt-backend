package com.projectManagementApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

	List<Message> findByChatIdOrderByCreatedAtAsc(Long id);


}
