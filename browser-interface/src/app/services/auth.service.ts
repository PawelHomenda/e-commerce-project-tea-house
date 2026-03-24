import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import {
  User,
  LoginDTO,
  RegisterDTO,
  AuthResponse,
  UpdateProfileDTO,
  ChangePasswordDTO
} from '../models/user.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  // ← CORREGIDO: el login directo va al auth server, no al api service
  private authServerUrl = `${environment.authServerUrl}/auth`;
  private apiUrl        = `${environment.apiUrl}/auth`;
  private userUrl       = `${environment.apiUrl}/users`;
  private tokenExchangeUrl = `${environment.apiUrl}/auth/token`;

  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public  currentUser$       = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public  isAuthenticated$       = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  register(registerDTO: RegisterDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, registerDTO).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  // ← CORREGIDO: llama al endpoint REST del auth server con usuario/contraseña
  login(loginDTO: LoginDTO): Observable<any> {
    return this.http.post<any>(`${this.authServerUrl}/login`, loginDTO).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  authorize(): void {
    const authorizationUrl = new URL('http://localhost:9000/oauth2/authorize');
    authorizationUrl.searchParams.set('response_type', 'code');
    authorizationUrl.searchParams.set('client_id', 'client-app');
    authorizationUrl.searchParams.set('redirect_uri', 'http://localhost:4200/authorized');
    authorizationUrl.searchParams.set('scope', 'openid profile email user:client');
    window.location.href = authorizationUrl.toString();
  }

  exchangeCode(code: string) {
    return this.http.post<any>(this.tokenExchangeUrl, { code }).pipe(
      tap((response) => {
        const token = response?.access_token || response?.token;
        if (token) {
          this.setToken(token);
          this.getCurrentUserProfile().subscribe({
            next:  (user) => this.currentUserSubject.next(user),
            error: ()     => this.currentUserSubject.next(null)
          });
        }
      })
    );
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
    this.isAuthenticatedSubject.next(true);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        if (response.refreshToken) {
          localStorage.setItem('refreshToken', response.refreshToken);
        }
      })
    );
  }

  getCurrentUserProfile(): Observable<User> {
    return this.http.get<User>(`${this.userUrl}/me`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  updateProfile(updateDTO: UpdateProfileDTO): Observable<User> {
    return this.http.put<User>(`${this.userUrl}/me`, updateDTO).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  changePassword(changePasswordDTO: ChangePasswordDTO): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/me/password`, changePasswordDTO);
  }

  requestPasswordReset(email: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  verifyEmail(token: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/verify-email`, { token });
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value && !this.isTokenExpired();
  }

  // ← CORREGIDO: el backend devuelve roles: ["ROLE_ADMIN"], no user.role
  isAdmin(): boolean {
    const user = this.getCurrentUser();
    if (user?.role) return user.role === 'ADMIN';            // modelo antiguo
    const stored = localStorage.getItem('user');
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        return (parsed.roles as string[] ?? [])
          .some(r => r === 'ROLE_ADMIN' || r === 'admin');
      } catch { return false; }
    }
    return false;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // ← CORREGIDO: soporta tanto {token, user} (modelo viejo) como {token, username, roles} (nuevo)
  private handleAuthentication(response: any): void {
    if (!response?.token) return;

    localStorage.setItem('token', response.token);

    if (response.refreshToken) {
      localStorage.setItem('refreshToken', response.refreshToken);
    }

    // Respuesta del nuevo endpoint REST: { token, username, roles }
    if (response.username) {
      const user: any = {
        username: response.username,
        role: (response.roles as string[])
          ?.find(r => !r.startsWith('ROLE_') ? r : r.substring(5))
          ?.replace('ROLE_', '') ?? ''
      };
      localStorage.setItem('user', JSON.stringify(user));
      this.currentUserSubject.next(user);
    }

    // Respuesta del endpoint OAuth2 antiguo: { token, user }
    if (response.user) {
      localStorage.setItem('user', JSON.stringify(response.user));
      this.currentUserSubject.next(response.user);
    }

    this.isAuthenticatedSubject.next(true);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('token') && !this.isTokenExpired();
  }

  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try { return JSON.parse(userStr) as User; }
      catch { return null; }
    }
    return null;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() >= payload.exp * 1000;
    } catch { return true; }
  }
}