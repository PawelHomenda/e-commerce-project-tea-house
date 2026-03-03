/**
 * Interfaz CartItem - Item en el carrito
 */
export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
  subtotal: number;
}

/**
 * Interfaz Cart - Carrito de compras
 */
export interface Cart {
  id: number;
  userId?: number;
  items: CartItem[];
  total: number;
  itemCount: number;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para añadir items al carrito
 */
export interface AddToCartDTO {
  productId: number;
  quantity: number;
}

/**
 * DTO para actualizar cantidad en carrito
 */
export interface UpdateCartItemDTO {
  cartItemId: number;
  quantity: number;
}

/**
 * Importando el modelo Product
 */
import { Product } from './product.model';
