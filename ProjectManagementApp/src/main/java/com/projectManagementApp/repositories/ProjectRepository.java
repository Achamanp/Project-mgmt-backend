package com.projectManagementApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectManagementApp.entities.Project;
import com.projectManagementApp.entities.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	
//	List<Project> findByOwner(User user);
	
	List<Project> findByNameContainingAndTeamContains(String partialName, User user);
	
//	@Query("SELECT p From Project p join p.team t where t=:user")
//	List<Project> findProjectByteam(@Param("user") User user);

	List<Project> findByTeamContainingOrOwner(User user, User owner);

}
