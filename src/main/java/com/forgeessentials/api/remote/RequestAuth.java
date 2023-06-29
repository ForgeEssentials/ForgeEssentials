package com.forgeessentials.api.remote;

/**
 * Represents an authentication token for remote sessions
 */
public class RequestAuth {

	public String username;

	public String password;

	public RequestAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
