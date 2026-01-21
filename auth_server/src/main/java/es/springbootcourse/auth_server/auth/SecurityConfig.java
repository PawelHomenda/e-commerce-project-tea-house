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
				.requestMatchers("/login", "/logout", "/register").permitAll()
				.anyRequest().authenticated()
			)
			// Form login handles the redirect to the login page from the
			// authorization server filter chain
            .csrf(scrf -> scrf.disable())
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
				.successHandler(successHandler)
				.permitAll());

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
		// CLIENTES (Customers)
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
		
		UserDetails client4 = User.builder()
				.username("client4")
				.password("{noop}1234")
				.roles("CLIENT")
				.build();
		
		UserDetails client5 = User.builder()
				.username("client5")
				.password("{noop}1234")
				.roles("CLIENT")
				.build();

		// ============================================
		// EMPLEADOS (Employees)
		// ============================================
		UserDetails empUser = User.builder()
				.username("user")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails empTanaka = User.builder()
				.username("tanaka")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails emp4 = User.builder()
				.username("employee4")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails emp5 = User.builder()
				.username("employee5")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();
		
		UserDetails emp6 = User.builder()
				.username("employee6")
				.password("{noop}1234")
				.roles("EMPLOYEE")
				.build();

		// ============================================
		// PROVEEDORES (Providers)
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
		
		UserDetails prov4 = User.builder()
				.username("provider4")
				.password("{noop}1234")
				.roles("PROVIDER")
				.build();
		
		UserDetails prov5 = User.builder()
				.username("provider5")
				.password("{noop}1234")
				.roles("PROVIDER")
				.build();

		return new InMemoryUserDetailsManager(
			// Admin
			admin,
			// Clients
			client1, client2, client3, client4, client5,
			// Employees
			empUser, empTanaka, emp4, emp5, emp6,
			// Providers
			prov1, prov2, prov3, prov4, prov5
		);
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("client-app")
				.clientSecret("{noop}1234")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/client-app")
				.redirectUri("http://127.0.0.1:8080/authorized")
				.postLogoutRedirectUri("http://127.0.0.1:8080/logout")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
				.scope("user:client")
				.scope("user:employee")
				.scope("user:provider")
				.scope("admin")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
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
