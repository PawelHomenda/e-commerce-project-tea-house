package es.springbootcourse.auth_server.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

	@Autowired
	private JwtEncoder jwtEncoder;

	public String generateToken(Authentication authentication) {
		Instant now = Instant.now();
		long expiresIn = 3600; // 1 hora

		String scopes = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(auth -> auth.startsWith("ROLE_") ? auth.substring(5).toLowerCase() : auth.toLowerCase())
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("http://127.0.0.1:9000")
				.subject(authentication.getName())
				.issuedAt(now)
				.expiresAt(now.plusSeconds(expiresIn))
				.claim("scope", scopes)
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}
}
