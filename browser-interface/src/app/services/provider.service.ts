import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Provider } from '../models/product.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProviderService {
  private readonly apiUrl = `${environment.apiUrl}/api/providers`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<any>(this.apiUrl, { params });
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
