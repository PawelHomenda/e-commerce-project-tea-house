package es.springbootcourse.auth_server.auth;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtEncoder jwtEncoder;

	public JwtAuthenticationSuccessHandler(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// Generar JWT token
		Instant now = Instant.now();
		long expiresIn = 3600; // 1 hora

		// Mapeo de roles a scopes específicos
		String scopes = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(auth -> {
					String role = auth.startsWith("ROLE_") ? auth.substring(5).toUpperCase() : auth.toUpperCase();
					switch(role) {
						case "CLIENT": return "user:client";
						case "EMPLOYEE": return "user:employee";
						case "PROVIDER": return "user:provider";
						case "ADMIN": return "admin";
						default: return role.toLowerCase();
					}
				})
				.collect(Collectors.joining(" "));

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("http://127.0.0.1:9000")
				.subject(authentication.getName())
				.issuedAt(now)
				.expiresAt(now.plusSeconds(expiresIn))
				.claim("scope", scopes)
				.build();

		String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		
		// Redirigir a api_service con el token
		response.sendRedirect("http://127.0.0.1:8080/authorized?token=" + token);
	}
}

