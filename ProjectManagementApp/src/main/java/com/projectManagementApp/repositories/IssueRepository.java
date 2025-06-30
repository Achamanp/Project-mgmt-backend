package com.projectManagementApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Issue;


@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>{
	
	public List<Issue> findByProjectId(Long id);

}
