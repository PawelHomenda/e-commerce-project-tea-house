import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, LOCALE_ID, inject } from '@angular/core';
import { provideRouter, Router, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es';
import { tap } from 'rxjs/operators';

registerLocaleData(localeEs);

const PUBLIC_API_GET_PATHS = [
  '/api/products',
  '/api/categories'
];

function normalizeToken(rawToken: string | null): string | null {
  if (!rawToken) return null;
  const trimmed = rawToken.trim();
  if (!trimmed) return null;
  return trimmed.toLowerCase().startsWith('bearer ')
    ? trimmed.substring(7).trim()
    : trimmed;
}

function isLikelyJwt(token: string): boolean {
  return token.split('.').length === 3;
}

function isPublicGetRequest(url: string, method: string): boolean {
  if (method !== 'GET') return false;
  try {
    const parsed = new URL(url, window.location.origin);
    return PUBLIC_API_GET_PATHS.some((path) => parsed.pathname.startsWith(path));
  } catch {
    return false;
  }
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' })),
    provideHttpClient(withInterceptors([
      (req, next) => {
        const router = inject(Router);

        if (!isPublicGetRequest(req.url, req.method)) {
          const normalizedToken = normalizeToken(localStorage.getItem('token'));
          if (normalizedToken && isLikelyJwt(normalizedToken)) {
            req = req.clone({ setHeaders: { Authorization: `Bearer ${normalizedToken}` } });
          } else if (normalizedToken) {
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
          }
        }

        return next(req).pipe(
          tap({
            error: (err) => {
              if (err.status === 401 && localStorage.getItem('token')) {
                localStorage.removeItem('token');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('user');
                router.navigate(['/login']);
              }
            }
          })
        );
      }
    ])),
    { provide: LOCALE_ID, useValue: 'es' },
  ]
};