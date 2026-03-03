import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { 
  Product, 
  ProductDTO, 
  ProductFilter, 
  PagedResponse,
  Category 
} from '../models/product.model';

/**
 * Servicio de Productos
 * Maneja todas las operaciones relacionadas con productos
 * Equivalente al ProductService de Spring Boot
 */
@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;
  private categoriesUrl = `${environment.apiUrl}/categories`;

  // Estado local para caché (opcional)
  private productsCache = new BehaviorSubject<Product[]>([]);
  public products$ = this.productsCache.asObservable();

  constructor(private http: HttpClient) { }

  /**
   * Obtener todos los productos con paginación y filtros
   */
  getAllProducts(filter?: ProductFilter): Observable<PagedResponse<Product>> {
    let params = new HttpParams();

    if (filter) {
      if (filter.categoryId) params = params.set('categoryId', filter.categoryId.toString());
      if (filter.minPrice) params = params.set('minPrice', filter.minPrice.toString());
      if (filter.maxPrice) params = params.set('maxPrice', filter.maxPrice.toString());
      if (filter.minRating) params = params.set('minRating', filter.minRating.toString());
      if (filter.inStock !== undefined) params = params.set('inStock', filter.inStock.toString());
      if (filter.isNew !== undefined) params = params.set('isNew', filter.isNew.toString());
      if (filter.isBestseller !== undefined) params = params.set('isBestseller', filter.isBestseller.toString());
      if (filter.searchTerm) params = params.set('searchTerm', filter.searchTerm);
      if (filter.sortBy) params = params.set('sortBy', filter.sortBy);
      if (filter.sortOrder) params = params.set('sortOrder', filter.sortOrder);
      if (filter.page !== undefined) params = params.set('page', filter.page.toString());
      if (filter.size !== undefined) params = params.set('size', filter.size.toString());
    }

    return this.http.get<PagedResponse<Product>>(this.apiUrl, { params });
  }

  /**
   * Obtener producto por ID
   */
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtener productos destacados (featured)
   */
  getFeaturedProducts(limit: number = 8): Observable<Product[]> {
    const params = new HttpParams()
      .set('featured', 'true')
      .set('limit', limit.toString());
    
    return this.http.get<Product[]>(`${this.apiUrl}/featured`, { params });
  }

  /**
   * Obtener productos nuevos
   */
  getNewProducts(limit: number = 8): Observable<Product[]> {
    const params = new HttpParams()
      .set('isNew', 'true')
      .set('limit', limit.toString());
    
    return this.http.get<Product[]>(`${this.apiUrl}/new`, { params });
  }

  /**
   * Obtener bestsellers
   */
  getBestsellers(limit: number = 8): Observable<Product[]> {
    const params = new HttpParams()
      .set('isBestseller', 'true')
      .set('limit', limit.toString());
    
    return this.http.get<Product[]>(`${this.apiUrl}/bestsellers`, { params });
  }

  /**
   * Obtener productos por categoría
   */
  getProductsByCategory(categoryId: number, page: number = 0, size: number = 12): Observable<PagedResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PagedResponse<Product>>(`${this.apiUrl}/category/${categoryId}`, { params });
  }

  /**
   * Buscar productos
   */
  searchProducts(searchTerm: string, page: number = 0, size: number = 12): Observable<PagedResponse<Product>> {
    const params = new HttpParams()
      .set('q', searchTerm)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PagedResponse<Product>>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Obtener productos relacionados
   */
  getRelatedProducts(productId: number, limit: number = 4): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/${productId}/related`, {
      params: { limit: limit.toString() }
    });
  }

  /**
   * Crear producto (solo admin)
   */
  createProduct(productDTO: ProductDTO): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, productDTO);
  }

  /**
   * Actualizar producto (solo admin)
   */
  updateProduct(id: number, productDTO: Partial<ProductDTO>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, productDTO);
  }

  /**
   * Eliminar producto (solo admin)
   */
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtener todas las categorías
   */
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoriesUrl);
  }

  /**
   * Obtener categoría por ID
   */
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.categoriesUrl}/${id}`);
  }

  /**
   * Obtener categoría por slug
   */
  getCategoryBySlug(slug: string): Observable<Category> {
    return this.http.get<Category>(`${this.categoriesUrl}/slug/${slug}`);
  }

  /**
   * Verificar disponibilidad de stock
   */
  checkStock(productId: number, quantity: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${productId}/check-stock`, {
      params: { quantity: quantity.toString() }
    });
  }

  /**
   * Actualizar caché local de productos
   */
  private updateCache(products: Product[]): void {
    this.productsCache.next(products);
  }

  /**
   * Limpiar caché
   */
  clearCache(): void {
    this.productsCache.next([]);
  }
}
