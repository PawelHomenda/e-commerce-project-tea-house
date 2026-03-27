import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { Cart } from '../../models/cart.model';
import { PaymentMethod } from '../../models/order.model';

type ServiceType = 'TAKEAWAY' | 'TABLE' | 'DELIVERY';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class CheckoutComponent implements OnInit {
  cart: Cart | null = null;
  currentStep = 1;
  isSubmitting = false;
  errorMessage = '';

  // Step 1 - Service Type
  serviceType: ServiceType = 'TAKEAWAY';
  tableNumber = '';
  clientAddress = '';
  clientId: number | null = null;

  // Step 2 - Payment
  paymentMethod: PaymentMethod = PaymentMethod.CREDIT_CARD;
  PaymentMethod = PaymentMethod;

  // Dummy card form
  cardNumber = '';
  cardExpiry = '';
  cardCvv = '';
  cardName = '';

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
      if (!cart || cart.items.length === 0) {
        this.router.navigate(['/cart']);
      }
    });

    // Load client profile to get address and ID
    this.authService.getCurrentUserProfile().subscribe({
      next: (user: any) => {
        this.clientAddress = user.address || user.email || '';
        this.clientId = user.id || null;
      },
      error: () => {}
    });
  }

  nextStep(): void {
    if (this.currentStep < 3) {
      this.currentStep++;
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  goToStep(step: number): void {
    if (step <= this.currentStep) {
      this.currentStep = step;
    }
  }

  isCardPayment(): boolean {
    return this.paymentMethod === PaymentMethod.CREDIT_CARD ||
           this.paymentMethod === PaymentMethod.DEBIT_CARD;
  }

  getPaymentLabel(): string {
    switch (this.paymentMethod) {
      case PaymentMethod.CREDIT_CARD: return 'Tarjeta de Crédito';
      case PaymentMethod.DEBIT_CARD: return 'Tarjeta de Débito';
      case PaymentMethod.PAYPAL: return 'PayPal';
      case PaymentMethod.BANK_TRANSFER: return 'Transferencia Bancaria';
      case PaymentMethod.CASH_ON_DELIVERY: return 'Pago Contra Entrega';
      default: return '';
    }
  }

  getServiceLabel(): string {
    switch (this.serviceType) {
      case 'TAKEAWAY': return 'Recoger en tienda';
      case 'TABLE': return `Mesa ${this.tableNumber || '—'}`;
      case 'DELIVERY': return 'Envío a domicilio';
    }
  }

  formatCardNumber(): void {
    this.cardNumber = this.cardNumber
      .replace(/\D/g, '')
      .replace(/(.{4})/g, '$1 ')
      .trim()
      .substring(0, 19);
  }

  formatExpiry(): void {
    this.cardExpiry = this.cardExpiry
      .replace(/\D/g, '')
      .replace(/^(\d{2})(\d)/, '$1/$2')
      .substring(0, 5);
  }

  confirmOrder(): void {
    if (!this.cart || this.cart.items.length === 0 || this.isSubmitting) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    const orderPayload = {
      client: { id: this.clientId },
      serviceType: this.serviceType,
      orderState: 'PENDENT',
      orderDate: new Date().toISOString().split('T')[0],
      detailOrderClients: this.cart.items.map(item => ({
        product: { id: item.product.id },
        quantity: item.quantity,
        unitPrice: item.price
      }))
    };

    this.orderService.createOrderBackend(orderPayload).subscribe({
      next: (order: any) => {
        this.cartService.clearCart().subscribe();
        this.isSubmitting = false;
        this.router.navigate(['/order-confirmation', order.id]);
      },
      error: (err: any) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Error al confirmar el pedido. Inténtalo de nuevo.';
      }
    });
  }
}
