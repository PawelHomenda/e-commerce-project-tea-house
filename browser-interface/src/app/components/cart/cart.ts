import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { Cart, CartItem } from '../../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;

  constructor(public cartService: CartService) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => this.cart = cart);
  }

  updateQuantity(item: CartItem, delta: number): void {
    const newQty = item.quantity + delta;
    if (newQty <= 0) {
      this.removeItem(item.id);
    } else {
      this.cartService.updateItemQuantity({ cartItemId: item.id, quantity: newQty }).subscribe();
    }
  }

  removeItem(cartItemId: number): void {
    this.cartService.removeItem(cartItemId).subscribe();
  }

  clearCart(): void {
    this.cartService.clearCart().subscribe();
  }

  getItemSubtotal(item: CartItem): number {
    return item.price * item.quantity;
  }
}
