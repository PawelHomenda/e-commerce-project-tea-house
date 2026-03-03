/**
 * Roles de usuario
 */
export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
  MODERATOR = 'MODERATOR'
}

/**
 * Interfaz User - Usuario
 */
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: UserRole;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * DTO para registro de usuario
 */
export interface RegisterDTO {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
}

/**
 * DTO para login
 */
export interface LoginDTO {
  email: string;
  password: string;
}

/**
 * Respuesta de autenticación
 */
export interface AuthResponse {
  token: string;
  refreshToken?: string;
  user: User;
  expiresIn: number;
}

/**
 * DTO para actualizar perfil
 */
export interface UpdateProfileDTO {
  firstName?: string;
  lastName?: string;
  phone?: string;
}

/**
 * DTO para cambiar contraseña
 */
export interface ChangePasswordDTO {
  currentPassword: string;
  newPassword: string;
}
