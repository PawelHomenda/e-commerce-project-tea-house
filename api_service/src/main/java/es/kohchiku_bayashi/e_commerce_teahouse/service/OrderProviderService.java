package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.exception.DataIntegrityException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderProvider;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderProviderRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.DetailOrderProviderRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.InvoiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderProviderService {
    
    private final OrderProviderRepository orderProviderRepository;
    private final DetailOrderProviderRepository detailOrderProviderRepository;
    private final InvoiceProviderRepository invoiceProviderRepository;
    private final ProviderService providerService;
    private final EmployeeService employeeService;
    
    public List<OrderProvider> findAll() {
        return orderProviderRepository.findAll();
    }
    
    public OrderProvider findById(Long id) {
        return orderProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }
    
    // ✅ Nuevo: Buscar órdenes por oauth2Id del proveedor
    public List<OrderProvider> findByProviderOauth2Id(String oauth2Id) {
        return orderProviderRepository.findByProviderOauth2Id(oauth2Id);
    }
    
    public OrderProvider save(OrderProvider orderProvider) {
        // ✅ Cargar el proveedor existente (obligatorio)
        if (orderProvider.getProvider() != null && orderProvider.getProvider().getId() != null) {
            orderProvider.setProvider(providerService.findById(orderProvider.getProvider().getId()));
        }
        
        // ✅ Cargar el empleado si se proporciona (opcional)
        if (orderProvider.getEmployee() != null && orderProvider.getEmployee().getId() != null) {
            orderProvider.setEmployee(employeeService.findById(orderProvider.getEmployee().getId()));
        }
        
        return orderProviderRepository.save(orderProvider);
    }
    
    public OrderProvider update(Long id, OrderProvider orderProvider) {
        OrderProvider existing = findById(id);
        
        // ✅ Actualizar proveedor si se proporciona
        if (orderProvider.getProvider() != null && orderProvider.getProvider().getId() != null) {
            existing.setProvider(providerService.findById(orderProvider.getProvider().getId()));
        }
        
        // ✅ Actualizar empleado si se proporciona
        if (orderProvider.getEmployee() != null && orderProvider.getEmployee().getId() != null) {
            existing.setEmployee(employeeService.findById(orderProvider.getEmployee().getId()));
        }
        
        existing.setOrderDate(orderProvider.getOrderDate());
        existing.setTotal(orderProvider.getTotal());
        existing.setObservations(orderProvider.getObservations());
        existing.setDiscountPercentage(orderProvider.getDiscountPercentage());
        
        return orderProviderRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        OrderProvider order = findById(id);
        
        // ✅ Verificar si tiene detalles asociados
        List<?> details = detailOrderProviderRepository.findByOrderProvider(order);
        if (!details.isEmpty()) {
            throw new DataIntegrityException(
                "No se puede eliminar el pedido. Tiene " + details.size() + " detalle(s) asociado(s). Elimina los detalles primero."
            );
        }
        
        // ✅ Verificar si tiene factura asociada
        if (order.getInvoiceProvider() != null) {
            throw new DataIntegrityException(
                "No se puede eliminar el pedido. Tiene una factura asociada. Elimina la factura primero."
            );
        }
        
        orderProviderRepository.deleteById(id);
    }
    
    public List<OrderProvider> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderProviderRepository.findByOrderDateBetween(startDate, endDate);
    }
    
    public List<OrderProvider> findByMonthAndYear(int month, int year) {
        return orderProviderRepository.findByMonthAndYear(month, year);
    }
    
    public Double getTotalCost() {
        return orderProviderRepository.getTotalCost();
    }
    
    public Double getTotalCostByMonth(int month) {
        return orderProviderRepository.getTotalCostByMonth(month);
    }

    public List<OrderProvider>  findByProviderOAuth2Id(String oauth2Id) {
        return orderProviderRepository.findByProviderOauth2Id(oauth2Id);
    }
}
