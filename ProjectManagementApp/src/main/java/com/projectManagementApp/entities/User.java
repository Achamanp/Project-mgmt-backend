package com.projectManagementApp.entities;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Collate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users_table",
uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private Long userId;
	@NotBlank
	@Size(max=25)
	@Column(name = "user_name")
	private String username;
	@Email
	private String email;
	@OneToMany(mappedBy = "user")
	private List<PasswordResetToken> passwordResetTokens;
	
	
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonBackReference
    private Role role;
    
	@JsonIgnore
	@OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
	private List<Issue> asssignedIssue =  new ArrayList<>();
	
	private Long projectSize;
	public Long getUserId() {
		return userId;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<Issue> getAsssignedIssue() {
		return asssignedIssue;
	}
	public void setAsssignedIssue(List<Issue> asssignedIssue) {
		this.asssignedIssue = asssignedIssue;
	}
	public Long getProjectSize() {
		return projectSize;
	}
	public void setProjectSize(Long projectSize) {
		this.projectSize = projectSize;
	}
	public List<PasswordResetToken> getPasswordResetTokens() {
		return passwordResetTokens;
	}
	public void setPasswordResetTokens(List<PasswordResetToken> passwordResetTokens) {
		this.passwordResetTokens = passwordResetTokens;
	}
	
	
	

}
