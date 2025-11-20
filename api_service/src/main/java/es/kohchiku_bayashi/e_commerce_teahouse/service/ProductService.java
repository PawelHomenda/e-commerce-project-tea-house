package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ProductCategory;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }
    
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    public Product update(Long id, Product product) {
        Product existing = findById(id);
        
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setMeasureUnit(product.getMeasureUnit());
        existing.setActive(product.getActive());
        
        return productRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    public List<Product> findByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }
    
    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrue();
    }
    
    public List<Product> findInactiveProducts() {
        return productRepository.findByActiveFalse();
    }
    
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Product> findAllOrderByPriceAsc() {
        return productRepository.findAllOrderByPriceAsc();
    }
    
    public List<Product> findAllOrderByPriceDesc() {
        return productRepository.findAllOrderByPriceDesc();
    }
    
    public Product deactivateProduct(Long id) {
        Product product = findById(id);
        product.setActive(false);
        return productRepository.save(product);
    }
    
    public Product activateProduct(Long id) {
        Product product = findById(id);
        product.setActive(true);
        return productRepository.save(product);
    }
}
