package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.model.Inventory;
import es.kohchiku_bayashi.e_commerce_teahouse.model.Product;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }
    
    public Inventory findById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con id: " + id));
    }
    
    public Inventory findByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para el producto: " + productId));
    }
    
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    public Inventory update(Long id, Inventory inventory) {
        Inventory existing = findById(id);
        
        existing.setCurrentQuantity(inventory.getCurrentQuantity());
        existing.setMinimumQuantity(inventory.getMinimumQuantity());
        
        return inventoryRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Inventario no encontrado con id: " + id);
        }
        inventoryRepository.deleteById(id);
    }
    
    public List<Inventory> findLowStockProducts() {
        return inventoryRepository.findLowStockProductsWithDetails();
    }
    
    public Inventory updateStock(Long productId, Integer quantity) {
        Inventory inventory = findByProductId(productId);
        inventory.setCurrentQuantity(inventory.getCurrentQuantity() + quantity);
        return inventoryRepository.save(inventory);
    }
    
    public Inventory reduceStock(Long productId, Integer quantity) {
        Inventory inventory = findByProductId(productId);
        int newQuantity = inventory.getCurrentQuantity() - quantity;
        
        if (newQuantity < 0) {
            throw new RuntimeException("Stock insuficiente para el producto: " + productId);
        }
        
        inventory.setCurrentQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }
}

