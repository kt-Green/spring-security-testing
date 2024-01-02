package com.kstech.warroom.payload.request;

import lombok.Data;

public @Data class LoginRequest {
	private String username;
	private String password;
}
