package com.kstech.warroom.payload.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public @Data class SignupRequest {
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String username;
	
	@NotBlank
	@Size(min = 6, max = 40)
	private String password;
	
	@NotBlank
	@Email
	@Size(max=50)
	private String email;
	
	private Set<String> roles;
}
