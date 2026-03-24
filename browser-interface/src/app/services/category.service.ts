import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, timeout, retry } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Category } from '../models/product.model';

/**
 * Servicio de Categorías
 * Maneja todas las operaciones relacionadas con categorías
 */
@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) { }

  /**
   * Obtener todas las categorías
   */
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener categorías desde API:', err.message || err);
        console.warn('Usando array vacío como fallback');
        return of([]);
      })
    );
  }

  /**
   * Obtener categorías activas
   */
  getActiveCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/active`).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener categorías activas:', err.message || err);
        console.warn('Usando array vacío como fallback');
        return of([]);
      })
    );
  }

  /**
   * Obtener categoría por ID
   */
  getCategoryById(id: number): Observable<Category | null> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error al obtener categoría:', err.message || err);
        return of(null);
      })
    );
  }

  /**
   * Buscar categorías por nombre
   */
  searchCategories(query: string): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/search`, {
      params: { q: query }
    }).pipe(
      timeout(5000),
      retry(1),
      catchError(err => {
        console.error('Error buscando categorías:', err.message || err);
        return of([]);
      })
    );
  }
}
