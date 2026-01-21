package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 15)
    @Pattern(regexp = "^[0-9]+$")
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;
    
    @Column(name = "address", length = 100)
    private String address;
    
    @NotBlank(message = "El ID OAuth2 es obligatorio")
    @Column(name = "oauth2_id", nullable = false, unique = true, length = 255)
    private String oauth2Id;
    
    @Column(name = "oauth2_provider", length = 50)
    private String oauth2Provider;
    
    // ✅ Relación con los pedidos del cliente
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<OrderClient> orderClients;
}
