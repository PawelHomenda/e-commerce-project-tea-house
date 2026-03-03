import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { 
  User, 
  LoginDTO, 
  RegisterDTO, 
  AuthResponse,
  UpdateProfileDTO,
  ChangePasswordDTO 
} from '../models/user.model';
import { environment } from '../environments/environment';

/**
 * Servicio de Autenticación
 * Maneja login, registro, tokens y sesión de usuario
 * Equivalente al AuthService y SecurityConfig de Spring Boot
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private userUrl = `${environment.apiUrl}/users`;

  // Estado del usuario actual
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  // Estado de autenticación
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  /**
   * Registrar nuevo usuario
   */
  register(registerDTO: RegisterDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, registerDTO).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  /**
   * Login de usuario
   */
  login(loginDTO: LoginDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginDTO).pipe(
      tap(response => this.handleAuthentication(response))
    );
  }

  /**
   * Logout de usuario
   */
  logout(): void {
    // Llamar al endpoint de logout en el backend (opcional)
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe();

    // Limpiar storage local
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    
    // Actualizar estado
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    
    // Redirigir al login
    this.router.navigate(['/login']);
  }

  /**
   * Refrescar token
   */
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

  /**
   * Obtener perfil del usuario actual
   */
  getCurrentUserProfile(): Observable<User> {
    return this.http.get<User>(`${this.userUrl}/me`).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  /**
   * Actualizar perfil de usuario
   */
  updateProfile(updateDTO: UpdateProfileDTO): Observable<User> {
    return this.http.put<User>(`${this.userUrl}/me`, updateDTO).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  /**
   * Cambiar contraseña
   */
  changePassword(changePasswordDTO: ChangePasswordDTO): Observable<void> {
    return this.http.put<void>(`${this.userUrl}/me/password`, changePasswordDTO);
  }

  /**
   * Solicitar recuperación de contraseña
   */
  requestPasswordReset(email: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/forgot-password`, { email });
  }

  /**
   * Resetear contraseña con token
   */
  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  /**
   * Verificar email
   */
  verifyEmail(token: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/verify-email`, { token });
  }

  /**
   * Obtener usuario actual del estado
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  /**
   * Verificar si el usuario es admin
   */
  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user ? user.role === 'ADMIN' : false;
  }

  /**
   * Obtener token del localStorage
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Manejar autenticación exitosa
   */
  private handleAuthentication(response: AuthResponse): void {
    // Guardar tokens
    localStorage.setItem('token', response.token);
    if (response.refreshToken) {
      localStorage.setItem('refreshToken', response.refreshToken);
    }
    
    // Guardar usuario
    localStorage.setItem('user', JSON.stringify(response.user));
    
    // Actualizar estado
    this.currentUserSubject.next(response.user);
    this.isAuthenticatedSubject.next(true);
  }

  /**
   * Verificar si existe token en storage
   */
  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Obtener usuario del storage
   */
  private getUserFromStorage(): User | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        return JSON.parse(userStr) as User;
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  /**
   * Verificar si el token ha expirado
   */
  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiry = payload.exp * 1000; // Convertir a milisegundos
      return Date.now() >= expiry;
    } catch (e) {
      return true;
    }
  }
}
