package es.kohchiku_bayashi.e_commerce_teahouse.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Profile("!test")
public class SecurityConfig {
    
    /**
     * AuthenticationEntryPoint personalizado para manejar errores de token expirado.
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException authException) throws IOException, ServletException {
                
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                
                String message = "Token expirado o inválido. Por favor, inicia sesión nuevamente.";
                
                // Personaliza el mensaje según el tipo de error
                if (authException.getMessage() != null) {
                    String error = authException.getMessage().toLowerCase();
                    if (error.contains("expired")) {
                        message = "El token ha expirado. Por favor, solicita uno nuevo en el servidor de autenticación.";
                    } else if (error.contains("invalid") || error.contains("malformed")) {
                        message = "El token es inválido o está mal formado. Por favor, inicia sesión nuevamente.";
                    }
                }
                
                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", java.time.LocalDateTime.now().toString());
                body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                body.put("error", "Unauthorized");
                body.put("message", message);
                body.put("path", request.getServletPath());
                
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(body));
            }
        };
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authHttp) -> authHttp
                        // PÚBLICOS - Swagger y documentación
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authorized").permitAll()

                        // ============================================
                        // PRODUCTOS - Lectura todos, escritura solo ADMIN
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyAuthority("SCOPE_user:client", "SCOPE_user:employee", "SCOPE_user:provider", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("SCOPE_admin")

                        // ============================================
                        // PEDIDOS DE CLIENTES - Clientes ven/crean solo los suyos, ADMIN ve todos
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients").hasAnyAuthority("SCOPE_user:client","SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients/**").hasAnyAuthority("SCOPE_user:client","SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients/admin/all").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/orders/clients/**").hasAnyAuthority("SCOPE_user:client", "SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/clients/**").hasAuthority("SCOPE_admin")

                        // ============================================
                        // PEDIDOS DE PROVEEDORES - Proveedores ven/crean solo los suyos, ADMIN/EMPLOYEE ve todos
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/orders/providers").hasAnyAuthority("SCOPE_user:provider", "SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.GET, "/api/orders/providers/**").hasAnyAuthority("SCOPE_user:provider", "SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/orders/providers/**").hasAnyAuthority("SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/providers/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/providers/**").hasAuthority("SCOPE_admin")

                        // ============================================
                        // PROVEEDORES - Solo ADMIN puede gestionar
                        // ============================================
                        .requestMatchers("/api/providers/**").hasAuthority("SCOPE_admin")
                        
                        // ============================================
                        // INVENTARIO - Solo ADMIN y EMPLOYEE
                        // ============================================
                        .requestMatchers("/api/inventory/**").hasAnyAuthority("SCOPE_user:employee", "SCOPE_admin")
                        
                        // ============================================
                        // EMPLEADOS - Solo ADMIN y EMPLOYEE
                        // ============================================
                        .requestMatchers("/api/employees/**").hasAnyAuthority("SCOPE_user:employee", "SCOPE_admin")
                        
                        // ============================================
                        // FACTURAS DE CLIENTES - Cliente ve la suya, ADMIN/EMPLOYEE ve todo
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/invoices/clients/**")
                        .hasAnyAuthority("SCOPE_user:client", "SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        
                        // ============================================
                        // FACTURAS DE PROVEEDORES - Proveedor ve la suya, ADMIN/EMPLOYEE ve todo
                        // ============================================
                        .requestMatchers(HttpMethod.GET, "/api/invoices/providers/**")
                        .hasAnyAuthority("SCOPE_user:provider", "SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        
                        .anyRequest().authenticated())
                        .csrf(csrf-> csrf.disable())
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(exceptionHandling -> exceptionHandling
                            .authenticationEntryPoint(customAuthenticationEntryPoint())
                            .accessDeniedHandler((request, response, accessDeniedException) -> {
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                
                                Map<String, Object> body = new HashMap<>();
                                body.put("timestamp", java.time.LocalDateTime.now().toString());
                                body.put("status", HttpServletResponse.SC_FORBIDDEN);
                                body.put("error", "Access Denied");
                                body.put("message", "No tienes permisos para acceder a este recurso.");
                                body.put("path", request.getServletPath());
                                
                                ObjectMapper objectMapper = new ObjectMapper();
                                response.getWriter().write(objectMapper.writeValueAsString(body));
                            }))
                        .oauth2ResourceServer(resourceServer -> resourceServer
                            .jwt(withDefaults())
                            .authenticationEntryPoint(customAuthenticationEntryPoint()));

        return http.build();
    }
}
