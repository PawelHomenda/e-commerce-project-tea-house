import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, timeout, retry } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { 
  Product, 
  ProductDTO, 
  ProductFilter, 
  PagedResponse
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

  // Estado local para caché
  private productsCache = new BehaviorSubject<Product[]>([]);
  public products$ = this.productsCache.asObservable();

  constructor(private http: HttpClient) { }

  /**
   * Obtener todos los productos
   */
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener productos:', err.message || err);
        console.warn('Usando array vacío como fallback para productos');
        return of([]);
      })
    );
  }

  /**
   * Obtener todos los productos activos
   */
  getActiveProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/active`).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener productos activos:', err.message || err);
        console.warn('Usando array vacío como fallback para productos activos');
        return of([]);
      })
    );
  }

  /**
   * Obtener producto por ID
   */
  getProductById(id: number): Observable<Product | null> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener producto:', err.message || err);
        return of(null);
      })
    );
  }

  /**
   * Obtener productos por categoría
   */
  getProductsByCategory(categoryId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/category/${categoryId}`).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener productos por categoría:', err.message || err);
        console.warn('Usando array vacío como fallback para categoría');
        return of([]);
      })
    );
  }

  /**
   * Crear producto (solo admin)
   */
  createProduct(productDTO: ProductDTO): Observable<Product | null> {
    return this.http.post<Product>(this.apiUrl, productDTO).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error al crear producto:', err.message || err);
        return of(null);
      })
    );
  }

  /**
   * Actualizar producto (solo admin)
   */
  updateProduct(id: number, productDTO: Partial<ProductDTO>): Observable<Product | null> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, productDTO).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error al actualizar producto:', err.message || err);
        return of(null);
      })
    );
  }

  /**
   * Eliminar producto (solo admin)
   */
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error al eliminar producto:', err.message || err);
        return of(void 0);
      })
    );
  }

  /**
   * Actualizar caché local de productos
   */
  updateCache(products: Product[]): void {
    this.productsCache.next(products);
  }

  /**
   * Limpiar caché
   */
  clearCache(): void {
    this.productsCache.next([]);
  }
}
