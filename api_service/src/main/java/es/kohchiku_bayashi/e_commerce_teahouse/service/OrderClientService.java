package es.kohchiku_bayashi.e_commerce_teahouse.service;

import es.kohchiku_bayashi.e_commerce_teahouse.exception.ResourceNotFoundException;
import es.kohchiku_bayashi.e_commerce_teahouse.exception.DataIntegrityException;
import es.kohchiku_bayashi.e_commerce_teahouse.model.OrderClient;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.OrderState;
import es.kohchiku_bayashi.e_commerce_teahouse.model.enums.ServiceType;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.OrderClientRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.DetailOrderClientRepository;
import es.kohchiku_bayashi.e_commerce_teahouse.repository.InvoiceClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderClientService {
    
    private final OrderClientRepository orderClientRepository;
    private final DetailOrderClientRepository detailOrderClientRepository;
    private final InvoiceClientRepository invoiceClientRepository;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    
    public List<OrderClient> findAll() {
        return orderClientRepository.findAll();
    }
    
    public OrderClient findById(Long id) {
        return orderClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }
    
    // ✅ Nuevo: Buscar pedidos por oauth2Id del cliente
    public List<OrderClient> findByClientOauth2Id(String oauth2Id) {
        return orderClientRepository.findByClientOauth2Id(oauth2Id);
    }
    
    public OrderClient save(OrderClient orderClient) {
        // ✅ Cargar el cliente existente (obligatorio)
        if (orderClient.getClient() != null && orderClient.getClient().getId() != null) {
            orderClient.setClient(clientService.findById(orderClient.getClient().getId()));
        }
        
        // ✅ Cargar el empleado si se proporciona (opcional)
        if (orderClient.getEmployee() != null && orderClient.getEmployee().getId() != null) {
            orderClient.setEmployee(employeeService.findById(orderClient.getEmployee().getId()));
        }
        
        return orderClientRepository.save(orderClient);
    }
    
    public OrderClient update(Long id, OrderClient orderClient) {
        OrderClient existing = findById(id);
        
        // ✅ Actualizar cliente si se proporciona
        if (orderClient.getClient() != null && orderClient.getClient().getId() != null) {
            existing.setClient(clientService.findById(orderClient.getClient().getId()));
        }
        
        // ✅ Actualizar empleado si se proporciona
        if (orderClient.getEmployee() != null && orderClient.getEmployee().getId() != null) {
            existing.setEmployee(employeeService.findById(orderClient.getEmployee().getId()));
        }
        
        existing.setOrderDate(orderClient.getOrderDate());
        existing.setOrderState(orderClient.getOrderState());
        existing.setServiceType(orderClient.getServiceType());
        existing.setDiscountPercentage(orderClient.getDiscountPercentage());
        
        return orderClientRepository.save(existing);
    }
    
    public void deleteById(Long id) {
        OrderClient order = findById(id);
        
        // ✅ Verificar si tiene detalles asociados
        List<?> details = detailOrderClientRepository.findByOrderClient(order);
        if (!details.isEmpty()) {
            throw new DataIntegrityException(
                "No se puede eliminar el pedido. Tiene " + details.size() + " detalle(s) asociado(s). Elimina los detalles primero."
            );
        }
        
        // ✅ Verificar si tiene factura asociada
        if (order.getInvoiceClient() != null) {
            throw new DataIntegrityException(
                "No se puede eliminar el pedido. Tiene una factura asociada. Elimina la factura primero."
            );
        }
        
        orderClientRepository.deleteById(id);
    }
    
    public List<OrderClient> findByOrderState(OrderState orderState) {
        return orderClientRepository.findByOrderState(orderState);
    }
    
    public List<OrderClient> findByServiceType(ServiceType serviceType) {
        return orderClientRepository.findByServiceType(serviceType);
    }
    
    public List<OrderClient> findActiveOrders() {
        return orderClientRepository.findActiveOrders();
    }
    
    public OrderClient updateOrderState(Long id, OrderState newState) {
        OrderClient order = findById(id);
        order.setOrderState(newState);
        return orderClientRepository.save(order);
    }
    
    public List<OrderClient> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderClientRepository.findByOrderDateBetween(startDate, endDate);
    }
}
