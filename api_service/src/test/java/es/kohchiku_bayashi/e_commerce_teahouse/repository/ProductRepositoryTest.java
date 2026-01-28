package es.kohchiku_bayashi.e_commerce_teahouse.repository;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration," +
        "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration",
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.sql.init.mode=never"
})
@DisplayName("Pruebas del Repositorio de Productos")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .name("Té Bebida Premium")
                .description("Bebida de té de alta calidad, 100% orgánico")
                .category(ProductCategory.DRINK)
                .price(12.50)
                .measureUnit("g")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Debe crear un nuevo producto correctamente")
    void testCreateProduct() {
        // Act
        Product savedProduct = productRepository.save(testProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Té Bebida Premium", savedProduct.getName());
        assertEquals(12.50, savedProduct.getPrice());
        assertTrue(savedProduct.getActive());
    }

    @Test
    @DisplayName("Debe obtener un producto por ID")
    void testGetProductById() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("Té Bebida Premium", foundProduct.get().getName());
        assertEquals(ProductCategory.DRINK, foundProduct.get().getCategory());
    }

    @Test
    @DisplayName("Debe actualizar un producto existente")
    void testUpdateProduct() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        savedProduct.setName("Té Bebida Premium Plus");
        savedProduct.setPrice(15.99);
        Product updatedProduct = productRepository.save(savedProduct);

        // Assert
        assertEquals("Té Bebida Premium Plus", updatedProduct.getName());
        assertEquals(15.99, updatedProduct.getPrice());
    }

    @Test
    @DisplayName("Debe eliminar un producto por ID")
    void testDeleteProductById() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Long productId = savedProduct.getId();

        // Act
        productRepository.deleteById(productId);

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Debe eliminar un producto por entidad")
    void testDeleteProduct() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        productRepository.delete(savedProduct);

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Debe contar todos los productos")
    void testCountAllProducts() {
        // Arrange
        productRepository.save(testProduct);
        Product product2 = Product.builder()
                .name("Postre Delicado")
                .description("Postre artesanal")
                .category(ProductCategory.DESSERT)
                .price(10.00)
                .measureUnit("ud")
                .active(true)
                .build();
        productRepository.save(product2);

        // Act
        long count = productRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe un producto")
    void testProductExists() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        boolean exists = productRepository.existsById(savedProduct.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe retornar false cuando el producto no existe")
    void testProductNotExists() {
        // Act
        boolean exists = productRepository.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Debe recuperar todos los productos")
    void testFindAllProducts() {
        // Arrange
        productRepository.save(testProduct);
        Product product2 = Product.builder()
                .name("Postre Premium")
                .description("Postre premium")
                .category(ProductCategory.DESSERT)
                .price(18.50)
                .build();
        productRepository.save(product2);

        // Act
        var products = productRepository.findAll();

        // Assert
        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("Debe actualizar solo el precio de un producto")
    void testPartialUpdateProduct() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);
        Double newPrice = 20.00;

        // Act
        savedProduct.setPrice(newPrice);
        Product updatedProduct = productRepository.save(savedProduct);

        // Assert
        assertEquals(newPrice, updatedProduct.getPrice());
        assertEquals("Té Bebida Premium", updatedProduct.getName());
    }

    @Test
    @DisplayName("Debe cambiar el estado activo de un producto")
    void testDeactivateProduct() {
        // Arrange
        Product savedProduct = productRepository.save(testProduct);

        // Act
        savedProduct.setActive(false);
        Product deactivatedProduct = productRepository.save(savedProduct);

        // Assert
        assertFalse(deactivatedProduct.getActive());
    }
}
