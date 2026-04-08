import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { 
  Order, 
  CreateOrderDTO, 
  OrderStatus, 
  PagedResponse,
  OrderSummary 
} from '../models/order.model';

/**
 * Servicio de Órdenes/Pedidos
 * Maneja todas las operaciones relacionadas con órdenes
 * Equivalente al OrderService de Spring Boot
 */
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;
  private clientsApiUrl = `${environment.apiUrl}/orders/clients`;
  private providersApiUrl = `${environment.apiUrl}/orders/providers`;

  // Estado de órdenes
  private ordersSubject = new BehaviorSubject<Order[]>([]);
  public orders$ = this.ordersSubject.asObservable();

  // Orden actual (para vista de detalle)
  private currentOrderSubject = new BehaviorSubject<Order | null>(null);
  public currentOrder$ = this.currentOrderSubject.asObservable();

  constructor(private http: HttpClient) { }

  /**
   * Obtener las órdenes del usuario actual (desde /api/orders/clients)
   */
  getMyOrders(): Observable<any[]> {
    return this.http.get<any[]>(this.clientsApiUrl);
  }

  /**
   * Obtener las órdenes del proveedor actual (desde /api/orders/providers)
   */
  getMyProviderOrders(): Observable<any[]> {
    return this.http.get<any[]>(this.providersApiUrl);
  }

  /**
   * Obtener todas las órdenes del usuario actual con paginación
   */
  getUserOrders(page: number = 0, size: number = 10): Observable<PagedResponse<Order>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PagedResponse<Order>>(this.apiUrl, { params }).pipe(
      tap(response => {
        this.ordersSubject.next(response.content);
      })
    );
  }

  /**
   * Obtener orden por ID
   */
  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`).pipe(
      tap(order => {
        this.currentOrderSubject.next(order);
      })
    );
  }

  /**
   * Obtener orden por número de pedido
   */
  getOrderByNumber(orderNumber: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/number/${orderNumber}`).pipe(
      tap(order => {
        this.currentOrderSubject.next(order);
      })
    );
  }

  /**
   * Crear nueva orden desde el carrito
   */
  createOrder(createOrderDTO: CreateOrderDTO): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, createOrderDTO).pipe(
      tap(order => {
        const currentOrders = this.ordersSubject.value;
        this.ordersSubject.next([order, ...currentOrders]);
      })
    );
  }

  /**
   * Obtener resumen de orden (útil para confirmación antes de crear)
   */
  getOrderSummary(): Observable<OrderSummary> {
    return this.http.get<OrderSummary>(`${this.apiUrl}/summary`);
  }

  /**
   * Actualizar estado de orden (solo admin)
   */
  updateOrderStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/clients/${id}/state?newState=${status}`, {}).pipe(
      tap(order => {
        const orders = this.ordersSubject.value;
        const index = orders.findIndex(o => o.id === id);
        if (index !== -1) {
          orders[index] = order;
          this.ordersSubject.next([...orders]);
        }
      })
    );
  }

  /**
   * Cancelar orden
   */
  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/${id}/cancel`, {}).pipe(
      tap(order => {
        const orders = this.ordersSubject.value;
        const index = orders.findIndex(o => o.id === id);
        if (index !== -1) {
          orders[index] = order;
          this.ordersSubject.next([...orders]);
        }
      })
    );
  }

  /**
   * Reordenar (crear nueva orden con los mismos productos)
   */
  reorder(orderId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/${orderId}/reorder`, {});
  }

  /**
   * Obtener historial de órdenes filtrado
   */
  getOrderHistory(status?: OrderStatus, page: number = 0, size: number = 10): Observable<PagedResponse<Order>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<PagedResponse<Order>>(`${this.apiUrl}/history`, { params });
  }

  /**
   * Obtener todas las órdenes (solo admin)
   */
  getAllOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients`);
  }

  /**
   * Obtener órdenes filtradas por estado (solo admin)
   */
  getOrdersByStatus(status: OrderStatus): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients/state/${status}`);
  }

  /**
   * Buscar órdenes por número o usuario (solo admin)
   */
  searchOrders(searchTerm: string, page: number = 0, size: number = 20): Observable<PagedResponse<Order>> {
    const params = new HttpParams()
      .set('q', searchTerm)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PagedResponse<Order>>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Descargar factura PDF
   */
  downloadInvoice(orderId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${orderId}/invoice`, { responseType: 'blob' });
  }

  /**
   * Enviar email de confirmación
   */
  sendConfirmationEmail(orderId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${orderId}/send-email`, {});
  }

  /**
   * Obtener órdenes en caché
   */
  getCachedOrders(): Order[] {
    return this.ordersSubject.value;
  }

  /**
   * Limpiar caché de órdenes
   */
  clearCache(): void {
    this.ordersSubject.next([]);
    this.currentOrderSubject.next(null);
  }

  /**
   * Crear pedido enviando el formato que espera el backend (OrderClient entity)
   */
  createOrderBackend(orderPayload: any): Observable<any> {
    return this.http.post<any>(this.clientsApiUrl, orderPayload);
  }

  /**
   * Obtener pedido por ID desde el endpoint de clients
   */
  getOrderByIdBackend(id: number): Observable<any> {
    return this.http.get<any>(`${this.clientsApiUrl}/${id}`);
  }
}
