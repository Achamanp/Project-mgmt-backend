package com.projectManagementApp.security.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectManagementApp.entities.User;

public class UserDetailsImpl implements UserDetails{
	
	 private static final long serialVersionUID = 1L;

	    private Long id;
	    private String username;
	    private String email;
	    
	    @JsonIgnore
	    private String password;
	    
	    private Collection<? extends GrantedAuthority> authorities;
	    
	    public UserDetailsImpl(Long userId, String username , String email, String password,  Collection<? extends GrantedAuthority> authorities) {
	    	this.id = userId;
	    	this.username = username;
	    	this.email = email;
	    	this.password = password;
	    	this.authorities = authorities;
	    }
	    public static UserDetailsImpl build(User user) {
	        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName());
	        return new UserDetailsImpl(
	            user.getUserId(),
	            user.getEmail(), // ← Use email instead of username
	            user.getEmail(), 
	            user.getPassword(), 
	            List.of(authority)
	        );
	    }

	    @Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			
			return authorities;
		}

		@Override
		public String getPassword() {
			
			return password;
		}
		  public Long getId() {
		        return id;
		    }

		    public String getEmail() {
		        return email;
		    }


		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			return username;
		}

}
