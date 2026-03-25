import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Provider } from '../models/product.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProviderService {
  private readonly apiUrl = `${environment.apiUrl}/providers`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Provider[]> {
    return this.http.get<Provider[]>(this.apiUrl);
  }

  getById(id: number): Observable<Provider> {
    return this.http.get<Provider>(`${this.apiUrl}/${id}`);
  }

  create(provider: Omit<Provider, 'id' | 'createdAt' | 'updatedAt'>): Observable<Provider> {
    return this.http.post<Provider>(this.apiUrl, provider);
  }

  update(id: number, provider: Partial<Provider>): Observable<Provider> {
    return this.http.put<Provider>(`${this.apiUrl}/${id}`, provider);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Búsqueda por nombre
   */
  search(name?: string): Observable<Provider[]> {
    let params = new HttpParams();
    if (name) params = params.set('name', name);
    return this.http.get<Provider[]>(`${this.apiUrl}/search`, { params });
  }
}
