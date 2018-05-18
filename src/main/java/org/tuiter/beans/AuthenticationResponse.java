package org.tuiter.beans;

import org.tuiter.models.User;

public class AuthenticationResponse {
	private String token;
	private User user;
	
	
	public AuthenticationResponse(String token, User user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}
	
	
	public User getUser() {
		return user;
	}
}
