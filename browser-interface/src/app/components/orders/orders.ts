import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { OrderStatus } from '../../models/order.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class OrdersComponent implements OnInit, OnDestroy {
  allOrders: any[] = [];
  orders: any[] = [];
  selectedOrder: any | null = null;
  isLoading = true;
  isProvider = false;

  statusFilter: string = '';
  
  successMessage = '';
  errorMessage = '';
  
  OrderStatus = OrderStatus;
  
  private destroy$ = new Subject<void>();

  statusLabels: { [key: string]: string } = {
    'PENDENT': $localize`:orders.statusPendent@@orders.statusPendent:Pendiente`,
    'PREPARING': $localize`:orders.statusPreparing@@orders.statusPreparing:En Preparación`,
    'DELIVERED': $localize`:orders.statusDelivered@@orders.statusDelivered:Entregada`,
    'CANCELED': $localize`:orders.statusCanceled@@orders.statusCanceled:Cancelada`
  };

  statusColors: { [key: string]: string } = {
    'PENDENT': 'warning',
    'PREPARING': 'info',
    'DELIVERED': 'success',
    'CANCELED': 'danger'
  };

  serviceTypeLabels: { [key: string]: string } = {
    'TAKEAWAY': $localize`:orders.serviceTakeaway@@orders.serviceTakeaway:Para Llevar`,
    'TABLE': $localize`:orders.serviceTable@@orders.serviceTable:En Mesa`,
    'DELIVERY': $localize`:orders.serviceDelivery@@orders.serviceDelivery:A Domicilio`
  };

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
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
    this.errorMessage = '';

    const user = this.authService.getCurrentUser();
    this.isProvider = user?.role === 'PROVIDER';
    const orders$ = this.isProvider
      ? this.orderService.getMyProviderOrders()
      : this.orderService.getMyOrders();

    orders$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (orders) => {
          this.allOrders = orders;
          this.applyFilter();
          this.isLoading = false;
        },
        error: (error) => this.handleError(error, $localize`:orders.loadError@@orders.loadError:Error al cargar las órdenes`)
      });
  }

  applyFilter(): void {
    if (this.statusFilter) {
      this.orders = this.allOrders.filter(o => o.orderState === this.statusFilter);
    } else {
      this.orders = [...this.allOrders];
    }
  }

  viewOrderDetails(order: any): void {
    this.selectedOrder = order;
  }

  closeOrderDetails(): void {
    this.selectedOrder = null;
  }

  cancelOrder(order: any): void {
    if (confirm($localize`:orders.confirmCancel@@orders.confirmCancel:¿Estás seguro de que deseas cancelar el pedido #${order.id}:orderId:?`)) {
      this.orderService.updateOrderStatus(order.id, OrderStatus.CANCELED)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            order.orderState = 'CANCELED';
            this.applyFilter();
            this.successMessage = $localize`:orders.cancelSuccess@@orders.cancelSuccess:Pedido cancelado exitosamente`;
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (error) => this.handleError(error, $localize`:orders.cancelError@@orders.cancelError:Error al cancelar el pedido`)
        });
    }
  }

  onStatusFilterChange(): void {
    this.applyFilter();
  }

  private handleError(error: any, message: string): void {
    this.errorMessage = error.error?.message || message;
    this.isLoading = false;
    console.error('Error:', error);
  }

  getStatusLabel(status: string): string {
    return this.statusLabels[status] || status;
  }

  getStatusColor(status: string): string {
    return this.statusColors[status] || 'secondary';
  }

  getServiceTypeLabel(type: string): string {
    return this.serviceTypeLabels[type] || type;
  }

  canCancelOrder(order: any): boolean {
    return order.orderState === 'PENDENT';
  }

  getOrderStatusIcon(status: string): string {
    switch (status) {
      case 'PENDENT': return '⏳';
      case 'PREPARING': return '⚙️';
      case 'DELIVERED': return '✓✓';
      case 'CANCELED': return '✗';
      default: return '?';
    }
  }

  getItemCount(order: any): number {
    return order.detailOrderClients?.length || order.detailOrderProviders?.length || 0;
  }
}
