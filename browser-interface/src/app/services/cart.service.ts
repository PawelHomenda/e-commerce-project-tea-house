import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Cart, CartItem, AddToCartDTO, UpdateCartItemDTO } from '../models/cart.model';

/**
 * Servicio de Carrito de Compras
 * Maneja todas las operaciones del carrito
 * Equivalente al CartService de Spring Boot
 */
@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiUrl}/cart`;

  // Estado del carrito en tiempo real
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  // Contador de items
  private itemCountSubject = new BehaviorSubject<number>(0);
  public itemCount$ = this.itemCountSubject.asObservable();

  constructor(private http: HttpClient) {
    // Cargar carrito al iniciar
    this.loadCart();
  }

  /**
   * Cargar el carrito del usuario o crear uno nuevo
   */
  private loadCart(): void {
    this.getCart().subscribe(
      cart => {
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      },
      error => {
        console.error('Error loading cart:', error);
      }
    );
  }

  /**
   * Obtener el carrito actual
   */
  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      })
    );
  }

  /**
   * Añadir producto al carrito
   */
  addToCart(addToCartDTO: AddToCartDTO): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/add`, addToCartDTO).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      })
    );
  }

  /**
   * Añadir producto por ID (método simplificado)
   */
  addProduct(productId: number, quantity: number = 1): Observable<Cart> {
    return this.addToCart({ productId, quantity });
  }

  /**
   * Actualizar cantidad de un item
   */
  updateItemQuantity(updateDTO: UpdateCartItemDTO): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/update`, updateDTO).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      })
    );
  }

  /**
   * Eliminar item del carrito
   */
  removeItem(cartItemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${cartItemId}`).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        this.updateItemCount(cart);
      })
    );
  }

  /**
   * Vaciar el carrito
   */
  clearCart(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/clear`).pipe(
      tap(() => {
        this.cartSubject.next(null);
        this.itemCountSubject.next(0);
      })
    );
  }

  /**
   * Obtener el total del carrito
   */
  getTotal(): number {
    const cart = this.cartSubject.value;
    return cart ? cart.total : 0;
  }

  /**
   * Obtener el número de items
   */
  getItemCount(): number {
    return this.itemCountSubject.value;
  }

  /**
   * Verificar si un producto está en el carrito
   */
  isInCart(productId: number): boolean {
    const cart = this.cartSubject.value;
    if (!cart) return false;
    return cart.items.some(item => item.product.id === productId);
  }

  /**
   * Obtener cantidad de un producto en el carrito
   */
  getProductQuantity(productId: number): number {
    const cart = this.cartSubject.value;
    if (!cart) return 0;
    const item = cart.items.find(item => item.product.id === productId);
    return item ? item.quantity : 0;
  }

  /**
   * Aplicar cupón de descuento
   */
  applyCoupon(couponCode: string): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/coupon`, { code: couponCode }).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
      })
    );
  }

  /**
   * Remover cupón
   */
  removeCoupon(): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/coupon`).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
      })
    );
  }

  /**
   * Actualizar contador de items
   */
  private updateItemCount(cart: Cart | null): void {
    if (cart) {
      this.itemCountSubject.next(cart.itemCount);
    } else {
      this.itemCountSubject.next(0);
    }
  }

  /**
   * Sincronizar carrito (útil después de login)
   */
  syncCart(): Observable<Cart> {
    return this.getCart();
  }
}
