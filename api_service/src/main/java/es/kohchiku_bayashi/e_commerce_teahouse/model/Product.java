package es.kohchiku_bayashi.e_commerce_teahouse.model;

import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ProductCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "La categoría es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private Double price;
    
    @Size(max = 10)
    @Column(name = "measure_unit", length = 10)
    private String measureUnit;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean active = true;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Inventory inventory;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DetailOrderProvider> detailOrderProviders;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<DetailOrderClient> detailOrderClients;
}

