import { Product } from './product.model';

/**
 * Estados posibles de un pedido
 * Equivalente a OrderState en Spring Boot
 */
export enum OrderStatus {
  PENDENT = 'PENDENT',
  PREPARING = 'PREPARING',
  DELIVERED = 'DELIVERED',
  CANCELED = 'CANCELED'
}

/**
 * Métodos de pago
 * Equivalente a PaymentMethod en Spring Boot
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
 * Equivalente a PaymentState en Spring Boot
 */
export enum PaymentStatus {
  PAID = 'PAID',
  PENDENT = 'PENDENT'
}

/**
 * Tipo de servicio
 * Equivalente a ServiceType en Spring Boot
 */
export type ServiceType = 'TAKEAWAY' | 'TABLE' | 'DELIVERY';

/**
 * Interfaz OrderItem - Detalle de un pedido
 */
export interface OrderItem {
  id: number;
  product: Product;
  quantity: number;
  unitPrice: number;
  subtotal?: number;
}

/**
 * Interfaz Order - Pedido de cliente
 * Equivalente a OrderClient en Spring Boot
 */
export interface Order {
  id: number;
  client: any;
  employee?: any;
  orderDate: string;
  orderState: OrderStatus;
  serviceType: ServiceType;
  discountPercentage?: number;
  detailOrderClients: OrderItem[];
  invoiceClient?: any;
  subtotal?: number;
  total?: number;
}

/**
 * DTO para crear un pedido
 */
export interface CreateOrderDTO {
  client: { id: number };
  serviceType: ServiceType;
  orderState?: string;
  orderDate?: string;
  detailOrderClients: {
    product: { id: number };
    quantity: number;
    unitPrice: number;
  }[];
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
