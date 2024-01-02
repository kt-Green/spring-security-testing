package com.kstech.warroom.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kstech.warroom.entity.ERole;
import com.kstech.warroom.entity.Role;
import com.kstech.warroom.entity.User;
import com.kstech.warroom.payload.request.LoginRequest;
import com.kstech.warroom.payload.request.SignupRequest;
import com.kstech.warroom.payload.response.JwtResponse;
import com.kstech.warroom.payload.response.MessageResponse;
import com.kstech.warroom.repository.RoleRepository;
import com.kstech.warroom.repository.UserRepository;
import com.kstech.warroom.security.jwt.JwtUtils;
import com.kstech.warroom.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	UserRepository userRepo;

	@Autowired
	RoleRepository rolesRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
		if (userRepo.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Email already exits!"));
		}

		if (userRepo.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Username already exits!"));
		}

		User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
				encoder.encode(signupRequest.getPassword()));

		Set<String> strRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = rolesRepo.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("user role not found!"));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = rolesRepo.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("admin role not found!"));
					roles.add(adminRole);
					break;
				default:
					Role userRole = rolesRepo.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("user role not found!"));
					roles.add(userRole);
					break;
				}
			});
		}
		
		user.setRoles(roles);
		userRepo.saveAndFlush(user);
		
		return ResponseEntity.ok(new MessageResponse("user registered successfully"));
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest loginRequest){
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		System.out.println("authentication object: "+authentication.toString());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = jwtUtils.generateJwtToken(authentication);
		System.out.println("jwt token: "+jwt);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(role -> role.getAuthority())
				.collect(Collectors.toList());
		return ResponseEntity.ok(new JwtResponse(
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles,
				jwt));
	}
}

