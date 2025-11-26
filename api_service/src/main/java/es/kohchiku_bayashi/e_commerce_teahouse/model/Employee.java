package es.kohchiku_bayashi.e_commerce_teahouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    
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
    
    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private Double salary;
    
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 15)
    @Pattern(regexp = "^[0-9]+$")
    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    
    // ✅ Ignoramos completamente estas colecciones para evitar ciclos
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<OrderClient> orderClients;
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<OrderProvider> orderProviders;
}