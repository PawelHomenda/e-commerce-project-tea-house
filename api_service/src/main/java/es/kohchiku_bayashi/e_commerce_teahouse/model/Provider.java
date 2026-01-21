package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;
    
    @NotBlank(message = "El contacto es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String contact;
    
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 10)
    @Pattern(regexp = "^[0-9]+$")
    @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String address;
    
    @NotBlank(message = "El ID OAuth2 es obligatorio")
    @Column(name = "oauth2_id", nullable = false, unique = true, length = 255)
    private String oauth2Id;
    
    @Column(name = "oauth2_provider", length = 50)
    private String oauth2Provider;
    
    // ✅ Ignoramos esta colección para evitar ciclos
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<OrderProvider> orderProviders;
}