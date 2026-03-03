/**
 * Interfaz Product - Representa un producto de té
 * Equivalente a la entidad Product en Spring Boot
 */
export interface Product {
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
