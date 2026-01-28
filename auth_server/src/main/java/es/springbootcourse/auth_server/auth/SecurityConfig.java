package es.springbootcourse.auth_server.auth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
			throws Exception {
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				OAuth2AuthorizationServerConfigurer.authorizationServer();

		http
			.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
			.with(authorizationServerConfigurer, (authorizationServer) ->
				authorizationServer
					.oidc(Customizer.withDefaults())	// Enable OpenID Connect 1.0
			)
			.authorizeHttpRequests((authorize) ->
				authorize
					.anyRequest().authenticated()
			)
			// Redirect to the login page when not authenticated from the
			// authorization endpoint
			.exceptionHandling((exceptions) -> exceptions
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
            .oauth2ResourceServer((resourceserver -> resourceserver
            .jwt(Customizer.withDefaults())));

		return http.build();
	}

	@Bean 
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtEncoder jwtEncoder)
			throws Exception {
		JwtAuthenticationSuccessHandler successHandler = new JwtAuthenticationSuccessHandler(jwtEncoder);
		
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/login", "/logout", "/register", "/error").permitAll()
				.requestMatchers("/actuator/health").permitAll()
				.anyRequest().authenticated()
			)
			// CSRF Protection
			.csrf((csrf) -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			)
			// Form login configuration
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
				.successHandler(successHandler)
				.failureUrl("/login?error=true")
				.permitAll()
			)
			// Logout configuration
			.logout((logout) -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout=true")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
				.permitAll()
			)
			// Session management
			.sessionManagement((session) -> session
				.sessionConcurrency((concurrency) -> concurrency
					.maximumSessions(1)
					.expiredUrl("/login?session=expired")
				)
			)
			// Headers security
			.headers((headers) -> headers
				.frameOptions((frame) -> frame.sameOrigin())
			)
			// CORS handling
			.cors(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		// ============================================
		// ADMINISTRADOR
		// ============================================
		UserDetails admin = User.builder()
				.username("admin")
				.password("{noop}1234")
				.roles("ADMIN")
				.build();

		// ============================================
		// CLIENTES (Customers) - Sincronizados con BD
		// ============================================
		UserDetails client1 = User.builder()
				.username("client1")
				.password("{noop}1234")
				.roles("CLIENT")
				.build();
		
		UserDetails client2 = User.builder()
				.username("client2")
				.password("{noop}1234")
				.roles("CLIENT")
				.build();
		
		UserDetails client3 = User.builder()
				.username("client3")
				.password("{noop}1234")
				.roles("CLIENT")
				.build();

		// ============================================
		// EMPLEADOS (Employees) - Sincronizados con BD
		// ============================================
		UserDetails emp1 = User.builder()
				.username("employee1")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails emp2 = User.builder()
				.username("employee2")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();

		UserDetails emp3 = User.builder()
				.username("employee3")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();

		// ============================================
		// PROVEEDORES (Providers) - Sincronizados con BD
		// ============================================
		UserDetails prov1 = User.builder()
				.username("provider1")
				.password("{noop}1234")
				.roles("PROVIDER")
				.build();
		
		UserDetails prov2 = User.builder()
				.username("provider2")
				.password("{noop}1234")
				.roles("PROVIDER")
				.build();
		
		UserDetails prov3 = User.builder()
				.username("provider3")
				.password("{noop}1234")
				.roles("PROVIDER")
				.build();

		return new InMemoryUserDetailsManager(
			// Admin
			admin,
			// Clients (3 usuarios coinciden con BD)
			client1, client2, client3,
			// Employees (3 usuarios coinciden con BD)
			emp1, emp2, emp3,
 			// Providers (3 usuarios coinciden con BD)
			prov1, prov2, prov3
		);
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("client-app")
				.clientSecret("{noop}1234")
				.clientName("Tea House Client App")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				// Authorization Code Flow (para usuarios)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				// Refresh Token Flow (para renovar tokens)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				// Redirect URIs después del login
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/client-app")
				.redirectUri("http://127.0.0.1:8080/authorized")
				// Post-logout redirect
				.postLogoutRedirectUri("http://127.0.0.1:8080/logout")
				// Scopes solicitados
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.scope(OidcScopes.EMAIL)
                .scope("read")
                .scope("write")
				.scope("user:client")
				.scope("user:employee")
				.scope("user:provider")
				.scope("admin")
				// Configuración del cliente
				.clientSettings(ClientSettings.builder()
					.requireAuthorizationConsent(true)  // Mostrar pantalla de consentimiento
					.requireProofKey(false)             // PKCE no requerido
					.build()
				)
				.build();

		return new InMemoryRegisteredClientRepository(oidcClient);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

}
