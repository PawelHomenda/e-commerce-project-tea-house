import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const AuthGuard = () => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Guardar la URL actual para redirigir después del login
  const currentUrl = router.routerState.snapshot.url;
  localStorage.setItem('returnUrl', currentUrl);

  return router.createUrlTree(['/login']);
};