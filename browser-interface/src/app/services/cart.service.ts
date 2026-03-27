import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { Cart, CartItem, AddToCartDTO, UpdateCartItemDTO } from '../models/cart.model';
import { Product } from '../models/product.model';

/**
 * Servicio de Carrito de Compras
 * Maneja todas las operaciones del carrito usando localStorage
 * Almacenamiento local sin conexión a servidor
 */
@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly CART_KEY = 'teahouse_cart';

  // Estado del carrito en tiempo real
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  // Contador de items
  private itemCountSubject = new BehaviorSubject<number>(0);
  public itemCount$ = this.itemCountSubject.asObservable();

  constructor() {
    // Cargar carrito al iniciar
    this.loadCart();
  }

  private loadCart(): void {
    const cartData = localStorage.getItem(this.CART_KEY);
    if (cartData) {
      try {
        const cart: Cart = JSON.parse(cartData);
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      } catch (error) {
        console.error('Error al cargar carrito desde localStorage:', error);
        this.initializeEmptyCart();
      }
    } else {
      this.initializeEmptyCart();
    }
  }

  private initializeEmptyCart(): void {
    const emptyCart: Cart = {
      id: 0,
      items: [],
      itemCount: 0,
      total: 0,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    this.cartSubject.next(emptyCart);
    this.updateItemCount(emptyCart);
  }

  private saveCart(cart: Cart): void {
    localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
  }

  getCart(): Observable<Cart> {
    const cart = this.cartSubject.value;
    return of(cart || this.createEmptyCart());
  }

  private createEmptyCart(): Cart {
    return {
      id: 0,
      items: [],
      itemCount: 0,
      total: 0,
      createdAt: new Date(),
      updatedAt: new Date()
    };
  }

  addToCart(addToCartDTO: AddToCartDTO, product?: Product): Observable<Cart> {
    const cart = this.cartSubject.value || this.createEmptyCart();
    
    const existingItem = cart.items.find(item => item.product.id === addToCartDTO.productId);
    
    if (existingItem) {
      existingItem.quantity += addToCartDTO.quantity;
    } else {
      const newItem: CartItem = {
        id: Date.now(),
        product: product || { id: addToCartDTO.productId } as any,
        quantity: addToCartDTO.quantity,
        price: addToCartDTO.price || 0
      };
      cart.items.push(newItem);
    }
    
    this.updateCartTotals(cart);
    this.cartSubject.next(cart);
    this.saveCart(cart);
    this.updateItemCount(cart);
    
    return of(cart);
  }

  addProduct(productId: number, quantity: number = 1, product?: Product): Observable<Cart> {
    return this.addToCart({ productId, quantity, price: product?.price }, product);
  }

  updateItemQuantity(updateDTO: UpdateCartItemDTO): Observable<Cart> {
    const cart = this.cartSubject.value;
    if (!cart) return of(this.createEmptyCart());
    
    const item = cart.items.find(i => i.id === updateDTO.cartItemId);
    if (item) {
      item.quantity = updateDTO.quantity;
      if (item.quantity <= 0) {
        cart.items = cart.items.filter(i => i.id !== updateDTO.cartItemId);
      }
    }
    
    this.updateCartTotals(cart);
    this.cartSubject.next(cart);
    this.saveCart(cart);
    this.updateItemCount(cart);
    
    return of(cart);
  }

  removeItem(cartItemId: number): Observable<Cart> {
    const cart = this.cartSubject.value;
    if (!cart) return of(this.createEmptyCart());
    
    cart.items = cart.items.filter(item => item.id !== cartItemId);
    
    this.updateCartTotals(cart);
    this.cartSubject.next(cart);
    this.saveCart(cart);
    this.updateItemCount(cart);
    
    return of(cart);
  }

  clearCart(): Observable<void> {
    const emptyCart = this.createEmptyCart();
    this.cartSubject.next(emptyCart);
    localStorage.removeItem(this.CART_KEY);
    this.itemCountSubject.next(0);
    return of(void 0);
  }

  getTotal(): number {
    const cart = this.cartSubject.value;
    return cart ? cart.total : 0;
  }

  getItemCount(): number {
    return this.itemCountSubject.value;
  }

  isInCart(productId: number): boolean {
    const cart = this.cartSubject.value;
    if (!cart) return false;
    return cart.items.some(item => item.product.id === productId);
  }

  getProductQuantity(productId: number): number {
    const cart = this.cartSubject.value;
    if (!cart) return 0;
    const item = cart.items.find(item => item.product.id === productId);
    return item ? item.quantity : 0;
  }

  private updateCartTotals(cart: Cart): void {
    cart.itemCount = cart.items.reduce((sum, item) => sum + item.quantity, 0);
    cart.total = cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    cart.updatedAt = new Date();
  }

  private updateItemCount(cart: Cart | null): void {
    if (cart) {
      this.itemCountSubject.next(cart.itemCount);
    } else {
      this.itemCountSubject.next(0);
    }
  }

  syncCart(): Observable<Cart> {
    return this.getCart();
  }
}
