/**
 * Interfaz Product - Representa un producto de té
 * Equivalente a la entidad Product en Spring Boot
 */
export interface Product {
  active: boolean;
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  category: Category;
  imageUrl: string;
  thumbnailUrl?: string;
  origin: string;
  weight: number;
  unit: 'g' | 'kg';
  rating: number;
  reviewCount: number;
  isNew: boolean;
  isBestseller: boolean;
  isLimitedEdition: boolean;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para crear/actualizar productos
 */
export interface ProductDTO {
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
  imageUrl: string;
  thumbnailUrl?: string;
  origin: string;
  weight: number;
  unit: 'g' | 'kg';
}

/**
 * Interfaz Category - Categorías de té
 * Equivalente a la entidad Category en Spring Boot
 */
export interface Category {
  id: number;
  name: string;
  description: string;
  slug: string;
  productCount: number;
  imageUrl?: string;
}

/**
 * Filtros para búsqueda de productos
 */
export interface ProductFilter {
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
  inStock?: boolean;
  isNew?: boolean;
  isBestseller?: boolean;
  searchTerm?: string;
  sortBy?: 'price' | 'name' | 'rating' | 'createdAt';
  sortOrder?: 'asc' | 'desc';
  page?: number;
  size?: number;
}

/**
 * Respuesta paginada de productos
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

/**
 * Interfaz Client - Cliente/Usuario de la plataforma
 */
export interface Client {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
  oauth2Id?: string;
  oauth2Provider?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para actualizar cliente
 */
export interface ClientUpdateDTO {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
}

/**
 * Interfaz Employee - Empleado/Personal de la tienda
 */
export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  salary: number;
  position?: string;
  department?: string;
  oauth2Id?: string;
  oauth2Provider?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para crear/actualizar empleado
 */
export interface EmployeeUpdateDTO {
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  salary?: number;
  position?: string;
  department?: string;
}

/**
 * Interfaz Provider - Proveedor/Distribuidor de té
 */
export interface Provider {
  id: number;
  name: string;
  contact: string;
  email: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
  oauth2Id?: string;
  oauth2Provider?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para crear/actualizar proveedor
 */
export interface ProviderUpdateDTO {
  name?: string;
  contact?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  country?: string;
}

/**
 * Interfaz OrderClient - Pedido de cliente (modelo del backend)
 */
export interface OrderClient {
  id: number;
  client: Client;
  employee?: Employee;
  orderDate: string;
  orderState: string;
  serviceType: string;
  discountPercentage: number;
  invoiceClient?: any;
  detailOrderClients?: any[];
  subtotal: number;
  total: number;
}
