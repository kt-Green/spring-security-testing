package com.kstech.warroom.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public @Data @AllArgsConstructor class JwtResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private String token;
	private final String type = "Bearer";
}
