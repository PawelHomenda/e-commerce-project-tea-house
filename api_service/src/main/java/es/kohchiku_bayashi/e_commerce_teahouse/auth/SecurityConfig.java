package es.kohchiku_bayashi.e_commerce_teahouse.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authHttp) -> authHttp
                        // PÚBLICOS - Swagger y documentación
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authorized").permitAll()

                        // PRODUCTOS - Lectura todos, escritura solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyAuthority("SCOPE_user", "SCOPE_user:client", "SCOPE_user:employee", "SCOPE_user:provider", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("SCOPE_admin")

                        // PEDIDOS DE CLIENTES
                        // Clientes: ven/crean solo sus pedidos
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients").hasAnyAuthority("SCOPE_user:client", "SCOPE_admin")
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients/**").hasAnyAuthority("SCOPE_user:client", "SCOPE_admin")
                        // ADMIN: ve todos
                        .requestMatchers(HttpMethod.GET, "/api/orders/clients/admin/all").hasAuthority("SCOPE_admin")
                        // Empleados: pueden gestionar (PUT, DELETE)
                        .requestMatchers(HttpMethod.POST, "/api/orders/clients/**").hasAnyAuthority("SCOPE_user:client", "SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/clients/**").hasAuthority("SCOPE_admin")

                        // PEDIDOS DE PROVEEDORES - Solo EMPLOYEE y ADMIN
                        .requestMatchers("/api/orders/providers/**").hasAnyAuthority("SCOPE_user:employee", "SCOPE_admin")

                        // PROVEEDORES - Solo ADMIN
                        .requestMatchers("/api/providers/**").hasAuthority("SCOPE_admin")
                        
                        // INVENTARIO - Solo ADMIN y EMPLOYEE
                        .requestMatchers("/api/inventory/**").hasAnyAuthority("SCOPE_user:employee", "SCOPE_admin")
                        
                        // EMPLEADOS - Solo ADMIN
                        .requestMatchers("/api/employees/**").hasAuthority("SCOPE_admin")
                        
                        // FACTURAS DE CLIENTES - Cliente ve la suya, ADMIN todo, EMPLOYEE gestiona
                        .requestMatchers(HttpMethod.GET, "/api/invoices/clients/**")
                        .hasAnyAuthority("SCOPE_user:client", "SCOPE_user:employee", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/invoices/clients/**").hasAuthority("SCOPE_admin")
                        
                        // FACTURAS DE PROVEEDORES - Proveedor ve la suya, ADMIN todo
                        .requestMatchers(HttpMethod.GET, "/api/invoices/providers/**")
                        .hasAnyAuthority("SCOPE_user:provider", "SCOPE_admin")
                        .requestMatchers(HttpMethod.POST, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.PUT, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/invoices/providers/**").hasAuthority("SCOPE_admin")
                        
                        .anyRequest().authenticated())
                        .csrf(csrf-> csrf.disable())
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .oauth2ResourceServer( resourceServer -> resourceServer.jwt(withDefaults()));

        return http.build();
    }
}
