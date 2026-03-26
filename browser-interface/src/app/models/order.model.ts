import { Product } from './product.model';
import { User } from './user.model';

/**
 * Estados posibles de un pedido
 */
export enum OrderStatus {
  PENDENT = 'PENDENT',
  PREPARING = 'PREPARING',
  DELIVERED = 'DELIVERED',
  CANCELED = 'CANCELED'
}

/**
 * Métodos de pago
 */
export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  PAYPAL = 'PAYPAL',
  BANK_TRANSFER = 'BANK_TRANSFER',
  CASH_ON_DELIVERY = 'CASH_ON_DELIVERY'
}

/**
 * Estados de pago
 */
export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

/**
 * Interfaz OrderItem - Item de un pedido
 */
export interface OrderItem {
  id: number;
  product: Product;
  quantity: number;
  price: number;
  subtotal: number;
}

/**
 * Interfaz ShippingAddress - Dirección de envío
 */
export interface ShippingAddress {
  id?: number;
  fullName: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  phone: string;
}

/**
 * Interfaz Order - Pedido
 */
export interface Order {
  id: number;
  orderNumber: string;
  user: User;
  items: OrderItem[];
  subtotal: number;
  tax: number;
  shippingCost: number;
  total: number;
  status: OrderStatus;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  shippingAddress: ShippingAddress;
  notes?: string;
  createdAt: Date;
  updatedAt: Date;
  estimatedDelivery?: Date;
}

/**
 * DTO para crear un pedido
 */
export interface CreateOrderDTO {
  cartId: number;
  shippingAddress: ShippingAddress;
  paymentMethod: PaymentMethod;
  notes?: string;
}

/**
 * Resumen de pedido para confirmación
 */
export interface OrderSummary {
  items: OrderItem[];
  subtotal: number;
  tax: number;
  shippingCost: number;
  total: number;
  itemCount: number;
}

/**
 * Respuesta paginada genérica
 */
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
