package com.projectManagementApp.entities;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "project_table")
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "project_id")
	private Long id;
	@Column(name = "project_name")
	@NotBlank
	@Size(max = 50, min = 5)
	private String name;
	@NotBlank
	@Size(min = 25, max = 200)
	@Column(name = "project_description")
	private String description;
	@NotBlank
	@Size(min = 5, max = 50)
	@Column(name = "project_category")
	private String category;
	
	@ElementCollection
	@CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
	@Column(name = "tag")
	private List<String> tags = new ArrayList<>();

	@JsonIgnore
	@OneToOne(mappedBy = "project" , cascade = CascadeType.ALL)
	private Chat chat;
	
	@ManyToOne
	private User owner;
	
	@OneToMany(mappedBy = "project",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Issue> issues = new ArrayList<Issue>();
	
	@ManyToMany()
	private List<User> team = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public List<User> getTeam() {
		return team;
	}

	public void setTeam(List<User> team) {
		this.team = team;
	}	
	
}
