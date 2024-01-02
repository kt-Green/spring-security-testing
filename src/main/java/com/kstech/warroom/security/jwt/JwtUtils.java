package com.kstech.warroom.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.kstech.warroom.security.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	  @Value("${ktTech.app.jwtSecret}")
	  private String jwtSecret;

	  @Value("${ktTech.app.jwtExpirationMs}")
	  private int jwtExpirationMs;
	  
	  public String generateJwtToken(Authentication authentication) {
		  UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		  
		  return Jwts.builder()
				  .setSubject(userPrincipal.getUsername())
				  .setIssuedAt(new Date())
				  .setExpiration(new Date(new Date().getTime()+jwtExpirationMs))
				  .signWith(key(), SignatureAlgorithm.HS256)
				  .compact();
	  }
	  
	  private Key key() {
		  return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	  }
	  
	  public String getUsernameFromJwtToken(String token) {
		  return Jwts
				  .parserBuilder()
				  .setSigningKey(key())
				  .build()
				  .parseClaimsJws(token)
				  .getBody()
				  .getSubject();
	  }
	  
	  public boolean validateJwtToken(String authToken) {
		  try {
			  Jwts
			  .parserBuilder()
			  .setSigningKey(key())
			  .build()
			  .parse(authToken);
			  
			  return true;
		  }
		  catch (MalformedJwtException m) {
			  System.out.println("Encountered malformed JWT token: "+m.getMessage() );
		  }
		  catch (ExpiredJwtException e) {
			  System.out.println("Encountered expired JWT token: "+e.getMessage() );
		  }
		  catch (UnsupportedJwtException u) {
			  System.out.println("Encountered unsupported JWT token: "+u.getMessage() );
		  }
		  catch(IllegalArgumentException i) {
			  System.out.println("Encountered Illegal arguments: "+i.getMessage() );
		  }
		  return false;
	  }
}
