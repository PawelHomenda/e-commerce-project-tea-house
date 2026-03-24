import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-authorized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="authorized-screen">
      <h2>Procesando inicio de sesión...</h2>
      <p *ngIf="message">{{ message }}</p>
    </div>
  `,
  styles: [
    `.authorized-screen { padding: 3rem; text-align: center; color: var(--fg); }`,
    `.authorized-screen h2 { font-size: 1.75rem; margin-bottom: 1rem; }`,
    `.authorized-screen p { opacity: 0.85; }`
  ]
})
export class AuthorizedComponent implements OnInit {
  message = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const code = params.get('code');
      const token = params.get('token');

      if (token) {
        this.message = 'Autenticación completada. Redirigiendo...';
        this.authService.setToken(token);
        this.router.navigate(['/']);
        return;
      }

      if (code) {
        this.message = 'Intercambiando código por token...';
        this.authService.exchangeCode(code).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: (err) => {
            console.error('Error intercambiando código:', err);
            this.message = 'No se pudo completar el inicio de sesión. Intenta de nuevo.';
          }
        });
        return;
      }

      this.message = 'No se recibió código ni token en la URL.';
    });
  }
}
