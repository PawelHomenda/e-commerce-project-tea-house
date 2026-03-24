package es.springbootcourse.auth_server.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import es.springbootcourse.auth_server.service.JwtTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenService jwtTokenService;

	public JwtAuthenticationSuccessHandler(JwtTokenService jwtTokenService) {
		this.jwtTokenService = jwtTokenService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
				
			String token = jwtTokenService.generateToken(authentication);

			response.setContentType("application/json");
			response.getWriter().write("{\"token\": \"" + token + "\"}");
			response.getWriter().flush();
	}
}

