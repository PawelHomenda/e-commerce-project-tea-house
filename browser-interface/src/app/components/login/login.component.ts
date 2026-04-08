import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],   // ← CORREGIDO: era 'email', el backend espera 'username'
      password: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = $localize`:login.fieldsRequired@@login.fieldsRequired:Por favor completa todos los campos`;
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    // ← CORREGIDO: usa las credenciales del formulario en vez de redirigir a OAuth2
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        const returnUrl = localStorage.getItem('returnUrl') || '/';
        localStorage.removeItem('returnUrl');
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.errorMessage = err.status === 401
          ? $localize`:login.invalidCredentials@@login.invalidCredentials:Usuario o contraseña incorrectos`
          : $localize`:login.serverError@@login.serverError:Error al conectar con el servidor`;
        this.isSubmitting = false;
      }
    });
  }
}