import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-confirmation.html',
  styleUrls: ['./order-confirmation.css']
})
export class OrderConfirmationComponent implements OnInit {
  order: any = null;
  loading = true;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.orderService.getOrderByIdBackend(+id).subscribe({
        next: (order) => {
          this.order = order;
          this.loading = false;
        },
        error: () => {
          this.error = true;
          this.loading = false;
        }
      });
    } else {
      this.error = true;
      this.loading = false;
    }
  }

  getServiceLabel(type: string): string {
    switch (type) {
      case 'TAKEAWAY': return 'Recoger en tienda';
      case 'TABLE': return 'En mesa';
      case 'DELIVERY': return 'Envío a domicilio';
      default: return type;
    }
  }

  getStateLabel(state: string): string {
    switch (state) {
      case 'PENDENT': return 'Pendiente';
      case 'PREPARING': return 'Preparando';
      case 'DELIVERED': return 'Entregado';
      case 'CANCELED': return 'Cancelado';
      default: return state;
    }
  }
}
