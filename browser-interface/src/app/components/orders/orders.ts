import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Order, OrderStatus, PagedResponse } from '../../models/order.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class OrdersComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  selectedOrder: Order | null = null;
  isLoading = true;
  isCancelling = false;
  isDownloadingInvoice = false;
  
  statusFilter: OrderStatus | '' = '';
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  
  successMessage = '';
  errorMessage = '';
  
  // Exponemos los tipos para el template
  OrderStatus = OrderStatus;
  
  private destroy$ = new Subject<void>();

  // Mapeo de estados
  statusLabels: { [key in OrderStatus]: string } = {
    [OrderStatus.PENDENT]: 'Pendiente',
    [OrderStatus.PREPARING]: 'En Preparación',
    [OrderStatus.DELIVERED]: 'Entregada',
    [OrderStatus.CANCELED]: 'Cancelada'
  };

  statusColors: { [key in OrderStatus]: string } = {
    [OrderStatus.PENDENT]: 'warning',
    [OrderStatus.PREPARING]: 'info',
    [OrderStatus.DELIVERED]: 'success',
    [OrderStatus.CANCELED]: 'danger'
  };

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrders(): void {
    this.isLoading = true;
    
    if (this.statusFilter) {
      this.orderService.getOrderHistory(this.statusFilter, this.currentPage, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => this.handleOrdersResponse(response),
          error: (error) => this.handleError(error, 'Error al cargar las órdenes')
        });
    } else {
      this.orderService.getUserOrders(this.currentPage, this.pageSize)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => this.handleOrdersResponse(response),
          error: (error) => this.handleError(error, 'Error al cargar las órdenes')
        });
    }
  }

  private handleOrdersResponse(response: PagedResponse<Order>): void {
    this.orders = response.content;
    this.totalPages = response.totalPages;
    this.isLoading = false;
    
    if (this.orders.length === 0) {
      this.errorMessage = 'No hay órdenes para mostrar';
    } else {
      this.errorMessage = '';
    }
  }

  viewOrderDetails(order: Order): void {
    this.selectedOrder = order;
  }

  closeOrderDetails(): void {
    this.selectedOrder = null;
  }

  cancelOrder(order: Order): void {
    if (confirm(`¿Estás seguro de que deseas cancelar la orden #${order.orderNumber}?`)) {
      this.isCancelling = true;
      this.orderService.cancelOrder(order.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (updatedOrder) => {
            const index = this.orders.findIndex(o => o.id === order.id);
            if (index !== -1) {
              this.orders[index] = updatedOrder;
            }
            this.successMessage = 'Orden cancelada exitosamente';
            this.isCancelling = false;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => {
            this.handleError(error, 'Error al cancelar la orden');
            this.isCancelling = false;
          }
        });
    }
  }

  downloadInvoice(order: Order): void {
    this.isDownloadingInvoice = true;
    this.orderService.downloadInvoice(order.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `factura_${order.orderNumber}.pdf`;
          link.click();
          window.URL.revokeObjectURL(url);
          this.isDownloadingInvoice = false;
        },
        error: (error) => {
          this.handleError(error, 'Error al descargar la factura');
          this.isDownloadingInvoice = false;
        }
      });
  }

  reorder(order: Order): void {
    if (confirm(`¿Deseas crear una nueva orden con los productos de la orden #${order.orderNumber}?`)) {
      this.orderService.reorder(order.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.successMessage = 'Nueva orden creada. Redirigiendo al carrito...';
            setTimeout(() => {
              this.router.navigate(['/cart']);
            }, 2000);
          },
          error: (error) => {
            this.handleError(error, 'Error al crear la nueva orden');
          }
        });
    }
  }

  onStatusFilterChange(): void {
    this.currentPage = 0;
    this.loadOrders();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadOrders();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadOrders();
    }
  }

  private handleError(error: any, message: string): void {
    this.errorMessage = error.error?.message || message;
    this.isLoading = false;
    console.error('Error:', error);
  }

  getStatusLabel(status: OrderStatus): string {
    return this.statusLabels[status] || status;
  }

  getStatusColor(status: OrderStatus): string {
    return this.statusColors[status] || 'secondary';
  }

  canCancelOrder(order: Order): boolean {
    return order.status === OrderStatus.PENDENT;
  }

  canReorder(order: Order): boolean {
    return order.status === OrderStatus.DELIVERED || order.status === OrderStatus.CANCELED;
  }

  getOrderStatusIcon(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDENT:
        return '⏳';
      case OrderStatus.PREPARING:
        return '⚙️';
      case OrderStatus.DELIVERED:
        return '✓✓';
      case OrderStatus.CANCELED:
        return '✗';
      default:
        return '?';
    }
  }
}
